from .models import Customer, Order
from django.contrib.auth import get_user_model

User = get_user_model()


def update_user_profile(user_id, phone_number=None, first_name=None, last_name=None, delivery_address=None,
                        password=None):
    try:
        user = User.objects.get(pk=user_id)
        # Оновлюємо дані користувача
        if phone_number:
            user.phone_number = phone_number

        if first_name:
            user.first_name = first_name

        if last_name:
            user.last_name = last_name

        if password:
            user.set_password(password)

        # Зберігаємо зміни у моделі User
        user.save()

        # Отримуємо об'єкт Customer для даного користувача
        customer = Customer.objects.get(user=user)

        # Оновлюємо поле delivery_address, якщо передано відповідний параметр
        if delivery_address:
            customer.delivery_address = delivery_address
            customer.save()

        return True, "Профіль користувача успішно оновлено."
    except User.DoesNotExist:
        return False, "Користувач з вказаним ID не існує."
    except Customer.DoesNotExist:
        return False, "Користувач має відсутній запис у моделі Customer."


def update_order(order_id, total_price=None, address=None, selected_date=None, selected_time=None, number_people=None,
                 note=None):
    try:
        comment = ""
        order = Order.objects.get(pk=order_id)

        if total_price:
            order.price = total_price

        if address:
            comment += "Адреса доставки: " + address

        if selected_date and selected_time:
            comment += "\nДата та час доставки: " + selected_date + " " + selected_time

        if number_people:
            comment += "\nКількість приборів: " + str(number_people)

        if note:
            comment += "\nПримітка: " + note

        if comment:
            order.comment = comment
        order.save()

        return True, "Замовлення з номером " + str(order_id) + " успішно оновлено."
    except Order.DoesNotExist:
        return False, "Замовлення з вказаним ID не існує."
