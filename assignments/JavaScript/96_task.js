function User(name, age) {
    if (!name || typeof name !== 'string') {
        throw new Error("Некорректное имя");
    }
    if (typeof age !== 'number' || age <= 0) {
        throw new Error("Некорректный возраст");
    }
    this.name = name;
    this.age = age;
}