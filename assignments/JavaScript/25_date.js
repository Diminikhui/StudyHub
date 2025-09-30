function isValidDate(dateStr) {
    const date = new Date(dateStr);
    return !isNaN(date.getTime());
}