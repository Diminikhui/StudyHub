import pandas as pd
import numpy as np

authors = pd.DataFrame({
    "author_id": [1, 2, 3],
    "author_name": ['Тургенев', 'Чехов', 'Островский']
})

book = pd.DataFrame({
    "author_id": [1, 1, 1, 2, 2, 3, 3],
    "book_title": ['Отцы и дети', 'Рудин', 'Дворянское гнездо', 'Толстый и тонкий', 'Дама с собачкой', 'Гроза', 'Таланты и поклонники'],
    "price": [450, 300, 350, 500, 450, 370, 290]
})

authors_price = pd.merge(authors, book, on='author_id')
top5 = authors_price.nlargest(5, 'price')
print("Top5 книг по цене:\n", top5)
authors_stat = authors_price.groupby('author_name').agg(min_price=('price', 'min'), max_price=('price', 'max'), mean_price=('price', 'mean')).reset_index()
print("\nСтатистика авторов:\n", authors_stat)
authors_price['cover'] = ['твердая', 'мягкая', 'мягкая', 'твердая', 'твердая', 'мягкая', 'мягкая']
book_info = pd.pivot_table(authors_price, values='price', index='author_name', columns='cover', aggfunc=np.sum, fill_value=0)
book_info.to_pickle("book_info.pkl")
print("\nСводная таблица по авторам и обложкам:\n", book_info)
