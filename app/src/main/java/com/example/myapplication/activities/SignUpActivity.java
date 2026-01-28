package com.example.myapplication.activities;

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

import com.example.myapplication.R;
import com.example.myapplication.utils.FirestoreManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestoreManager = FirestoreManager.getInstance();

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.signUpEmailEditText);
        passwordEditText = findViewById(R.id.signUpPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView backToLogin = findViewById(R.id.backToLoginTextView);

        signUpButton.setOnClickListener(v -> attemptSignUp());
        backToLogin.setOnClickListener(v -> finish());
    }

    private void attemptSignUp() {
        String name = Objects.requireNonNull(fullNameEditText.getText()).toString().trim();
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String pass = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
        String confirmPass = Objects.requireNonNull(confirmPasswordEditText.getText()).toString().trim();

        if (TextUtils.isEmpty(name)) {
            fullNameEditText.setError("Name is required");
            return;
        }
        if (!email.contains("@")) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            passwordEditText.setError(getString(R.string.error_password_required));
            return;
        }
        if (!pass.equals(confirmPass)) {
            confirmPasswordEditText.setError(getString(R.string.error_passwords_dont_match));
            return;
        }

        firestoreManager.createUser(email, pass)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Account created for " + name, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Sign-up failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
