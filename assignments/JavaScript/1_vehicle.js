class Vehicle {
    drive() {
        return "Moving forward";
    }
}
class Car extends Vehicle {
    drive() {
        return super.drive() + " → with car";
    }
}