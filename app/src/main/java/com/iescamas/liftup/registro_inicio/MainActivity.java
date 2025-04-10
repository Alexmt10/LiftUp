package com.iescamas.liftup.registro_inicio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.iescamas.liftup.R;

public class MainActivity extends AppCompatActivity {

    private EditText editUsuario;
    private EditText editContrasena;
    private Button btnInicioSesion;
    private TextView txtRegistrarse;
    private TextView txtOlvidoContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editUsuario = findViewById(R.id.edit_usuario);
        editContrasena = findViewById(R.id.edit_contrasena);
        btnInicioSesion = findViewById(R.id.btnInicioSesion);
        txtRegistrarse = findViewById(R.id.txtregistro);
        txtOlvidoContrasena = findViewById(R.id.txtOlvidoContrasena);

        txtRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrarteActivity.class);
            startActivity(intent);
        });

        txtOlvidoContrasena.setOnClickListener(v -> {
            Intent intent = new Intent(this, OlvidoContrasenaActivity.class);
            startActivity(intent);
        });

    }

    }

