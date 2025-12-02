# Налаштування PayPal Sandbox

## Що таке PayPal Sandbox?

PayPal Sandbox - це **реальний PayPal API**, але в тестовому режимі. Він:
- ✅ Імітує справжню оплату
- ✅ Використовує реальні API endpoints PayPal
- ✅ Підтримує всі функції реального API
- ❌ **НЕ списує реальні гроші**
- ❌ Використовує тестові картки та акаунти

## Як отримати PayPal Sandbox credentials

### 1. Реєстрація на PayPal Developer

1. Перейдіть на https://developer.paypal.com/
2. Натисніть "Sign Up" або "Log In"
3. Створіть або увійдіть у свій акаунт

### 2. Створення додатку

1. Після входу перейдіть до **Dashboard**
2. Натисніть **"Create App"** або **"My Apps & Credentials"**
3. Заповніть форму:
   - **App Name**: Назва вашого додатку (наприклад, "Mobile Delivery")
   - **Merchant**: Виберіть ваш бізнес-акаунт (або створіть новий)
   - **Features**: Виберіть "Accept Payments"
4. Натисніть **"Create App"**

### 3. Отримання credentials

Після створення додатку ви побачите:
- **Client ID** (Sandbox)
- **Client Secret** (Sandbox) - натисніть "Show" щоб побачити

⚠️ **Важливо**: Використовуйте **Sandbox** credentials, а не Live!

### 4. Налаштування в проекті

#### Frontend (Android)

✅ **PayPal Sandbox credentials вже налаштовано!**

Credentials вже встановлені в файлі `frontend/src/main/kotlin/com/mobiledelivery/di/NetworkModule.kt`:

```kotlin
private fun getPayPalClientId(): String {
    return "AQIso8YTLHu3s9AQYChK2iofdaEW5icfCL-8tblf4a9X7qDBjWElCHcflD-pLMi5onl2u_kiGJS_orPH"
}

private fun getPayPalClientSecret(): String {
    return "EI0OadJd3nflmYUx6xGvC-pGv-hvmUozT8EQ5eB3OvLLaaUshtphLpYzwx8rBUKkCEc6kbLmO-m1GSwB"
}
```

Якщо потрібно змінити credentials, просто відредагуйте ці функції.

**Або** використовуйте BuildConfig (рекомендовано для безпеки):

1. Додайте в `frontend/build.gradle.kts`:

```kotlin
android {
    buildTypes {
        debug {
            buildConfigField("String", "PAYPAL_CLIENT_ID", "\"ВАШ_SANDBOX_CLIENT_ID\"")
            buildConfigField("String", "PAYPAL_CLIENT_SECRET", "\"ВАШ_SANDBOX_CLIENT_SECRET\"")
        }
    }
}
```

2. Оновіть `NetworkModule.kt`:

```kotlin
private fun getPayPalClientId(): String {
    return BuildConfig.PAYPAL_CLIENT_ID
}

private fun getPayPalClientSecret(): String {
    return BuildConfig.PAYPAL_CLIENT_SECRET
}
```

#### Backend (Django)

Додайте в файл `.env` в директорії `backend/`:

```env
PAYPAL_CLIENT_ID=AQIso8YTLHu3s9AQYChK2iofdaEW5icfCL-8tblf4a9X7qDBjWElCHcflD-pLMi5onl2u_kiGJS_orPH
PAYPAL_CLIENT_SECRET=EI0OadJd3nflmYUx6xGvC-pGv-hvmUozT8EQ5eB3OvLLaaUshtphLpYzwx8rBUKkCEc6kbLmO-m1GSwB
```

⚠️ **Важливо**: Створіть файл `.env` в директорії `backend/`, якщо його ще немає. Не комітьте `.env` в Git!

## Тестування оплати

### Тестові акаунти PayPal

PayPal Sandbox автоматично створює тестові акаунти. Ви можете:

1. Перейти до **Sandbox** → **Accounts** в PayPal Developer Dashboard
2. Використовувати існуючі тестові акаунти або створити нові
3. Тестові акаунти мають вигляд: `buyer@personal.example.com`

### Налаштований тестовий акаунт

Для тестування використовуйте наступний тестовий акаунт:

- **Email**: `sb-hvazj30895385@personal.example.com`
- **Password**: `@6gKARs`

Цей акаунт можна використовувати для тестування оплати в додатку.

### Тестові картки

PayPal Sandbox не вимагає реальних карток. Ви можете:
- Використовувати тестові акаунти PayPal (вище)
- Або використовувати тестові картки (якщо PayPal підтримує)

### Перевірка роботи

1. Запустіть додаток
2. Створіть замовлення
3. Натисніть "Pay with card"
4. Виконайте оплату через PayPal Sandbox
5. Перевірте в PayPal Developer Dashboard → **Transactions**, що транзакція відображається

## Перехід на Production

Коли будете готові до продакшну:

1. Створіть новий додаток з типом **Live** (не Sandbox)
2. Отримайте **Live** credentials
3. Змініть `isSandbox = false` в коді
4. Оновіть credentials на Live
5. ⚠️ **Увага**: Live API списує реальні гроші!

## Документація

- [PayPal Developer Documentation](https://developer.paypal.com/docs/)
- [PayPal Orders API](https://developer.paypal.com/docs/api/orders/v2/)
- [PayPal Sandbox Testing Guide](https://developer.paypal.com/docs/api-basics/sandbox/)

