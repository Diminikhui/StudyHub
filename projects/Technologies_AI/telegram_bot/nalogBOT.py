import telebot
from telebot.types import InlineKeyboardMarkup, InlineKeyboardButton

API_TOKEN = '????????????????????'
bot = telebot.TeleBot(API_TOKEN)

user_data = {}

# Вопросы и варианты
questions = [
    {
        'text': '1. Какая у вас форма деятельности?',
        'options': ['Физлицо', 'Самозанятый', 'ИП', 'ООО']
    },
    {
        'text': '2. Выберите ваш вид деятельности:',
        'options': [
            'Ремонт техники', 'Репетиторство', 'Веб-дизайн',
            'Парикмахер', 'Фото/видео съёмка', 'Доставка',
            'Торговля', 'Производство', 'Аренда',
            'Сдача жилья', 'Консультации', 'Сельское хозяйство',
            'Разработка ПО', 'Маркетинг', 'Строительство'
        ]
    },
    {
        'text': '3. Какой у вас примерный доход в год?',
        'options': ['До 2.4 млн', 'До 60 млн', 'До 200 млн', 'Более 200 млн']
    },
    {
        'text': '4. Есть ли у вас сотрудники(офицально)?',
        'options': ['Работаю один', '1–5 сотрудников', 'Более 5 сотрудников']
    },
    {
        'text': '5. С кем вы работаете?',
        'options': ['С физлицами', 'С юрлицами', 'И с теми, и с другими']
    },
    {
        'text': '6. Просят ли вас выставлять НДС?',
        'options': ['Да', 'Нет']
    },
    {
        'text': '7. Какие у вас расходы?',
        'options': ['Почти нет', 'Средние', 'Высокие']
    },
    {
        'text': '8. В каком регионе вы зарегистрированы?',
        'options': ['Москва', 'Санкт-Петербург', 'Ростовская область', 'Краснодарский край', 'Республика Татарстан', 'Свердловская область', 'Другой регион']
    }
]

# Старт
@bot.message_handler(commands=['start'])
def start(message):
    user_data[message.chat.id] = {'step': 0, 'answers': []}
    ask_question(message.chat.id)

def ask_question(chat_id):
    step = user_data[chat_id]['step']
    if step < len(questions):
        q = questions[step]
        markup = InlineKeyboardMarkup()
        for opt in q['options']:
            markup.add(InlineKeyboardButton(opt, callback_data=opt))
        bot.send_message(chat_id, q['text'], reply_markup=markup)
    else:
        show_result(chat_id)

def show_result(chat_id):
    answers = user_data[chat_id]['answers']

    form = answers[0]
    activity = answers[1]
    income = answers[2]
    employees = answers[3]
    clients = answers[4]
    asks_nds = answers[5]
    expenses = answers[6]
    region = answers[7]

    result = ''

    if form == 'Физлицо':
        if income == 'До 2.4 млн' and employees == 'Работаю один' and activity in ['Репетиторство', 'Фото/видео съёмка', 'Доставка', 'Парикмахер', 'Консультации']:
            result = ('✅ Вам подойдёт режим: НПД (налог на профессиональный доход)\n'
                      '➕ Плюсы: простая регистрация, 4–6% налог, не нужны отчёты\n'
                      '➖ Минусы: нельзя нанимать сотрудников, нельзя перепродажа\n'
                      '📌 Рекомендации: используйте приложение "Мой налог" для учёта')
        else:
            result = '⚠️ Вам нужно зарегистрироваться как ИП или самозанятый для легальной деятельности.'

    elif form == 'Самозанятый':
        if income == 'До 2.4 млн' and employees == 'Работаю один':
            result = ('✅ Вы можете оставаться на НПД.\n'
                      '➕ Упрощённая система, нет отчётности\n'
                      '➖ Ограничения по видам деятельности\n'
                      '📌 Следите, чтобы не выйти за лимит 2.4 млн руб.')
        else:
            result = '⚠️ Вы превысили лимиты самозанятости. Рассмотрите переход на ИП с УСН.'

    elif form == 'ИП':
        if income in ['До 2.4 млн', 'До 60 млн', 'До 200 млн']:
            if expenses == 'Высокие':
                rate = '15%'
                mode = 'УСН 15% (доходы минус расходы)'
            else:
                rate = '6%'
                mode = 'УСН 6% (доходы)'
            region_note = f'📍 В регионе "{region}" могут действовать пониженные ставки УСН, проверьте на сайте ФНС.'
            result = (f'✅ Вам подойдёт режим: {mode}\n'
                      f'➕ Простота учёта, пониженные ставки для некоторых регионов\n'
                      f'➖ Необходимость сдачи отчётности, ограничения по доходу и числу сотрудников\n'
                      f'{region_note}\n📌 Рекомендуется использовать онлайн-бухгалтерию (например, Моё дело, Контур).')
            if activity in ['Ремонт техники', 'Парикмахер', 'Фото/видео съёмка'] and employees == 'Работаю один':
                result += '\n📌 Также можно рассмотреть ПСН (патент), если доступен в вашем регионе.'
        else:
            result = '⚠️ Вы превысили лимит УСН. Вам нужно перейти на ОСНО.'

    elif form == 'ООО':
        if asks_nds == 'Да' or clients in ['С юрлицами', 'И с теми, и с другими']:
            result = ('✅ Вам подойдёт режим: ОСНО (общая система налогообложения с НДС)\n'
                      '➕ Возможность работать с крупными заказчиками, вычеты по НДС\n'
                      '➖ Сложная отчётность, высокий налог на прибыль\n'
                      '📌 Рекомендуется вести учёт через бухгалтера или сервис типа 1С')
        elif income in ['До 200 млн']:
            if expenses == 'Высокие':
                mode = 'УСН 15%'
            else:
                mode = 'УСН 6%'
            result = (f'✅ Вам подойдёт режим: {mode} для ООО\n'
                      '➕ Упрощённый учёт\n'
                      '➖ Нет НДС — может не подойти для B2B\n📌 Подходит для малого бизнеса без сложных операций.')
        else:
            result = '⚠️ ООО с большой выручкой должно применять ОСНО.'

    else:
        result = '❓ Пожалуйста, уточните информацию у налогового консультанта.'

    bot.send_message(chat_id, result)

    markup = InlineKeyboardMarkup()
    markup.add(InlineKeyboardButton('🔁 Пройти заново', callback_data='restart'))
    bot.send_message(chat_id, 'Хотите пройти опрос ещё раз?', reply_markup=markup)
    user_data.pop(chat_id, None)

@bot.callback_query_handler(func=lambda call: True)
def callback_query(call):
    chat_id = call.message.chat.id

    if call.data == 'restart':
        user_data[chat_id] = {'step': 0, 'answers': []}
        ask_question(chat_id)
        return

    step = user_data[chat_id]['step']
    user_data[chat_id]['answers'].append(call.data)
    user_data[chat_id]['step'] += 1
    ask_question(chat_id)

bot.polling(none_stop=True)
