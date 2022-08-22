package com.digitaldealsolution.plotlinetask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.digitaldealsolution.plotlinetask.Api.ApiClient;
import com.digitaldealsolution.plotlinetask.Api.ApiService;
import com.digitaldealsolution.plotlinetask.Models.ImageUpload;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private String[] PERMISSION;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    public  static Uri selectedImageUri;
    String imageUrl;
    FirebaseAuth firebaseAuth;
    MaterialCardView cameraBtn, galleryBtn, urlBtn, historyBtn;
    MaterialButton logoutBtn;

    private FirebaseDatabase rootNode;
    private DatabaseReference rootRefrence;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        rootRefrence = rootNode.getReference("Images");
        cameraBtn = findViewById(R.id.camera_option);
        galleryBtn = findViewById(R.id.gallery_option);
        urlBtn = findViewById(R.id.url_option);
        logoutBtn = findViewById(R.id.logout_btn);
        historyBtn = findViewById(R.id.history_btn);
        PERMISSION = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.MANAGE_DOCUMENTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_MEDIA
        };
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                selectFromCamera();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View v) {
                selectFromGallery();
            }
        });

        urlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFromUrl();
            }
        });
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(intent);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void selectFromUrl() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setTitle("Enter Url & Click Confirm");
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_url_option, null);
        builder.setView(view)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText inputTemp = view.findViewById(R.id.url_txt);
                        String enteredUrl = inputTemp.getText().toString();

                        if(!validateUrl(enteredUrl)){
                           Toast.makeText(getApplicationContext(), "Please enter correct url.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Glide.with(getApplicationContext()).asBitmap().load(enteredUrl).listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    assert e != null;
                                    Toast.makeText(getApplicationContext(),"Image not loaded, Please check Url",Toast.LENGTH_LONG).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    try {
                                        File imageFile = createImageFile();
                                        FileOutputStream fos = new FileOutputStream(imageFile);
                                        resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                        fos.close();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    uploadImages(firebaseAuth.getUid());
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }

                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
    private boolean validateUrl(String url ) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void selectFromGallery() {
        PackageManager pm = getPackageManager();
        int hasPerm = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
        if (hasPerm == PackageManager.PERMISSION_GRANTED) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
        }
        else{
            requestPermissions(PERMISSION,1);
        }
    }
    @SuppressLint("QueryPermissionsNeeded")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void selectFromCamera() {
        PackageManager pm = getPackageManager();
        int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
        if (hasPerm == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ignored) {

                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA);
                }
            }
        }
        else{
            requestPermissions(PERMISSION,1);
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        imageUrl = image.getAbsolutePath();
        return image;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_CAMERA) {
                try {
                  uploadImages(firebaseAuth.getUid());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PICK_IMAGE_GALLERY) {
                selectedImageUri = data.getData();
                try {
                    imageUrl = getRealPathFromURI1(selectedImageUri);
                    uploadImages(firebaseAuth.getUid());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI1(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void uploadImages(String user) {
        File file  = new File(imageUrl);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image_url", file.getName(),requestFile);
        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"),user);
        Retrofit retrofit = ApiClient.getRetrofit();
        ApiService apiService = retrofit.create(ApiService.class);
        Dialog dialog = new  Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        Call<ImageUpload> call = apiService.uploadImage(body,userId);

        call.enqueue(new Callback<ImageUpload>() {
            @Override
            public void onResponse(Call<ImageUpload> call, Response<ImageUpload> response) {
                Toast.makeText(MainActivity.this,"uploaded",Toast.LENGTH_SHORT);
                DatabaseReference databaseReference;
                databaseReference = rootRefrence.child(user).push();
                databaseReference.setValue(response.body());
                Intent intent = new Intent(getApplicationContext(), OutputActivity.class);
                ImageUpload imageUpload = response.body();
                intent.putExtra("images", (Parcelable) imageUpload);
                startActivity(intent);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ImageUpload> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Not uploaded",Toast.LENGTH_SHORT);
            }
        });
    }
}