class Person {
    constructor(name, age) {
        this.name = name;
        this.age = age;
    }
}
class Student extends Person {
    constructor(name, age, university) {
        super(name, age);
        this.university = university;
    }
}