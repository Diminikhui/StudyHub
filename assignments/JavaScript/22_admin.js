let correctLogin = "admin";
let correctPassword = "12345";
let login = prompt("Введите логин:");
let password = prompt("Введите пароль:");
if (login === correctLogin && password === correctPassword) {
    alert("Доступ разрешён");
} else if (login !== correctLogin && password !== correctPassword) {
    alert("Неверный логин и пароль");
} else if (login !== correctLogin) {
    alert("Неверный логин");
} else {
    alert("Неверный пароль");
}