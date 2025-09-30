const account = {
    _balance: 0,
    get balance() {
        return this._balance;
    },
    set balance(value) {
        if (value < 0) {
            throw new Error("Баланс не может быть отрицательным");
        }
        this._balance = value;
    }
};