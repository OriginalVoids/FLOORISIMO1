package com.example.myapplication.activities.establishment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.activities.establishment.models.Establishment;
import java.util.List;

public class EstablishmentListAdapter extends RecyclerView.Adapter<EstablishmentListAdapter.ViewHolder> {

    private List<Establishment> establishments;
    private OnEstablishmentActionListener listener;
    private static final String MALL_IMAGE = "https://images.ctfassets.net/z78475or6i3d/4ncEpBqUYr2OOpGsYVPv9B/ffd4235f9b0f702e904df616eccfe76d/Urban_Strip_Mall_Retail_to_Housing_Concept_Plan.jpg";
    private static final String OFFICE_IMAGE = "https://i.pinimg.com/736x/bd/d6/f5/bdd6f5247dbea0e5eedf33fe8cc491ee--office-layout-plan-office-floor-plan.jpg";
    private static final String RESTAURANT_IMAGE = "https://th.bing.com/th/id/R.2f7e50ef9157e082f4775217792b39b1?rik=79LiudyrK3VCkQ&pid=ImgRaw&r=0";

    public interface OnEstablishmentActionListener {
        void onEstablishmentView(Establishment establishment);
        void onEstablishmentOptions(Establishment establishment, View anchor);
    }

    public EstablishmentListAdapter(List<Establishment> establishments, OnEstablishmentActionListener listener) {
        this.establishments = establishments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_establishment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Establishment est = establishments.get(position);
        holder.nameTextView.setText(est.getName());
        holder.typeTextView.setText(est.getEstablishmentType());
        String imageUrl = est.getLogoUri();
        if (imageUrl == null) {
            String type = est.getEstablishmentType();
            if ("Mall".equalsIgnoreCase(type)) imageUrl = MALL_IMAGE;
            else if ("Office".equalsIgnoreCase(type) || "Office Building".equalsIgnoreCase(type)) imageUrl = OFFICE_IMAGE;
            else if ("Restaurant".equalsIgnoreCase(type)) imageUrl = RESTAURANT_IMAGE;
        }
        if (imageUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.iconImageView);
        } else {
            holder.iconImageView.setImageResource(android.R.drawable.ic_menu_myplaces);
        }
        holder.optionsButton.setOnClickListener(v -> listener.onEstablishmentOptions(est, v));
        holder.itemView.setOnClickListener(v -> listener.onEstablishmentView(est));
    }

    @Override
    public int getItemCount() {
        return establishments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView typeTextView;
        ImageView iconImageView;
        ImageButton optionsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.establishmentNameTextView);
            typeTextView = itemView.findViewById(R.id.establishmentTypeTextView);
            iconImageView = itemView.findViewById(R.id.establishmentIcon);
            optionsButton = itemView.findViewById(R.id.optionsButton);
        }
    }
}
