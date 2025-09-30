const user2 = {
    _email: '',
    get email() {
        return this._email.toLowerCase();
    },
    set email(value) {
        if (!value.includes('@')) {
            throw new Error("Невалидный email");
        }
        this._email = value;
    }
};