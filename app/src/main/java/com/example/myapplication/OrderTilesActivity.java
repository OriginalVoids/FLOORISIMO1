package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class OrderTilesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_tiles);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerBackground), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        TextInputEditText editAmount = findViewById(R.id.editAmount);
        TextInputEditText editDate = findViewById(R.id.editDate);
        Spinner spinnerTileType = findViewById(R.id.spinnerTileType);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        String[] tileTypes = {"Sensing", "Routing", "Pressurized", "Energy Producing"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tileTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTileType.setAdapter(adapter);

        btnPlaceOrder.setOnClickListener(v -> {
            String amount = Objects.requireNonNull(editAmount.getText()).toString();
            String date = Objects.requireNonNull(editDate.getText()).toString();
            String type = spinnerTileType.getSelectedItem().toString();

            if (amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Order placed for " + amount + " " + type + " tiles on " + date, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
