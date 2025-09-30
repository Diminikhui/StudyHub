function validateLength(text, maxLength) {
    if (text.length > maxLength) {
        throw new Error(`Длина строки ${text.length}, максимально допустимая — ${maxLength}`);
    }
    return true;
}