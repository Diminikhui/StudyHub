const thermostat = {
    _celsius: 0,
    get fahrenheit() {
        return this._celsius * 1.8 + 32;
    },
    set fahrenheit(f) {
        this._celsius = (f - 32) / 1.8;
    }
};