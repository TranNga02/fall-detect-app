package com.example.fall_detect.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.example.fall_detect.R;
import com.example.fall_detect.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FCMToken"; // Định nghĩa TAG là "FCMToken"
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(binding.navBottom, navController);

        getFcmToken();
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
