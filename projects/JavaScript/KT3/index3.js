//    ██
//   ███
//  ████
//    ██
//    ██
//    ██
//    ██
//    ██
//    ██
//  ██████
function checkElem(num) {
    if (num % 7 === 0) {
        console.log(true);
    } else {
        console.log(false);
    }
}


checkElem(13); // Выведет: false
checkElem(14); // Выведет: true
//  █████
//      ██
//      ██
//     ██
//    ██
//   ██
//  ██
//  ███████
function changeElem(array, n) {
    let newArray = array.map(function(element) {
        return element * n;
    });
    return newArray;
}


let array = [1, 2, 3, 4];
let result = changeElem(array, 3);
console.log(result);
//  █████
//      ██
//      ██
//    ███
//      ██
//      ██
//     ██
//  █████
function sumElems(array) {
    let sum = 0;

    for (let i = 0; i < array.length; i++) {
        let num = Number(array[i]);

        if (!isNaN(num)) {
            sum += num;
        }
    }

    return sum;
}


let array = ['10', 'Строка', '5g', '15', '05'];
let result = sumElems(array);
console.log(result);
//     ██
//    ███
//   █ ██
//  ██ ██
//  ██████
//      ██
//      ██
function reverseIndex(array) {
    let newArray = [];

    for (let i = array.length - 1; i >= 0; i--) {
        newArray.push(array[i]);
    }

    console.log(newArray);
}

let array = [1, 2, 3, 4, 5];
reverseIndex(array);

//  ██████
//  ██
//  ██
//  █████
//      ██
//      ██
//      ██
//  █████
function checkElem(array, callback) {
    for (let i = 0; i < array.length; i++) {
        if (callback(array[i])) {
            return true;
        }
    }
    return false;
}

let array = [1, 2, 3, 4];
let result = checkElem(array, (elem) => elem == 3);

console.log(result);
