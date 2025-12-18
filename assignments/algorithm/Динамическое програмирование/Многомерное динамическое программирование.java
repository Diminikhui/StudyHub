public class KingPaths {

    public static long countPaths(int n, int m) {
        long[][] dp = new long[n][m];

        dp[0][0] = 1;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (i == 0 && j == 0) continue;

                long fromLeft = (j > 0) ? dp[i][j - 1] : 0;
                long fromTop = (i > 0) ? dp[i - 1][j] : 0;
                long fromDiag = (i > 0 && j > 0) ? dp[i - 1][j - 1] : 0;

                dp[i][j] = fromLeft + fromTop + fromDiag;
            }
        }

        return dp[n - 1][m - 1];
    }

    public static void main(String[] args) {
        int n = 5;
        int m = 6;

        System.out.println("Количество маршрутов: " + countPaths(n, m));
    }
}
