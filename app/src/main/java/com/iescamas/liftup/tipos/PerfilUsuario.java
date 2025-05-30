package com.iescamas.liftup.tipos;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iescamas.liftup.Adaptadores.AdapterOtroPerfil;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemPost;
import com.iescamas.liftup.pojo.Usuario;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class PerfilUsuario extends AppCompatActivity {
    private RecyclerView recyclerOtroPerfil;
    private AdapterOtroPerfil adaptadorOtroPerfil;
    private List<ItemPost> listaPublicaciones;

    private ImageView iconPerfil;
    private TextView txtNombreUsuario, txtSeguidores, txtSeguidos, txtPublicaciones, txtDescripcion;
    private Button btnSeguir;

    private String uidUsuario;

    private FirebaseFirestore db;
    private ListenerRegistration seguidoresListener;
    private ListenerRegistration seguidosListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);


        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Views
        iconPerfil = findViewById(R.id.IconPerfilOtroid);
        txtNombreUsuario = findViewById(R.id.txtNombreUsuarioOtroid);
        txtSeguidores = findViewById(R.id.txtNumeroSeguidoresOtroId);
        txtSeguidos = findViewById(R.id.txtNumeroSeguidosOtroId);
        txtPublicaciones = findViewById(R.id.txtNumeroPublicaiconesOtroId);
        txtDescripcion = findViewById(R.id.txtDescripcionOtroId);

        // RecyclerView
        recyclerOtroPerfil = findViewById(R.id.recyclerOtroPerfil);
        recyclerOtroPerfil.setLayoutManager(new GridLayoutManager(this, 2));
        listaPublicaciones = new ArrayList<>();
        adaptadorOtroPerfil = new AdapterOtroPerfil(listaPublicaciones, this);
        recyclerOtroPerfil.setAdapter(adaptadorOtroPerfil);

        // Obtener UID del usuario a mostrar
        uidUsuario = getIntent().getStringExtra("uidUsuario");
        Log.d("PerfilUsuario", "UID recibido en intent: " + uidUsuario);
        if (uidUsuario == null) {
            uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("PerfilUsuario", "UID por defecto (usuario actual): " + uidUsuario);
        }

        cargarPublicacionesUsuario();
        cargarDatosUsuario();
        cargarSeguidores();
        cargarSeguidos();

    }

    private void cargarDatosUsuario() {
        Log.d("PerfilUsuario", "Cargando datos usuario UID: " + uidUsuario);
        db.collection("Usuarios").document(uidUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String descripcion = documentSnapshot.getString("descripcion");
                        String imagenPerfil = documentSnapshot.getString("imagenPerfilUrl");

                        Log.d("PerfilUsuario", "Usuario cargado: " + username);
                        txtNombreUsuario.setText(username != null ? username : "Usuario");
                        txtDescripcion.setText(descripcion != null ? descripcion : "");

                        if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
                            Log.d("PerfilUsuario", "Cargando imagen de perfil");
                            Glide.with(PerfilUsuario.this)
                                    .load(imagenPerfil)
                                    .placeholder(R.drawable.icon_me)
                                    .circleCrop()
                                    .into(iconPerfil);

                        } else {
                            Log.d("PerfilUsuario", "No hay imagen de perfil, uso icono por defecto");
                            iconPerfil.setImageResource(R.drawable.icon_me);
                        }
                    } else {
                        Log.d("PerfilUsuario", "No existe el documento del usuario");
                    }
                })
                .addOnFailureListener(e -> Log.e("PerfilUsuario", "Error cargando datos del usuario", e));
    }

    private void cargarSeguidores() {
        CollectionReference seguidoresRef = db.collection("seguidores").document(uidUsuario).collection("usuarios");
        seguidoresListener = seguidoresRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("PerfilUsuario", "Error cargando seguidores", e);
                return;
            }
            if (querySnapshot != null) {
                long count = querySnapshot.size();
                Log.d("PerfilUsuario", "Número seguidores: " + count);
                txtSeguidores.setText(String.valueOf(count));
            }
        });
    }

    private void cargarSeguidos() {
        CollectionReference seguidosRef = db.collection("seguidos").document(uidUsuario).collection("usuarios");
        seguidosListener = seguidosRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("PerfilUsuario", "Error cargando seguidos", e);
                return;
            }
            if (querySnapshot != null) {
                long count = querySnapshot.size();
                Log.d("PerfilUsuario", "Número seguidos: " + count);
                txtSeguidos.setText(String.valueOf(count));
            }
        });
    }

    private void cargarPublicacionesUsuario() {
        Log.d("PerfilUsuario", "Cargando publicaciones para UID: " + uidUsuario);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("publicaciones");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPublicaciones.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ItemPost post = postSnapshot.getValue(ItemPost.class);
                    if (uidUsuario.equals(post.getUidUsuario())) {

                        listaPublicaciones.add(post);
                        Log.d("PerfilUsuario", "Publicación cargada: " + post.getImagenPost());
                    }
                }

                if (listaPublicaciones.isEmpty()) {
                    Log.d("PerfilUsuario", "No hay publicaciones");
                }

                adaptadorOtroPerfil.notifyDataSetChanged();
                txtPublicaciones.setText(String.valueOf(listaPublicaciones.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PerfilUsuario", "Error al cargar publicaciones: " + error.getMessage());
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desconectar listeners para evitar memory leaks
        if (seguidoresListener != null) seguidoresListener.remove();
        if (seguidosListener != null) seguidosListener.remove();

    }
}
