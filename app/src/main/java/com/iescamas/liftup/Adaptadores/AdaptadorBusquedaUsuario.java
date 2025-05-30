package com.iescamas.liftup.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.Usuario;
import com.iescamas.liftup.tipos.PerfilUsuario;

import java.util.List;

public class AdaptadorBusquedaUsuario extends RecyclerView.Adapter<AdaptadorBusquedaUsuario.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private Context contexto;
    private FirebaseFirestore db;
    private FirebaseAuth autenticacion;

    public AdaptadorBusquedaUsuario(List<Usuario> listaUsuarios, Context contexto) {
        this.listaUsuarios = listaUsuarios;
        this.contexto = contexto;
        this.db = FirebaseFirestore.getInstance();
        this.autenticacion = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_busqueda, parent, false);
        return new UsuarioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int posicion) {
        Usuario usuario = listaUsuarios.get(posicion);

        db.collection("Usuarios")
                .document(usuario.getIdUsuario())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreUsuario = documentSnapshot.getString("username");
                        String imagenPerfil = documentSnapshot.getString("imagenPerfilUrl");

                        holder.textoUsuario.setText(nombreUsuario != null ? nombreUsuario : "Usuario");

                        if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
                            Glide.with(contexto)
                                    .load(imagenPerfil)
                                    .circleCrop()
                                    .into(holder.imagenPerfil);
                        } else {
                            holder.imagenPerfil.setImageResource(R.drawable.icon_me);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FIREBASE", "Error obteniendo datos de usuario", e));

        verificarEstadoSeguimiento(usuario, holder.botonSeguir);

        holder.botonSeguir.setOnClickListener(v -> {
            alternarSeguimiento(usuario, holder.botonSeguir);
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(contexto, PerfilUsuario.class);
            intent.putExtra("uidUsuario", usuario.getIdUsuario());
            contexto.startActivity(intent);
        });
    }

    private void verificarEstadoSeguimiento(Usuario usuario, Button botonSeguir) {
        Log.d("SEGUIMIENTO", "Verificando seguimiento para: " + usuario.getUsername());

        db.collection("Usuarios")
                .document(autenticacion.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Usuario usuarioActual = documentSnapshot.toObject(Usuario.class);
                        if (usuarioActual != null && usuarioActual.getSiguiendo() != null &&
                                usuarioActual.getSiguiendo().contains(usuario.getIdUsuario())) {

                            botonSeguir.setText("Siguiendo");
                            botonSeguir.setBackgroundColor(
                                    ContextCompat.getColor(contexto, android.R.color.darker_gray));
                            Log.d("SEGUIMIENTO", "Ya sigue a " + usuario.getUsername());
                        } else {
                            botonSeguir.setText("Seguir");
                            botonSeguir.setBackgroundColor(
                                    ContextCompat.getColor(contexto, android.R.color.holo_blue_dark));
                            Log.d("SEGUIMIENTO", "No sigue a " + usuario.getUsername());
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("SEGUIMIENTO", "Error al verificar seguimiento", e));
    }


    private void alternarSeguimiento(Usuario usuario, Button botonSeguir) {
        String idUsuarioActual = autenticacion.getCurrentUser().getUid();

        db.collection("Usuarios")
                .document(idUsuarioActual)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Usuario usuarioActual = documentSnapshot.toObject(Usuario.class);

                    if (usuarioActual != null && usuarioActual.getSiguiendo() != null) {
                        boolean yaSigue = usuarioActual.getSiguiendo().contains(usuario.getIdUsuario());

                        if (yaSigue) {
                            Log.d("SEGUIMIENTO", "Dejando de seguir a: " + usuario.getUsername());

                            db.collection("usuarios").document(idUsuarioActual)
                                    .update("siguiendo", FieldValue.arrayRemove(usuario.getIdUsuario()));

                            db.collection("usuarios").document(usuario.getIdUsuario())
                                    .update("seguidores", FieldValue.arrayRemove(idUsuarioActual));

                            botonSeguir.setText("Seguir");
                            botonSeguir.setBackgroundColor(
                                    ContextCompat.getColor(contexto, android.R.color.holo_blue_dark));
                        } else {
                            Log.d("SEGUIMIENTO", "Siguiendo a: " + usuario.getUsername());

                            db.collection("usuarios").document(idUsuarioActual)
                                    .update("siguiendo", FieldValue.arrayUnion(usuario.getIdUsuario()));

                            db.collection("usuarios").document(usuario.getIdUsuario())
                                    .update("seguidores", FieldValue.arrayUnion(idUsuarioActual));

                            botonSeguir.setText("Siguiendo");
                            botonSeguir.setBackgroundColor(
                                    ContextCompat.getColor(contexto, android.R.color.darker_gray));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("SEGUIMIENTO", "Error al alternar seguimiento", e));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenPerfil;
        TextView textoUsuario;
        Button botonSeguir;

        public UsuarioViewHolder(@NonNull View vista) {
            super(vista);
            imagenPerfil = vista.findViewById(R.id.imagen_perfil);
            textoUsuario = vista.findViewById(R.id.texto_usuario);
            botonSeguir = vista.findViewById(R.id.boton_seguir);
        }
    }
}
