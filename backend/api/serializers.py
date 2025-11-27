from rest_framework import serializers
from api.models import *
from django.conf import settings
from django.contrib.auth import get_user_model


class RegistrationSerializer(serializers.ModelSerializer):
    password2 = serializers.CharField(style={"input_type": "password"})

    class Meta:
        model = get_user_model()
        fields = ("first_name", "last_name", "email", "phone_number", "password", "password2")
        extra_kwargs = {
            "password": {"write_only": True},
            "password2": {"write_only": True}
        }

    def save(self):
        user = get_user_model()(
            email=self.validated_data["email"],
            role_id=1,
            first_name=self.validated_data["first_name"],
            phone_number=self.validated_data["phone_number"],
            last_name=self.validated_data["last_name"],
        )

        password = self.validated_data["password"]
        password2 = self.validated_data["password2"]

        if password != password2:
            raise serializers.ValidationError(
                {"password": "Passwords do not match!"})

        user.set_password(password)
        user.save()

        return user


class LoginSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField(
        style={"input_type": "password"}, write_only=True)


class CategoriesSerializer(serializers.ModelSerializer):
    class Meta:
        model = Category
        fields = '__all__'


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ("id", "email", "phone_number", "is_staff", "first_name", "last_name")


class FeedbackSerializer(serializers.ModelSerializer):
    user_first_name = serializers.SerializerMethodField()

    def get_user_first_name(self, obj):
        try:
            customer = Customer.objects.get(feedback=obj)
            user = customer.user
            return user.first_name
        except Customer.DoesNotExist:
            return None

    class Meta:
        model = Feedback
        fields = ['id', 'user', 'user_first_name', 'review_text', 'rating', 'review_date']


class CustomerSerializer(serializers.ModelSerializer):
    class Meta:
        model = Customer
        fields = '__all__'


class OrderSerializer(serializers.ModelSerializer):
    class Meta:
        model = Order
        fields = '__all__'


class DishSerializer(serializers.ModelSerializer):
    category = CategoriesSerializer()

    class Meta:
        model = Dish
        fields = ['id', 'product_name', 'description', 'price', 'discount', 'category', 'mass', 'calories', 'protein',
                  'fat', 'carbohydrate', 'image_url', 'available']


class DishToOrderSerializer(serializers.ModelSerializer):
    dish = DishSerializer()
    order = OrderSerializer()

    class Meta:
        model = DishToOrder
        fields = '__all__'


class GetDishToOrderSerializer(serializers.ModelSerializer):
    class Meta:
        model = DishToOrder
        fields = '__all__'


class DeliverySerializer(serializers.ModelSerializer):
    user_phone_number = serializers.SerializerMethodField()

    class Meta:
        model = Delivery
        fields = '__all__'

    def get_user_phone_number(self, obj):
        try:
            order = Order.objects.get(id=obj.current_order_id)
            customer_id = order.user_id
            customer = Customer.objects.get(id=customer_id)
            return str(customer.user.phone_number)
        except Order.DoesNotExist:
            return None


class CourierSerializer(serializers.ModelSerializer):
    class Meta:
        model = Courier
        fields = '__all__'


class UserRoleSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['role_id']
