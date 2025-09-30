function average(arr) {
    try {
        if (!Array.isArray(arr)) throw new TypeError("Ожидается массив");
        if (arr.length === 0) throw new Error("Массив пуст");
        return arr.reduce((sum, n) => sum + n, 0) / arr.length;
    } catch (err) {
        return err.message;
    }
}