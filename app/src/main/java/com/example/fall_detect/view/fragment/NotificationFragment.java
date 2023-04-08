package com.example.fall_detect.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fall_detect.databinding.FragmentNotificationBinding;
import com.example.fall_detect.model.Video;
import com.example.fall_detect.viewmodel.VideoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    private FragmentNotificationBinding binding;
    ArrayList<Video> videoArrayList;
    VideoAdapter videoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);

        View rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        binding.rvVideo.setLayoutManager(new LinearLayoutManager(getContext()));

        videoArrayList = new ArrayList<>();
        videoAdapter = new VideoAdapter(getContext(),videoArrayList);
        binding.rvVideo.setAdapter(videoAdapter);
    }
}