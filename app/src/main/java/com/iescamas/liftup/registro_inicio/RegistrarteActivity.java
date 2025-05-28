package com.iescamas.liftup.registro_inicio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.R;
import com.iescamas.liftup.tipos.Inicio;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrarteActivity extends AppCompatActivity {

    EditText editUsuarioRegistro, editCorreoRegistro, editContrasenaRegistro, editReptContrasenaRegistro;

    Button btnregistrarse;

    TextView txtInfocontrasena;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarte);

        editUsuarioRegistro = findViewById(R.id.editUsuarioRegistro);
        editCorreoRegistro = findViewById(R.id.editCorreoRegistro);
        editContrasenaRegistro = findViewById(R.id.editContrasenaRegistro);
        editReptContrasenaRegistro = findViewById(R.id.editReptContrasenaRegistro);
        btnregistrarse = findViewById(R.id.btnCrearCuenta);
        txtInfocontrasena = findViewById(R.id.txtInfoCONtrasena);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        btnregistrarse.setOnClickListener(v -> {
            String usuario = editUsuarioRegistro.getText().toString().trim();
            String correo = editCorreoRegistro.getText().toString().trim();
            String contrasena = editContrasenaRegistro.getText().toString().trim();
            String reptContrasena = editReptContrasenaRegistro.getText().toString().trim();

            if (usuario.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || reptContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contrasena.equals(reptContrasena)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                editReptContrasenaRegistro.setText("");
                return;
            }

            if (!isValidPassword(contrasena)) {
                txtInfocontrasena.setVisibility(View.VISIBLE);
                editContrasenaRegistro.setText("");
                editReptContrasenaRegistro.setText("");
                return;
            }

            // Verificar si el nombre de usuario ya existe en Firestore
            db.collection("usuarios")
                    .whereEqualTo("usuario", usuario)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(this, "Ese nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.createUserWithEmailAndPassword(correo, contrasena)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                String uid = user.getUid();

                                                Map<String, Object> datosUsuario = new HashMap<>();
                                                datosUsuario.put("usuario", usuario);
                                                datosUsuario.put("correo", correo);

                                                db.collection("usuarios").document(uid).set(datosUsuario)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(this, Inicio.class));
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e ->
                                                                Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show()
                                                        );
                                            }
                                        } else {
                                            String errorMsg = task.getException().getMessage();
                                            Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                                            Log.e("FIREBASE_REGISTRO", "Error al registrar usuario: " + errorMsg, task.getException());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al verificar usuario", Toast.LENGTH_SHORT).show();
                        Log.e("FIRESTORE_CHECK", "Error al buscar usuario: ", e);
                    });
        });

        editReptContrasenaRegistro.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                txtInfocontrasena.setVisibility(View.GONE);
            }
        });


    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        return Pattern.matches(regex, password);
    }
}