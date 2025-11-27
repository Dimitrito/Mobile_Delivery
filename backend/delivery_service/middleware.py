from django.http import HttpResponseForbidden
from dotenv import load_dotenv
from decouple import config

load_dotenv()

ALLOWED_IPS = config('ALLOWED_IPS').split(',')

class AdminIPRestrictionMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        if request.path.startswith('/' + config('ADMIN_SITE') + '/'):
            ip = request.META.get('REMOTE_ADDR')
            if ip not in ALLOWED_IPS:
                return HttpResponseForbidden("Доступ заборонено")
        return self.get_response(request)
