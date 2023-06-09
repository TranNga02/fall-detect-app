package com.example.fall_detect.model;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.IOException;

public class Video implements Parcelable {
    private String url;
    private String time;
    private String duration;

    public Video() {}

    public Video(String time, String url) {
        this.url = url;
        this.time = time;
    }

    protected Video(Parcel in) {
        url = in.readString();
        time = in.readString();
        duration = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVideoDuration() throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this.url);

        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long durationInMillis = Long.parseLong(duration);

        retriever.release();

        long seconds = (durationInMillis / 1000) % 60;
        long minutes = (durationInMillis / (1000 * 60)) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(time);
        dest.writeString(duration);
    }
}
