import java.util.*;

public class PrimAlgorithm {

    static class Edge {
        int to;
        int weight;

        Edge(int t, int w) {
            to = t;
            weight = w;
        }
    }

    public static void prim(int start, List<List<Edge>> graph) {
        int n = graph.size();

        boolean[] used = new boolean[n];
        int[] minEdge = new int[n];
        int[] parent = new int[n];

        Arrays.fill(minEdge, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        minEdge[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.add(new int[]{0, start});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int v = cur[1];

            if (used[v])
                continue;

            used[v] = true;

            for (Edge e : graph.get(v)) {
                if (!used[e.to] && e.weight < minEdge[e.to]) {
                    minEdge[e.to] = e.weight;
                    parent[e.to] = v;
                    pq.add(new int[]{e.weight, e.to});
                }
            }
        }

        System.out.println("Минимальное остовное дерево (Прим):");
        for (int i = 0; i < n; i++) {
            if (parent[i] != -1) {
                System.out.println(parent[i] + " — " + i + " : " + minEdge[i]);
            }
        }
    }

    public static void main(String[] args) {
        int n = 5;

        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

        addEdge(graph, 0, 1, 2);
        addEdge(graph, 0, 3, 6);
        addEdge(graph, 1, 2, 3);
        addEdge(graph, 1, 3, 8);
        addEdge(graph, 1, 4, 5);
        addEdge(graph, 2, 4, 7);
        addEdge(graph, 3, 4, 9);

        prim(0, graph);
    }

    private static void addEdge(List<List<Edge>> g, int a, int b, int w) {
        g.get(a).add(new Edge(b, w));
        g.get(b).add(new Edge(a, w));
    }
}
