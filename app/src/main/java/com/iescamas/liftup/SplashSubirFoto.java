package com.iescamas.liftup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iescamas.liftup.Fragment.YoFragment;
import com.iescamas.liftup.pojo.ItemPost;
import com.iescamas.liftup.pojo.ItemEntrenamiento;
import com.iescamas.liftup.pojo.PlanComida;

public class SplashSubirFoto extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_subir_foto); // tu layout de splash

        // Recoger datos
        Intent intent = getIntent();
        String descripcion = intent.getStringExtra("descripcion");
        String imagenUriString = intent.getStringExtra("imagenUri");
        Uri imagenUri = Uri.parse(imagenUriString);
        ItemEntrenamiento planEntreno = (ItemEntrenamiento) intent.getSerializableExtra("planEntreno");
        PlanComida planComida = (PlanComida) intent.getSerializableExtra("planComida");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = user.getUid();
        String userName = user.getDisplayName() != null ? user.getDisplayName() : "Anónimo";
        String fileName = "post_" + System.currentTimeMillis() + ".jpg";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("imagenes_posts/" + fileName);
        storageRef.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String urlImagen = uri.toString();

                                ItemPost post = new ItemPost(
                                        userName,
                                        descripcion,
                                        userId,
                                        urlImagen,
                                        planEntreno,
                                        planComida
                                );

                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("publicaciones");
                                dbRef.push()
                                        .setValue(post)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(this, "Publicado con éxito", Toast.LENGTH_SHORT).show();

                                            // Abrir MainActivity con YoFragment
                                            Intent mainIntent = new Intent(SplashSubirFoto.this, YoFragment.class);
                                            mainIntent.putExtra("abrirFragment", "yo");
                                            startActivity(mainIntent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            finish();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al obtener URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
