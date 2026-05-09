package com.example.myapplication.activities.establishment;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.myapplication.R;
import com.example.myapplication.activities.establishment.adapters.EstablishmentPagerAdapter;
import com.example.myapplication.activities.establishment.models.Establishment;
import com.example.myapplication.fragments.CreateEstablishmentFragment;
import com.example.myapplication.fragments.EstablishmentListFragment;
import com.example.myapplication.utils.FirestoreManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EstablishmentActivity extends AppCompatActivity implements 
        EstablishmentListFragment.OnEstablishmentEditListener, 
        CreateEstablishmentFragment.OnEstablishmentSavedListener {

    private FirestoreManager firestoreManager;
    private TextView greetingTextView;
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private EstablishmentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_establishment);
        firestoreManager = FirestoreManager.getInstance();
        firestoreManager.loadUserData();
        viewModel = new ViewModelProvider(this).get(EstablishmentViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });
        greetingTextView = findViewById(R.id.greetingTextView);
        loadUserGreeting();
        viewPager = findViewById(R.id.viewPager);
        EstablishmentPagerAdapter adapter = new EstablishmentPagerAdapter(this);
        viewPager.setAdapter(adapter);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position != 1) {
                    viewModel.selectEstablishment(null);
                }
                int selectedId = -1;
                switch (position) {
                    case 0: selectedId = R.id.nav_establishments; break;
                    case 1: selectedId = R.id.nav_create; break;
                    case 2: selectedId = R.id.nav_tile_data; break;
                    case 3: selectedId = R.id.nav_profile; break;
                }
                if (selectedId != -1 && bottomNavigationView.getSelectedItemId() != selectedId) {
                    bottomNavigationView.setSelectedItemId(selectedId);
                }
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId != R.id.nav_create) {
                viewModel.selectEstablishment(null);
            }
            if (itemId == R.id.nav_establishments) {
                viewPager.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.nav_create) {
                viewPager.setCurrentItem(1, true);
                return true;
            } else if (itemId == R.id.nav_tile_data) {
                viewPager.setCurrentItem(2, true);
                return true;
            } else if (itemId == R.id.nav_profile) {
                viewPager.setCurrentItem(3, true);
                return true;
            }
            return false;
        });
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
        viewModel.selectEstablishment(establishment);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onEstablishmentSaved() {
        viewModel.selectEstablishment(null);
        viewPager.setCurrentItem(0);
    }
}
