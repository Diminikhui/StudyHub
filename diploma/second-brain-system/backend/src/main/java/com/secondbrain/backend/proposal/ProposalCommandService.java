package com.secondbrain.backend.proposal;

import com.secondbrain.backend.action.ActionItem;
import com.secondbrain.backend.action.ActionItemRepository;
import com.secondbrain.backend.embedding.KnowledgeEmbeddingService;
import com.secondbrain.backend.embedding.KnowledgeEntityType;
import com.secondbrain.backend.fact.Fact;
import com.secondbrain.backend.fact.FactRepository;
import com.secondbrain.backend.person.Person;
import com.secondbrain.backend.person.PersonService;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.topic.Topic;
import com.secondbrain.backend.topic.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProposalCommandService {

    private static final Pattern CLAUSE_TEXT_PATTERN =
            Pattern.compile("\\\"clauseText\\\":\\\"(.*?)\\\"");

    private static final Pattern SOURCE_ORDER_PATTERN =
            Pattern.compile("\\\"sourceOrder\\\":(\\d+)");

    private final ProposalRepository proposalRepository;
    private final ActionItemRepository actionItemRepository;
    private final FactRepository factRepository;
    private final TopicService topicService;
    private final PersonService personService;
    private final KnowledgeEmbeddingService knowledgeEmbeddingService;

    public ProposalCommandService(
            ProposalRepository proposalRepository,
            ActionItemRepository actionItemRepository,
            FactRepository factRepository,
            TopicService topicService,
            PersonService personService,
            KnowledgeEmbeddingService knowledgeEmbeddingService
    ) {
        this.proposalRepository = proposalRepository;
        this.actionItemRepository = actionItemRepository;
        this.factRepository = factRepository;
        this.topicService = topicService;
        this.personService = personService;
        this.knowledgeEmbeddingService = knowledgeEmbeddingService;
    }

    public void accept(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proposal not found: " + proposalId
                ));

        proposal.setStatus(ProposalStatus.ACCEPTED);
        proposal.setUpdatedAt(LocalDateTime.now());
        proposalRepository.save(proposal);

        if (proposal.getProposalType() == ProposalType.ACTION_CANDIDATE) {
            ActionItem actionItem = new ActionItem();
            actionItem.setRawItem(proposal.getRawItem());
            actionItem.setTitle(proposal.getTitle());
            actionItem.setDone(false);
            actionItem.setCreatedAt(LocalDateTime.now());
            actionItem.setUpdatedAt(LocalDateTime.now());

            actionItemRepository.save(actionItem);

            knowledgeEmbeddingService.upsert(
                    KnowledgeEntityType.ACTION,
                    actionItem.getId(),
                    actionItem.getTitle()
            );

            bindTopicToActionFromContext(proposal, actionItem);
        }

        if (proposal.getProposalType() == ProposalType.FACT_CANDIDATE) {
            Fact fact = new Fact();
            fact.setRawItem(proposal.getRawItem());
            fact.setContentText(proposal.getTitle());
            fact.setCreatedAt(LocalDateTime.now());
            fact.setUpdatedAt(LocalDateTime.now());

            factRepository.save(fact);

            knowledgeEmbeddingService.upsert(
                    KnowledgeEntityType.FACT,
                    fact.getId(),
                    fact.getContentText()
            );
        }

        if (proposal.getProposalType() == ProposalType.TOPIC_CANDIDATE) {
            Topic topic = topicService.findOrCreate(proposal.getTitle());

            knowledgeEmbeddingService.upsert(
                    KnowledgeEntityType.TOPIC,
                    topic.getId(),
                    topic.getName()
            );

            List<Fact> facts = factRepository.findByRawItemOrderByCreatedAtAsc(proposal.getRawItem());
            for (Fact fact : facts) {
                if (fact.getTopic() == null) {
                    fact.setTopic(topic);
                    fact.setUpdatedAt(LocalDateTime.now());
                    factRepository.save(fact);
                }
            }

            bindTopicToActionsFromContext(proposal, topic);
        }

        if (proposal.getProposalType() == ProposalType.PERSON_CANDIDATE) {
            Person person = personService.findOrCreate(proposal.getTitle());
            bindPersonToActionsFromSameClause(proposal, person);
        }

        relinkRawItem(proposal.getRawItem());
    }

    public void reject(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proposal not found: " + proposalId
                ));

        proposal.setStatus(ProposalStatus.REJECTED);
        proposal.setUpdatedAt(LocalDateTime.now());

        proposalRepository.save(proposal);
    }

    private void bindPersonToActionsFromSameClause(Proposal personProposal, Person person) {
        String clauseText = extractClauseText(personProposal.getPayloadJson());
        if (clauseText.isBlank()) {
            return;
        }

        List<Proposal> proposals = proposalRepository.findByRawItemOrderByCreatedAtAsc(personProposal.getRawItem());
        for (Proposal proposal : proposals) {
            if (proposal.getProposalType() != ProposalType.ACTION_CANDIDATE) {
                continue;
            }
            if (proposal.getStatus() != ProposalStatus.ACCEPTED) {
                continue;
            }

            String actionClauseText = extractClauseText(proposal.getPayloadJson());
            if (!clauseText.equals(actionClauseText)) {
                continue;
            }

            Optional<ActionItem> actionItemOptional = actionItemRepository.findByRawItemAndTitle(
                    proposal.getRawItem(),
                    proposal.getTitle()
            );

            if (actionItemOptional.isPresent()) {
                ActionItem actionItem = actionItemOptional.get();
                if (actionItem.getPerson() == null) {
                    actionItem.setPerson(person);
                    actionItem.setUpdatedAt(LocalDateTime.now());
                    actionItemRepository.save(actionItem);
                }
            }
        }
    }

    private void bindTopicToActionsFromContext(Proposal topicProposal, Topic topic) {
        String topicClauseText = extractClauseText(topicProposal.getPayloadJson());
        int topicSourceOrder = extractSourceOrder(topicProposal.getPayloadJson());

        List<Proposal> proposals = proposalRepository.findByRawItemOrderByCreatedAtAsc(topicProposal.getRawItem());
        for (Proposal proposal : proposals) {
            if (proposal.getProposalType() != ProposalType.ACTION_CANDIDATE) {
                continue;
            }
            if (proposal.getStatus() != ProposalStatus.ACCEPTED) {
                continue;
            }

            String actionClauseText = extractClauseText(proposal.getPayloadJson());
            int actionSourceOrder = extractSourceOrder(proposal.getPayloadJson());

            boolean sameClause = !topicClauseText.isBlank() && topicClauseText.equals(actionClauseText);

            boolean forwardPronounRef = actionSourceOrder > topicSourceOrder
                    && actionSourceOrder <= topicSourceOrder + 2
                    && looksLikePronounReference(actionClauseText);

            boolean backwardPronounRef = topicSourceOrder > actionSourceOrder
                    && topicSourceOrder <= actionSourceOrder + 2
                    && looksLikePronounReference(actionClauseText);

            if (!sameClause && !forwardPronounRef && !backwardPronounRef) {
                continue;
            }

            Optional<ActionItem> actionItemOptional = actionItemRepository.findByRawItemAndTitle(
                    proposal.getRawItem(),
                    proposal.getTitle()
            );

            if (actionItemOptional.isPresent()) {
                ActionItem actionItem = actionItemOptional.get();
                if (actionItem.getTopic() == null) {
                    actionItem.setTopic(topic);
                    actionItem.setUpdatedAt(LocalDateTime.now());
                    actionItemRepository.save(actionItem);
                }
            }
        }
    }

    private void bindTopicToActionFromContext(Proposal actionProposal, ActionItem actionItem) {
        String actionClauseText = extractClauseText(actionProposal.getPayloadJson());
        int actionSourceOrder = extractSourceOrder(actionProposal.getPayloadJson());

        List<Proposal> proposals = proposalRepository.findByRawItemOrderByCreatedAtAsc(actionProposal.getRawItem());

        Optional<Proposal> bestTopicProposal = proposals.stream()
                .filter(p -> p.getProposalType() == ProposalType.TOPIC_CANDIDATE)
                .filter(p -> p.getStatus() == ProposalStatus.ACCEPTED)
                .filter(p -> isTopicRelevantForAction(p, actionClauseText, actionSourceOrder))
                .max(Comparator.comparingInt(p -> extractSourceOrder(p.getPayloadJson())));

        if (bestTopicProposal.isPresent()) {
            Proposal topicProposal = bestTopicProposal.get();
            Topic topic = topicService.findOrCreate(topicProposal.getTitle());

            if (actionItem.getTopic() == null) {
                actionItem.setTopic(topic);
                actionItem.setUpdatedAt(LocalDateTime.now());
                actionItemRepository.save(actionItem);
            }
        }
    }

    private boolean isTopicRelevantForAction(Proposal topicProposal, String actionClauseText, int actionSourceOrder) {
        String topicClauseText = extractClauseText(topicProposal.getPayloadJson());
        int topicSourceOrder = extractSourceOrder(topicProposal.getPayloadJson());

        boolean sameClause = !topicClauseText.isBlank() && topicClauseText.equals(actionClauseText);

        boolean backwardPronounRef = actionSourceOrder > topicSourceOrder
                && actionSourceOrder <= topicSourceOrder + 2
                && looksLikePronounReference(actionClauseText);

        boolean forwardPronounRef = topicSourceOrder > actionSourceOrder
                && topicSourceOrder <= actionSourceOrder + 2
                && looksLikePronounReference(actionClauseText);

        return sameClause || backwardPronounRef || forwardPronounRef;
    }

    private boolean looksLikePronounReference(String clauseText) {
        if (clauseText == null || clauseText.isBlank()) {
            return false;
        }

        String lower = clauseText.toLowerCase();
        return lower.contains(" это ")
                || lower.startsWith("это ")
                || lower.contains(" эту идею")
                || lower.contains("эта идея")
                || lower.contains(" этот проект")
                || lower.contains("этот проект");
    }

    private String extractClauseText(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) {
            return "";
        }

        Matcher matcher = CLAUSE_TEXT_PATTERN.matcher(payloadJson);
        if (matcher.find()) {
            return matcher.group(1)
                    .replace("\\\"", "\"")
                    .trim();
        }

        return "";
    }

    private int extractSourceOrder(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) {
            return -1;
        }

        Matcher matcher = SOURCE_ORDER_PATTERN.matcher(payloadJson);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return -1;
    }

    public void relinkRawItem(RawItem rawItem) {
        if (rawItem == null) {
            return;
        }

        List<Proposal> proposals = proposalRepository.findByRawItemOrderByCreatedAtAsc(rawItem);

        for (Proposal proposal : proposals) {
            if (proposal.getStatus() != ProposalStatus.ACCEPTED) {
                continue;
            }

            if (proposal.getProposalType() == ProposalType.TOPIC_CANDIDATE) {
                Topic topic = topicService.findOrCreate(proposal.getTitle());

                List<Fact> facts = factRepository.findByRawItemOrderByCreatedAtAsc(rawItem);
                for (Fact fact : facts) {
                    if (fact.getTopic() == null) {
                        fact.setTopic(topic);
                        fact.setUpdatedAt(LocalDateTime.now());
                        factRepository.save(fact);
                    }
                }

                bindTopicToActionsFromContext(proposal, topic);
            }

            if (proposal.getProposalType() == ProposalType.PERSON_CANDIDATE) {
                Person person = personService.findOrCreate(proposal.getTitle());
                bindPersonToActionsFromSameClause(proposal, person);
            }

            if (proposal.getProposalType() == ProposalType.ACTION_CANDIDATE) {
                Optional<ActionItem> actionItemOptional = actionItemRepository.findByRawItemAndTitle(
                        proposal.getRawItem(),
                        proposal.getTitle()
                );

                actionItemOptional.ifPresent(actionItem -> bindTopicToActionFromContext(proposal, actionItem));
            }
        }
    }
}