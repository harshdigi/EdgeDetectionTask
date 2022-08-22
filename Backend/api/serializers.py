from rest_framework import serializers
from .models import MyModel

class MyModelSerializer(serializers.ModelSerializer):

    userId = serializers.CharField()
    image_url = serializers.ImageField()
    output_url = serializers.ImageField()

    class Meta:
        model = MyModel
        fields = ['userId', 'date', 'image_url', 'output_url']
