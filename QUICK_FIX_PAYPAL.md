# PayPal Sandbox - Налаштування та виправлення помилок

## ✅ Статус

PayPal Sandbox credentials **вже налаштовано** в коді!

- **Client ID**: Налаштовано в `NetworkModule.kt`
- **Client Secret**: Налаштовано в `NetworkModule.kt`
- **Режим**: Sandbox (тестовий, без списання реальних грошей)

## Тестовий акаунт PayPal

Для тестування оплати використовуйте:

- **Email**: `sb-hvazj30895385@personal.example.com`
- **Password**: `@6gKARs`

## Якщо виникає помилка 401 Unauthorized

### Можливі причини:

1. **Credentials некоректні** - перевірте, що Client ID та Client Secret правильні
2. **Credentials застарілі** - можливо, вони були змінені в PayPal Dashboard
3. **Проблеми з мережею** - перевірте інтернет-з'єднання

### Швидке виправлення

1. **Отримайте PayPal Sandbox credentials:**
   - Перейдіть на https://developer.paypal.com/
   - Увійдіть або зареєструйтеся
   - Перейдіть до **Dashboard** → **My Apps & Credentials**
   - Створіть новий додаток (Sandbox)
   - Скопіюйте **Client ID** та **Client Secret**

2. **Вставте credentials в код:**
   - Відкрийте файл: `frontend/src/main/kotlin/com/mobiledelivery/di/NetworkModule.kt`
   - Знайдіть функції `getPayPalClientId()` та `getPayPalClientSecret()` (рядки ~88-102)
   - Замініть:
     ```kotlin
     return "YOUR_PAYPAL_SANDBOX_CLIENT_ID"
     ```
     на:
     ```kotlin
     return "ваш_реальний_client_id"
     ```
   - І замініть:
     ```kotlin
     return "YOUR_PAYPAL_SANDBOX_CLIENT_SECRET"
     ```
     на:
     ```kotlin
     return "ваш_реальний_client_secret"
     ```

3. **Перезапустіть додаток**

### Варіант 2: Тимчасово використати мок-режим (для тестування без PayPal)

Якщо ви ще не отримали credentials, можете тимчасово повернутися до мок-режиму:

1. Відкрийте файл: `frontend/src/main/kotlin/com/mobiledelivery/presentation/navigation/AppNavigation.kt`
2. Знайдіть рядки ~79-80:
   ```kotlin
   createPayPalPaymentUseCase = UseCaseModule.createPayPalPaymentUseCase(useTestMode = false),
   capturePayPalPaymentUseCase = UseCaseModule.createCapturePayPalPaymentUseCase(useTestMode = false)
   ```
3. Змініть на:
   ```kotlin
   createPayPalPaymentUseCase = UseCaseModule.createPayPalPaymentUseCase(useTestMode = true),
   capturePayPalPaymentUseCase = UseCaseModule.createCapturePayPalPaymentUseCase(useTestMode = true)
   ```

⚠️ **Увага**: Мок-режим не виконує реальні платежі, це лише для тестування UI.

## Детальні інструкції

Повна інструкція з налаштування PayPal знаходиться в файлі **PAYPAL_SETUP.md**

## Перевірка

Після налаштування credentials:
- Перезапустіть додаток
- Спробуйте створити замовлення
- Помилка 401 має зникнути
- Якщо все налаштовано правильно, ви побачите діалог PayPal оплати

