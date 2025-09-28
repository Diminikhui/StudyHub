import books_module

# Вспомогательные функции для вывода

def print_list_of_lists(lst):
    if not lst:
        return
    print(lst[0])
    print_list_of_lists(lst[1:])

def print_list_of_tuples(lst):
    if not lst:
        return
    isbn, total = lst[0]
    print(f"{isbn}: {total:.2f}")
    print_list_of_tuples(lst[1:])

def main():
    filename = "books.csv"

    # Задание 1
    books = books_module.get_books(filename)
    print("Все книги:")
    print_list_of_lists(books)
    print()

    # Задание 2
    keyword = input("Введите ключевое слово для фильтрации по названию книги: ").strip()
    filtered = books_module.filtered_books(books, keyword)
    print(f"\nКниги с '{keyword}' в названии:")
    print_list_of_lists(filtered)
    print()

    # Задание 3
    answer = input("Показать итоговые суммы (quantity * price)? Введите 'да' или 'нет': ").strip().lower()
    if answer == 'да':
        totals = books_module.books_total_price(filtered)
        print("ISBN и общая стоимость (quantity * price):")
        print_list_of_tuples(totals)
    else:
        print("Итоговые суммы не показаны.")

if __name__ == "__main__":
    main()
