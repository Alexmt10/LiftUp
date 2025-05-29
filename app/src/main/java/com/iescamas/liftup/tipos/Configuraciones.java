package com.iescamas.liftup.tipos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.iescamas.liftup.R;
import com.iescamas.liftup.registro_inicio.LoginActivity;

public class Configuraciones extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuraciones);

        Button btnSalir = findViewById(R.id.btnSalirDelPerfilId);
        btnSalir.setOnClickListener(v -> {
            new AlertDialog.Builder(Configuraciones.this)
                    .setTitle("Confirmar salida")
                    .setMessage("¿Estás seguro de que quieres salir del perfil?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Si pulsa "Sí", cerrar sesión y volver al login
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(Configuraciones.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null) // No hace nada, cierra el diálogo
                    .show();
        });
    }

    }
