from module import *

load_words()
word_desc = get_random_word_and_desc()
word = word_desc[0].lower()
description = word_desc[1]

lives = get_lives()
symbol = get_symbol()
displayed = make_word_displayed(symbol, len(word))
flag = True

while is_alive(lives) and symbol in displayed:
    print("\nОбъяснение:", description)
    show_hangman(lives)
    display_word(displayed)
    answer = get_user_answer("\nВведи букву или все слово: ")

    if answer == word:
        flag = False
        break

    if len(answer) == 1 and check_letter_in_word(answer, word):
        displayed = replace_letters_in_displayed(answer, displayed, word)
    else:
        lives = take_life_away(lives)

if flag:
    show_hangman(lives)
    display_word(displayed)
    check_win_or_lose(word, displayed)
else:
    print("\nТы ВЫИГРАЛ!!!")
