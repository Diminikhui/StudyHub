//     ██
//    ███
//   ████
//     ██
//     ██
//     ██
//     ██
//     ██
//     ██
//   ██████
let obj = {
    price1: 100,
    price2: 150,
    price3: 200,
    price4: 100,
    price5: 150,
};

let values = Object.values(obj);

let total = values.reduce((sum, val) => sum + val, 0);

console.log(total);

//  █████
//      ██
//      ██
//     ██
//    ██
//   ██
//  ██
//  ███████

let array = [
    { id: 1, name: 'apple' },
    { id: 2, name: 'watermelon' },
    { id: 3, name: 'qiwi' },
    { id: 4, name: 'lemon' }
];

let result = array.map(obj => Object.values(obj));

console.log(result);

//  █████
//      ██
//      ██
//    ███
//      ██
//      ██
//     ██
//  █████

function logString(...args) {

    let result = args.join(' ');


    console.log(result + ' ');
}

logString("Hello", "my", "world!");

//     ██
//    ███
//   █ ██
//  ██ ██
//  ██████
//      ██
//      ██
xs
 function checkObj(obj) {

     return 'particle' in obj;
 }

 console.log(checkObj({ id: 1, particle: 10 }));     // true
 console.log(checkObj({ id: 2, name: "tag" }));       // false


//  ██████
//  ██
//  ██
//  █████
//      ██
//      ██
//      ██
//  █████



function generateArray(array2) {
    for (let i = 0; i < array2.length; i++) {
        if (!Array.isArray(array2[i]) && typeof array2[i] === 'object' && array2[i] !== null) {
            array2[i] = Object.values(array2[i]);
        }
    }
    return array2;
}


let array2 = [[1], { id: 40 }, [100], [300], { part: 10 }];
console.log(generateArray(array2));

//    ███
//   ██
//  ██
//  █████
//  ██  ██
//  ██  ██
//  ██  ██
//   ████


let users = [];


let nextId = 1;


function addUser(firstName, lastName, age) {
    const newUser = {
        id: nextId++,
        firstName: firstName,
        lastName: lastName,
        age: age
    };
    users.push(newUser);
}


function updateUser(id, firstName, lastName, age) {
    const user = users.find(user => user.id === id);
    if (user) {
        user.firstName = firstName;
        user.lastName = lastName;
        user.age = age;
    } else {
        console.log(`Пользователь с id ${id} не найден.`);
    }
}


function deleteUser(id) {
    users = users.filter(user => user.id !== id);
}


addUser("Иван", "Иванов", 25);
addUser("Мария", "Петрова", 30);
console.log(users);

updateUser(1, "Иван", "Смирнов", 26);
console.log(users);

deleteUser(2);
console.log(users);



let users = [
    { id: 1, name: 'Alex', lastname: 'Wilyam', age: 20 },
    { id: 2, name: 'Steven', lastname: 'King', age: 34 }
];


function addUser(name, lastname, age) {
    const newId = users.length > 0 ? users[users.length - 1].id + 1 : 1;
    const newUser = { id: newId, name, lastname, age };
    users.push(newUser);
}


function updateUser(id, name, lastname, age) {
    const user = users.find(user => user.id === id);
    if (user) {
        user.name = name;
        user.lastname = lastname;
        user.age = age;
    } else {
        console.log(`Пользователь с id ${id} не найден.`);
    }
}


function deleteUser(id) {
    const index = users.findIndex(user => user.id === id);
    if (index !== -1) {
        users.splice(index, 1);
    } else {
        console.log(`Пользователь с id ${id} не найден.`);
    }
}


addUser("Maria", "Smith", 28);


updateUser(2, "Steve", "Kingston", 35);


deleteUser(1);


console.log(users);

//  ███████
//      ██
//     ██
//    ██
//   ██
//  ██
//  ██


const products = [
    {
        id: 1,
        title: 'велосипед',
        price: 45000,
        count: 3,
        marks: [5, 5, 5]
    },
    {
        id: 2,
        title: 'самокат',
        price: 850,
        count: 12,
        marks: [4, 3]
    },
    {
        id: 3,
        title: 'ролики',
        price: 1500,
        count: 5,
        marks: [5, 4, 3, 4]
    },
    {
        id: 4,
        title: 'скейтборд',
        price: 1200,
        count: 18,
        marks: [2, 2, 3]
    }
];


const task_1 = products.filter(product => product.count > 10);
console.log('task_1:', task_1);


const task_2 = products.find(product => product.price >= 800 && product.price <= 900);
console.log('task_2:', task_2);


const task_3 = [...products].sort((a, b) => b.price - a.price);
console.log('task_3:', task_3);


const task_4 = products.reduce((total, product) => total + (product.price * product.count), 0);
console.log('task_4:', task_4);


const task_5 = products
    .map(product => ({
        ...product,
        marks_total: product.marks.reduce((sum, mark) => sum + mark, 0)
    }))
    .sort((a, b) => b.marks_total - a.marks_total);

console.log('task_5:', task_5);



//   ████
//  ██  ██
//  ██  ██
//   ████
//  ██  ██
//  ██  ██
//  ██  ██
//   ████
class Email {
    constructor(email) {
        this.email = email;
    }


    get isValid() {
        const [login, domainZone] = this.email.split('@');
        const [domain, zone] = domainZone.split('.');
        const invalidSymbols = ['*', '#', '$', '%', '^'];
        const hasInvalid = invalidSymbols.some(char => login.includes(char));
        return !hasInvalid && zone.length <= 3;
    }


    set setEmail([login, domain, zone]) {
        this.email = `${login}@${domain}.${zone}`;
    }
}


const email1 = new Email('example@gmail.com');
console.log(email1.email)
console.log(email1.isValid);

const email2 = new Email('exam*ple@mail.ru');
console.log(email2.email);
console.log(email2.isValid);

email2.setEmail = ['newUser', 'gmail', 'com'];
console.log(email2.email);
console.log(email2.isValid);

//   ████
//  ██  ██
//  ██  ██
//  ██  ██
//   █████
//      ██
//     ██
//   ███

class Email {
    constructor(email) {
        this._email = email;
    }

    get email() {
        return this._email;
    }

    set email(value) {
        this._email = value;
    }
}


class Contact extends Email {
    constructor(email, phone) {
        super(email);
        this._phone = phone;
    }

    get phone() {
        return this._phone;
    }

    set phone(value) {
        this._phone = value;
    }

    get phoneType() {
        const phone = this._phone;

        if (!phone.includes('+')) {
            return 'Неизвестный';
        }

        const cleanPhone = phone.replace('+', '');

        if (cleanPhone.length === 12) {
            return 'Мобильный';
        } else if (cleanPhone.length === 18) {
            return 'Городской';
        } else {
            return 'Неизвестный';
        }
    }
}


const contact1 = new Contact('ivan@mail.ru', '+790012345678');
console.log(contact1.phoneType);

const contact2 = new Contact('petr@mail.ru', '+781234567890123456');
console.log(contact2.phoneType);

const contact3 = new Contact('anna@mail.ru', '78123456789');
console.log(contact3.phoneType);

//    ██     ████
//   ███    ██  ██
//  ████    ██  ██
//    ██    ██  ██
//    ██    ██  ██
//    ██    ██  ██
//    ██    ██  ██
//    ██     ████
//    ██
//  ██████

class Animal {
    makeSound() {
        console.log("Some generic sound");
    }
}


class Dog extends Animal {
    makeSound() {
        console.log("Woof!");
    }
}


const genericAnimal = new Animal();
genericAnimal.makeSound();

const dog = new Dog();
dog.makeSound();

//    ██     ██
//   ███    ███
//  ████   ████
//    ██     ██
//    ██     ██
//    ██     ██
//    ██     ██
//    ██     ██
//    ██     ██
//  ██████ ██████

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


const student1 = new Student("Иван", 20, "МГУ");
console.log(student1.name);
console.log(student1.age);
console.log(student1.university);

//    ██    █████
//   ███        ██
//  ████        ██
//    ██       ██
//    ██      ██
//    ██     ██
//    ██    ██
//    ██   ███████
//    ██
//  ██████


class Vehicle {
    drive() {
        return "Moving forward";
    }
}


class Car extends Vehicle {
    drive() {
        return super.drive() + " on the road";
    }
}


const myCar = new Car();
console.log(myCar.drive());
//    ██    █████
//   ███        ██
//  ████        ██
//    ██      ███
//    ██        ██
//    ██        ██
//    ██        ██
//    ██     █████
//    ██
//  ██████

class Rectangle {
    constructor(width, height) {
        this.width = width;
        this.height = height;
    }

    area() {
        return this.width * this.height;
    }
}


class Square extends Rectangle {
    constructor(side) {
        super(side, side);
    }
}


const rect = new Rectangle(4, 5);
console.log(rect.area());

const square = new Square(3);
console.log(square.area());

//    ██      ██
//   ███     ███
//  ████    █ ██
//    ██   ██ ██
//    ██  ██████
//    ██      ██
//    ██      ██
//    ██      ██
//    ██
//  ██████

class Device {
    powerOn() {
        return "Device is on";
    }
}


class Computer extends Device {
    powerOn() {
        return super.powerOn() + ", loading OS...";
    }
}


class Laptop extends Computer {
    powerOn() {
        return super.powerOn() + ", battery is charged";
    }
}

const myLaptop = new Laptop();
console.log(myLaptop.powerOn());
