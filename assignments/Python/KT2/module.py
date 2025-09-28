import random

map_hang_files = {
    3: '0.txt',
    2: '1.txt',
    1: '2.txt',
    0: '3.txt',
}

words_list = []

def load_words(filename='words.txt'):
    global words_list
    with open(filename) as f:
        words_list = [line.strip().split(' - ') for line in f]

def get_random_word_and_desc():
    return random.choice(words_list)

def get_lives():
    return 3

def is_alive(lives):
    return lives > 0

def show_hangman(lives):
    filename = map_hang_files.get(lives, '0.txt')
    with open(filename, encoding='utf-8') as f:
        print(f.read())

def get_user_answer(prompt):
    return input(prompt).strip().lower()

def check_letter_in_word(letter, word):
    return letter in word

def take_life_away(lives):
    return lives - 1

def make_word_displayed(symbol, length):
    return [symbol] * length

def replace_letters_in_displayed(answer, displayed, word):
    for i, ch in enumerate(word):
        if ch == answer:
            displayed[i] = answer
    return displayed

def display_word(displayed):
    print(' '.join(displayed))

def has_won(displayed, symbol):
    return symbol not in displayed

def check_win_or_lose(word, displayed):
    if has_won(displayed, '■'):
        print("\nМои поздравления!!! Ты угадал сдово!!)))")
    else:
        print("\nТЫ ПРОИГРАЛ((((!")

def get_symbol():
    return '■'
