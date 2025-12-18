import java.util.ArrayList;
import java.util.List;

public class WarshallDemo {

    /*
     * Алгоритм Уоршалла строит матрицу достижимости R для ориентированного графа.
     * В R[i][j] = 1, если из вершины i можно добраться до вершины j по пути длины ≥ 1.
     *
     * Исходные рёбра: 0→1, 0→2, 1→3, 1→4
     * Вершины: 0..4
     *
     * Матрица смежности A:
     * 0: [0 1 1 0 0]
     * 1: [0 0 0 1 1]
     * 2: [0 0 0 0 0]
     * 3: [0 0 0 0 0]
     * 4: [0 0 0 0 0]
     *
     * Формула обновления:
     * R[i][j] = R[i][j] OR (R[i][k] AND R[k][j])
     *
     * Ключевые изменения по итерациям:
     * k=0: новых достижений нет
     * k=1: появляются 0→3 и 0→4 (путь через 1)
     * k=2..4: без изменений
     *
     * Итоговая матрица достижимости:
     * 0: [0 1 1 1 1]
     * 1: [0 0 0 1 1]
     * 2: [0 0 0 0 0]
     * 3: [0 0 0 0 0]
     * 4: [0 0 0 0 0]
     *
     * Достижимые вершины:
     * из 0: [1, 2, 3, 4]
     * из 1: [3, 4]
     * из 2: []
     * из 3: []
     * из 4: []
     *
     * Транзитивное замыкание добавляет рёбра 0→3 и 0→4, потому что существуют пути 0→1→3 и 0→1→4.
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

    private static int[][] warshall(int[][] adj) {
        int n = adj.length;

        int[][] r = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(adj[i], 0, r[i], 0, n);
        }

        System.out.println("Матрица смежности:");
        printMatrix(r);

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (r[i][k] == 0) continue;
                for (int j = 0; j < n; j++) {
                    if (r[k][j] == 1) r[i][j] = 1;
                }
            }

            System.out.println("\nПосле k = " + k + ":");
            printMatrix(r);
        }

        return r;
    }

    private static void printReachable(int[][] r) {
        System.out.println("\nДостижимые вершины:");
        for (int i = 0; i < r.length; i++) {
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < r.length; j++) {
                if (r[i][j] == 1) list.add(j);
            }
            System.out.println("Из " + i + ": " + list);
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

        System.out.println("Исходный граф: рёбра 0→1, 0→2, 1→3, 1→4\n");

        int[][] closure = warshall(adj);

        System.out.println("\nИтоговая матрица достижимости:");
        printMatrix(closure);

        printReachable(closure);
    }
}