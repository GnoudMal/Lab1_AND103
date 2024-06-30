package com.vdsl.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vdsl.myapplication.databinding.ActivityHomeBinding;
import com.vdsl.myapplication.databinding.ItemAddCityBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ActivityHomeBinding homeBinding;
    FirebaseFirestore db;

    private RecyclerView rcvCity;
    private CityAdapter cityAdapter;
    private List<CityModel> list;

    CityModel city;
    ItemAddCityBinding dialogBinding;


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

        homeBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        cityAdapter = new CityAdapter(list);
        homeBinding.recyclerView.setAdapter(cityAdapter);

        db = FirebaseFirestore.getInstance();

//        ghiDulieu();

        docDulieu();

//        LottieAnimationView animationView = findViewById(R.id.animationView);

        homeBinding.btnLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, ActivityLoginEmail.class);
            startActivity(intent);
        });

        homeBinding.btnLogOut.setOnClickListener(v -> {
            mAuth.signOut();
        });

        homeBinding.flAddSach.setOnClickListener(v -> {
            openDialog(HomeActivity.this,0);
        });

        cityAdapter.setOnItemLongClick(new CityAdapter.OnItemLongClick() {
            @Override
            public void onItemLongClick(int position) {
                city = list.get(position);
                openDialog(HomeActivity.this,1);
                Log.e("IndexCity", "onItemLongClick: " + city.toString() );
            }
        });

    }

    private  void openDialog(final Context context,final int type){
        dialogBinding = ItemAddCityBinding.inflate(LayoutInflater.from(context));
        Dialog dialog = new Dialog(context);
        dialog.setContentView(dialogBinding.getRoot());

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        dialogBinding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (type == 1 && city != null) {
            dialogBinding.edtCity.setText(city.getName());
            dialogBinding.edtCountry.setText(city.getCountry());
            dialogBinding.edtPopulation.setText(String.valueOf(city.getPopulation()));
        }

        dialogBinding.btnAdd.setOnClickListener(v -> {
            String cityName = dialogBinding.edtCity.getText().toString();
            String country = dialogBinding.edtCountry.getText().toString();
            String population = dialogBinding.edtPopulation.getText().toString();

            if (!cityName.isEmpty() && !country.isEmpty() && !population.isEmpty()) {
                CollectionReference cities = db.collection("cities");

                if (type == 0) {
                    CityModel newCity = new CityModel(cityName, country, Integer.parseInt(population));
                    cities.add(newCity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                docDulieu();
                                Toast.makeText(HomeActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "Error adding document", task.getException());
                            }
                        }
                    });
                } else if (type == 1 && city != null) {
                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("name", cityName);
                    updatedData.put("country", country);
                    updatedData.put("population", Integer.parseInt(population));

                    db.collection("cities").document(city.getId())
                            .update(updatedData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    docDulieu();
                                    Toast.makeText(HomeActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error updating document", e);
                                    Toast.makeText(HomeActivity.this, "Lỗi khi cập nhật tài liệu", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(HomeActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void ghiDulieu () {
        CollectionReference cities = db.collection("cities");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "San Francisco");
        data1.put("state", "CA");
        data1.put("country", "USA");
        data1.put("capital", false);
        data1.put("population", 860000);
        data1.put("regions", Arrays.asList("west_coast", "norcal"));
        cities.document("SF").set(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Los Angeles");
        data2.put("state", "CA");
        data2.put("country", "USA");
        data2.put("capital", false);
        data2.put("population", 3900000);
        data2.put("regions", Arrays.asList("west_coast", "socal"));
        cities.document("LA").set(data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "Washington D.C.");
        data3.put("state", null);
        data3.put("country", "USA");
        data3.put("capital", true);
        data3.put("population", 680000);
        data3.put("regions", Arrays.asList("east_coast"));
        cities.document("DC").set(data3);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "Tokyo");
        data4.put("state", null);
        data4.put("country", "Japan");
        data4.put("capital", true);
        data4.put("population", 9000000);
        data4.put("regions", Arrays.asList("kanto", "honshu"));
        cities.document("TOK").set(data4);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "Beijing");
        data5.put("state", null);
        data5.put("country", "China");
        data5.put("capital", true);
        data5.put("population", 21500000);
        data5.put("regions", Arrays.asList("jingjinji", "hebei"));
        cities.document("BJ").set(data5);
    }

    String TAG = "HomeActivity";
    private void docDulieu () {
        db.collection("cities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CityModel cityData = document.toObject(CityModel.class);
                                cityData.setId(document.getId());
                                list.add(cityData);
                            }
                            cityAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}