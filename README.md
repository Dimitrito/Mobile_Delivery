# Mobile Delivery

Android додаток для служби доставки на Kotlin з Jetpack Compose.

## Стек технологій

### Backend (без змін)
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

## Клонування та розгортання проекту

### 1. Клонування репозиторію

```bash
git clone <URL_ВАШОГО_РЕПОЗИТОРІЮ> Mobile_Delivery
cd Mobile_Delivery
```

### 2. Швидкий старт

Детальна інструкція з налаштування знаходиться у файлі [SETUP.md](SETUP.md).

## Структура проекту

```
Mobile_Delivery/
├── backend/          # Django backend
├── frontend/         # Android додаток
├── docker-compose.yml
└── build.gradle.kts
```

## Встановлення та запуск

### 1. Налаштування бекенду

1. Бекенд вже скопійовано в директорію `backend/`
2. Перейдіть до директорії `backend/`:
   ```bash
   cd backend
   ```
3. Створіть віртуальне середовище:
   ```bash
   python -m venv venv
   venv\Scripts\activate  # Windows
   ```
4. Встановіть залежності:
   ```bash
   pip install -r requirements.txt
   ```
5. Створіть файл `.env` з `.env.dist` та налаштуйте змінні оточення
6. Запустіть Docker Compose для бази даних (з кореневої директорії):
   ```bash
   docker-compose up -d
   ```
7. Застосуйте міграції:
   ```bash
   python manage.py migrate
   ```
8. Запустіть сервер розробки:
   ```bash
   python manage.py runserver
   ```

### 2. Налаштування фронтенду

1. Переконайтеся, що встановлено:
   - JDK 17 або вище
   - Android Studio або Android SDK
   - Android SDK Platform 34
   - Android SDK Build-Tools
2. Налаштуйте Gradle Wrapper (див. [SETUP.md](SETUP.md))
3. Відкрийте проект в Android Studio
4. Синхронізуйте Gradle файли
5. Підключіть Android пристрій або запустіть емулятор
6. Запустіть додаток через Android Studio або:
   ```bash
   .\gradlew.bat :frontend:installDebug  # Windows
   ./gradlew :frontend:installDebug      # Linux/Mac
   ```

Або відкрийте проект в Android Studio та запустіть з IDE.

## Розробка

### Backend
Backend знаходиться в директорії `backend/` та використовує стандартну структуру Django проекту.

### Frontend
Frontend знаходиться в директорії `frontend/` та використовує:
- Jetpack Compose для UI
- Ktor Client для HTTP запитів до API
- kotlinx.serialization для серіалізації JSON
- Android Activity для навігації

API клієнт налаштований на `http://10.0.2.2:8000/api/` для Android емулятора (це адреса localhost хоста). Для реального пристрою використовуйте IP адресу вашого комп'ютера. Налаштування можна змінити в `frontend/src/main/kotlin/com/mobiledelivery/frontend/api/ApiClient.kt`.

## Ліцензія

[Вкажіть ліцензію]
