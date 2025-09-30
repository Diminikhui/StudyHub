class MathUtils {
    static sum(a, b) {
        return a + b;
    }
    static factorial(n) {
        if (n === 0) return 1;
        return n * MathUtils.factorial(n - 1);
    }
    static pi = 3.14;
}