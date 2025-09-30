function sumElems(array) {
    return array.reduce((sum, elem) => {
        let num = Number(elem);
        return isNaN(num) ? sum : sum + num;
    }, 0);
}