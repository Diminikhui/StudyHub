function calculateAverage(arr) {
    try {
        if (!Array.isArray(arr)) throw new TypeError('Ожидается массив');
        if (arr.length === 0) throw new Error('Массив пуст');
        let sum = arr.reduce((a, b) => a + b, 0);
        return sum / arr.length;
    } catch (error) {
        return error.message;
    }
}