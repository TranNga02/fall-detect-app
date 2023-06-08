package com.example.fall_detect.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.example.fall_detect.R;
import com.example.fall_detect.databinding.FragmentUploadVideoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadVideoFragment extends Fragment {
    public static final int PICK_VIEW = 1;
    private FragmentUploadVideoBinding binding;
    private ProgressDialog progressDialog;
    private Uri videoUri;
    private MediaController mediaController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadVideoBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        progressDialog = new ProgressDialog(getContext());
        mediaController = new MediaController(getContext());
        binding.videoView.setMediaController(mediaController);
        binding.videoView.start();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadVideoToFireStorage();
            }
        });

        binding.btnChoseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_VIEW);
            }
        });

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadVideoToFireStorage();
                videoUri = null;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_VIEW || resultCode == RESULT_OK
                || data!=null || data.getData()!=null){
            videoUri = data.getData();
            binding.videoView.setVideoURI(videoUri);
        }
    }

    private void UploadVideoToFireStorage() {
        if(videoUri != null){
            // Get a FirebaseStorage instance
            FirebaseStorage storage = FirebaseStorage.getInstance();
            Date date = new Date();

            // Create a storage reference to the video file
            String videoTitle = "";
            Cursor cursor = getActivity().getContentResolver().query(videoUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int titleIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (titleIndex >= 0) {
                    videoTitle = cursor.getString(titleIndex);
                }
                cursor.close();
            }

            StorageReference videoRef = storage.getReference().child(videoTitle);

            // Create a new UploadTask to upload the video file
            UploadTask uploadTask = videoRef.putFile(videoUri);

            // Set up a listener for the upload progress
            uploadTask.addOnProgressListener(taskSnapshot -> {
                binding.tvResult.setText("Uploading");
            }).addOnPausedListener(taskSnapshot -> {
                binding.tvResult.setText("Upload is paused");
            }).addOnFailureListener(exception -> {
                binding.tvResult.setText("Failed to upload video" + exception);
                Log.e("DEBUG", "Failed to upload video", exception);
            }).addOnSuccessListener(taskSnapshot -> {
                Log.d("DEBUG", "Video uploaded successfully");

                // Get the download URL of the uploaded video
                videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String videoUrl = uri.toString();
                    Log.d("DEBUG", "Video URL: " + videoUrl);

                    // Do something with the video URL, like storing it in a Firestore document or displaying it in a VideoView

                    UpLoadVideoInfoToRealTimeDB(date, videoUrl);

                }).addOnFailureListener(exception -> {
                    Log.e("DEBUG", "Failed to get download URL for video", exception);
                });
            });
        }
        else{
            binding.tvResult.setText("All fields are required");
        }
    }

    private void UpLoadVideoInfoToRealTimeDB(Date date, String videoUrl) {
        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference parentRef = database.getReference("users");
        DatabaseReference childRef = parentRef.child(userId).child("videos");

        Map<String, Object> data = new HashMap<>();
        data.put("time", date.toString());
        data.put("url", videoUrl);

        childRef.push().setValue(data);
        Log.d("DEBUG", "Done");
        binding.tvResult.setText("Upload successful");
    }
}