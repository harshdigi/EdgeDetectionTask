package com.digitaldealsolution.plotlinetask.Adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldealsolution.plotlinetask.Models.ImageUpload;
import com.digitaldealsolution.plotlinetask.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    ArrayList<ImageUpload> imageUploads = new ArrayList<>();
    Context context;
    long reference;
    String base = "https://plotlinetask.asquarestudio.in";

    public HistoryAdapter(ArrayList<ImageUpload> imageUploads, Context context) {
        this.imageUploads = imageUploads;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        v = layoutInflater.inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.outputTxt.setText("Output " + position);
        holder.inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(imageUploads.get(position).getImageUrl(), imageUploads.get(position).getDate());
            }
        });
        holder.outputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(imageUploads.get(position).getOutputUrl(), imageUploads.get(position).getDate());
            }
        });
        holder.dateTxt.setText(imageUploads.get(position).getDate().substring(0, 10));
    }

    private void downloadImage(String outputUrl, String date) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(base + outputUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, date.substring(0, 10) + ".png");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        long reference = manager.enqueue(request);
        Toast.makeText(context, "File added for Downloading, Check download folder", Toast.LENGTH_LONG).show(); //not needed, but maybe usefull
    }

    @Override
    public int getItemCount() {
        return imageUploads.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView outputTxt, dateTxt;
        Button inputBtn, outputBtn;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            outputTxt = itemView.findViewById(R.id.history_name);
            dateTxt = itemView.findViewById(R.id.date);
            inputBtn = itemView.findViewById(R.id.download_input);
            outputBtn = itemView.findViewById(R.id.download_output);


        }
    }
}
