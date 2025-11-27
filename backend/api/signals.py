from django.db.models.signals import post_save
from django.dispatch import receiver
from .models import User, Order, Delivery
from .models import Customer, Admin, Courier


@receiver(post_save, sender=User)
def create_courier(sender, instance, created, **kwargs):
    if created and instance.role_id == 5 and instance.is_staff:
        Courier.objects.create(user=instance)


@receiver(post_save, sender=User)
def create_customer(sender, instance, created, **kwargs):
    if created and not instance.is_staff and instance.role != "Courier":
        Customer.objects.create(user=instance)


@receiver(post_save, sender=User)
def create_admin(sender, instance, created, **kwargs):
    if created and instance.is_admin and instance.role != "Courier":
        Admin.objects.create(user=instance)


@receiver(post_save, sender=Order)
def create_delivery_on_order_paid(sender, instance, created, **kwargs):
    if instance.order_status == "paid":
        free_courier = Courier.find_free_courier()
        Delivery.objects.create(
            current_order=instance,
            courier=free_courier,
            start_time=instance.start_time,
            end_time=instance.end_time,
            delivery_address=instance.parse_delivery_address(),  # Використовуйте правильну адресу доставки
            delivery_status="in_delivery"
        )


@receiver(post_save, sender=Delivery)
def update_order_status(sender, instance, **kwargs):
    if instance.current_order_id:
        try:
            order = Order.objects.get(id=instance.current_order_id)
            order.order_status = instance.delivery_status
            order.save()
        except Order.DoesNotExist:
            pass