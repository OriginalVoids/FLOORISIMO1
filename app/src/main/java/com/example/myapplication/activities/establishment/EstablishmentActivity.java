package com.example.myapplication.activities.establishment;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activities.establishment.models.Establishment;
import com.example.myapplication.fragments.CreateEstablishmentFragment;
import com.example.myapplication.fragments.EstablishmentListFragment;
import com.example.myapplication.fragments.PlaceholderFragment;
import com.example.myapplication.fragments.ProfileFragment;
import com.example.myapplication.utils.FirestoreManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

public class EstablishmentActivity extends AppCompatActivity implements 
        EstablishmentListFragment.OnEstablishmentEditListener, 
        CreateEstablishmentFragment.OnEstablishmentSavedListener {

    private FirestoreManager firestoreManager;
    private TextView greetingTextView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_establishment);

        firestoreManager = FirestoreManager.getInstance();
        firestoreManager.loadUserData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        greetingTextView = findViewById(R.id.greetingTextView);
        loadUserGreeting();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> handleNavigation(item.getItemId()));

        // Set initial fragment
        if (savedInstanceState == null) {
            switchFragment(new EstablishmentListFragment());
        }
    }

    private boolean handleNavigation(int itemId) {
        Fragment selectedFragment = null;

        if (itemId == R.id.nav_establishments) {
            selectedFragment = new EstablishmentListFragment();
        } else if (itemId == R.id.nav_create) {
            selectedFragment = new CreateEstablishmentFragment();
        } else if (itemId == R.id.nav_placeholder) {
            selectedFragment = new PlaceholderFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        }

        if (selectedFragment != null) {
            switchFragment(selectedFragment);
            return true;
        }
        return false;
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void loadUserGreeting() {
        firestoreManager.getUserDetails().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                if (name != null && !name.isEmpty()) {
                    greetingTextView.setText("Hello, " + name + "!");
                } else {
                    greetingTextView.setText("Hello, User!");
                }
            }
        }).addOnFailureListener(e -> greetingTextView.setText("Hello!"));
    }

    @Override
    public void onEditEstablishment(Establishment establishment) {
        // Switch to create/edit fragment with data
        Fragment editFragment = CreateEstablishmentFragment.newInstance(establishment);
        
        // Temporarily disable listener so setSelectedItemId doesn't trigger a new (empty) fragment
        bottomNavigationView.setOnItemSelectedListener(null);
        bottomNavigationView.setSelectedItemId(R.id.nav_create);
        bottomNavigationView.setOnItemSelectedListener(item -> handleNavigation(item.getItemId()));

        switchFragment(editFragment);
    }

    @Override
    public void onEstablishmentSaved() {
        // Switch back to list fragment
        bottomNavigationView.setOnItemSelectedListener(null);
        bottomNavigationView.setSelectedItemId(R.id.nav_establishments);
        bottomNavigationView.setOnItemSelectedListener(item -> handleNavigation(item.getItemId()));

        switchFragment(new EstablishmentListFragment());
    }
}
