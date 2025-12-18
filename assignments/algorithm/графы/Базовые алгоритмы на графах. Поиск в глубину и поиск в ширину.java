import java.util.*;

public class GraphTraversal {

    private Map<Integer, List<Integer>> graph = new HashMap<>();

    // Граф был задан в виде картинки, и сначала я переписал его в список смежности.
    // По рисунку видно, что вершины соединены так:
    // 1 — 2, 1 — 3, 1 — 4, 2 — 3, 2 — 5, 3 — 4
    // Это обычный неориентированный граф.
    public void addEdge(int a, int b) {
        graph.putIfAbsent(a, new ArrayList<>());
        graph.putIfAbsent(b, new ArrayList<>());
        graph.get(a).add(b);
        graph.get(b).add(a);
    }

    // Обход в глубину (DFS) работает как бы «вглубь» графа, пока не упрётся в вершину, откуда дальше идти некуда.
    // Если начинать с вершины 1, то получается такой порядок:
    // 1 → 2 → 3 → 4 → (возврат) → 5
    // То есть, DFS: 1, 2, 3, 4, 5
    public void dfs(int start) {
        Set<Integer> visited = new HashSet<>();
        System.out.print("DFS: ");
        dfsRec(start, visited);
        System.out.println();
    }

    // Рекурсивная реализация DFS, где мы идём вглубь графа.
    private void dfsRec(int node, Set<Integer> visited) {
        visited.add(node);
        System.out.print(node + " ");
        for (int next : graph.get(node)) {
            if (!visited.contains(next)) {
                dfsRec(next, visited);
            }
        }
    }

    // Обход в ширину (BFS) работает по принципу обхода слоями.
    // Сначала мы посещаем всех соседей 1, потом уже соседей этих вершин.
    // Если начинать с вершины 1, то обход будет: 1 → 2 → 3 → 4 → 5
    // Это почти такой же результат, как и для DFS, но метод обхода отличается.
    public void bfs(int start) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        System.out.print("BFS: ");
        while (!queue.isEmpty()) {
            int v = queue.poll();
            System.out.print(v + " ");

            for (int next : graph.get(v)) {
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        System.out.println();
    }

    // Основной метод для тестирования
    public static void main(String[] args) {
        GraphTraversal g = new GraphTraversal();

        // Создаём граф с рёбрами
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(2, 3);
        g.addEdge(2, 5);
        g.addEdge(3, 4);

        // Выполняем обход в глубину и ширину
        g.dfs(1);
        g.bfs(1);
    }
}
