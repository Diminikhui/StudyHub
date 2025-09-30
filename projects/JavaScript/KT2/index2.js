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
let word = 'Арнольд';

let result = '';

for (let i = 0; i < word.length; i++) {
    let char = word[i];
    if (char.toLowerCase() !== 'а' && char.toLowerCase() !== 'о') {
        result += char;
    }
}
console.log(result);

//  █████
//      ██
//      ██
//     ██
//    ██
//   ██
//  ██
//  ███████
let num = 20;

for (let i = 1; i <= num; i++) {
    if (i % 3 === 0) {
        console.log(i);
    }
}
//  █████
//      ██
//      ██
//    ███
//      ██
//      ██
//     ██
//  █████
let num = 5;
let result = "";

for (let i = 1; i <= num; i++) {
    result += i;
    console.log(result);
}
//     ██
//    ███
//   █ ██
//  ██ ██
//  ██████
//      ██
//      ██
let num = 4;
let sum = 0;
let factorial = 1;

for (let i = 1; i <= num; i++) {
    factorial *= i;
    sum += factorial;
}
console.log(sum);

//  ██████
//  ██
//  ██
//  █████
//      ██
//      ██
//      ██
//  █████
let word = 'потоп';

let reversed = '';

for (let i = word.length - 1; i >= 0; i--) {
    reversed += word[i];
}

if (word === reversed) {
    console.log("YES");
} else {
    console.log("NO");
}
