package com.vdsl.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vdsl.myapplication.databinding.ActivityHomeBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ActivityHomeBinding homeBinding;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        db = FirebaseFirestore.getInstance();

        ghiDulieu();

        docDulieu();

        LottieAnimationView animationView = findViewById(R.id.animationView);

        animationView.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, ActivityLoginEmail.class);
            startActivity(intent);
        });

        homeBinding.btnLogOut.setOnClickListener(v -> {
            mAuth.signOut();
        });

    }

    private void ghiDulieu() {
        CollectionReference foods = db.collection("foods");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Pizza");
        data1.put("type", "Fast Food");
        data1.put("calories", 285);
        data1.put("ingredients", Arrays.asList("Cheese", "Tomato Sauce", "Dough"));
        foods.document("Pizza").set(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Sushi");
        data2.put("type", "Japanese");
        data2.put("calories", 200);
        data2.put("ingredients", Arrays.asList("Rice", "Fish", "Seaweed"));
        foods.document("Sushi").set(data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "Burger");
        data3.put("type", "Fast Food");
        data3.put("calories", 354);
        data3.put("ingredients", Arrays.asList("Beef Patty", "Lettuce", "Bun", "Cheese"));
        foods.document("Burger").set(data3);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "Salad");
        data4.put("type", "Healthy");
        data4.put("calories", 150);
        data4.put("ingredients", Arrays.asList("Lettuce", "Tomato", "Cucumber", "Dressing"));
        foods.document("Salad").set(data4);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "Pasta");
        data5.put("type", "Italian");
        data5.put("calories", 220);
        data5.put("ingredients", Arrays.asList("Pasta", "Tomato Sauce", "Cheese"));
        foods.document("Pasta").set(data5);
    }


    String TAG = "HomeActivity";
    private void docDulieu() {
        db.collection("foods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}