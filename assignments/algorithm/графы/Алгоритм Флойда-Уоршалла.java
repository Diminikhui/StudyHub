import java.util.Arrays;

public class FloydWarshallGraph {
    static final int INF = 1_000_000_000;

    public static void main(String[] args) {
        int n = 7;
        int[][] dist = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }

        addEdge(dist, 6, 3, 5);
        addEdge(dist, 3, 1, 8);
        addEdge(dist, 1, 2, 2);
        addEdge(dist, 1, 5, 4);
        addEdge(dist, 2, 4, 7);
        addEdge(dist, 4, 7, 3);

        for (int k = 1; k <= n; k++) {
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        printMatrix(dist, n);
    }

    private static void addEdge(int[][] dist, int u, int v, int w) {
        dist[u][v] = w;
        dist[v][u] = w;
    }

    private static void printMatrix(int[][] dist, int n) {
        System.out.println("Матрица кратчайших расстояний (Floyd–Warshall):\n");

        System.out.print("     ");
        for (int j = 1; j <= n; j++) System.out.printf("%5d", j);
        System.out.println();
        System.out.println("---------------------------------------------------");

        for (int i = 1; i <= n; i++) {
            System.out.printf("%3d |", i);
            for (int j = 1; j <= n; j++) {
                if (dist[i][j] >= INF)
                    System.out.printf("%5s", "∞");
                else
                    System.out.printf("%5d", dist[i][j]);
            }
            System.out.println();
        }
    }
}
