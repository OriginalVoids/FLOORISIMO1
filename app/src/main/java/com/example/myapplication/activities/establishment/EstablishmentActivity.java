package com.example.myapplication.activities.establishment;

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
import com.example.myapplication.R;
import com.example.myapplication.activities.establishment.adapters.EstablishmentListAdapter;
import com.example.myapplication.activities.establishment.models.Establishment;
import com.example.myapplication.activities.establishment.repositories.EstablishmentRepository;
import com.example.myapplication.utils.FirestoreManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class EstablishmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EstablishmentListAdapter adapter;
    private FirestoreManager firestoreManager;
    private List<Establishment> establishmentList = new ArrayList<>();

    private LinearLayout detailPane;
    private TextView placeholderTextView;
    private ImageView paneLogoImageView, panePlanImageView;
    private TextView paneInitialsTextView, paneEstNameLeft;
    private TextView paneTextType, paneTextLocation, paneTextFloors, paneTextTiles;
    private TextView paneTextWeaknesses, paneTextTileTypes, paneTextSuppliers;
    private TextView titleTextView;

    private static final String MALL_IMAGE = "https://images.ctfassets.net/z78475or6i3d/4ncEpBqUYr2OOpGsYVPv9B/ffd4235f9b0f702e904df616eccfe76d/Urban_Strip_Mall_Retail_to_Housing_Concept_Plan.jpg";
    private static final String OFFICE_IMAGE = "https://i.pinimg.com/736x/bd/d6/f5/bdd6f5247dbea0e5eedf33fe8cc491ee--office-layout-plan-office-floor-plan.jpg";
    private static final String RESTAURANT_IMAGE = "https://th.bing.com/th/id/R.2f7e50ef9157e082f4775217792b39b1?rik=79LiudyrK3VCkQ&pid=ImgRaw&r=0";

    private final ActivityResultLauncher<Intent> createLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Establishment newEst = (Establishment) result.getData().getSerializableExtra("new_establishment");
                    if (newEst != null) {
                        firestoreManager.addOrUpdateEstablishment(newEst);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_establishment);

        firestoreManager = FirestoreManager.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        titleTextView = findViewById(R.id.titleTextView);
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

        EstablishmentRepository.getInstance().getEstablishmentList().observe(this, establishments -> {
            this.establishmentList.clear();
            this.establishmentList.addAll(establishments);
            adapter.notifyDataSetChanged();
            if (establishments.isEmpty()) {
                showPlaceholder();
            } else {
                showEstablishmentDetails(establishments.get(0));
            }
        });

        FloatingActionButton addFab = findViewById(R.id.addEstablishmentFab);
        addFab.setOnClickListener(v -> {
            Intent intent = new Intent(EstablishmentActivity.this, CreateEstablishmentActivity.class);
            createLauncher.launch(intent);
        });

        updateTitle();
    }

    private void updateTitle() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null && !email.isEmpty()) {
                String username = email.split("@")[0];
                titleTextView.setText("FLOORISIMO - " + username + "'s Establishments");
            } else {
                titleTextView.setText("FLOORISIMO - My Establishments");
            }
        } else {
            titleTextView.setText("FLOORISIMO - My Establishments");
        }
    }

    private void showPlaceholder() {
        placeholderTextView.setVisibility(View.VISIBLE);
        detailPane.setVisibility(View.GONE);
    }

    private void showEstablishmentDetails(Establishment est) {
        placeholderTextView.setVisibility(View.GONE);
        detailPane.setVisibility(View.VISIBLE);

        paneEstNameLeft.setText(est.getName());
        paneInitialsTextView.setText(est.getInitials());

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

        String planUrl = est.getEstablishmentType().equalsIgnoreCase("Mall") ? MALL_IMAGE :
                (est.getEstablishmentType().equalsIgnoreCase("Office") || est.getEstablishmentType().equalsIgnoreCase("Office Building")) ? OFFICE_IMAGE : RESTAURANT_IMAGE;
        Glide.with(this).load(planUrl).into(panePlanImageView);

        paneTextType.setText("Type: " + est.getEstablishmentType());
        paneTextLocation.setText("Country: " + est.getCountry());
        paneTextFloors.setText("Floors: " + est.getNumberOfFloors());
        paneTextTiles.setText("Total Tiles: " + est.getNumberOfTiles());
        paneTextWeaknesses.setText(est.getStructuralWeaknesses().isEmpty() ? "None reported" : TextUtils.join(", ", est.getStructuralWeaknesses()));
        paneTextTileTypes.setText("Types: " + TextUtils.join(", ", est.getPreferredTileTypes()));

        StringBuilder suppliers = new StringBuilder("Preferred Suppliers:\n");
        est.getPreferredSuppliers().forEach((key, value) ->
                suppliers.append(" • ").append(key).append(": ").append(value).append("\n"));
        paneTextSuppliers.setText(suppliers.toString().trim());
    }

    private void showOptionsPopup(Establishment est, View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Edit Info");
        popup.getMenu().add("Delete");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Edit Info")) {
                Intent intent = new Intent(this, CreateEstablishmentActivity.class);
                intent.putExtra("establishment", est);
                createLauncher.launch(intent);
                return true;
            } else if (item.getTitle().equals("Delete")) {
                firestoreManager.deleteEstablishment(est);
                return true;
            }
            return false;
        });
        popup.show();
    }
}
