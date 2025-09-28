#ЗАДАНИЕ 1
def get_books(filename):
    with open(filename, encoding='utf-8') as f:
        lines = f.read().strip().split('\n')

    def parse_line(line):
        fields = line.split('|')
        return [
            fields[0],           # isbn
            fields[1],           # title
            fields[2],           # author
            int(fields[3]),      # quantity
            float(fields[4])     # price
        ]

    return list(map(parse_line, lines[1:]))

# ЗАДАНИЕ 2
def filtered_books(books, keyword):
    keyword_lower = keyword.lower()

    def contains_keyword(book):
        return keyword_lower in book[1].lower()

    def transform(book):
        return [
            book[0],                       # isbn
            f"{book[1]}, {book[2]}",      # title и author вместе
            book[3],                      # quantity
            book[4]                       # price
        ]

    filtered = filter(contains_keyword, books)
    return list(map(transform, filtered))

# Задание 3
def books_total_price(filtered_books_list):
    def to_tuple(book):
        return (book[0], book[2] * book[3])
    return list(map(to_tuple, filtered_books_list))
