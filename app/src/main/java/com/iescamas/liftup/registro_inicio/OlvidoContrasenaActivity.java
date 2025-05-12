package com.iescamas.liftup.registro_inicio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.iescamas.liftup.R;

public class OlvidoContrasenaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editCorreo;
    private Button btnRecuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_olvido_contrasena);

        mAuth = FirebaseAuth.getInstance();

        editCorreo = findViewById(R.id.editCorreoRecuperarContrasena);
        btnRecuperar = findViewById(R.id.btnrecuperar);

        btnRecuperar.setOnClickListener(v -> {
            String email = editCorreo.getText().toString().trim();

            if (email.isEmpty()) {

                Toast.makeText(this, "Por favor, introduce tu correo", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "No se pudo enviar el correo. Verifica el email", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
