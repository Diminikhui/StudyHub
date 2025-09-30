function safeDivide(a, b) {
    if (b === 0) {
        return 'Ошибка: деление на ноль';
    }
    return a / b;
}