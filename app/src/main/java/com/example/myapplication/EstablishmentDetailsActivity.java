package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

public class EstablishmentDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_establishment_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerBackground), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Establishment est = (Establishment) getIntent().getSerializableExtra("establishment");

        if (est != null) {
            // Header
            TextView nameHeader = findViewById(R.id.detailEstNameHeader);
            nameHeader.setText(est.getName());

            // Left Pane
            TextView initialsTextView = findViewById(R.id.detailInitialsTextView);
            ImageView logoImageView = findViewById(R.id.detailLogoImageView);
            TextView nameLeft = findViewById(R.id.detailEstNameLeft);

            nameLeft.setText(est.getName());
            initialsTextView.setText(est.getInitials());

            if (est.getLogoUri() != null) {
                try {
                    logoImageView.setImageURI(Uri.parse(est.getLogoUri()));
                    logoImageView.setVisibility(View.VISIBLE);
                    initialsTextView.setVisibility(View.GONE);
                } catch (Exception e) {
                    logoImageView.setVisibility(View.GONE);
                    initialsTextView.setVisibility(View.VISIBLE);
                }
            } else {
                logoImageView.setVisibility(View.GONE);
                initialsTextView.setVisibility(View.VISIBLE);
            }

            // Right Pane
            TextView typeTextView = findViewById(R.id.textType);
            TextView locationTextView = findViewById(R.id.textLocation);
            TextView floorsTextView = findViewById(R.id.textFloors);
            TextView tilesTextView = findViewById(R.id.textTiles);
            TextView weaknessesTextView = findViewById(R.id.textWeaknesses);
            TextView typesTextView = findViewById(R.id.textTileTypes);
            TextView suppliersTextView = findViewById(R.id.textSuppliers);

            typeTextView.setText("Type: " + est.getEstablishmentType());
            locationTextView.setText("Country: " + est.getCountry());
            floorsTextView.setText("Number of Floors: " + est.getNumberOfFloors());
            tilesTextView.setText("Tiles Provided: " + est.getNumberOfTiles());

            if (est.getStructuralWeaknesses() != null && !est.getStructuralWeaknesses().isEmpty()) {
                weaknessesTextView.setText(TextUtils.join(", ", est.getStructuralWeaknesses()));
            } else {
                weaknessesTextView.setText("None reported");
            }

            if (est.getPreferredTileTypes() != null) {
                typesTextView.setText("Preferred Types: " + TextUtils.join(", ", est.getPreferredTileTypes()));
            }

            StringBuilder suppliersBuilder = new StringBuilder("Preferred Suppliers:\n");
            Map<String, String> suppliersMap = est.getPreferredSuppliers();
            if (suppliersMap != null && !suppliersMap.isEmpty()) {
                for (Map.Entry<String, String> entry : suppliersMap.entrySet()) {
                    suppliersBuilder.append(" • ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                suppliersTextView.setText(suppliersBuilder.toString().trim());
            } else {
                suppliersTextView.setText("No preferred suppliers set");
            }
        }
    }
}
