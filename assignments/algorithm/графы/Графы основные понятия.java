import java.util.*;

public class GraphExample {

    private Map<String, List<String>> graph = new HashMap<>();

    public void addVertex(String v) {
        graph.putIfAbsent(v, new ArrayList<>());
    }

    public void addEdge(String a, String b) {
        graph.get(a).add(b);
        graph.get(b).add(a);
    }

    public List<String> bfs(String start, String target) {
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(target)) {
                List<String> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = parent.get(current);
                }
                Collections.reverse(path);
                return path;
            }

            for (String neighbor : graph.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        GraphExample g = new GraphExample();

        g.addVertex("A");
        g.addVertex("B");
        g.addVertex("C");
        g.addVertex("D");
        g.addVertex("E");

        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "E");
        g.addEdge("D", "E");

        System.out.println("Путь из A в E:");

        List<String> path = g.bfs("A", "E");

        System.out.println(path);
    }
}
