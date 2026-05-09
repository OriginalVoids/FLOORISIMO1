package com.example.myapplication.activities.establishment;

import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.myapplication.R;
import com.example.myapplication.activities.establishment.models.Establishment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Locale;
import java.util.Map;

public class EstablishmentDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Establishment currentEstablishment;
    private TextToSpeech tts;

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
        currentEstablishment = (Establishment) getIntent().getSerializableExtra("establishment");
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });
        if (currentEstablishment != null) {
            TextView nameHeader = findViewById(R.id.detailEstNameHeader);
            nameHeader.setText(currentEstablishment.getName());
            TextView initialsTextView = findViewById(R.id.detailInitialsTextView);
            ImageView logoImageView = findViewById(R.id.detailLogoImageView);
            TextView nameLeft = findViewById(R.id.detailEstNameLeft);
            nameLeft.setText(currentEstablishment.getName());
            initialsTextView.setText(currentEstablishment.getInitials());
            if (currentEstablishment.getLogoUri() != null) {
                try {
                    logoImageView.setImageURI(Uri.parse(currentEstablishment.getLogoUri()));
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
            TextView typeTextView = findViewById(R.id.textType);
            TextView locationTextView = findViewById(R.id.textLocation);
            TextView floorsTextView = findViewById(R.id.textFloors);
            TextView tilesTextView = findViewById(R.id.textTiles);
            TextView weaknessesTextView = findViewById(R.id.textWeaknesses);
            TextView typesTextView = findViewById(R.id.textTileTypes);
            TextView suppliersTextView = findViewById(R.id.textSuppliers);
            typeTextView.setText("Type: " + currentEstablishment.getEstablishmentType());
            locationTextView.setText("Country: " + currentEstablishment.getCountry());
            floorsTextView.setText("Number of Floors: " + currentEstablishment.getNumberOfFloors());
            tilesTextView.setText("Tiles Provided: " + currentEstablishment.getNumberOfTiles());
            if (currentEstablishment.getStructuralWeaknesses() != null && !currentEstablishment.getStructuralWeaknesses().isEmpty()) {
                weaknessesTextView.setText(TextUtils.join(", ", currentEstablishment.getStructuralWeaknesses()));
            } else {
                weaknessesTextView.setText("None reported");
            }
            if (currentEstablishment.getPreferredTileTypes() != null) {
                typesTextView.setText("Preferred Types: " + TextUtils.join(", ", currentEstablishment.getPreferredTileTypes()));
            }
            StringBuilder suppliersBuilder = new StringBuilder("Preferred Suppliers:\n");
            Map<String, String> suppliersMap = currentEstablishment.getPreferredSuppliers();
            if (suppliersMap != null && !suppliersMap.isEmpty()) {
                for (Map.Entry<String, String> entry : suppliersMap.entrySet()) {
                    suppliersBuilder.append(" • ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                suppliersTextView.setText(suppliersBuilder.toString().trim());
            } else {
                suppliersTextView.setText("No preferred suppliers set");
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
            Button btnSpeak = findViewById(R.id.btnSpeakDetails);
            btnSpeak.setOnClickListener(v -> speakEstablishmentDetails());
        }
    }

    private void speakEstablishmentDetails() {
        if (currentEstablishment == null || tts == null) return;
        String text = "Establishment name is " + currentEstablishment.getName() + 
                     ". It is a " + currentEstablishment.getEstablishmentType() + 
                     " located in " + currentEstablishment.getCountry() + 
                     ". It has " + currentEstablishment.getNumberOfFloors() + " floors and " + 
                     currentEstablishment.getNumberOfTiles() + " total tiles.";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (currentEstablishment != null) {
            LatLng location = new LatLng(currentEstablishment.getLatitude(), currentEstablishment.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(currentEstablishment.getName()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
