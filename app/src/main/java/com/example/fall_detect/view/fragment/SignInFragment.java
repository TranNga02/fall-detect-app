package com.example.fall_detect.view.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.fall_detect.databinding.FragmentSignInBinding;
import com.example.fall_detect.view.activity.MainActivity;
import com.example.fall_detect.viewmodel.SingInViewModel;
import com.google.android.gms.tasks.Task;

public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        progressDialog = new ProgressDialog(getContext());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SingInViewModel singInViewModel = new ViewModelProvider(this).get(SingInViewModel.class);

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();

                Task<Boolean> signInTask = singInViewModel.signIn(email, password);

                signInTask.addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful() && task.getResult()) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finishAffinity();
                    } else {
                        binding.tvIncorrect.setText("Incorrect email or password.");
                    }
                });
            }
        });
    }
}