package com.iescamas.liftup.tipos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iescamas.liftup.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InformacionAdicional extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgPerfil;
    private TextInputEditText editNombreCompleto, editApellidos, editAltura, editPeso, editFechaNacimiento;
    private Spinner editSexo;
    private Uri imagenPerfilUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_adicional);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imgPerfil = findViewById(R.id.imgPerfil);
        editNombreCompleto = findViewById(R.id.editNombreCompleto);
        editApellidos = findViewById(R.id.editApellidos);
        editSexo = findViewById(R.id.spinnerSexo);
        editAltura = findViewById(R.id.editAltura);
        editPeso = findViewById(R.id.editPeso);
        editFechaNacimiento = findViewById(R.id.editFechaNacimiento);
        Button btnRegistrar = findViewById(R.id.btnRegistrarUsuario);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sexo_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSexo.setAdapter(adapter);

        imgPerfil.setOnClickListener(v -> openImageChooser());

        editFechaNacimiento.setOnClickListener(v -> showDatePickerDialog());

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
            editFechaNacimiento.setText(fechaSeleccionada);
        }, year, month, day);
        datePicker.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagenPerfilUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenPerfilUri);
                imgPerfil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registrarUsuario() {
        String nombre = editNombreCompleto.getText() != null ? editNombreCompleto.getText().toString().trim() : "";
        String apellidos = editApellidos.getText() != null ? editApellidos.getText().toString().trim() : "";
        String sexo = editSexo.getSelectedItem().toString().trim();
        String altura = editAltura.getText() != null ? editAltura.getText().toString().trim() : "";
        String peso = editPeso.getText() != null ? editPeso.getText().toString().trim() : "";
        String fechaNacimiento = editFechaNacimiento.getText() != null ? editFechaNacimiento.getText().toString().trim() : "";

        if (nombre.isEmpty() || apellidos.isEmpty() || sexo.isEmpty() || altura.isEmpty() || peso.isEmpty() || fechaNacimiento.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre", nombre);
        datosUsuario.put("apellidos", apellidos);
        datosUsuario.put("sexo", sexo);
        datosUsuario.put("altura", altura);
        datosUsuario.put("peso", peso);
        datosUsuario.put("fechaNacimiento", fechaNacimiento);

        if (imagenPerfilUri != null) {
            // Subir la imagen a Firebase Storage
            StorageReference storageRef = storage.getReference().child("fotos_perfil/" + uid + ".jpg");
            storageRef.putFile(imagenPerfilUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                datosUsuario.put("imagenPerfilUrl", uri.toString());

                                // Guardar los datos en Firestore
                                guardarDatosEnFirestore(uid, datosUsuario);
                            }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show());
        } else {
            guardarDatosEnFirestore(uid, datosUsuario);
        }
    }

    private void guardarDatosEnFirestore(String uid, Map<String, Object> datos) {
        db.collection("Usuarios").document(uid)
                .update(datos)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Inicio.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show());
    }
}
