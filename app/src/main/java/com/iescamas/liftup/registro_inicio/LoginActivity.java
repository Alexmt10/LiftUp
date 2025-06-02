package com.iescamas.liftup.registro_inicio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.R;
import com.iescamas.liftup.tipos.Inicio;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsuarioo;
    private EditText editContrasena;
    private Button btnInicioSesion;
    private TextView txtRegistrarse;
    private TextView txtOlvidoContrasena;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);

        editUsuarioo = findViewById(R.id.edit_usuario);
        editContrasena = findViewById(R.id.edit_contrasena);
        btnInicioSesion = findViewById(R.id.btnInicioSesion);
        txtRegistrarse = findViewById(R.id.txtregistro);
        txtOlvidoContrasena = findViewById(R.id.txtOlvidoContrasena);


        mAuth = FirebaseAuth.getInstance();
        checkCurrentUser();

        txtRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrarteActivity.class);
            startActivity(intent);
        });

        txtOlvidoContrasena.setOnClickListener(v -> {
            Intent intent = new Intent(this, OlvidoContrasenaActivity.class);
            startActivity(intent);
        });

        btnInicioSesion.setOnClickListener(v -> {
            String userInput = editUsuarioo.getText().toString().trim();
            String password = editContrasena.getText().toString().trim();

            if (userInput.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userInput.contains("@")) {
                loginConEmail(userInput, password);
            } else {
                buscarEmailPorUsernameYLogin(userInput, password);
            }
        });


    }

    private void loginConEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            redirectToMainActivity();
                        }
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión, usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                        Log.e("LOGIN_FIREBASE", "Error al iniciar sesión", task.getException());
                    }
                });
    }

    private void buscarEmailPorUsernameYLogin(String username, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String email = queryDocumentSnapshots.getDocuments().get(0).getString("email");
                        loginConEmail(email, password);
                    } else {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar usuario", Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE", "Error buscando email por username", e);
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkCurrentUser();
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            redirectToMainActivity();
        }
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, Inicio.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    }



