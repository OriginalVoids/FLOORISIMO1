package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.resetEmailEditText);
        Button sendResetButton = findViewById(R.id.sendResetLinkButton);
        TextView backToLogin = findViewById(R.id.backToLoginFromForgot);

        sendResetButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError(getString(R.string.error_email_required));
            } else if (!email.contains("@")) {
                emailEditText.setError(getString(R.string.error_invalid_email));
            } else {
                Toast.makeText(this, "Reset link sent to " + email, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        backToLogin.setOnClickListener(v -> finish());
    }
}
