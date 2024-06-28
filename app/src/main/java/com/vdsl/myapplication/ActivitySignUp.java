package com.vdsl.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vdsl.myapplication.databinding.ActivitySignUpBinding;

public class ActivitySignUp extends AppCompatActivity {

    private ActivitySignUpBinding signUpBinding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();

//        signUpBinding.btnBack.setOnClickListener(v -> {
//            finish();
//        });

        signUpBinding.btnsignup.setOnClickListener(v -> {
            String email = signUpBinding.edemail.getText().toString();
            String password = signUpBinding.edpassword.getText().toString();
            String confirmPassword = signUpBinding.edrppassword.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ActivitySignUp.this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(ActivitySignUp.this, "Mật khẩu không khớp nhau!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(ActivitySignUp.this, "Địa chỉ email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6 || !Character.isUpperCase(password.charAt(0))) {
                Toast.makeText(ActivitySignUp.this, "Mật khẩu phải có ít nhất 6 kí tự và viết hoa chữ cái đầu tiên!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("Main", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(ActivitySignUp.this, ActivityLoginEmail.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                startActivity(intent);
                                Toast.makeText(ActivitySignUp.this, "Đăng Ký Thành Công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w("Main", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(ActivitySignUp.this, "Đăng Ký Thất Bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
        signUpBinding.txtLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitySignUp.this,ActivityLoginEmail.class);
            startActivity(intent);
        });
    }



    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
