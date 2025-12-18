//Условие задачи:
//Вам нужно забраться на верх лестницы, состоящей из n ступеней. Каждый раз вы можете
//подняться либо на 1 ступень, либо на 2 ступени. Сколько существует различных способов
//забраться на верх лестницы?

public class Stairs {
    public static long ways(int n) {
        if (n == 0) return 1;
        if (n == 1) return 1;

        long[] dp = new long[n + 1];
        dp[0] = 1;
        dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n];
    }

    public static void main(String[] args) {
        for (int n = 0; n <= 10; n++) {
            System.out.println("n = " + n + " → " + ways(n));
        }
    }
}
