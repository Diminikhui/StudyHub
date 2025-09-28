import random

count_poka = 0

while True:
    user_input = input("ЧЕГО СКАЗАТЬ-ТО ХОТЕЛ, МИЛОК?!> ")

    if user_input == "ПОКА!":
        count_poka += 1
        if count_poka == 3:
            print("ДО СВИДАНИЯ, МИЛЫЙ!")
            break
        else:
            print(f"НЕТ, НИ РАЗУ С {random.randint(1930, 1950)} ГОДА!")
    else:
        count_poka = 0
        if user_input.isupper():
            print(f"НЕТ, НИ РАЗУ С {random.randint(1930, 1950)} ГОДА!")
        else:
            print("АСЬ?! ГОВОРИ ГРОМЧЕ, ВНУЧЕК!")