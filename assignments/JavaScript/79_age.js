const people = [
    { name: 'Иван', age: 17 },
    { name: 'Пётр', age: 20 },
    { name: 'Анна', age: 25 }
];
const found = people.find(person => person.age > 18);