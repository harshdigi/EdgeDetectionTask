package com.digitaldealsolution.plotlinetask.Api;

import com.digitaldealsolution.plotlinetask.Models.ImageUpload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {


        @Multipart
        @POST("/upload/")
        Call<ImageUpload> uploadImage(@Part MultipartBody.Part image_url,
                                      @Part("userId") RequestBody requestBody);


}
