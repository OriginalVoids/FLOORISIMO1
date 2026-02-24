package com.example.myapplication;

import android.content.Intent;
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

import com.example.myapplication.activities.ForgotPasswordActivity;
import com.example.myapplication.activities.establishment.EstablishmentActivity;
import com.example.myapplication.utils.FirestoreManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        TextView signUpTextView = findViewById(R.id.signUpTextView);

        firestoreManager = FirestoreManager.getInstance();

        loginButton.setOnClickListener(v -> attemptLogin());
        
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.myapplication.SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_email_required));
            return;
        }

        if (!email.contains("@")) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_password_required));
            return;
        }

        firestoreManager.loginUser(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, getString(R.string.login_success_toast, email), Toast.LENGTH_SHORT).show();
                    // Navigate to the new Establishment flip-card screen
                    Intent intent = new Intent(MainActivity.this, EstablishmentActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
