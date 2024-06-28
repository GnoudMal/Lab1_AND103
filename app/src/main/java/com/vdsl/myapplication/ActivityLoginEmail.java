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
import com.vdsl.myapplication.databinding.ActivityLoginEmailBinding;

public class ActivityLoginEmail extends AppCompatActivity {

    private ActivityLoginEmailBinding loginEmailBinding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginEmailBinding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(loginEmailBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle ex = intent.getExtras();
            if (ex != null) {
                loginEmailBinding.edtUsername.setText(ex.getString("email"));
                loginEmailBinding.edtPassword.setText(ex.getString("password"));
            }
        }

        loginEmailBinding.btnLogin.setOnClickListener(v -> {
            String email = loginEmailBinding.edtUsername.getText().toString();
            String password = loginEmailBinding.edtPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(ActivityLoginEmail.this, "Không được bỏ trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(ActivityLoginEmail.this, "Địa chỉ email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(ActivityLoginEmail.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("Main", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(ActivityLoginEmail.this, "Đăng Nhập Thành Công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityLoginEmail.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                Log.w("Main", "signInWithEmail:failure", task.getException());
                                Toast.makeText(ActivityLoginEmail.this, "Sai Tài Khoản Hoặc Mật khẩu!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        loginEmailBinding.btnRegister.setOnClickListener(v -> {
            Intent intent1 = new Intent(ActivityLoginEmail.this, ActivitySignUp.class);
            startActivity(intent1);
        });

        loginEmailBinding.txtForgetPass.setOnClickListener(v -> {
            String email = loginEmailBinding.edtUsername.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(ActivityLoginEmail.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ActivityLoginEmail.this, "Kiểm tra gmail để đặt lại mật khẩu!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ActivityLoginEmail.this, "Lỗi khi gửi mail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
