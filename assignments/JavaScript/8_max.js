let a = Number(prompt("Введите первое число:"));
let b = Number(prompt("Введите второе число:"));
if (a > b) {
    console.log("Максимальное число:", a);
} else if (b > a) {
    console.log("Максимальное число:", b);
} else {
    console.log("Числа равны");
}