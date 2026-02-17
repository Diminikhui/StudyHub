# Books CRUD — Spring Boot сервер + Postman коллекция

## Структура проекта
1) **postman_server/** — тут лежит сервер  
   - внутри неё основная папка сервера: **Postman/** (Spring Boot проект)

2) **postman/** — тут лежат экспорты Postman  
   - `Books CRUD.postman_collection.json` — коллекция CRUD  
   - `local.postman_environment.json` — окружение (Environment)

---

## 1) Запуск сервера

### IntelliJ IDEA
1. Открыть в IntelliJ IDEA папку:  
   `postman_server/Postman`
2. Нажать кнопку **RUN** (запуск Spring Boot приложения)

### Проверка в браузере
Открыть:
`http://localhost:8080/api/users/1/books`

Ожидаемый результат: `[]` или список книг в JSON.

---

## 2) Действия в Postman

### Импорт коллекции и окружения
1. Открыть **Import** в Postman
2. Выбрать файлы из папки `postman/`:
   - `Books CRUD.postman_collection.json`
   - `local.postman_environment.json`

### Выбор окружения
Справа сверху (Environment) выбрать:  
`local`

### Запуск всей коллекции
1. Нажать **Runner**
2. Нажать **Run** (запуск коллекции)