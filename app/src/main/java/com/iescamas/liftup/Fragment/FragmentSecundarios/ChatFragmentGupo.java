package com.iescamas.liftup.Fragment.FragmentSecundarios;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iescamas.liftup.Adaptadores.AdaptadorGrupoChat;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemGrupoChat;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Fragmento para gestionar los grupos de chat.
 * Permite crear nuevos grupos, visualizar los grupos existentes y participar en ellos.
 */
public class ChatFragmentGupo extends Fragment {

    /**
     * Botón flotante para añadir un nuevo grupo.
     */
    FloatingActionButton fatNuevoGrupo;
    /**
     * Código de solicitud para seleccionar una imagen.
     */
    private static final int REQUEST_CODE_IMAGEN = 1001;
    /**
     * URI de la imagen seleccionada.
     */
    private Uri imagenSeleccionadaUri;
    /**
     * RecyclerView para mostrar la lista de grupos.
     */
    private RecyclerView recyclerView;
    /**
     * Adaptador para el RecyclerView de grupos.
     */
    private AdaptadorGrupoChat adaptador;
    /**
     * Lista de objetos ItemGrupoChat que representan los grupos.
     */
    private List<ItemGrupoChat> listaGrupos = new ArrayList<>();
    /**
     * ImageView para mostrar el icono del grupo en el diálogo de creación.
     */
    private ImageView imagenViewDelDialogo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_gupo, container, false);

        fatNuevoGrupo = view.findViewById(R.id.floatingAnadirNuevoGrupoId);

        recyclerView = view.findViewById(R.id.recycler_grupos_chatsId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new AdaptadorGrupoChat(getContext(), listaGrupos);
        recyclerView.setAdapter(adaptador);

        cargarGruposDesdeFirebase();


        fatNuevoGrupo.setOnClickListener(v -> mostrarDialogoCrearGrupo());


        return view;
    }
    /**
     * Muestra un diálogo para crear un nuevo grupo.
     * Permite al usuario ingresar el nombre del grupo y seleccionar un icono.
     */
    private void mostrarDialogoCrearGrupo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_nuevo_grupo, null);

        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombreGrupoId);
        imagenViewDelDialogo = dialogView.findViewById(R.id.imageViewIconoGrupoId);


        imagenViewDelDialogo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_IMAGEN);
        });

        builder.setView(dialogView)
                .setTitle("Nuevo grupo")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombreGrupo = editTextNombre.getText().toString().trim();
                    if (!nombreGrupo.isEmpty() && imagenSeleccionadaUri != null) {
                        subirGrupoAFirebase(nombreGrupo, imagenSeleccionadaUri);
                    } else {
                        Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Sube la información del nuevo grupo a Firebase Storage y Firestore.
     * @param nombreGrupo El nombre del grupo.
     * @param imagenUri La URI del icono del grupo.
     */
    private void subirGrupoAFirebase(String nombreGrupo, Uri imagenUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String grupoId = UUID.randomUUID().toString();
        StorageReference refImagen = storage.getReference().child("iconos_grupos/" + grupoId + ".jpg");

        refImagen.putFile(imagenUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return refImagen.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> {
                    String urlImagen = uri.toString();
                    Map<String, Object> grupo = new HashMap<>();
                    grupo.put("id", grupoId);
                    grupo.put("nombre", nombreGrupo);
                    grupo.put("iconoUrl", urlImagen);
                    grupo.put("creadorId", auth.getCurrentUser().getUid());

                    // Guardar miembros como array
                    List<String> miembros = new ArrayList<>();
                    miembros.add(auth.getCurrentUser().getUid());
                    grupo.put("miembros", miembros);

                    firestore.collection("grupos").document(grupoId).set(grupo)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Grupo creado", Toast.LENGTH_SHORT).show();

                                Map<String, Object> grupoInfo = new HashMap<>();
                                grupoInfo.put("nombre", nombreGrupo);
                                grupoInfo.put("iconoUrl", urlImagen);

                                firestore.collection("Usuarios")
                                        .document(auth.getCurrentUser().getUid())
                                        .collection("grupos")
                                        .document(grupoId)
                                        .set(grupoInfo);
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al guardar grupo", Toast.LENGTH_SHORT).show());
                });
    }

    /**
     * Se llama cuando una actividad iniciada por este fragmento devuelve un resultado.
     * En este caso, se utiliza para manejar la selección de una imagen de la galería.
     * @param requestCode El código de solicitud original.
     * @param resultCode El código de resultado devuelto por la actividad.
     * @param data Los datos devueltos por la actividad (puede ser nulo).
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGEN && resultCode == getActivity().RESULT_OK && data != null) {
            imagenSeleccionadaUri = data.getData();


            if (imagenViewDelDialogo != null && imagenSeleccionadaUri != null) {
                Glide.with(getContext())
                        .load(imagenSeleccionadaUri)
                        .circleCrop()
                        .into(imagenViewDelDialogo);
            }
        }
    }
    /**
     * Carga los grupos desde Firebase Firestore en los que el usuario actual es miembro.
     * Actualiza el RecyclerView con los grupos cargados.
     */
    private void cargarGruposDesdeFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String miId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("grupos")
                .whereArrayContains("miembros", miId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error al cargar grupos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listaGrupos.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        String id = doc.getString("id");
                        String nombre = doc.getString("nombre");
                        String iconoUrl = doc.getString("iconoUrl");

                        listaGrupos.add(new ItemGrupoChat(id, nombre, iconoUrl));
                    }
                    adaptador.notifyDataSetChanged();
                });
    }
}