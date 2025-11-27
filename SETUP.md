# Инструкция по настройке проекта

## Шаг 1: Настройка бэкенда

1. Скопируйте содержимое репозитория [delivery_service](https://github.com/Dimitrito/delivery_service) в директорию `backend/`

   Вы можете сделать это одним из способов:
   
   **Вариант A: Через git (рекомендуется)**
   ```bash
   cd backend
   git clone https://github.com/Dimitrito/delivery_service.git temp
   xcopy temp\* . /E /I /Y
   rmdir /S /Q temp
   cd ..
   ```
   
   **Вариант B: Вручную**
   - Скачайте репозиторий как ZIP
   - Распакуйте содержимое в директорию `backend/`

2. Настройте виртуальное окружение и зависимости (см. `backend/README.md`)

## Шаг 2: Настройка Gradle Wrapper

Gradle Wrapper требует файл `gradle/wrapper/gradle-wrapper.jar`. 

**Вариант A: Через Android Studio (рекомендуется)**
1. Откройте проект в Android Studio
2. Android Studio автоматически предложит скачать Gradle Wrapper
3. Или: File → Settings → Build, Execution, Deployment → Build Tools → Gradle → Use Gradle from: 'gradle-wrapper.properties' file

**Вариант B: Через командную строку**
Если у вас установлен Gradle:
```bash
gradle wrapper --gradle-version 8.4
```

**Вариант C: Скачать вручную**
1. Скачайте `gradle-wrapper.jar` с https://github.com/gradle/gradle/releases
2. Поместите его в `gradle/wrapper/gradle-wrapper.jar`

## Шаг 3: Запуск проекта

### Backend
```bash
cd backend
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
docker-compose up -d
python manage.py migrate
python manage.py runserver
```

### Frontend

1. Установите Android Studio и Android SDK
2. Откройте проект в Android Studio
3. Дождитесь синхронизации Gradle
4. Подключите Android устройство или запустите эмулятор
5. Запустите приложение через Android Studio (кнопка Run) или:
   ```bash
   # Установка на устройство/эмулятор
   .\gradlew.bat :frontend:installDebug  # Windows
   ./gradlew :frontend:installDebug      # Linux/Mac
   ```

**Важно для работы с локальным бэкендом:**
- На эмуляторе используйте адрес `http://10.0.2.2:8000/api/` (это localhost хоста)
- На реальном устройстве используйте IP адрес вашего компьютера в локальной сети
- Убедитесь, что бэкенд разрешает подключения с вашего устройства (настройте ALLOWED_HOSTS в Django)

## Требования

- JDK 17 или выше
- Python 3.12
- Docker и docker-compose
- Android Studio
- Android SDK (Platform 34, Build-Tools)

