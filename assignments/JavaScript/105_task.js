class Cache {
    static storage = new Map();
    static set(key, value) {
        this.storage.set(key, value);
    }
    static get(key) {
        return this.storage.get(key);
    }
}
class UserCache extends Cache {
    static getUser(id) {
        return this.get(id);
    }
}