import java.util.*;

public class NetworkDelay {

    static class Edge {
        int to;
        int weight;

        Edge(int t, int w) {
            to = t;
            weight = w;
        }
    }

    public static long computeTotalDelay(int r, List<List<Edge>> graph, List<Integer> entryPoints) {

        int[] dist = new int[r];
        Arrays.fill(dist, Integer.MAX_VALUE);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        for (int entry : entryPoints) {
            dist[entry] = 0;
            pq.add(new int[]{0, entry});
        }

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0];
            int node = cur[1];

            if (d > dist[node]) continue;

            for (Edge e : graph.get(node)) {
                int newDist = d + e.weight;
                if (newDist < dist[e.to]) {
                    dist[e.to] = newDist;
                    pq.add(new int[]{newDist, e.to});
                }
            }
        }

        long total = 0;
        for (int i = 0; i < r; i++) {
            total += dist[i];
        }

        return total;
    }

    public static void main(String[] args) {

        int r = 6;

        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < r; i++) graph.add(new ArrayList<>());

        addEdge(graph, 0, 1, 10);
        addEdge(graph, 1, 2, 5);
        addEdge(graph, 1, 3, 7);
        addEdge(graph, 3, 4, 2);
        addEdge(graph, 4, 5, 4);

        List<Integer> entryPoints = Arrays.asList(0, 5);

        long totalDelay = computeTotalDelay(r, graph, entryPoints);

        System.out.println("Общая задержка сети = " + totalDelay + " метров");
    }

    private static void addEdge(List<List<Edge>> graph, int a, int b, int w) {
        graph.get(a).add(new Edge(b, w));
        graph.get(b).add(new Edge(a, w));
    }
}
