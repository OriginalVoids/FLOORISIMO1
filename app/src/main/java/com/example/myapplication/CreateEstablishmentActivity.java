package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateEstablishmentActivity extends AppCompatActivity {

    private TextInputEditText editName, editFloors, editTiles, editCountry, editWeaknesses;
    private TextInputEditText editSensingSupplier, editRoutingSupplier, editPressurizedSupplier, editEnergySupplier;
    private CheckBox checkSensing, checkRouting, checkPressurized, checkEnergy;
    private RadioGroup radioGroupType;
    private ImageView imgLogoPreview;
    private Uri selectedLogoUri;

    private final ActivityResultLauncher<String> logoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedLogoUri = uri;
                    imgLogoPreview.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_establishment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerBackground), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        imgLogoPreview = findViewById(R.id.imgLogoPreview);
        Button btnUploadLogo = findViewById(R.id.btnUploadLogo);
        btnUploadLogo.setOnClickListener(v -> logoPickerLauncher.launch("image/*"));

        editName = findViewById(R.id.editName);
        editFloors = findViewById(R.id.editFloors);
        editTiles = findViewById(R.id.editTiles);
        editCountry = findViewById(R.id.editCountry);
        editWeaknesses = findViewById(R.id.editWeaknesses);
        
        radioGroupType = findViewById(R.id.radioGroupType);
        
        checkSensing = findViewById(R.id.checkSensing);
        checkRouting = findViewById(R.id.checkRouting);
        checkPressurized = findViewById(R.id.checkPressurized);
        checkEnergy = findViewById(R.id.checkEnergy);

        editSensingSupplier = findViewById(R.id.editSensingSupplier);
        editRoutingSupplier = findViewById(R.id.editRoutingSupplier);
        editPressurizedSupplier = findViewById(R.id.editPressurizedSupplier);
        editEnergySupplier = findViewById(R.id.editEnergySupplier);

        TextInputLayout inputSensing = findViewById(R.id.inputSensingSupplier);
        TextInputLayout inputRouting = findViewById(R.id.inputRoutingSupplier);
        TextInputLayout inputPressurized = findViewById(R.id.inputPressurizedSupplier);
        TextInputLayout inputEnergy = findViewById(R.id.inputEnergySupplier);

        checkSensing.setOnCheckedChangeListener((v, checked) -> inputSensing.setVisibility(checked ? View.VISIBLE : View.GONE));
        checkRouting.setOnCheckedChangeListener((v, checked) -> inputRouting.setVisibility(checked ? View.VISIBLE : View.GONE));
        checkPressurized.setOnCheckedChangeListener((v, checked) -> inputPressurized.setVisibility(checked ? View.VISIBLE : View.GONE));
        checkEnergy.setOnCheckedChangeListener((v, checked) -> inputEnergy.setVisibility(checked ? View.VISIBLE : View.GONE));

        Button btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(v -> saveEstablishment());
    }

    private void saveEstablishment() {
        String name = Objects.requireNonNull(editName.getText()).toString().trim();
        if (name.isEmpty()) {
            editName.setError("Required");
            return;
        }

        int floors = Integer.parseInt(Objects.requireNonNull(editFloors.getText()).toString().isEmpty() ? "0" : editFloors.getText().toString());
        int tiles = Integer.parseInt(Objects.requireNonNull(editTiles.getText()).toString().isEmpty() ? "0" : editTiles.getText().toString());
        String country = Objects.requireNonNull(editCountry.getText()).toString();
        
        List<String> weaknesses = new ArrayList<>();
        String wText = Objects.requireNonNull(editWeaknesses.getText()).toString();
        if (!wText.isEmpty()) {
            weaknesses = Arrays.asList(wText.split(","));
        }

        String type = "Office";
        int selectedType = radioGroupType.getCheckedRadioButtonId();
        if (selectedType == R.id.radioMall) type = "Mall";
        else if (selectedType == R.id.radioRestaurant) type = "Restaurant";

        List<String> tileTypes = new ArrayList<>();
        Map<String, String> suppliers = new HashMap<>();

        if (checkSensing.isChecked()) {
            tileTypes.add("Sensing");
            suppliers.put("Sensing", editSensingSupplier.getText().toString());
        }
        if (checkRouting.isChecked()) {
            tileTypes.add("Routing");
            suppliers.put("Routing", editRoutingSupplier.getText().toString());
        }
        if (checkPressurized.isChecked()) {
            tileTypes.add("Pressurized");
            suppliers.put("Pressurized", editPressurizedSupplier.getText().toString());
        }
        if (checkEnergy.isChecked()) {
            tileTypes.add("Energy Producing");
            suppliers.put("Energy Producing", editEnergySupplier.getText().toString());
        }

        String logoUriString = selectedLogoUri != null ? selectedLogoUri.toString() : null;

        Establishment newEst = new Establishment(name, floors, tiles, weaknesses, country, tileTypes, suppliers, type, logoUriString);

        Intent data = new Intent();
        data.putExtra("new_establishment", newEst);
        setResult(RESULT_OK, data);
        finish();
    }
}
