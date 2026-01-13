package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ClientDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerBackground), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Client client = (Client) getIntent().getSerializableExtra("client");

        if (client != null) {
            TextView nameTextView = findViewById(R.id.detailClientName);
            TextView weaknessesTextView = findViewById(R.id.weaknessesTextView);
            TextView designTextView = findViewById(R.id.designTextView);
            TextView floorsTextView = findViewById(R.id.floorsTextView);
            TextView flooringsProvidedTextView = findViewById(R.id.flooringsProvidedTextView);

            nameTextView.setText(client.getName());
            
            if (client.getStructuralWeaknesses() != null && !client.getStructuralWeaknesses().isEmpty()) {
                weaknessesTextView.setText(TextUtils.join("\n", client.getStructuralWeaknesses()));
            } else {
                weaknessesTextView.setText("No weaknesses reported");
            }
            
            designTextView.setText(client.getPreferredDesign());
            floorsTextView.setText(String.valueOf(client.getNumberOfFloors()));
            flooringsProvidedTextView.setText(String.valueOf(client.getNumberFlooringsProvided()));
        }
    }
}
