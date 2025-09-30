async function checkRandom() {
    const number = Math.floor(Math.random() * 100) + 1;
    if (number < 50) throw new Error(`Случайное число меньше 50: ${number}`);
    return number;
}