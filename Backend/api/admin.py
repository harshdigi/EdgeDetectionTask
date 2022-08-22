from django.contrib import admin
from .models import MyModel

class profileAdmin(admin.ModelAdmin):
    list_display = ['userId', 'date','image_url']
admin.site.register(MyModel)
# Register your models here.
