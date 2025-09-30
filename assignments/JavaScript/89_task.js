function validateEmailFormat(email) {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!pattern.test(email)) {
        throw new Error(`Невалидный email: ${email}`);
    }
    return true;
}