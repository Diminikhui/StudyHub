function Calculator(a, b) {
    this.sum = a + b;
    this.diff = a - b;
    this.mul = a * b;
    this.div = b !== 0 ? a / b : null;
}