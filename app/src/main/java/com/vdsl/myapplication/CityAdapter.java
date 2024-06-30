package com.vdsl.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.vdsl.myapplication.databinding.ItemCityBinding;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private List<CityModel> list;
    private FirebaseFirestore db;

    public CityAdapter(List<CityModel> list) {
        this.list = list;
        db = FirebaseFirestore.getInstance();
    }

    public interface OnItemLongClick {
        void onItemLongClick(int position);
    }

    private CityAdapter.OnItemLongClick mListener;

    public void setOnItemLongClick(CityAdapter.OnItemLongClick listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCityBinding binding = ItemCityBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CityAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CityModel city = list.get(position);
        holder.binding.txtCityName.setText("Thành Phố: " + city.getName());
        holder.binding.txtCountry.setText("Quốc Gia: " + city.getCountry());
        holder.binding.txtPopulation.setText("Dân số: " + city.getPopulation());

        holder.binding.btnDelete.setOnClickListener(v -> {
            String cityId = city.getId();
            Log.e("adu", "onBindViewHolder: " + cityId );
            db.collection("cities").document(cityId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                        Toast.makeText(holder.itemView.getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(holder.itemView.getContext(), "Lỗi khi xóa tài liệu", Toast.LENGTH_SHORT).show();
                    });
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (mListener != null) {
                mListener.onItemLongClick(holder.getAdapterPosition());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemCityBinding binding;

        public ViewHolder(@NonNull ItemCityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
