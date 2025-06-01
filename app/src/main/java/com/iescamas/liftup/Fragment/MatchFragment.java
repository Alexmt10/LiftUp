package com.iescamas.liftup.Fragment;

import android.os.Bundle;
import android.util.Log;  // <--- Importa Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class MatchFragment extends Fragment {

    private static final String TAG = "MatchFragment";

    private ImageView imgPerfil;
    private TextView txtNombre, txtGym;
    private Button btnNoLike, btnLike;

    private FirebaseFirestore db;
    private String currentUserId;
    private String currentUserGym;

    private List<DocumentSnapshot> userList = new ArrayList<>();
    private int currentIndex = 0;

    public MatchFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_match, container, false);

        imgPerfil = view.findViewById(R.id.imgPerfil);
        txtNombre = view.findViewById(R.id.txtNombre);
        txtGym = view.findViewById(R.id.txtGym);
        btnNoLike = view.findViewById(R.id.btnNoLike);
        btnLike = view.findViewById(R.id.btnLike);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Usuario actual ID: " + currentUserId);

        db.collection("Usuarios").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUserGym = documentSnapshot.getString("gym");
                        Log.d(TAG, "Gym del usuario actual: " + currentUserGym);
                        if (currentUserGym != null) {
                            cargarUsuariosDelMismoGym();
                        } else {
                            Toast.makeText(getContext(), "No tienes gimnasio asignado", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Usuario sin gym asignado");
                        }
                    } else {
                        Log.w(TAG, "Documento usuario no existe");
                        Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error obteniendo usuario", e);
                    Toast.makeText(getContext(), "Error obteniendo usuario", Toast.LENGTH_SHORT).show();
                });

        btnNoLike.setOnClickListener(v -> {
            Log.d(TAG, "Botón NO like pulsado");
            mostrarSiguienteUsuario();
        });

        btnLike.setOnClickListener(v -> {
            if (userList.isEmpty()) {
                Log.w(TAG, "Lista de usuarios vacía, no se puede dar like");
                return;
            }

            DocumentSnapshot likedUser = userList.get(currentIndex);
            Log.d(TAG, "Botón LIKE pulsado para usuario: " + likedUser.getId());

            if (likedUser.getId().equals(currentUserId)) {
                Toast.makeText(getContext(), "No puedes darte like a ti mismo", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Intento de darse like a sí mismo");
                mostrarSiguienteUsuario();
                return;
            }

            enviarNotificacionLike(likedUser);
            Toast.makeText(getContext(), "Notificación enviada a " + likedUser.getString("nombre"), Toast.LENGTH_SHORT).show();
            mostrarSiguienteUsuario();
        });

        return view;
    }

    private void cargarUsuariosDelMismoGym() {
        Log.d(TAG, "Cargando usuarios del mismo gym: " + currentUserGym);
        db.collection("Usuarios")
                .whereEqualTo("gym", currentUserGym)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        if (!doc.getId().equals(currentUserId)) {
                            userList.add(doc);
                            Log.d(TAG, "Usuario añadido: " + doc.getString("nombre"));
                        }
                    }

                    if (userList.isEmpty()) {
                        Log.w(TAG, "No hay usuarios en tu gimnasio");
                        txtNombre.setText("No hay usuarios en tu gimnasio");
                        txtGym.setText("");
                        imgPerfil.setImageResource(R.drawable.icon_me);
                        btnNoLike.setEnabled(false);
                        btnLike.setEnabled(false);
                    } else {
                        currentIndex = 0;
                        Log.d(TAG, "Usuarios cargados: " + userList.size());
                        mostrarUsuarioActual();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error cargando usuarios", e);
                    Toast.makeText(getContext(), "Error cargando usuarios", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarUsuarioActual() {
        if (userList.isEmpty()) {
            Log.w(TAG, "No hay usuarios disponibles para mostrar");
            txtNombre.setText("No hay usuarios disponibles");
            txtGym.setText("");
            imgPerfil.setImageResource(R.drawable.icon_me);
            return;
        }
        DocumentSnapshot user = userList.get(currentIndex);

        String nombre = user.getString("nombre");
        String gym = user.getString("gym");
        String fotoUrl = user.getString("imagenPerfilUrl");

        Log.d(TAG, "Mostrando usuario: " + nombre + " del gym: " + gym);

        txtNombre.setText(nombre != null ? nombre : "Nombre no disponible");
        txtGym.setText(gym != null ? "Gimnasio: " + gym : "Gimnasio no disponible");

        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(fotoUrl)
                    .placeholder(R.drawable.icon_me)
                    .into(imgPerfil);
        } else {
            imgPerfil.setImageResource(R.drawable.icon_me);
        }
    }

    private void mostrarSiguienteUsuario() {
        if (userList.isEmpty()) {
            Log.w(TAG, "Lista de usuarios vacía, no hay siguiente usuario");
            return;
        }

        currentIndex++;
        if (currentIndex >= userList.size()) {
            currentIndex = 0;
            Toast.makeText(getContext(), "No hay más usuarios", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Se ha llegado al final de la lista de usuarios");
        }
        mostrarUsuarioActual();
    }

    private void enviarNotificacionLike(DocumentSnapshot likedUser) {
        String likedUserId = likedUser.getId();
        String likedUserToken = likedUser.getString("fcmToken");

        Log.d(TAG, "Preparando para enviar notificación a: " + likedUserId);

        if (likedUserToken == null || likedUserToken.isEmpty()) {
            Toast.makeText(getContext(), "El usuario no tiene token para notificaciones", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Usuario sin token FCM");
            return;
        }

        db.collection("Usuarios").document(likedUserId)
                .collection("likesReceived")
                .document(currentUserId)
                .set(new ItemLike(currentUserId, ""))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Like registrado en Firestore"))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error registrando like en Firestore", e);
                    Toast.makeText(getContext(), "Error enviando notificación", Toast.LENGTH_SHORT).show();
                });

        Log.d(TAG, "Notificación enviada (simulada) a token: " + likedUserToken);
    }

}
