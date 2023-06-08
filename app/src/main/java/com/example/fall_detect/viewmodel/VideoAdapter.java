package com.example.fall_detect.viewmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fall_detect.R;
import com.example.fall_detect.model.Video;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    Context context;
    ArrayList<Video> videoArrayList;

    public VideoAdapter(Context context, ArrayList<Video> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video video = videoArrayList.get(position);
        holder.tvDateTime.setText(video.getTime());

        Uri videoUri = Uri.parse(video.getUrl());

        holder.videoView.setVideoURI(videoUri);
        if(position == 0) {
            holder.videoView.start();
            holder.ivThumbnail.setVisibility(View.GONE);
        }

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Video playback is completed, show the thumbnail
                holder.ivThumbnail.setVisibility(View.VISIBLE);
            }
        });

        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.videoView.isPlaying()) {
                    holder.videoView.pause();
                    holder.ivThumbnail.setVisibility(View.VISIBLE);
                } else {
                    holder.videoView.start();
                    holder.ivThumbnail.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public VideoView videoView;
        public TextView tvDateTime;
        public ImageView ivThumbnail;

        public ViewHolder(View view) {
            super(view);
            videoView = view.findViewById(R.id.video_view);
            tvDateTime = view.findViewById(R.id.tv_date_time);
            ivThumbnail = view.findViewById(R.id.thumbnail_image);
        }
    }
}