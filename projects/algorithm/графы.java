import java.util.ArrayList;
import java.util.List;

public class WarshallDemo {

    /*
     * Решение: алгоритм Уоршалла (транзитивное замыкание / матрица достижимости)
     *
     * Исходный ориентированный граф:
     * Рёбра: 0→1, 0→2, 1→3, 1→4
     * Вершины: 0..4
     *
     * Матрица смежности A (0/1):
     * 0: [0 1 1 0 0]
     * 1: [0 0 0 1 1]
     * 2: [0 0 0 0 0]
     * 3: [0 0 0 0 0]
     * 4: [0 0 0 0 0]
     *
     * Правило Уоршалла:
     * r[i][j] = r[i][j] OR (r[i][k] AND r[k][j])
     * То есть, если i достигает k и k достигает j, то i достигает j.
     *
     * Промежуточные матрицы:
     *
     * k = 0: изменений нет
     * 0: [0 1 1 0 0]
     * 1: [0 0 0 1 1]
     * 2: [0 0 0 0 0]
     * 3: [0 0 0 0 0]
     * 4: [0 0 0 0 0]
     *
     * k = 1: добавляются пути 0→3 и 0→4 (через вершину 1)
     * 0: [0 1 1 1 1]
     * 1: [0 0 0 1 1]
     * 2: [0 0 0 0 0]
     * 3: [0 0 0 0 0]
     * 4: [0 0 0 0 0]
     *
     * k = 2, 3, 4: изменений нет (у 2,3,4 нет исходящих рёбер)
     *
     * Итоговая матрица достижимости R:
     * 0: [0 1 1 1 1]
     * 1: [0 0 0 1 1]
     * 2: [0 0 0 0 0]
     * 3: [0 0 0 0 0]
     * 4: [0 0 0 0 0]
     *
     * Достижимые вершины:
     * из 0: {1, 2, 3, 4}
     * из 1: {3, 4}
     * из 2: {}
     * из 3: {}
     * из 4: {}
     *
     * Граф транзитивного замыкания (рёбра):
     * 0→1, 0→2, 0→3, 0→4, 1→3, 1→4
     * Новые рёбра по сравнению с исходным: 0→3, 0→4 (появились из-за пути через 1)
     */

    private static void printMatrix(int[][] m) {
        for (int i = 0; i < m.length; i++) {
            System.out.print(i + " [");
            for (int j = 0; j < m.length; j++) {
                System.out.print(m[i][j]);
                if (j + 1 < m.length) System.out.print(" ");
            }
            System.out.println("]");
        }
    }

    private static int[][] warshallWithSteps(int[][] adj) {
        int n = adj.length;

        int[][] r = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(adj[i], 0, r[i], 0, n);
        }

        System.out.println("=== Исходная матрица смежности ===");
        printMatrix(r);

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (r[i][k] == 1 && r[k][j] == 1) r[i][j] = 1;
                }
            }
            System.out.println("\n=== После итерации k = " + k + " ===");
            printMatrix(r);
        }

        return r;
    }

    private static void printReachableSets(int[][] r) {
        System.out.println("\n=== Анализ достижимости ===");
        for (int i = 0; i < r.length; i++) {
            List<Integer> reachable = new ArrayList<>();
            for (int j = 0; j < r.length; j++) {
                if (r[i][j] == 1) reachable.add(j);
            }
            System.out.println("Из вершины " + i + " достижимы: " + reachable);
        }
    }

    public static void main(String[] args) {
        int[][] adj = {
                {0, 1, 1, 0, 0},
                {0, 0, 0, 1, 1},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        System.out.println("=== Исходный граф === Рёбра: 0→1, 0→2, 1→3, 1→4");

        int[][] closure = warshallWithSteps(adj);

        System.out.println("\n=== Результат === Матрица достижимости (транзитивное замыкание):");
        printMatrix(closure);

        printReachableSets(closure);
    }
}