function formatDate(dateStr, formatType) {
    const date = new Date(dateStr);
    const options = {
        short: { day: '2-digit', month: '2-digit', year: 'numeric' },
        long: { day: 'numeric', month: 'long', year: 'numeric' },
        full: { day: 'numeric', month: 'long', year: 'numeric', weekday: 'long' }
    };
    return date.toLocaleDateString('ru-RU', options[formatType]);
}