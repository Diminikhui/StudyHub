const str = '23 45 12 67';
const sortedStr = str.split(' ').map(Number).sort((a, b) => a - b).join(',');