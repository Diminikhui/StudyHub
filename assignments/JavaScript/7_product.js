class Product {
    constructor(name, price) {
        this.name = name;
        this.price = price;
    }
    static createExpensive(name) {
        return new Product(name, 1000);
    }
}