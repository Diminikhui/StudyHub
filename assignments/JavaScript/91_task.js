function Person(name, age) {
    this.name = name;
    this.age = age;
    this.getInfo = function () {
        return `Имя: ${this.name}, возраст: ${this.age}`;
    };
}