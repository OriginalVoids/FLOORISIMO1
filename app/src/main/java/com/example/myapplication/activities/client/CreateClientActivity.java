package com.example.myapplication.activities.client;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CreateClientActivity extends AppCompatActivity {

    private TextInputEditText nameEdit, weaknessesEdit, designEdit, floorsEdit, flooringsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_client);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerBackground), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        nameEdit = findViewById(R.id.newClientName);
        weaknessesEdit = findViewById(R.id.newClientWeaknesses);
        designEdit = findViewById(R.id.newClientDesign);
        floorsEdit = findViewById(R.id.newClientFloors);
        flooringsEdit = findViewById(R.id.newClientFloorings);
        Button saveButton = findViewById(R.id.saveClientButton);

        saveButton.setOnClickListener(v -> {
            String name = Objects.requireNonNull(nameEdit.getText()).toString().trim();
            String weaknessesStr = Objects.requireNonNull(weaknessesEdit.getText()).toString().trim();
            String design = Objects.requireNonNull(designEdit.getText()).toString().trim();
            String floorsStr = Objects.requireNonNull(floorsEdit.getText()).toString().trim();
            String flooringsStr = Objects.requireNonNull(flooringsEdit.getText()).toString().trim();

            if (TextUtils.isEmpty(name)) {
                nameEdit.setError("Name is required");
                return;
            }

            List<String> weaknesses = new ArrayList<>();
            if (!TextUtils.isEmpty(weaknessesStr)) {
                weaknesses = Arrays.asList(weaknessesStr.split(","));
            }

            int floors = TextUtils.isEmpty(floorsStr) ? 0 : Integer.parseInt(floorsStr);
            int floorings = TextUtils.isEmpty(flooringsStr) ? 0 : Integer.parseInt(flooringsStr);

            Client newClient = new Client(name, weaknesses, design, floors, floorings);
            
            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_client", newClient);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
