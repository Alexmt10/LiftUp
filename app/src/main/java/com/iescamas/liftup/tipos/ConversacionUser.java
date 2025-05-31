package com.iescamas.liftup.tipos;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
        setContentView(R.layout.activity_conversacion_user);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        idUsuarioDestino = getIntent().getStringExtra("idUsuarioDestino");
        idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                        if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
                            Glide.with(this)
                                    .load(imagenPerfil)
                                    .circleCrop()
                                    .into(imagenUsuarioChat);
                        } else {
                            imagenUsuarioChat.setImageResource(R.drawable.icon_me);
                        }
                    }
                });

        botonEnviar.setOnClickListener(v -> enviarMensaje());

        escucharMensajes();
    }

    private void enviarMensaje() {
        String texto = campoMensaje.getText().toString().trim();
        if (!texto.isEmpty()) {
            Mensaje mensaje = new Mensaje(idUsuarioActual, idUsuarioDestino, texto, new Date());
            db.collection("chats")
                    .add(mensaje);
            campoMensaje.setText("");
        }
    }

    private void escucharMensajes() {
        db.collection("chats")
                .orderBy("fecha", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error == null && value != null) {
                        listaMensajes.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Mensaje m = doc.toObject(Mensaje.class);
                            if ((m.getEmisor().equals(idUsuarioActual) && m.getReceptor().equals(idUsuarioDestino)) ||
                                    (m.getEmisor().equals(idUsuarioDestino) && m.getReceptor().equals(idUsuarioActual))) {
                                listaMensajes.add(m);
                            }
                        }
                        adaptador.notifyDataSetChanged();
                        recyclerMensajes.scrollToPosition(listaMensajes.size() - 1);
                    }
                });
    }
}