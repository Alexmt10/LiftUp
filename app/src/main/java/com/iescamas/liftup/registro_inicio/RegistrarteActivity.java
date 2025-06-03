package com.iescamas.liftup.registro_inicio;
import com.google.firebase.messaging.FirebaseMessaging;

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
import com.iescamas.liftup.Mensajes.Token;
import com.iescamas.liftup.R;
import com.iescamas.liftup.tipos.InformacionAdicional;
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

    private static final String TAG = "RegistroUsuario";

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

            Log.d(TAG, "Intentando registrar usuario: " + usuario);

            if (usuario.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || reptContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Campos incompletos");
                return;
            }

            if (!contrasena.equals(reptContrasena)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Contraseñas no coinciden");
                editReptContrasenaRegistro.setText("");
                return;
            }

            if (!isValidPassword(contrasena)) {
                txtInfocontrasena.setVisibility(View.VISIBLE);
                editContrasenaRegistro.setText("");
                editReptContrasenaRegistro.setText("");
                Toast.makeText(this, "Contraseña no válida. Debe tener al menos 8 caracteres, una mayúscula y un número", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Contraseña inválida según regex");
                return;
            }

            Log.d(TAG, "Verificando si el usuario ya existe en Firestore...");

            db.collection("Usuarios")
                    .whereEqualTo("username", usuario)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(this, "Ese nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Usuario ya existe en Firestore: " + usuario);
                        } else {
                            Log.d(TAG, "Usuario no existe. Creando cuenta Firebase...");
                            mAuth.createUserWithEmailAndPassword(correo, contrasena)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                String uid = user.getUid();

                                                FirebaseMessaging.getInstance().getToken()
                                                        .addOnCompleteListener(tokenTask -> {
                                                            if (!tokenTask.isSuccessful()) {
                                                                Log.w(TAG, "Error al obtener el token FCM", tokenTask.getException());
                                                                return;
                                                            }

                                                            String token = tokenTask.getResult();

                                                            Map<String, Object> datosUsuario = new HashMap<>();
                                                            datosUsuario.put("username", usuario);
                                                            datosUsuario.put("correo", correo);
                                                            datosUsuario.put("fcmToken", token);

                                                            Log.d(TAG, "Guardando datos usuario en Firestore con UID: " + uid);
                                                            Token.guardarTokenEnFirestore(uid);
                                                            db.collection("Usuarios").document(uid).set(datosUsuario)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                                                        Log.i(TAG, "Datos guardados correctamente en Firestore con token FCM");
                                                                        Intent intent = new Intent(this, InformacionAdicional.class);
                                                                        startActivity(intent);
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                                                                        Log.e(TAG, "Error guardando datos en Firestore", e);
                                                                    });
                                                        });
                                            }
                                        } else {
                                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                                            Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                                            Log.e(TAG, "Error al registrar usuario en Firebase Auth: " + errorMsg, task.getException());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al verificar usuario", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error al buscar usuario en Firestore", e);
                    });

        });

        editReptContrasenaRegistro.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                txtInfocontrasena.setVisibility(View.GONE);
                Log.d(TAG, "Ocultando info de contraseña");
            }
        });
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        boolean valido = Pattern.matches(regex, password);
        Log.d(TAG, "Validación contraseña: " + valido);
        return valido;
    }
}
