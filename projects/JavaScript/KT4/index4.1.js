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

let sum = 0;
for (let key in obj) {
 sum = sum + obj[key];
}
console.log(sum);

//  █████
//      ██
//      ██
//     ██
//    ██
//   ██
//  ██
//  ███████
let array = [
 {id: 1, name: 'apple'},
 {id: 2, name: 'watermelon'},
 {id: 3, name: 'qiwi'},
 {id: 4, name: 'lemon'}
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
logString("Hello", "my", "world!")
function logString(...args) {
 console.log(args.join(" ") + " ");
}

//     ██
//    ███
//   █ ██
//  ██ ██
//  ██████
//      ██
//      ██
console.log(checkObj({id: 1, particle: 10}))
console.log(checkObj({id: 2, name: "tag"}))
function checkObj(obj) {
 return obj.hasOwnProperty("particle");
}

//  ██████
//  ██
//  ██
//  █████
//      ██
//      ██
//      ██
//  █████
let array5 = [[1], {id: 40}, [100], [300], {part: 10}]
console.log(generateArray(array5))

function generateArray(array) {
 for (let i = 0; i < array.length; i++) {
  if (typeof array[i] === 'object' && !Array.isArray(array[i])) {
   array[i] = Object.values(array[i]);
  }
 }
 return array;
}

//    ███
//   ██
//  ██
//  █████
//  ██  ██
//  ██  ██
//  ██  ██
//   ████
let users = [
 {id: 1, name: 'Alex', lastname: 'Wilyam', age: 20},
 {id: 2, name: 'Steven', lastname: 'King', age: 34}
];

function addUser(name, lastname, age) {
 let newId = users.length > 0 ? users[users.length - 1].id + 1 : 1;
 let newUser = {
  id: newId,
  name: name,
  lastname: lastname,
  age: age
 };
 users.push(newUser);
}

function updateUser(id, name, lastname, age) {
 for (let user of users) {
  if (user.id === id) {
   user.name = name;
   user.lastname = lastname;
   user.age = age;
   return true;
  }
 }
 return false;
}

function deleteUser(id) {
 for (let i = 0; i < users.length; i++) {
  if (users[i].id === id) {
   users.splice(i, 1);
   return true;
  }
 }
 return false;
}

console.log('Изначальный массив пользователей:', users);
addUser('John', 'Doe', 28);
console.log('После добавления John Doe:', users);
updateUser(2, 'Steven', 'Johnson', 35);
console.log('После обновления пользователя с id=2:', users);
deleteUser(1);
console.log('После удаления пользователя с id=1:', users);

//  ███████
//      ██
//     ██
//    ██
//   ██
//  ██
//  ██
const products = [
 { id: 1, title: 'велосипед', price: 45000, count: 3, marks: [4, 3, 5, 3] },
 { id: 2, title: 'ролики', price: 25000, count: 5, marks: [4, 3, 5, 5] },
 { id: 3, title: 'арбалет', price: 1700, count: 9, marks: [3, 3, 4, 5] },
 { id: 4, title: 'коньки', price: 4500, count: 3, marks: [4, 3, 4, 3] },
 { id: 5, title: 'ракетки', price: 900, count: 15, marks: [5, 3, 5, 3] },
 { id: 6, title: 'штанги', price: 14000, count: 5, marks: [3, 3, 3, 2] },
 { id: 7, title: 'стрелы', price: 1200, count: 55, marks: [3, 2, 4, 5] },
 { id: 8, title: 'мячи', price: 500, count: 49, marks: [5, 4, 4, 4] },
 { id: 9, title: 'сетка', price: 5000, count: 6, marks: [4, 5, 2, 5] },
 { id: 10, title: 'гантели', price: 3400, count: 12, marks: [3, 2, 4, 2] },
 { id: 11, title: 'маты', price: 16500, count: 7, marks: [4, 4, 4, 5] }
];

const task_1 = products.filter(p => p.count > 10);
console.log("1:", task_1);

const task_2 = products.find(p => p.price >= 800 && p.price <= 900);
console.log("2:", task_2);

const task_3 = [...products].sort((a, b) => b.price - a.price);
console.log("3:", task_3);

const task_4 = products.reduce((sum, p) => sum + p.price * p.count, 0);
console.log("4:", task_4);

const task_5 = products.map(p => ({
 ...p,
 marks_total: p.marks.reduce((a, b) => a + b, 0)
})).sort((a, b) => b.marks_total - a.marks_total);
console.log("5:", task_5);

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
  const invalidChars = /[*#$%^]/;
  const [_, __, domainZone] = this.email.split(/[@.]/);
  return !invalidChars.test(this.email) && domainZone.length <= 3;
 }

 set setEmail(arr) {
  this.email = `${arr[0]}@${arr[1]}.${arr[2]}`;
 }
}

const e1 = new Email("test@gmail.com");
console.log(e1.isValid);

const e2 = new Email("bad*email@yandex.ru");
console.log(e2.isValid);

e2.setEmail = ["newEmail", "mail", "com"];
console.log(e2.email);

//   ████
//  ██  ██
//  ██  ██
//  ██  ██
//   █████
//      ██
//     ██
//   ███
class Contact extends Email {
 constructor(email, phone) {
  super(email);
  this.phone = phone;
 }

 get phoneType() {
  if (this.phone.includes('+')) return "Неизвестный";
  if (this.phone.length === 12) return "Мобильный";
  if (this.phone.length === 18) return "Городской";
  return "Неизвестный";
 }
}

const c1 = new Contact("test@mail.com", "123456789012");
console.log(c1.phoneType);

const c2 = new Contact("test2@mail.com", "123456789012345678");
console.log(c2.phoneType);

const c3 = new Contact("test3@mail.com", "+1234567890");
console.log(c3.phoneType);

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

const animal = new Animal();
animal.makeSound();

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

const student = new Student("Иван", 20, "МГУ");
console.log(student);

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

const vehicle = new Vehicle();
console.log(vehicle.drive());

const car = new Car();
console.log(car.drive());

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

const square = new Square(6);
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

const device = new Device();
console.log(device.powerOn());

const computer = new Computer();
console.log(computer.powerOn());

const laptop = new Laptop();
console.log(laptop.powerOn());