package com.iescamas.liftup.tipos;

import android.content.Intent;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.*;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iescamas.liftup.Adaptadores.AdaptadorMensajesGrupo;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemMensajesGrupo;

import java.util.*;

/**
 * Clase que representa la pantalla de conversación de un grupo.
 */
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
    private static final int REQUEST_CODE_IMAGE_PICK = 101;

    private DatabaseReference gruposRef;
    private StorageReference storageReference;


    /**
     * Método que se llama cuando se crea la actividad.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion_grupo);

        recyclerMensajes = findViewById(R.id.recycler_mensajes_grupo);
        campoMensaje = findViewById(R.id.campo_mensaje);
        botonEnviar = findViewById(R.id.boton_enviar);

        grupoId = getIntent().getStringExtra("idGrupo");

        gruposRef = FirebaseDatabase.getInstance().getReference("grupos").child(grupoId);
        storageReference = FirebaseStorage.getInstance().getReference("imagenes_grupos");

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

        listaMensajes = new ArrayList<>();
        adaptador = new AdaptadorMensajesGrupo(listaMensajes, usuarioActualId);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(adaptador);

        botonEnviar.setOnClickListener(v -> enviarMensaje());

        escucharMensajes();


        MaterialToolbar toolbar = findViewById(R.id.chat_toolbar_grupo);
        setSupportActionBar(toolbar);
        View customToolbarView = getLayoutInflater().inflate(R.layout.toolbar_contenido_grupo, toolbar, false);
        toolbar.addView(customToolbarView);

        nombreGrupoTextView = customToolbarView.findViewById(R.id.nombre_grupo_chat);
        imagenGrupoImageView = customToolbarView.findViewById(R.id.imagen_grupo_chat);
    }


    /**
     * Método que se llama para crear el menú de opciones de la actividad.
     *
     * @param menu Menú de opciones.
     * @return True si se creó el menú correctamente, false en caso contrario.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grupo, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(item.getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0);
            item.setTitle(spanString);
        }

        return true;
    }

    /**
     * Método que se llama cuando se selecciona una opción del menú.
     *
     * @param item Opción del menú seleccionada.
     * @return True si se manejó la selección correctamente, false en caso contrario.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_agregar_usuario) {
            agregarUsuario();
            return true;
        }

        if (itemId == R.id.menu_editar_nombre) {
            editarNombreGrupo();
            return true;
        }

        if (itemId == R.id.menu_editar_foto) {
            editarFotoGrupo();
            return true;
        }
        if (itemId == R.id.menu_ver_participantes) {
            verparticipantes();
            return true;

        }
        if (itemId == R.id.menu_ver_estadisticas) {
            Intent intent = new Intent(this, EstadisticasGrupo.class);
            startActivity(intent);
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Método para ver los participantes de un grupo.
     */
    private void verparticipantes() {
        String idGrupo = getIntent().getStringExtra("idGrupo");
        if (idGrupo == null) {
            Toast.makeText(this, "Error: ID del grupo no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference grupoRef = db.collection("grupos").document(idGrupo);

        grupoRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> datos = documentSnapshot.getData();
                if (datos != null && datos.containsKey("miembros")) {
                    List<String> miembros = (List<String>) datos.get("miembros");

                    if (miembros == null || miembros.isEmpty()) {
                        Toast.makeText(this, "Este grupo no tiene miembros", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<String> nombres = new ArrayList<>();

                    FirebaseFirestore.getInstance().collection("Usuarios")
                            .whereIn(FieldPath.documentId(), miembros)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    String username = doc.getString("username");
                                    if (username != null) {
                                        nombres.add(username);
                                    }
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle("Participantes del grupo");

                                String mensaje = nombres.isEmpty() ? "No hay participantes" : TextUtils.join("\n", nombres);
                                builder.setMessage(mensaje);

                                builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
                                builder.show();

                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al obtener participantes", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(this, "Este grupo no tiene miembros", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Grupo no encontrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al obtener el grupo", Toast.LENGTH_SHORT).show();
        });
    }


    /**
     * Método para agregar un usuario a un grupo.
     */
    private void agregarUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar usuario por username");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String username = input.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Debes escribir un username", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String idGrupo = getIntent().getStringExtra("idGrupo");

            if (idGrupo == null || idGrupo.isEmpty()) {
                Toast.makeText(this, "ID del grupo no disponible", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("Usuarios")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (!snapshot.isEmpty()) {
                                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                                    String userId = doc.getId();

                                    DocumentReference grupoRef = db.collection("grupos").document(idGrupo);
                                    grupoRef.update("miembros", FieldValue.arrayUnion(userId))
                                            .addOnSuccessListener(aVoid -> {

                                                db.collection("Usuarios")
                                                        .document(userId)
                                                        .collection("grupos")
                                                        .document(idGrupo)
                                                        .set(new HashMap<>())
                                                        .addOnSuccessListener(unused ->
                                                                Toast.makeText(this, "Usuario agregado al grupo", Toast.LENGTH_SHORT).show()
                                                        )
                                                        .addOnFailureListener(e ->
                                                                Toast.makeText(this, "Error agregando grupo al usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                        );
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, "Error agregando usuario al grupo: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                            );
                                    break;
                                }
                            } else {
                                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Error buscando usuario", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    /**
     * Método para editar la foto de un grupo.
     */
    private void editarFotoGrupo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK);
    }

    /**
     * Método que se llama cuando se recibe un resultado de una actividad iniciada.
     *
     * @param requestCode Código de solicitud de la actividad.
     * @param resultCode  Código de resultado de la actividad.
     * @param data        Datos devueltos por la actividad.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            if (imageUri != null) {
                subirFotoGrupo(imageUri);
            }
        }
    }

    /**
     * Método para subir la foto de un grupo a Firebase Storage.
     *
     * @param imageUri URI de la imagen a subir.
     */
    private void subirFotoGrupo(Uri imageUri) {
        String fileName = "grupo_" + grupoId + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageReference.child(fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String urlFoto = uri.toString();

                            db.collection("grupos").document(grupoId)
                                    .update("foto", urlFoto)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Foto del grupo actualizada", Toast.LENGTH_SHORT).show();
                                        Glide.with(this)
                                                .load(urlFoto)
                                                .placeholder(R.drawable.icon_me)
                                                .circleCrop()
                                                .into(imagenGrupoImageView);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error guardando foto: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Error subiendo foto: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    /**
     * Método para editar el nombre de un grupo.
     */
    private void editarNombreGrupo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar nombre del grupo");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = input.getText().toString().trim();
            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("grupos").document(grupoId)
                    .update("nombre", nuevoNombre)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                        nombreGrupoTextView.setText(nuevoNombre);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error actualizando nombre: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    /**
     * Método para cargar la información de un grupo desde Firestore.
     */
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

    /**
     * Método para enviar un mensaje a un grupo.
     */
    private void enviarMensaje() {
        String texto = campoMensaje.getText().toString().trim();
        if (texto.isEmpty()) {
            Log.w(TAG, "Intento de enviar mensaje vacío.");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        db.collection("Usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String username = documentSnapshot.getString("username");

                    ItemMensajesGrupo mensaje = new ItemMensajesGrupo(uid, texto, new Date(), grupoId);
                    mensaje.setEmisorUsername(username);

                    db.collection("chatsGrupales")
                            .add(mensaje)
                            .addOnSuccessListener(documentReference -> Log.d(TAG, "Mensaje enviado"))
                            .addOnFailureListener(e -> Log.e(TAG, "Error al enviar mensaje", e));
                });

        campoMensaje.setText("");
    }


    /**
     * Método para escuchar los mensajes de un grupo en tiempo real.
     */
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
