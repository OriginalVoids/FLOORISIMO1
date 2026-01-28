package com.example.myapplication.activities.establishment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;
import java.util.Map;

public class EstablishmentAdapter extends RecyclerView.Adapter<EstablishmentAdapter.ViewHolder> {

    private List<Establishment> establishments;

    public EstablishmentAdapter(List<Establishment> establishments) {
        this.establishments = establishments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_establishment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Establishment est = establishments.get(position);
        
        // Reset state (ensure front is visible)
        holder.cardFront.setVisibility(View.VISIBLE);
        holder.cardBack.setVisibility(View.INVISIBLE);
        holder.cardFront.setRotationY(0);
        holder.cardBack.setRotationY(-180);
        holder.isFlipped = false;

        holder.nameFront.setText(est.getName());
        holder.nameBack.setText(est.getName());
        holder.typeTag.setText(est.getEstablishmentType());
        holder.floorsTilesInfo.setText("Floors: " + est.getNumberOfFloors() + " | Tiles: " + est.getNumberOfTiles());
        holder.countryInfo.setText(est.getCountry());
        
        if (est.getStructuralWeaknesses() != null && !est.getStructuralWeaknesses().isEmpty()) {
            holder.weaknessesInfo.setText(TextUtils.join(", ", est.getStructuralWeaknesses()));
        } else {
            holder.weaknessesInfo.setText("None");
        }

        holder.tileTypesInfo.setText(TextUtils.join(", ", est.getPreferredTileTypes()));

        StringBuilder suppliers = new StringBuilder();
        for (Map.Entry<String, String> entry : est.getPreferredSuppliers().entrySet()) {
            suppliers.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        holder.suppliersInfo.setText(suppliers.toString().trim());

        holder.cardContainer.setOnClickListener(v -> {
            flipCard(holder);
        });
    }

    private void flipCard(ViewHolder holder) {
        float startRotation = holder.isFlipped ? 180 : 0;
        float endRotation = holder.isFlipped ? 0 : 180;

        holder.cardContainer.animate()
                .rotationY(endRotation)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        // Halfway through rotation, swap visibility
                    }
                })
                .setUpdateListener(animation -> {
                    float rotation = (float) animation.getAnimatedValue();
                    if (rotation >= 90 && !holder.isMidway) {
                        holder.isMidway = true;
                        if (holder.isFlipped) {
                            holder.cardBack.setVisibility(View.INVISIBLE);
                            holder.cardFront.setVisibility(View.VISIBLE);
                        } else {
                            holder.cardFront.setVisibility(View.INVISIBLE);
                            holder.cardBack.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .withEndAction(() -> {
                    holder.isFlipped = !holder.isFlipped;
                    holder.isMidway = false;
                })
                .start();
        
        // Ensure child views aren't mirrored
        if (!holder.isFlipped) {
            holder.cardBack.setRotationY(180);
        } else {
            holder.cardFront.setRotationY(180);
        }
    }

    @Override
    public int getItemCount() {
        return establishments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardContainer;
        CardView cardFront, cardBack;
        TextView nameFront, nameBack, typeTag, floorsTilesInfo, countryInfo, weaknessesInfo, tileTypesInfo, suppliersInfo;
        boolean isFlipped = false;
        boolean isMidway = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            cardFront = itemView.findViewById(R.id.cardFront);
            cardBack = itemView.findViewById(R.id.cardBack);
            nameFront = itemView.findViewById(R.id.establishmentNameFront);
            nameBack = itemView.findViewById(R.id.establishmentNameBack);
            typeTag = itemView.findViewById(R.id.establishmentTypeTag);
            floorsTilesInfo = itemView.findViewById(R.id.floorsTilesInfo);
            countryInfo = itemView.findViewById(R.id.countryInfo);
            weaknessesInfo = itemView.findViewById(R.id.weaknessesInfo);
            tileTypesInfo = itemView.findViewById(R.id.tileTypesInfo);
            suppliersInfo = itemView.findViewById(R.id.suppliersInfo);
            
            // Set camera distance to avoid clipping during rotation
            float scale = itemView.getContext().getResources().getDisplayMetrics().density;
            cardContainer.setCameraDistance(8000 * scale);
        }
    }
}
