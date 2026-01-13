package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class SupplierDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerBackground), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Supplier supplier = (Supplier) getIntent().getSerializableExtra("supplier");

        if (supplier != null) {
            TextView companyNameTextView = findViewById(R.id.detailSupplierName);
            TextView personalNameTextView = findViewById(R.id.supplierPersonalNameTextView);
            TextView countryTextView = findViewById(R.id.supplierCountryTextView);
            TextView totalOrderedTextView = findViewById(R.id.totalOrderedTextView);
            TextView pricePerUnitTextView = findViewById(R.id.pricePerUnitTextView);

            companyNameTextView.setText(supplier.getCompanyName());
            personalNameTextView.setText(supplier.getPersonalName());
            countryTextView.setText(supplier.getCountry());
            totalOrderedTextView.setText(String.valueOf(supplier.getTotalFlooringsOrdered()));
            pricePerUnitTextView.setText(String.format(Locale.getDefault(), "$%.2f", supplier.getPricePerUnit()));
        }
    }
}
