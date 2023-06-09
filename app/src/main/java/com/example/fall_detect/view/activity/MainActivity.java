package com.example.fall_detect.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.fall_detect.R;
import com.example.fall_detect.databinding.ActivityMainBinding;
import com.example.fall_detect.model.Video;
import com.example.fall_detect.viewmodel.VideoThumbnailAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FCMToken"; // Định nghĩa TAG là "FCMToken"
    private ActivityMainBinding binding;
    ArrayList<Video> videoArrayList;
    VideoThumbnailAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getFcmToken();

        String userId = FirebaseAuth.getInstance().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference subCollectionRef = rootRef.child("users").child(userId).child("videos");

        ValueEventListener subCollectionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                videoArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Video video = new Video(snapshot.child("time").getValue(String.class),
                            snapshot.child("url").getValue(String.class));
                    videoArrayList.add(0,video);
                }
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.toException());
            }
        };

        subCollectionRef.addValueEventListener(subCollectionListener);

        binding.rvVideo.setLayoutManager(new LinearLayoutManager(this));

        videoArrayList = new ArrayList<>();
        videoAdapter = new VideoThumbnailAdapter(this,videoArrayList);
        binding.rvVideo.setAdapter(videoAdapter);

        binding.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sign_out:
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent( MainActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finishAffinity();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.menu_sign_out);
                popupMenu.show();
            }
        });
    }

    public void getFcmToken() {

        // Lấy FCM token của thiết bị
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String fcmToken = task.getResult();
                        // Lưu trữ FCM token lên Firebase Realtime Database hoặc Firestore
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Lấy ID của người dùng hiện tại
                        FirebaseDatabase.getInstance().getReference("/users/" + currentUserId + "/deviceTokens")
                                .setValue(fcmToken)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "Lưu trữ FCM token thành công");
                                    } else {
                                        Log.e(TAG, "Lưu trữ FCM token thất bại", task1.getException());
                                    }
                                });
                    } else {
                        Log.e(TAG, "Lấy FCM token thất bại", task.getException());
                    }
                });
    }
}
