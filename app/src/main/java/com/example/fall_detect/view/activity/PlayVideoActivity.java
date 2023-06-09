package com.example.fall_detect.view.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.fall_detect.R;
import com.example.fall_detect.model.Video;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.checkerframework.checker.units.qual.C;

import java.io.File;

public class PlayVideoActivity extends AppCompatActivity {

    Video video;
    PlayerView playerView;
    SimpleExoPlayer player;
    ConcatenatingMediaSource concatenatingMediaSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        playerView = findViewById(R.id.video_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            video = bundle.getParcelable("selectedVideo");
        }

        playVideo();
    }

    private void playVideo(){
        String uri = video.getUrl();
        player = new SimpleExoPlayer.Builder(this).build();

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "app"));

        concatenatingMediaSource = new ConcatenatingMediaSource();
        new File(String.valueOf(video));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(uri));
        concatenatingMediaSource.addMediaSource(mediaSource);

        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        player.seekTo(0);
        playError();

    }

    private void playError(){
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(PlayVideoActivity.this, "Video Playing Error", Toast.LENGTH_SHORT);
            }

        });
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}