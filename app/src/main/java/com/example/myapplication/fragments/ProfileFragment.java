package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.FirestoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView textProfileEmail, textEstablishmentCount;
    private FirestoreManager firestoreManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firestoreManager = FirestoreManager.getInstance();
        textProfileEmail = view.findViewById(R.id.textProfileEmail);
        textEstablishmentCount = view.findViewById(R.id.textEstablishmentCount);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        loadUserData();

        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            textProfileEmail.setText(currentUser.getEmail());
        }

        // Use the repository to get the count of establishments
        com.example.myapplication.activities.establishment.repositories.EstablishmentRepository.getInstance()
                .getEstablishmentList()
                .observe(getViewLifecycleOwner(), establishments -> {
                    if (establishments != null) {
                        textEstablishmentCount.setText("Establishments: " + establishments.size());
                    } else {
                        textEstablishmentCount.setText("Establishments: 0");
                    }
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
