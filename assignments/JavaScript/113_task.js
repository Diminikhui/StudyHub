const config = {
    _apiKey: '',
    get apiKey() {
        return '*' + this._apiKey.slice(-4);
    },
    set apiKey(value) {
        if (this._apiKey) {
            throw new Error("Нельзя изменить ключ после установки");
        }
        this._apiKey = value;
    }
};