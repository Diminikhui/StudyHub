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