package com.iescamas.liftup.Fragment.FragmentSecundarios;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.Adaptadores.AdaptadorConversaciones;
import com.iescamas.liftup.Adaptadores.AdaptadorMensajes;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.Usuario;
import com.iescamas.liftup.pojo.UsuarioChat;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerConversaciones;
    private AdaptadorConversaciones adaptador;
    private List<UsuarioChat> listaUsuariosSeguidos;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        listaUsuariosSeguidos = new ArrayList<>();


        String usuarioActualId = auth.getCurrentUser().getUid();

        adaptador = new AdaptadorConversaciones(getContext(), listaUsuariosSeguidos, usuarioActualId);
        recyclerConversaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerConversaciones.setAdapter(adaptador);

        cargarUsuariosSeguidos();

        return view;
    }

    private void cargarUsuariosSeguidos() {
        String usuarioActualId = auth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(usuarioActualId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Usuario usuarioActual = documentSnapshot.toObject(Usuario.class);
                    if (usuarioActual != null && usuarioActual.getSiguiendo() != null) {
                        for (String usuarioId : usuarioActual.getSiguiendo()) {
                            obtenerDetallesUsuario(usuarioId);
                        }
                    }
                });
    }

    private void obtenerDetallesUsuario(String usuarioId) {
        db.collection("usuarios")
                .document(usuarioId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    UsuarioChat usuario = documentSnapshot.toObject(UsuarioChat.class);
                    if (usuario != null) {
                        usuario.setId(documentSnapshot.getId());
                        listaUsuariosSeguidos.add(usuario);
                        adaptador.notifyDataSetChanged();
                    }
                });
    }
}