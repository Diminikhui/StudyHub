class IdGenerator {
    static lastId = 0;
    static generate() {
        return ++this.lastId;
    }
}