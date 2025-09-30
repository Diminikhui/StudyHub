function doubleValues(arr) {
    arr.forEach((val, index, array) => array[index] = val * 2);
    return arr;
}