#!/usr/bin/env python
"""
Тестовый скрипт для проверки is_courier
"""
import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mobile_delivery.settings')
django.setup()

from api.models import User, Courier
from api.serializers import UserSerializer

# Находим пользователя курьера
try:
    user = User.objects.get(email='courier@gmail.com')
    print(f"User found: {user.email} (id={user.id})")
    
    # Проверяем, есть ли запись Courier
    try:
        courier = Courier.objects.get(user=user)
        print(f"Courier found: id={courier.id}, user_id={courier.user.id}")
    except Courier.DoesNotExist:
        print("ERROR: Courier record NOT found for this user!")
        print("Creating Courier record...")
        courier = Courier.objects.create(user=user)
        print(f"Courier created: id={courier.id}")
    
    # Тестируем сериализатор
    serializer = UserSerializer(user)
    print(f"\nSerialized data:")
    print(f"  is_courier: {serializer.data.get('is_courier')}")
    print(f"  role_id: {serializer.data.get('role_id')}")
    print(f"  email: {serializer.data.get('email')}")
    print(f"\nFull response: {serializer.data}")
    
except User.DoesNotExist:
    print("ERROR: User 'courier@gmail.com' not found!")

