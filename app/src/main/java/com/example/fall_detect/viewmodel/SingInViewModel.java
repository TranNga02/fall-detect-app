package com.example.fall_detect.viewmodel;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SingInViewModel extends ViewModel {
    private MutableLiveData<String> email, password;

    public LiveData<String> getEmail(){
        if(email == null){
            email = new MutableLiveData<>();
            email.setValue("");
        }
        return email;
    }

    public LiveData<String> getPassword(){
        if(password == null){
            password = new MutableLiveData<>();
            password.setValue("");
        }
        return password;
    }

    public Task<Boolean> signIn(String mEmail, String mPassword){
        TaskCompletionSource<Boolean> signInResult = new TaskCompletionSource<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            signInResult.setResult(true);
                        } else {
                            signInResult.setResult(false);

                        }
                    }
                });

        return signInResult.getTask();
    }
}
