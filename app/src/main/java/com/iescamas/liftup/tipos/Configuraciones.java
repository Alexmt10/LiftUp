package com.iescamas.liftup.tipos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.R;
import com.iescamas.liftup.registro_inicio.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuraciones extends AppCompatActivity {

    private TextInputEditText editNombreCompleto, editUsername, editPeso, editDescripcion, editAlturaid;
    private Spinner spinnerGym;
    private Button btnGuardarCambios, btnSalir;
    private List<String> listaGimnasios;
    private ArrayAdapter<String> adapterGymConfi;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
     private FirebaseFirestore db;
     private String uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuraciones);

         mAuth = FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
         uid = mAuth.getCurrentUser().getUid();

        editNombreCompleto = findViewById(R.id.editNombreCompleto);
         editUsername = findViewById(R.id.editUsername);
         editPeso = findViewById(R.id.editPeso);
        editAlturaid = findViewById(R.id.editAlturaid);
         editDescripcion = findViewById(R.id.editDescripcion);
         spinnerGym = findViewById(R.id.spinnerGym);
         btnGuardarCambios = findViewById(R.id.btnGuardarCambiosid);


        listaGimnasios = new ArrayList<>();
        adapterGymConfi = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaGimnasios);
        adapterGymConfi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGym.setAdapter(adapterGymConfi);
        cargarGimnasiosDesdeFirestore();
        cargarDatosUsuario();

         btnSalir = findViewById(R.id.btnSalirDelPerfilId);

        btnSalir.setOnClickListener(v -> {
            new AlertDialog.Builder(Configuraciones.this)
                    .setTitle("Confirmar salida")
                    .setMessage("¿Estás seguro de que quieres salir del perfil?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(Configuraciones.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        btnGuardarCambios.setOnClickListener(view -> {
            guardarcambios();
        });
    }

    private void guardarcambios(){
        String nombre = editNombreCompleto.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String pesoStr = editPeso.getText().toString().trim();
        String alturaStr = editAlturaid.getText().toString().trim();
        String descripcion = editDescripcion.getText().toString().trim();
        String gimnasio = spinnerGym.getSelectedItem().toString();

        if (nombre.isEmpty() || username.isEmpty() || pesoStr.isEmpty() ||
                alturaStr.isEmpty() || descripcion.isEmpty() || gimnasio.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double peso = Double.parseDouble(pesoStr);
        int altura = Integer.parseInt(alturaStr);


        db.collection("Usuarios")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean usernameEnUso = false;

                    for (DocumentSnapshot doc : querySnapshot) {
                        if (!doc.getId().equals(uid)) {
                            usernameEnUso = true;
                            break;
                        }
                    }

                    if (usernameEnUso) {
                        Toast.makeText(this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> datosActualizados = new HashMap<>();
                        datosActualizados.put("nombre", nombre);
                        datosActualizados.put("username", username);
                        datosActualizados.put("peso", peso);
                        datosActualizados.put("altura", altura);
                        datosActualizados.put("descripcion", descripcion);
                        datosActualizados.put("gym", gimnasio);

                        db.collection("Usuarios").document(uid)
                                .update(datosActualizados)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al comprobar el username", Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarDatosUsuario() {
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String username = documentSnapshot.getString("username");
                        Double peso = documentSnapshot.getDouble("peso");
                        Long alturaLong = documentSnapshot.getLong("altura");
                        String descripcion = documentSnapshot.getString("descripcion");
                        String gym = documentSnapshot.getString("gym");

                        editNombreCompleto.setText(nombre);
                        editUsername.setText(username);
                        editPeso.setText(peso != null ? String.valueOf(peso) : "");
                        editAlturaid.setText(alturaLong != null ? String.valueOf(alturaLong) : "");
                        editDescripcion.setText(descripcion);

                        if (gym != null) {
                            int index = listaGimnasios.indexOf(gym);
                            if (index >= 0) {
                                spinnerGym.setSelection(index);
                            }
                        }
                    } else {
                        Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                });
    }
    private void cargarGimnasiosDesdeFirestore() {
        db.collection("gimnasios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaGimnasios.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombreGym = doc.getString("nombre");
                        if (nombreGym != null) {
                            listaGimnasios.add(nombreGym);
                        }
                    }
                    adapterGymConfi.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar gimnasios", Toast.LENGTH_SHORT).show();
                });
    }



}
