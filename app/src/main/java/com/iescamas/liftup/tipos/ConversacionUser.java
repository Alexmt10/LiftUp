package com.iescamas.liftup.tipos;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.iescamas.liftup.Adaptadores.AdaptadorMensajes;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.Mensaje;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversacionUser extends AppCompatActivity {

    private static final String TAG = "ConversacionUser";

    private String idUsuarioDestino;
    private String idUsuarioActual;
    private FirebaseFirestore db;
    private List<Mensaje> listaMensajes;
    private AdaptadorMensajes adaptador;
    private RecyclerView recyclerMensajes;
    private EditText campoMensaje;
    private ImageButton botonEnviar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Iniciando actividad ConversacionUser");
        setContentView(R.layout.activity_conversacion_user);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        idUsuarioDestino = getIntent().getStringExtra("idUsuarioDestino");
        idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        Log.d(TAG, "onCreate: idUsuarioDestino = " + idUsuarioDestino);
        Log.d(TAG, "onCreate: idUsuarioActual = " + idUsuarioActual);

        if (idUsuarioActual == null) {
            Log.e(TAG, "onCreate: Usuario actual no autenticado");
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        recyclerMensajes = findViewById(R.id.recycler_mensajes);
        campoMensaje = findViewById(R.id.campo_mensaje);
        botonEnviar = findViewById(R.id.boton_enviar);

        listaMensajes = new ArrayList<>();
        adaptador = new AdaptadorMensajes(listaMensajes, idUsuarioActual);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(adaptador);

        TextView nombreUsuarioChat = findViewById(R.id.nombre_usuario_chat);
        ImageView imagenUsuarioChat = findViewById(R.id.imagen_usuario_chat);

        String usernameDestino = getIntent().getStringExtra("usernameDestino");
        nombreUsuarioChat.setText(usernameDestino != null ? usernameDestino : "Usuario");

        db.collection("Usuarios")
                .document(idUsuarioDestino)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imagenPerfil = documentSnapshot.getString("imagenPerfilUrl");
                        Log.d(TAG, "Imagen perfil obtenida: " + imagenPerfil);
                        if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
                            Glide.with(this)
                                    .load(imagenPerfil)
                                    .circleCrop()
                                    .into(imagenUsuarioChat);
                        } else {
                            Log.d(TAG, "No hay imagen de perfil, se usa imagen por defecto");
                            imagenUsuarioChat.setImageResource(R.drawable.icon_me);
                        }
                    } else {
                        Log.e(TAG, "Documento usuario destino no existe");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener usuario destino", e));

        botonEnviar.setOnClickListener(v -> enviarMensaje());

        escucharMensajes();
    }

    private void enviarMensaje() {
        String texto = campoMensaje.getText().toString().trim();
        Log.d(TAG, "enviarMensaje: Texto a enviar = \"" + texto + "\"");
        if (!texto.isEmpty()) {
            Mensaje mensaje = new Mensaje(idUsuarioActual, idUsuarioDestino, texto, new Date());
            db.collection("chats")
                    .add(mensaje)
                    .addOnSuccessListener(docRef -> Log.d(TAG, "Mensaje enviado con ID: " + docRef.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "Error al enviar mensaje", e));
            campoMensaje.setText("");
        } else {
            Log.d(TAG, "enviarMensaje: Texto vacío, no se envía nada");
        }
    }

    private void escucharMensajes() {
        Log.d(TAG, "escucharMensajes: Iniciando escucha en Firestore");
        db.collection("chats")
                .orderBy("fecha", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error al escuchar mensajes", error);
                        return;
                    }
                    if (value != null) {
                        listaMensajes.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Mensaje m = doc.toObject(Mensaje.class);
                            if (m != null) {
                                if ((m.getEmisor().equals(idUsuarioActual) && m.getReceptor().equals(idUsuarioDestino)) ||
                                        (m.getEmisor().equals(idUsuarioDestino) && m.getReceptor().equals(idUsuarioActual))) {
                                    listaMensajes.add(m);
                                }
                            } else {
                                Log.w(TAG, "Mensaje nulo recuperado de Firestore");
                            }
                        }
                        Log.d(TAG, "escucharMensajes: Mensajes filtrados: " + listaMensajes.size());
                        adaptador.notifyDataSetChanged();
                        if (!listaMensajes.isEmpty()) {
                            recyclerMensajes.scrollToPosition(listaMensajes.size() - 1);
                        }
                    } else {
                        Log.d(TAG, "escucharMensajes: snapshot vacío");
                    }
                });
    }
}
