# Mobile Delivery

Android приложение для службы доставки на Kotlin с Jetpack Compose.

## Стек технологий

### Backend (без изменений)
- Python 3.12
- Django
- PostgreSQL
- Docker
- docker-compose

### Frontend (Android)
- Kotlin
- Jetpack Compose
- Ktor Client
- kotlinx.serialization
- Gradle Kotlin DSL
- Android SDK

## Структура проекта

```
Mobile_Delivery/
├── backend/          # Django backend (из репозитория delivery_service)
├── frontend/          # Android приложение
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── kotlin/com/mobiledelivery/frontend/
│   │   └── res/
├── docker-compose.yml
└── build.gradle.kts
```

## Быстрый старт

Подробная инструкция по настройке находится в файле [SETUP.md](SETUP.md).

## Установка и запуск

### 1. Настройка бэкенда

1. Бэкенд уже скопирован в директорию `backend/`
2. Перейдите в директорию `backend/`:
   ```bash
   cd backend
   ```
3. Создайте виртуальное окружение:
   ```bash
   python -m venv venv
   venv\Scripts\activate  # Windows
   ```
4. Установите зависимости:
   ```bash
   pip install -r requirements.txt
   ```
5. Создайте `.env` файл из `.env.dist` и настройте переменные окружения
6. Запустите Docker Compose для базы данных:
   ```bash
   docker-compose up -d
   ```
7. Примените миграции:
   ```bash
   python manage.py migrate
   ```
8. Запустите сервер разработки:
   ```bash
   python manage.py runserver
   ```

### 2. Настройка фронтенда

1. Убедитесь, что установлены:
   - JDK 17 или выше
   - Android Studio или Android SDK
   - Android SDK Platform 34
   - Android SDK Build-Tools
2. Настройте Gradle Wrapper (см. [SETUP.md](SETUP.md))
3. Откройте проект в Android Studio
4. Синхронизируйте Gradle файлы
5. Подключите Android устройство или запустите эмулятор
6. Запустите приложение через Android Studio или:
   ```bash
   .\gradlew.bat :frontend:installDebug  # Windows
   ./gradlew :frontend:installDebug      # Linux/Mac
   ```

**Важно:** Для работы с локальным бэкендом на эмуляторе используйте адрес `http://10.0.2.2:8000/api/` вместо `localhost`.

## Разработка

### Backend
Backend находится в директории `backend/` и использует стандартную структуру Django проекта.

### Frontend
Frontend находится в директории `frontend/` и использует:
- Jetpack Compose для UI
- Ktor Client для HTTP запросов к API
- kotlinx.serialization для сериализации JSON
- Android Activity для навигации

API клиент настроен на `http://10.0.2.2:8000/api/` для Android эмулятора (это адрес localhost хоста). Для реального устройства используйте IP адрес вашего компьютера. Настройки можно изменить в `frontend/src/main/kotlin/com/mobiledelivery/frontend/api/ApiClient.kt`.

## Лицензия

[Укажите лицензию]