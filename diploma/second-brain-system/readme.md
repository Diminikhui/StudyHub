Запуск локальной инфраструктуры:

cd second-brain-system/infra
docker compose up -d

Запуск backend:

cd second-brain-system/backend
./gradlew clean bootRun

Источник данных backend:

jdbc:postgresql://localhost:15432/second_brain_clean

Учётные данные:

second_brain / second_brain

Запуск Android-приложения:

cd second-brain-system/android
./gradlew installDebug

Базовый URL API для Android:

http://10.0.2.2:8080/

Локальные Docker-переопределения хранятся в:

second-brain-system/infra/.env