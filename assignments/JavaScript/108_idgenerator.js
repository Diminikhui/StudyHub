function Animal() {}
Animal.prototype.makeSound = function () {
    console.log("Some generic sound");
};
function Dog() {}
Dog.prototype = Object.create(Animal.prototype);
Dog.prototype.constructor = Dog;
Dog.prototype.makeSound = function () {
    console.log("Woof!");
};