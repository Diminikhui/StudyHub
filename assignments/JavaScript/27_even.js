function countdown(targetDateStr) {
    const now = new Date();
    const target = new Date(targetDateStr);
    const diffMs = target - now;
    if (diffMs <= 0) return "Дата в прошлом";
    const minutes = Math.floor(diffMs / 60000) % 60;
    const hours = Math.floor(diffMs / 3600000) % 24;
    const days = Math.floor(diffMs / 86400000);
    return `Осталось: ${days} дн., ${hours} ч., ${minutes} мин.`;
}