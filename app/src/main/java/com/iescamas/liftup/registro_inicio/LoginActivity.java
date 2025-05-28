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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
            String email = editUsuarioo.getText().toString().trim();
            String password = editContrasena.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();


                                redirectToMainActivity();

                            }
                        } else {
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText(this, "Error al iniciar sesión, Usuario o contraseña incorrectas", Toast.LENGTH_LONG).show();
                            Log.e("LOGIN_FIREBASE", "Error al iniciar sesión", task.getException());
                        }
                    });
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Verificar nuevamente por si el usuario cerró sesión mientras la actividad estaba en pausa
        checkCurrentUser();
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Usuario ya está autenticado, redirigir
            redirectToMainActivity();
        }
        // Si no hay usuario, permanecer en LoginActivity
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, Inicio.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finalizar LoginActivity para que no pueda volver atrás
    }

    }



