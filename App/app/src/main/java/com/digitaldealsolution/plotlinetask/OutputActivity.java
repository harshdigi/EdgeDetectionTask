package com.digitaldealsolution.plotlinetask;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.digitaldealsolution.plotlinetask.Models.ImageUpload;

public class OutputActivity extends AppCompatActivity {
    ImageUpload outputModel;
    ImageView inputImage, outputImage;
    Button inputBtn, outputBtn;

    String base_url ="https://plotlinetask.asquarestudio.in";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        inputImage = findViewById(R.id.input_image);
        outputImage = findViewById(R.id.output_image);
        outputModel = (ImageUpload) getIntent().getParcelableExtra("images");
        inputBtn = findViewById(R.id.download_input_img);
        outputBtn = findViewById(R.id.download_output_img);

        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(outputModel.getImageUrl(),outputModel.getDate());
            }
        });
        outputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(outputModel.getOutputUrl(),outputModel.getDate());
            }
        });
        Dialog dialog = new  Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Glide.with(OutputActivity.this)
                .load(base_url+outputModel.getOutputUrl())
                .placeholder(R.drawable.ic_placeholder)
                .dontAnimate()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        inputImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        dialog.dismiss();
                        inputImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(outputImage);
        Glide.with(OutputActivity.this)
                .load(base_url+outputModel.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .dontAnimate()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        outputImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        dialog.dismiss();
                        outputImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(inputImage);


    }

    private void downloadImage(String outputUrl, String date) {
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(base_url+outputUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir(OutputActivity.this, Environment.DIRECTORY_DOWNLOADS,date.substring(0,10)+".png");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        long reference = manager.enqueue(request);
        Toast.makeText(OutputActivity.this, "File added for downloading, check download folder", Toast.LENGTH_LONG).show(); //not needed, but maybe usefull
    }

}