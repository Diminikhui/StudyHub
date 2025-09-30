const square = {
    _side: 1,
    get side() {
        return this._side;
    },
    set side(value) {
        this._side = value;
    },
    get area() {
        return this._side ** 2;
    },
    set area(value) {
        this._side = Math.sqrt(value);
    }
};