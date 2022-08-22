from django.db import models

class MyModel(models.Model):
    def nameFile(instance, filename):
     return '/'.join(['images', str(instance.userId), filename])
    userId = models.CharField(max_length=255, blank= False, null =False)
    date =  models.DateTimeField(auto_now_add=True)
    image_url = models.ImageField(upload_to=nameFile, blank=True)
    output_url = models.ImageField(upload_to=nameFile, blank=True )