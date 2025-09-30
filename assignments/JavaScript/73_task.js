const arr9 = [1, 2, 3, 4];
const arr10 = [4, 3, 2, 1];
const isEqual = arr9.length === arr10.length &&
    arr9.every(item => arr10.includes(item));