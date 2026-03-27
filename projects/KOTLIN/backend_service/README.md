# Kotlinithub Backend

Многомодульный backend на Ktor для учебного магазина.

Стек:

- Ktor
- PostgreSQL
- Exposed
- Flyway
- JWT
- Redis
- RabbitMQ
- Swagger/OpenAPI
- Unit + Integration + E2E тесты
- Docker + docker-compose
- GitHub Actions

## Модули

- `api` — HTTP-слой, маршруты, DTO, точка входа приложения, Swagger.
- `domain` — модели, интерфейсы репозиториев и бизнес-сервисы.
- `data` — Exposed-репозитории, миграции, Redis-кэш, RabbitMQ worker.
- `core` — общая конфигурация, JWT, хеширование паролей и Ktor-плагины.

## Основные маршруты

- `POST /auth/register`
- `POST /auth/login`
- `GET /products`
- `GET /products/{id}`
- `POST /products` — только admin
- `PUT /products/{id}` — только admin
- `DELETE /products/{id}` — только admin
- `POST /orders`
- `GET /orders`
- `DELETE /orders/{id}`
- `GET /stats/orders` — только admin
- `GET /swagger`

## Быстрый запуск

1. Поднять инфраструктуру и приложение:

```bash
docker compose up --build
```

2. Открыть Swagger:

```text
http://localhost:8080/swagger
```

3. Для локального запуска без Docker:

```bash
docker compose -f docker-compose.yml -f docker-compose.local.yml up -d postgres redis rabbitmq
./gradlew :api:run
```

Файл `docker-compose.yml` по умолчанию не публикует PostgreSQL на хост, чтобы `docker compose up --build` не конфликтовал с локальной БД.
Для локального запуска backend через `./gradlew :api:run` используется дополнительный файл `docker-compose.local.yml`, который публикует PostgreSQL на `15432`.
Если нужен другой порт, можно переопределить его так:

```bash
POSTGRES_PORT=5544 docker compose -f docker-compose.yml -f docker-compose.local.yml up -d postgres redis rabbitmq
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5544/kotlinithub ./gradlew :api:run
```

## Полезные команды

```bash
./gradlew test
./gradlew :api:buildFatJar
./gradlew :api:run
```

## Данные администратора по умолчанию

- Email: `admin@gmail.com`
- Password: `admin123`

Эти значения можно переопределить переменными окружения `BOOTSTRAP_ADMIN_EMAIL`, `BOOTSTRAP_ADMIN_PASSWORD`, `BOOTSTRAP_ADMIN_NAME`.
