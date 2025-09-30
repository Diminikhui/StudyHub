let weight = Number(prompt("Введите массу тела в кг:"));
let height = Number(prompt("Введите рост в метрах:"));
let yd = weight / (height ** 2);
console.log("Удельный вес :", yd);