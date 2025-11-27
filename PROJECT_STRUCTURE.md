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
├── frontend/                         # Android додаток (Clean Architecture)
│   ├── build.gradle.kts              # Конфігурація Gradle для frontend модуля
│   ├── proguard-rules.pro            # Правила ProGuard
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Маніфест Android додатку
│           ├── kotlin/
│           │   └── com/
│           │       └── mobiledelivery/
│           │           ├── MainActivity.kt          # Головна Activity
│           │           ├── data/                    # Data Layer
│           │           │   ├── api/                 # API клієнт та сервіси
│           │           │   │   ├── ApiClient.kt
│           │           │   │   ├── DeliveryApiService.kt
│           │           │   │   ├── interceptors/
│           │           │   │   │   └── AuthInterceptor.kt
│           │           │   │   └── models/
│           │           │   │       ├── ApiRequest.kt
│           │           │   │       └── ApiResponse.kt
│           │           │   ├── repository/          # Репозиторії
│           │           │   │   ├── AuthRepository.kt
│           │           │   │   └── RestaurantRepository.kt
│           │           │   └── shared/              # Shared preferences
│           │           │       ├── PreferencesManager.kt
│           │           │       └── TokenManager.kt
│           │           ├── domain/                  # Domain Layer
│           │           │   ├── models/              # Domain моделі
│           │           │   │   ├── User.kt
│           │           │   │   ├── Restaurant.kt
│           │           │   │   ├── MenuItem.kt
│           │           │   │   ├── Order.kt
│           │           │   │   └── Cart.kt
│           │           │   └── usecases/            # Use cases
│           │           │       ├── LoginUseCase.kt
│           │           │       ├── GetRestaurantsUseCase.kt
│           │           │       └── PlaceOrderUseCase.kt
│           │           ├── presentation/            # Presentation Layer
│           │           │   ├── screens/             # Екрани
│           │           │   │   └── home/
│           │           │   │       └── HomeScreen.kt
│           │           │   ├── viewmodels/          # ViewModels
│           │           │   │   └── AuthViewModel.kt
│           │           │   ├── navigation/          # Навігація
│           │           │   │   └── AppNavigation.kt
│           │           │   ├── states/              # UI стани та події
│           │           │   │   ├── UiState.kt
│           │           │   │   └── UiEvent.kt
│           │           │   └── theme/               # Теми
│           │           │       └── MobileDeliveryTheme.kt
│           │           └── di/                      # Dependency Injection
│           │               ├── AppModule.kt
│           │               ├── NetworkModule.kt
│           │               └── RepositoryModule.kt
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
Android додаток з Jetpack Compose, організований за принципами Clean Architecture.

**Data Layer** (`data/`):
- API клієнт та сервіси для роботи з backend
- Репозиторії для абстракції джерел даних
- Менеджери для SharedPreferences та токенів

**Domain Layer** (`domain/`):
- Domain моделі (чисті бізнес-об'єкти)
- Use cases (бізнес-логіка)

**Presentation Layer** (`presentation/`):
- UI екрани на Compose
- ViewModels для управління станом
- Навігація
- UI стани та події

**DI Layer** (`di/`):
- Модулі для Dependency Injection

## Архітектура

Проект використовує Clean Architecture з розділенням на три основні шари:
1. **Data** - робота з даними (API, БД, SharedPreferences)
2. **Domain** - бізнес-логіка (use cases, domain моделі)
3. **Presentation** - UI (екрани, ViewModels, навігація)

## Наступні кроки

1. Клонуйте проект з репозиторію
2. Налаштуйте Gradle Wrapper (див. SETUP.md)
3. Налаштуйте змінні оточення для бекенду
4. Запустіть бекенд та базу даних
5. Запустіть frontend додаток
