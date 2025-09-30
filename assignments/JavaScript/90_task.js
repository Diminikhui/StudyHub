class Person {
    constructor(name, age) {
        this.name = name;
        this.age = age;
    }
    getInfo() {
        return `Имя: ${this.name}, возраст: ${this.age}`;
    }
}