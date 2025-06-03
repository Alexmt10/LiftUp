package com.iescamas.liftup.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemLike;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String nombreRemitente = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        Log.d(TAG, "Intentando enviar notificación a usuario ID: " + likedUserId);

        if (likedUserToken == null || likedUserToken.isEmpty()) {
            Toast.makeText(getContext(), "El usuario no tiene token para notificaciones", Toast.LENGTH_SHORT).show();
            Log.w(TAG, " Token FCM del usuario destino es nulo o vacío");
            return;
        }

        db.collection("Usuarios").document(likedUserId)
                .collection("likesReceived")
                .document(currentUserId)
                .set(new ItemLike(currentUserId, ""))
                .addOnSuccessListener(aVoid -> Log.d(TAG, " Like registrado en Firestore para: " + likedUserId))
                .addOnFailureListener(e -> {
                    Log.e(TAG, " Error registrando like en Firestore", e);
                    Toast.makeText(getContext(), "Error registrando like", Toast.LENGTH_SHORT).show();
                });

        try {
            JSONObject notification = new JSONObject();
            notification.put("to", likedUserToken);

            JSONObject data = new JSONObject();
            data.put("title", "LIFTUP");
            data.put("body", (nombreRemitente != null ? nombreRemitente : "Alguien") + " quiere ir al gym contigo ");
            notification.put("notification", data);

            Log.d(TAG, "Notificación JSON construida: " + notification.toString());

            enviarFCM(notification);

        } catch (Exception e) {
            Log.e(TAG, "Error construyendo la notificación JSON", e);
        }
    }

    private void enviarFCM(JSONObject notification) {
        String url = "https://fcm.googleapis.com/fcm/send";
        Log.d(TAG, " Enviando notificación FCM a: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, notification,
                response -> Log.d(TAG, " Notificación FCM enviada con éxito. Respuesta: " + response.toString()),
                error -> Log.e(TAG, "Error al enviar notificación FCM", error)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=AIzaSyCP63kieiE79NUl8vhTCZVepzfmnI4m6sQ");
                headers.put("Content-Type", "application/json");
                Log.d(TAG, "🛡 Headers preparados para FCM");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext().getApplicationContext());
        Log.d(TAG, " Añadiendo solicitud a la cola de Volley");
        queue.add(request);
    }





}
