class StringUtils {
    static VOWELS = "aeiouy";
    static reverse(str) {
        return str.split('').reverse().join('');
    }
    static countSymbols(str, symbol) {
        return str.split('').filter(ch => ch === symbol).length;
    }
}