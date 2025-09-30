function validateStringLength(str, maxLength) {
    if (str.length > maxLength) {
        throw new Error(`Длина строки ${str.length}, максимум — ${maxLength}`);
    }
    return true;
}