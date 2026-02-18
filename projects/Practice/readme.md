# FinTrack RESTful API 


## Стек
- Java 17
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- Spring Security (Basic Auth)
- PostgreSQL 16 (Docker)
- Lombok
- Swagger / OpenAPI (springdoc)

## Сущности 
- **User**: id, username, password, email
- **Category**: id, name
- **Transaction**: id, amount, description, type (EXPENSE/INCOME), timestamp, user_id, category_id



## 1) Запуск через Docker
docker compose up --build
### 2) открыть
Swagger UI: http://localhost:8081/swagger-ui.html
3) Остановка
docker compose down

## 1) Локальный запуск без Docker
кнопка RUN
### 2) открыть 
Swagger UI: http://localhost:8080/swagger-ui.html