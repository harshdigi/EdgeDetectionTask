from django.shortcuts import render
import cv2
import numpy as np
from .models import MyModel
from .serializers import MyModelSerializer
from rest_framework import permissions,routers,serializers,viewsets
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.decorators import api_view
from rest_framework.response import Response
from django.http import HttpResponse
from rest_framework import status
import urllib.request
from PIL import Image as im
from django.core.files.base import ContentFile
from io import StringIO 
from io import BytesIO

@api_view(['POST'])
def uploadImage(request):
    image = _grab_image(stream=request.FILES['image_url'])
    input_img = inputimage(image)
    output =outputimage(image)
    input =request.data
    input['image_url'] = input_img
    input['output_url'] = output
    serializer = MyModelSerializer(data=input)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)



def _grab_image(path=None, stream=None, url=None):
	# if the path is not None, then load the image from disk
	if path is not None:
		image = cv2.imread(path)
	# otherwise, the image does not reside on disk
	else:	
		# if the URL is not None, then download the image
		if url is not None:
			resp = urllib.request.urlopen(url)
			data = resp.read()
		# if the stream is not None, then the image has been uploaded
		elif stream is not None:
			data = stream.read()
		# convert the image to a NumPy array and then read it into
		# OpenCV format
		image = np.asarray(bytearray(data), dtype="uint8")
		image = cv2.imdecode(image, cv2.IMREAD_COLOR)
 
	# return the image
	return image

def outputimage(image):
    img_io = BytesIO()
    img_gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    img_blur = cv2.GaussianBlur(img_gray, (3,3), 0) 
    edges = cv2.Canny(image=img_blur, threshold1=25, threshold2=100)
    output = im.fromarray(edges)
    output.save(img_io, format='PNG', quality=100)
    img_content = ContentFile(img_io.getvalue(), 'img.jpg')
    return img_content

def inputimage(image):
    img_io = BytesIO()
    img_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    output = im.fromarray(img_rgb)
    output.save(img_io, format='PNG', quality=100)
    img_content = ContentFile(img_io.getvalue(), 'img.jpg')
    return img_content


    
# img = cv2.imread('test.png') 
# img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
# img_blur = cv2.GaussianBlur(img_gray, (3,3), 0) 
# edges = cv2.Canny(image=img_blur, threshold1=25, threshold2=100) # Canny Edge Detection

