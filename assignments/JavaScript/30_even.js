let number6;
while (true) {
    let input6 = prompt("Введите число:");
    number6 = Number(input6);
    if (number6 % 2 === 0) {
        alert("Вы ввели чётное число: ");
        break;
    }
}