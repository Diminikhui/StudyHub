function checkIfNumber(value) {
    if (isNaN(value)) {
        throw new Error("Введено не число");
    }
    return Number(value);
}