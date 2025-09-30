function SafeConstructor() {
    if (!new.target) {
        throw new Error("Конструктор должен быть вызван с new");
    }
    this.message = "Всё ок";
}