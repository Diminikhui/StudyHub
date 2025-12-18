import java.util.*;

public class TopologicalSortExample {

    private Map<Integer, List<Integer>> graph = new HashMap<>();

    public void addEdge(int a, int b) {
        graph.putIfAbsent(a, new ArrayList<>());
        graph.putIfAbsent(b, new ArrayList<>());
        graph.get(a).add(b);
    }

    public List<Integer> topoSort() {
        Map<Integer, Integer> indegree = new HashMap<>();

        for (int node : graph.keySet()) {
            indegree.putIfAbsent(node, 0);
            for (int next : graph.get(node)) {
                indegree.put(next, indegree.getOrDefault(next, 0) + 1);
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int node : indegree.keySet()) {
            if (indegree.get(node) == 0) {
                queue.add(node);
            }
        }

        List<Integer> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            int v = queue.poll();
            result.add(v);

            for (int next : graph.get(v)) {
                indegree.put(next, indegree.get(next) - 1);
                if (indegree.get(next) == 0) {
                    queue.add(next);
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        TopologicalSortExample g = new TopologicalSortExample();

        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(1, 4);
        g.addEdge(4, 5);

        System.out.println("Топологическая сортировка:");
        System.out.println(g.topoSort());
    }
}
