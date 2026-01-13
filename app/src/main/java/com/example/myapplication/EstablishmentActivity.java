package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstablishmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EstablishmentListAdapter adapter;
    private List<Establishment> establishmentList;
    
    // UI elements for the Detail Pane
    private LinearLayout detailPane;
    private TextView placeholderTextView;
    private ImageView paneLogoImageView, panePlanImageView;
    private TextView paneInitialsTextView, paneEstNameLeft;
    private TextView paneTextType, paneTextLocation, paneTextFloors, paneTextTiles;
    private TextView paneTextWeaknesses, paneTextTileTypes, paneTextSuppliers;

    private static final String MALL_IMAGE = "https://images.ctfassets.net/z78475or6i3d/4ncEpBqUYr2OOpGsYVPv9B/ffd4235f9b0f702e904df616eccfe76d/Urban_Strip_Mall_Retail_to_Housing_Concept_Plan.jpg";
    private static final String OFFICE_IMAGE = "https://i.pinimg.com/736x/bd/d6/f5/bdd6f5247dbea0e5eedf33fe8cc491ee--office-layout-plan-office-floor-plan.jpg";
    private static final String RESTAURANT_IMAGE = "https://th.bing.com/th/id/R.2f7e50ef9157e082f4775217792b39b1?rik=79LiudyrK3VCkQ&pid=ImgRaw&r=0";

    private final ActivityResultLauncher<Intent> createLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Establishment newEst = (Establishment) result.getData().getSerializableExtra("new_establishment");
                    if (newEst != null) {
                        establishmentList.add(newEst);
                        adapter.notifyItemInserted(establishmentList.size() - 1);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_establishment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        // Initialize Detail Pane Views
        detailPane = findViewById(R.id.detailPane);
        placeholderTextView = findViewById(R.id.placeholderTextView);
        paneLogoImageView = findViewById(R.id.paneLogoImageView);
        panePlanImageView = findViewById(R.id.panePlanImageView);
        paneInitialsTextView = findViewById(R.id.paneInitialsTextView);
        paneEstNameLeft = findViewById(R.id.paneEstNameLeft);
        paneTextType = findViewById(R.id.paneTextType);
        paneTextLocation = findViewById(R.id.paneTextLocation);
        paneTextFloors = findViewById(R.id.paneTextFloors);
        paneTextTiles = findViewById(R.id.paneTextTiles);
        paneTextWeaknesses = findViewById(R.id.paneTextWeaknesses);
        paneTextTileTypes = findViewById(R.id.paneTextTileTypes);
        paneTextSuppliers = findViewById(R.id.paneTextSuppliers);

        recyclerView = findViewById(R.id.establishmentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        establishmentList = new ArrayList<>();
        establishmentList.add(createDummyEstablishment("Grand Plaza Mall", "Mall", "USA", 5, 2000, Arrays.asList("Roof leaks")));
        establishmentList.add(createDummyEstablishment("Skyline Towers", "Office Building", "Canada", 42, 15000, new ArrayList<>()));
        establishmentList.add(createDummyEstablishment("The Gourmet Bistro", "Restaurant", "Italy", 2, 450, Arrays.asList("Old pipes")));
        establishmentList.add(createDummyEstablishment("Central Station", "Office Building", "UK", 12, 8000, Arrays.asList("Electrical issues")));
        establishmentList.add(createDummyEstablishment("Ocean View Resort", "Mall", "Bahamas", 4, 3200, Arrays.asList("Salt erosion")));
        establishmentList.add(createDummyEstablishment("Tech Valley Hub", "Office Building", "Germany", 8, 5500, Arrays.asList("Foundation settling")));
        establishmentList.add(createDummyEstablishment("Mountain Peak Lodge", "Restaurant", "Switzerland", 3, 900, Arrays.asList("Insulation gaps")));
        establishmentList.add(createDummyEstablishment("City Square Mall", "Mall", "Australia", 6, 7200, Arrays.asList("Skylight cracks")));

        adapter = new EstablishmentListAdapter(establishmentList, new EstablishmentListAdapter.OnEstablishmentActionListener() {
            @Override
            public void onEstablishmentView(Establishment establishment) {
                showEstablishmentDetails(establishment);
            }

            @Override
            public void onEstablishmentOptions(Establishment establishment, View anchor) {
                showOptionsPopup(establishment, anchor);
            }
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton addFab = findViewById(R.id.addEstablishmentFab);
        addFab.setOnClickListener(v -> {
            Intent intent = new Intent(EstablishmentActivity.this, CreateEstablishmentActivity.class);
            createLauncher.launch(intent);
        });
    }

    private void showEstablishmentDetails(Establishment est) {
        placeholderTextView.setVisibility(View.GONE);
        detailPane.setVisibility(View.VISIBLE);

        paneEstNameLeft.setText(est.getName());
        paneInitialsTextView.setText(est.getInitials());

        // Handling Logo vs Initials independently
        if (est.getLogoUri() != null) {
            try {
                paneLogoImageView.setImageURI(Uri.parse(est.getLogoUri()));
                paneLogoImageView.setVisibility(View.VISIBLE);
                paneInitialsTextView.setVisibility(View.GONE);
            } catch (Exception e) {
                paneLogoImageView.setVisibility(View.GONE);
                paneInitialsTextView.setVisibility(View.VISIBLE);
            }
        } else {
            paneLogoImageView.setVisibility(View.GONE);
            paneInitialsTextView.setVisibility(View.VISIBLE);
        }

        // Loading the Establishment Plan Image based on type separately
        String planUrl = null;
        if ("Mall".equalsIgnoreCase(est.getEstablishmentType())) planUrl = MALL_IMAGE;
        else if ("Office".equalsIgnoreCase(est.getEstablishmentType()) || "Office Building".equalsIgnoreCase(est.getEstablishmentType())) planUrl = OFFICE_IMAGE;
        else if ("Restaurant".equalsIgnoreCase(est.getEstablishmentType())) planUrl = RESTAURANT_IMAGE;

        if (planUrl != null) {
            Glide.with(this).load(planUrl).into(panePlanImageView);
        }

        paneTextType.setText("Type: " + est.getEstablishmentType());
        paneTextLocation.setText("Country: " + est.getCountry());
        paneTextFloors.setText("Floors: " + est.getNumberOfFloors());
        paneTextTiles.setText("Total Tiles: " + est.getNumberOfTiles());

        if (est.getStructuralWeaknesses() != null && !est.getStructuralWeaknesses().isEmpty()) {
            paneTextWeaknesses.setText(TextUtils.join(", ", est.getStructuralWeaknesses()));
        } else {
            paneTextWeaknesses.setText("None reported");
        }

        paneTextTileTypes.setText("Types: " + TextUtils.join(", ", est.getPreferredTileTypes()));

        StringBuilder suppliers = new StringBuilder("Preferred Suppliers:\n");
        for (Map.Entry<String, String> entry : est.getPreferredSuppliers().entrySet()) {
            suppliers.append(" • ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        paneTextSuppliers.setText(suppliers.toString().trim());
    }

    private void showOptionsPopup(Establishment est, View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Edit Info");
        popup.getMenu().add("Order New Tiles");
        popup.getMenu().add("Delete");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Order New Tiles")) {
                startActivity(new Intent(this, OrderTilesActivity.class));
                return true;
            } else if (item.getTitle().equals("Edit Info")) {
                Intent intent = new Intent(this, CreateEstablishmentActivity.class);
                intent.putExtra("edit_mode", true);
                intent.putExtra("establishment", est);
                startActivity(intent);
                return true;
            } else if (item.getTitle().equals("Delete")) {
                int pos = establishmentList.indexOf(est);
                establishmentList.remove(pos);
                adapter.notifyItemRemoved(pos);
                if (detailPane.getVisibility() == View.VISIBLE) {
                    detailPane.setVisibility(View.GONE);
                    placeholderTextView.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        });
        popup.show();
    }

    private Establishment createDummyEstablishment(String name, String type, String country, int floors, int tiles, List<String> weaknesses) {
        List<String> tileTypes = Arrays.asList("Sensing", "Energy Producing");
        Map<String, String> suppliers = new HashMap<>();
        suppliers.put("Sensing", "TechFlow Inc.");
        suppliers.put("Energy Producing", "EcoGen Sol.");
        return new Establishment(name, floors, tiles, weaknesses, country, tileTypes, suppliers, type, null);
    }
}
