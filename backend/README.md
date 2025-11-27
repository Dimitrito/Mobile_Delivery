# Backend (Django)

Django REST API для служби доставки.

## Встановлення

### Вимоги

- Python 3.12
- pip
- Docker та docker-compose

### 1. Створіть віртуальне середовище

```bash
python -m venv venv
```

На Windows:
```bash
venv\Scripts\activate
```

На Unix системах:
```bash
source venv/bin/activate
```

### 2. Встановіть залежності

```bash
pip install -r requirements.txt
```

### 3. Налаштуйте змінні оточення

Створіть файл `.env` з `.env.dist` та замініть секретні змінні на свої значення.

### 4. Запустіть Docker Compose

З кореневої директорії проекту:
```bash
docker-compose up -d
```

### 5. Застосуйте міграції бази даних

```bash
python manage.py migrate
```

### 6. Запустіть сервер розробки

```bash
python manage.py runserver
```

Сервер буде доступний за адресою `http://localhost:8000`

## API

API доступне за адресою `http://localhost:8000/api/`

Для Android емулятора використовуйте: `http://10.0.2.2:8000/api/`
