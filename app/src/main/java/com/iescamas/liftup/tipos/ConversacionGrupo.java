package com.iescamas.liftup.tipos;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import com.iescamas.liftup.Adaptadores.AdaptadorMensajesGrupo;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemMensajesGrupo;

import java.util.*;

public class ConversacionGrupo extends AppCompatActivity {

    private static final String TAG = "ConversacionGrupo";

    private String grupoId;
    private String usuarioActualId;
    private FirebaseFirestore db;
    private List<ItemMensajesGrupo> listaMensajes;
    private AdaptadorMensajesGrupo adaptador;
    private RecyclerView recyclerMensajes;
    private EditText campoMensaje;
    private ImageButton botonEnviar;
    TextView nombreGrupoTextView;
    ImageView imagenGrupoImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion_grupo);

        recyclerMensajes = findViewById(R.id.recycler_mensajes_grupo);
        campoMensaje = findViewById(R.id.campo_mensaje);
        botonEnviar = findViewById(R.id.boton_enviar);

        grupoId = getIntent().getStringExtra("idGrupo");

        if (grupoId == null) {
            Log.e(TAG, "ID del grupo es null. Cerrando actividad.");
            Toast.makeText(this, "Error al cargar el grupo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        usuarioActualId = FirebaseAuth.getInstance().getUid();
        if (usuarioActualId == null) {
            Log.e(TAG, "Usuario no autenticado. Cerrando actividad.");
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();


        cargarInfoGrupoDesdeFirestore();

        // Configurar mensajes
        listaMensajes = new ArrayList<>();
        adaptador = new AdaptadorMensajesGrupo(listaMensajes, usuarioActualId);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(adaptador);

        botonEnviar.setOnClickListener(v -> enviarMensaje());

        escucharMensajes();


        MaterialToolbar toolbar = findViewById(R.id.chat_toolbar_grupo);
        View customToolbarView = getLayoutInflater().inflate(R.layout.toolbar_contenido_grupo, toolbar, false);
        toolbar.addView(customToolbarView);

         nombreGrupoTextView = customToolbarView.findViewById(R.id.nombre_grupo_chat);
        imagenGrupoImageView = customToolbarView.findViewById(R.id.imagen_grupo_chat);
    }

    private void cargarInfoGrupoDesdeFirestore() {
        db.collection("grupos").document(grupoId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreGrupo = getIntent().getStringExtra("nombreGrupo");
                        String urlImagen = getIntent().getStringExtra("urlImagen");

                        if (nombreGrupo != null) {
                            nombreGrupoTextView.setText(nombreGrupo);
                        }

                        if (urlImagen != null && !urlImagen.isEmpty()) {
                            Glide.with(this)
                                    .load(urlImagen)
                                    .placeholder(R.drawable.icon_me)
                                    .circleCrop()
                                    .into(imagenGrupoImageView);
                        }
                    } else {
                        Log.w(TAG, "No se encontró el documento del grupo.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al cargar información del grupo", e));
    }

    private void enviarMensaje() {
        String texto = campoMensaje.getText().toString().trim();
        if (texto.isEmpty()) {
            Log.w(TAG, "Intento de enviar mensaje vacío.");
            return;
        }

        ItemMensajesGrupo mensaje = new ItemMensajesGrupo(usuarioActualId, texto, new Date(), grupoId);
        db.collection("chatsGrupales")
                .add(mensaje)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Mensaje enviado"))
                .addOnFailureListener(e -> Log.e(TAG, "Error al enviar mensaje", e));

        campoMensaje.setText("");
    }

    private void escucharMensajes() {
        db.collection("chatsGrupales")
                .orderBy("fecha", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error al escuchar mensajes", error);
                        return;
                    }

                    if (value == null) {
                        Log.w(TAG, "Snapshot de mensajes nulo");
                        return;
                    }

                    listaMensajes.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        ItemMensajesGrupo m = doc.toObject(ItemMensajesGrupo.class);
                        if (m != null && grupoId.equals(m.getGrupoId())) {
                            listaMensajes.add(m);
                        }
                    }

                    adaptador.notifyDataSetChanged();
                    recyclerMensajes.scrollToPosition(listaMensajes.size() - 1);
                    Log.d(TAG, "Mensajes cargados: " + listaMensajes.size());
                });
    }
}
