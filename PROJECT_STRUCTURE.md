# Структура проекту

```
Mobile_Delivery/
├── backend/                          # Django backend
│   ├── api/                          # API додаток Django
│   ├── delivery_service/             # Основний проект Django
│   ├── manage.py
│   ├── requirements.txt
│   ├── .env.dist
│   └── README.md
│
├── frontend/                          # Android додаток
│   ├── build.gradle.kts              # Конфігурація Gradle для frontend модуля
│   ├── proguard-rules.pro            # Правила ProGuard
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Маніфест Android додатку
│           ├── kotlin/
│           │   └── com/
│           │       └── mobiledelivery/
│           │           └── frontend/
│           │               ├── MainActivity.kt   # Головна Activity
│           │               ├── api/
│           │               │   └── ApiClient.kt  # HTTP клієнт для роботи з API
│           │               ├── models/           # Моделі даних
│           │               └── ui/               # UI компоненти
│           └── res/                   # Android ресурси
│               ├── values/
│               │   ├── strings.xml   # Строкові ресурси
│               │   └── themes.xml    # Теми додатку
│               └── xml/               # XML конфігурації
│
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties # Конфігурація Gradle Wrapper
│
├── build.gradle.kts                  # Кореневий build файл
├── settings.gradle.kts                # Налаштування проекту
├── gradle.properties                  # Властивості Gradle
├── gradlew.bat                        # Gradle Wrapper для Windows
├── docker-compose.yml                 # Конфігурація Docker для PostgreSQL
├── .gitignore                         # Ігноровані файли Git
├── README.md                          # Основна документація
├── SETUP.md                           # Інструкція з налаштування
└── PROJECT_STRUCTURE.md               # Цей файл
```

## Модулі

### Backend (`backend/`)
Django додаток з репозиторію [delivery_service](https://github.com/Dimitrito/delivery_service).
Містить:
- REST API
- Моделі даних
- Бізнес-логіку

### Frontend (`frontend/`)
Android додаток з Jetpack Compose.
Містить:
- UI компоненти на Compose
- HTTP клієнт (Ktor) з Android engine
- Моделі даних для серіалізації
- Логіку взаємодії з API
- Android Activity для навігації

## Наступні кроки

1. Клонуйте проект з репозиторію
2. Налаштуйте Gradle Wrapper (див. SETUP.md)
3. Налаштуйте змінні оточення для бекенду
4. Запустіть бекенд та базу даних
5. Запустіть frontend додаток
