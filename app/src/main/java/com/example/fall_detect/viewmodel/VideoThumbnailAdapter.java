package com.example.fall_detect.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.fall_detect.R;
import com.example.fall_detect.model.Video;
import com.example.fall_detect.view.activity.PlayVideoActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VideoThumbnailAdapter extends RecyclerView.Adapter<VideoThumbnailAdapter.ViewHolder> {
    Context context;
    ArrayList<Video> videoArrayList;

    public VideoThumbnailAdapter(Context context, ArrayList<Video> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_video_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video video = videoArrayList.get(position);
        Uri videoUri = Uri.parse(video.getUrl());
        holder.tvVideoTime.setText(video.getTime());
        try {
            holder.tvVideoDuration.setText(video.getVideoDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayVideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("selectedVideo", video);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        Glide.with(context)
                .asBitmap()
                .load(videoUri)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.ivThumbnail.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Log.e("TAG", "Failed to generate thumbnail image");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvVideoTime, tvVideoDuration;
        public ImageView ivThumbnail;

        public ViewHolder(View view) {
            super(view);
            tvVideoTime = view.findViewById(R.id.video_time);
            tvVideoDuration = view.findViewById(R.id.video_duration);
            ivThumbnail = view.findViewById(R.id.thumbnail);
        }
    }
}
