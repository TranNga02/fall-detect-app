package com.example.fall_detect.model;

public class Video {
    private String url;
    private String time;

    public Video() {}

    public Video(String time, String url) {
        this.url = url;
        this.time = time;
    }

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
}
