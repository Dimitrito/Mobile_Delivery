# Структура проекта

```
Mobile_Delivery/
├── backend/                          # Django backend (скопировать из delivery_service)
│   ├── api/                          # API приложение Django
│   ├── delivery_service/             # Основной проект Django
│   ├── manage.py
│   ├── requirements.txt
│   ├── .env.dist
│   └── README.md
│
├── frontend/                          # Android приложение
│   ├── build.gradle.kts              # Конфигурация Gradle для frontend модуля
│   ├── proguard-rules.pro            # Правила ProGuard
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Манифест Android приложения
│           ├── kotlin/
│           │   └── com/
│           │       └── mobiledelivery/
│           │           └── frontend/
│           │               ├── MainActivity.kt   # Главная Activity
│           │               ├── api/
│           │               │   └── ApiClient.kt  # HTTP клиент для работы с API
│           │               ├── models/           # Модели данных
│           │               └── ui/               # UI компоненты
│           └── res/                   # Android ресурсы
│               ├── values/
│               │   ├── strings.xml   # Строковые ресурсы
│               │   └── themes.xml    # Темы приложения
│               └── xml/               # XML конфигурации
│
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties # Конфигурация Gradle Wrapper
│
├── build.gradle.kts                  # Корневой build файл
├── settings.gradle.kts                # Настройки проекта
├── gradle.properties                  # Свойства Gradle
├── gradlew.bat                        # Gradle Wrapper для Windows
├── docker-compose.yml                 # Конфигурация Docker для PostgreSQL
├── .gitignore                         # Игнорируемые файлы Git
├── README.md                          # Основная документация
├── SETUP.md                           # Инструкция по настройке
└── PROJECT_STRUCTURE.md               # Этот файл
```

## Модули

### Backend (`backend/`)
Django приложение из репозитория [delivery_service](https://github.com/Dimitrito/delivery_service).
Содержит:
- REST API
- Модели данных
- Бизнес-логику

### Frontend (`frontend/`)
Android приложение с Jetpack Compose.
Содержит:
- UI компоненты на Compose
- HTTP клиент (Ktor) с Android engine
- Модели данных для сериализации
- Логику взаимодействия с API
- Android Activity для навигации

## Следующие шаги

1. Скопируйте бэкенд из репозитория в `backend/`
2. Настройте Gradle Wrapper (см. SETUP.md)
3. Настройте переменные окружения для бэкенда
4. Запустите бэкенд и базу данных
5. Запустите frontend приложение

