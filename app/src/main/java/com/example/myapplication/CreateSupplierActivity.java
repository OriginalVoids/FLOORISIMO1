package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class CreateSupplierActivity extends AppCompatActivity {

    private TextInputEditText companyNameEdit, personalNameEdit, priceEdit, countryEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_supplier);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerBackground), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        companyNameEdit = findViewById(R.id.newSupplierCompanyName);
        personalNameEdit = findViewById(R.id.newSupplierPersonalName);
        priceEdit = findViewById(R.id.newSupplierPrice);
        countryEdit = findViewById(R.id.newSupplierCountry);
        Button saveButton = findViewById(R.id.saveSupplierButton);

        saveButton.setOnClickListener(v -> {
            String companyName = Objects.requireNonNull(companyNameEdit.getText()).toString().trim();
            String personalName = Objects.requireNonNull(personalNameEdit.getText()).toString().trim();
            String priceStr = Objects.requireNonNull(priceEdit.getText()).toString().trim();
            String country = Objects.requireNonNull(countryEdit.getText()).toString().trim();

            if (TextUtils.isEmpty(companyName)) {
                companyNameEdit.setError("Company name is required");
                return;
            }

            double price = TextUtils.isEmpty(priceStr) ? 0.0 : Double.parseDouble(priceStr);

            Supplier newSupplier = new Supplier(companyName, personalName, 0, price, country);
            
            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_supplier", newSupplier);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
