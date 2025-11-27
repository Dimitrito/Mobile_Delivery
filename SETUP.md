# Інструкція з налаштування проекту

## Клонування репозиторію

Якщо ви ще не клонували проект, виконайте:

```bash
git clone <URL_ВАШОГО_РЕПОЗИТОРІЮ> Mobile_Delivery
cd Mobile_Delivery
```

## Крок 1: Налаштування бекенду

1. Бекенд вже скопійовано в директорію `backend/`

2. Налаштуйте віртуальне середовище та залежності (див. `backend/README.md`)

## Крок 2: Налаштування Gradle Wrapper

Gradle Wrapper потребує файл `gradle/wrapper/gradle-wrapper.jar`. 

**Варіант A: Через Android Studio (рекомендовано)**
1. Відкрийте проект в Android Studio
2. Android Studio автоматично запропонує завантажити Gradle Wrapper
3. Або: File → Settings → Build, Execution, Deployment → Build Tools → Gradle → Use Gradle from: 'gradle-wrapper.properties' file

**Варіант B: Через командний рядок**
Якщо у вас встановлено Gradle:
```bash
gradle wrapper --gradle-version 8.4
```

**Варіант C: Завантажити вручну**
1. Завантажте `gradle-wrapper.jar` з https://github.com/gradle/gradle/releases
2. Помістіть його в `gradle/wrapper/gradle-wrapper.jar`

## Крок 3: Запуск проекту

### Backend
```bash
cd backend
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
cd ..
docker-compose up -d
cd backend
python manage.py migrate
python manage.py runserver
```

### Frontend

1. Встановіть Android Studio та Android SDK
2. Відкрийте проект в Android Studio
3. Дочекайтеся синхронізації Gradle
4. Підключіть Android пристрій або запустіть емулятор
5. Запустіть додаток через Android Studio (кнопка Run) або:
   ```bash
   # Встановлення на пристрій/емулятор
   .\gradlew.bat :frontend:installDebug  # Windows
   ./gradlew :frontend:installDebug      # Linux/Mac
   ```

**Важливо для роботи з локальним бекендом:**
- На емуляторі використовуйте адресу `http://10.0.2.2:8000/api/` (це localhost хоста)
- На реальному пристрої використовуйте IP адресу вашого комп'ютера в локальній мережі
- Переконайтеся, що бекенд дозволяє підключення з вашого пристрою (налаштуйте ALLOWED_HOSTS в Django)

## Вимоги

- JDK 17 або вище
- Python 3.12
- Docker та docker-compose
- Android Studio
- Android SDK (Platform 34, Build-Tools)
