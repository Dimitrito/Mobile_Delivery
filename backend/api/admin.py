from django.contrib import admin
from api.models import *

# Register your models here.
admin.site.register(Category)
admin.site.register(UserRole)
admin.site.register(Dish)
admin.site.register(Admin)
admin.site.register(User)
admin.site.register(Customer)
admin.site.register(Courier)
admin.site.register(Feedback)
admin.site.register(DishToOrder)
admin.site.register(Order)
admin.site.register(Delivery)



