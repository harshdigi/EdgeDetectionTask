package com.digitaldealsolution.plotlinetask;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.digitaldealsolution.plotlinetask.Adapter.HistoryAdapter;
import com.digitaldealsolution.plotlinetask.Models.ImageUpload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private ArrayList<ImageUpload> imageUploads;
    DatabaseReference databaseReference;
    LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Images").child(FirebaseAuth.getInstance().getUid());
        imageUploads = new ArrayList<>();
        Dialog dialog = new  Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        lottieAnimationView = findViewById(R.id.empty);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this,LinearLayoutManager.VERTICAL,false){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
               dialog.dismiss();
            }
        });
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String date = dataSnapshot.child("date").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String outputUrl = dataSnapshot.child("outputUrl").getValue(String.class);
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    ImageUpload imageUpload = new ImageUpload(userId,date,imageUrl,outputUrl);
                    imageUploads.add(imageUpload);

                }
                if(!imageUploads.isEmpty()){
                    lottieAnimationView.setVisibility(View.GONE);
                }
                else{
                    lottieAnimationView.setVisibility(View.VISIBLE);
                }
                HistoryAdapter adapter = new HistoryAdapter(imageUploads, HistoryActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}