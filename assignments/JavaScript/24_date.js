function getWeekDay(dateStr) {
    const days = ['воскресенье', 'понедельник', 'вторник', 'среда', 'четверг', 'пятница', 'суббота'];
    const date = new Date(dateStr);
    return days[date.getDay()];
}