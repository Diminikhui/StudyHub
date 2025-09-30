async function getRandom() {
    const number = Math.floor(Math.random() * 100) + 1;
    if (number < 50) throw new Error(`Число слишком маленькое: ${number}`);
    return number;
}