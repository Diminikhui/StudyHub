import java.util.*;

class Edge implements Comparable<Edge> {
    String source;
    String destination;
    int weight;

    Edge(String s, String d, int w) {
        source = s;
        destination = d;
        weight = w;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return source + "-" + destination + ": " + weight;
    }
}

class DisjointSet {
    private Map<String, String> parent = new HashMap<>();
    private Map<String, Integer> rank = new HashMap<>();

    public void makeSet(Collection<String> vertices) {
        for (String v : vertices) {
            parent.put(v, v);
            rank.put(v, 0);
        }
    }

    public String find(String v) {
        if (!parent.get(v).equals(v)) {
            parent.put(v, find(parent.get(v)));
        }
        return parent.get(v);
    }

    public void union(String a, String b) {
        a = find(a);
        b = find(b);
        if (a.equals(b)) return;

        if (rank.get(a) < rank.get(b)) {
            parent.put(a, b);
        } else if (rank.get(a) > rank.get(b)) {
            parent.put(b, a);
        } else {
            parent.put(b, a);
            rank.put(a, rank.get(a) + 1);
        }
    }

    public boolean connected(String a, String b) {
        return find(a).equals(find(b));
    }
}

class Graph {
    private Map<String, List<Edge>> adj = new HashMap<>();
    private List<Edge> edges = new ArrayList<>();

    public void addEdge(String a, String b, int w) {
        adj.putIfAbsent(a, new ArrayList<>());
        adj.putIfAbsent(b, new ArrayList<>());

        Edge e = new Edge(a, b, w);
        edges.add(e);

        adj.get(a).add(e);
        adj.get(b).add(new Edge(b, a, w));
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Edge> getAdjacentEdges(String v) {
        return adj.getOrDefault(v, new ArrayList<>());
    }

    public Set<String> getVertices() {
        return adj.keySet();
    }
}

class Prim {
    public static List<Edge> mst(Graph g, String start) {
        List<Edge> result = new ArrayList<>();
        Set<String> used = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>();

        used.add(start);
        pq.addAll(g.getAdjacentEdges(start));

        while (!pq.isEmpty()) {
            Edge e = pq.poll();

            if (used.contains(e.destination))
                continue;

            result.add(e);
            used.add(e.destination);

            for (Edge next : g.getAdjacentEdges(e.destination)) {
                if (!used.contains(next.destination)) {
                    pq.add(next);
                }
            }
        }

        return result;
    }
}

class Kruskal {
    public static List<Edge> mst(Graph g) {
        List<Edge> sortedEdges = new ArrayList<>(g.getEdges());
        Collections.sort(sortedEdges);

        DisjointSet ds = new DisjointSet();
        ds.makeSet(g.getVertices());

        List<Edge> result = new ArrayList<>();

        for (Edge e : sortedEdges) {
            if (!ds.connected(e.source, e.destination)) {
                result.add(e);
                ds.union(e.source, e.destination);
            }
        }

        return result;
    }
}

public class MSTTest {
    public static void main(String[] args) {

        Graph g = new Graph();

        g.addEdge("A", "B", 4);
        g.addEdge("A", "C", 3);
        g.addEdge("B", "C", 2);
        g.addEdge("B", "D", 1);
        g.addEdge("B", "E", 5);
        g.addEdge("C", "E", 4);
        g.addEdge("C", "F", 6);
        g.addEdge("D", "E", 3);
        g.addEdge("E", "F", 2);
        g.addEdge("E", "G", 2);
        g.addEdge("F", "G", 1);
        g.addEdge("D", "G", 2);

        System.out.println("===== Алгоритм Прима =====");
        List<Edge> primMST = Prim.mst(g, "A");
        int primWeight = primMST.stream().mapToInt(e -> e.weight).sum();
        primMST.forEach(System.out::println);
        System.out.println("Общий вес: " + primWeight);

        System.out.println("\n===== Алгоритм Краскала =====");
        List<Edge> kruskalMST = Kruskal.mst(g);
        int kruskalWeight = kruskalMST.stream().mapToInt(e -> e.weight).sum();
        kruskalMST.forEach(System.out::println);
        System.out.println("Общий вес: " + kruskalWeight);
    }
}
