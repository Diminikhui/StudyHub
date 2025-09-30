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