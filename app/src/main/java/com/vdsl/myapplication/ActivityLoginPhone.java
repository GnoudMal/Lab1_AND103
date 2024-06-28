package com.vdsl.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vdsl.myapplication.databinding.ActivityLoginPhoneBinding;

import java.util.concurrent.TimeUnit;

public class ActivityLoginPhone extends AppCompatActivity {
    private ActivityLoginPhoneBinding binding;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d("PhoneActivity", "onVerificationCompleted:" + credential);
                Toast.makeText(ActivityLoginPhone.this, "Verification completed", Toast.LENGTH_SHORT).show();
                binding.edtOTP.setText(credential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w("PhoneActivity", "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(ActivityLoginPhone.this, "Invalid request", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(ActivityLoginPhone.this, "Too many requests, please wait", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                    Toast.makeText(ActivityLoginPhone.this, "reCAPTCHA verification attempted with null Activity", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        binding.btnGetOTP.setOnClickListener(view -> {
            String phoneNumber = binding.edtPhoneNumber.getText().toString();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            } else if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
            } else {
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNumber)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(this)                 // (optional) Activity for callback binding
                                // If no activity is passed, reCAPTCHA verification can not be used.
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });
        binding.btnLogin.setOnClickListener(v -> {
            String otp = binding.edtOTP.getText().toString();
            if (otp.isEmpty()) {
                Toast.makeText(ActivityLoginPhone.this, "Tàna", Toast.LENGTH_SHORT).show();
            } else {
                credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                signInWithPhoneAuthCredential(credential);
            }
        });

    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberPattern = "^\\+84[0-9]{9,10}$";
        return phoneNumber.matches(phoneNumberPattern);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d("PhoneActivity", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(ActivityLoginPhone.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ActivityLoginPhone.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Log.w("PhoneActivity", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(ActivityLoginPhone.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}