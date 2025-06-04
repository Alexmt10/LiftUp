package com.iescamas.liftup.Fragment.FragmentSecundarios;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import com.iescamas.liftup.Adaptadores.AdaptadorMensajes;
import com.iescamas.liftup.pojo.UsuarioChat;
import com.iescamas.liftup.Fragment.FragmentSecundarios.ChatFragmentGupo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.Adaptadores.AdaptadorConversaciones;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.Usuario;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Fragmento que muestra la lista de conversaciones del usuario.
 * Permite al usuario ver las personas con las que ha interactuado y acceder a los chats grupales.
 */

public class ChatFragment extends Fragment {

    private RecyclerView recyclerConversaciones;
    private AdaptadorConversaciones adaptador;
    private List<Usuario> listaUsuariosSeguidos;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ImageView grupo;

    /**
     * Se llama para que el fragmento instancie su vista de interfaz de usuario.
     *
     * @param inflater           El LayoutInflater que se puede usar para inflar cualquier vista en el fragmento.
     * @param container          Si no es nulo, esta es la vista principal a la que se debe adjuntar la interfaz de usuario del fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anteriormente.
     * @return                   Devuelve la Vista para la interfaz de usuario del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerConversaciones = view.findViewById(R.id.recycler_usuarios_chatsId);
        listaUsuariosSeguidos = new ArrayList<>();

        grupo = view.findViewById(R.id.imglistadoGruposId);

        grupo.setOnClickListener(v -> {
            ChatFragmentGupo chatFragmentGrupo = new ChatFragmentGupo();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameid, chatFragmentGrupo)
                    .addToBackStack(null)
                    .commit();
        });


        String usuarioActualId = auth.getCurrentUser().getUid();

        adaptador = new AdaptadorConversaciones(getContext(), listaUsuariosSeguidos, usuarioActualId);
        recyclerConversaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerConversaciones.setAdapter(adaptador);

        cargarConversaciones();


        return view;
    }

    private static final String TAG = "ChatFragment";

    /**
     * Carga las conversaciones del usuario actual desde Firestore y las muestra en el RecyclerView.
     */
    private void cargarConversaciones() {
        String miId = auth.getCurrentUser().getUid();
        Log.d(TAG, "Mi ID actual: " + miId);

        db.collection("chats")
                .whereEqualTo("emisor", miId)
                .get()
                .addOnSuccessListener(snapshotEmisor -> {
                    Log.d(TAG, "Mensajes enviados encontrados: " + snapshotEmisor.size());

                    db.collection("chats")
                            .whereEqualTo("receptor", miId)
                            .get()
                            .addOnSuccessListener(snapshotReceptor -> {
                                Log.d(TAG, "Mensajes recibidos encontrados: " + snapshotReceptor.size());

                                HashSet<String> idsConversados = new HashSet<>();

                                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : snapshotEmisor) {
                                    String receptorId = doc.getString("receptor");
                                    Log.d(TAG, "Mensaje enviado a: " + receptorId);
                                    if (receptorId != null && !receptorId.equals(miId)) {
                                        idsConversados.add(receptorId);
                                    }
                                }

                                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : snapshotReceptor) {
                                    String emisorId = doc.getString("emisor");
                                    Log.d(TAG, "Mensaje recibido de: " + emisorId);
                                    if (emisorId != null && !emisorId.equals(miId)) {
                                        idsConversados.add(emisorId);
                                    }
                                }

                                Log.d(TAG, "Usuarios únicos con los que he hablado: " + idsConversados.size());

                                listaUsuariosSeguidos.clear();

                                for (String idUsuario : idsConversados) {
                                    Log.d(TAG, "Obteniendo usuario: " + idUsuario);

                                    db.collection("Usuarios")
                                            .document(idUsuario)
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                Log.d(TAG, "Datos del documento bruto: " + documentSnapshot.getData());

                                                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                                if (usuario != null) {
                                                    usuario.setIdUsuario(documentSnapshot.getId());
                                                    listaUsuariosSeguidos.add(usuario);
                                                    adaptador.notifyDataSetChanged();
                                                    Log.d(TAG, "Usuario añadido al RecyclerView: " + usuario.getNombre());
                                                } else {
                                                    Log.d(TAG, "Usuario no encontrado o null para ID: " + idUsuario);
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e(TAG, "Error al obtener usuario: " + idUsuario, e));
                                }
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error al obtener mensajes recibidos", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener mensajes enviados", e));
    }


}