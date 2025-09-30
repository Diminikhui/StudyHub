let input7 = prompt("Введите число для таблицы умножения:");
let number7 = Number(input7);
if (isNaN(number7)) {
    alert("Пожалуйста, введите корректное число.");
} else {
    let result = "Таблица умножения для " + number7 + ":\n";
    for (let i = 1; i <= 10; i++) {
        result += number7 + " × " + i + " = " + (number7 * i) + "\n";
    }
    alert(result);
}