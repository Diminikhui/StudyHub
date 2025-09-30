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
let phones = [
    '590.423.4568',
    '650.124.7234',
    '650.507.9879',
    '011.44.1343.529268',
    '011.44.1344.478968',
    '011.44.1644.429267',
    '11.44.1343.52',
    '11.44.1643.52'
];

let maskPhones = phones.map(phone => {
    let lastDotIndex = phone.lastIndexOf('.');
    return phone.slice(0, lastDotIndex + 2) + '****';
});

console.log(maskPhones);

//  █████
//      ██
//      ██
//     ██
//    ██
//   ██
//  ██
//  ███████

let cards = [
    '4000 0012 0056 9499',
    '4000 0013 5456 7379',
    '4000 0014 1456 9869',
    '4000 0015 3466 7859',
    '4000 0016 3556 6899',
    '4000 0017 4456 4699'
];

let maskedCards = cards.map(card => {
    let digitsOnly = card.replace(/\s+/g, '');
    let first4 = digitsOnly.slice(0, 4);
    let last4 = digitsOnly.slice(-4);
    return first4 + '*****' + last4;
});

console.log(maskedCards);
//  █████
//      ██
//      ██
//    ███
//      ██
//      ██
//     ██
//  █████
function getInfo(prices) {
    let startWithPrice = 0;
    let endWithDollar = 0;

    prices.forEach(item => {
        if (item.startsWith('Цена')) {
            startWithPrice++;
        }
        if (item.endsWith('$')) {
            endWithDollar++;
        }
    });

    return [startWithPrice, endWithDollar];
}

let prices = [
    'Цена товара - 1200$',
    'Стоимость - 500$',
    'Цена не определена',
    '9999',
    'Ценовая категория - больше 300$',
    'Цена за услугу 500 EUR',
    '150$',
];

console.log(getInfo(prices));

//     ██
//    ███
//   █ ██
//  ██ ██
//  ██████
//      ██
//      ██

function kingSayd(string) {
    const prefix = 'Король сказал:';

    if (string.startsWith(prefix)) {
        console.log(string);
    } else {
        console.log(`${prefix} ${string}`);
    }
}


kingSayd("сегодня хорошая погода");
kingSayd("Король сказал: будет дождь");

//  ██████
//  ██
//  ██
//  █████
//      ██
//      ██
//      ██
//  █████
function iIstFridayToday() {
    const today = new Date().getDay();

    if (today === 5) {
        console.log("Сегодня пятница!");
    } else if (today === 6) {
        console.log("Пятница была вчера");
    } else if (today === 4) {
        console.log("Завтра пятница!");
    } else {
        let daysUntilFriday = (5 - today + 7) % 7;
        let suffix;

        if (daysUntilFriday === 1) {
            suffix = 'день';
        } else if (daysUntilFriday >= 2 && daysUntilFriday <= 4) {
            suffix = 'дня';
        } else {
            suffix = 'дней';
        }

        console.log(`Пятница будет через ${daysUntilFriday} ${suffix}`);
    }
}


iIstFridayToday();




