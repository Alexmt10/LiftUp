package com.iescamas.liftup.Adaptadores;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.Usuario;

import com.iescamas.liftup.tipos.ConversacionUser;

import java.util.List;

/**
 * Adaptador para mostrar la lista de conversaciones en un RecyclerView.
 * Este adaptador maneja la creación de vistas para cada elemento de la lista y el enlace de datos a esas vistas.
 */
public class AdaptadorConversaciones extends RecyclerView.Adapter<AdaptadorConversaciones.ConversacionViewHolder> {

    private Context context;
    private List<Usuario> listaUsuarios;
    private String usuarioActualId;

    /**
     * Constructor del adaptador.
     *
     * @param context         El contexto de la aplicación.
     * @param listaUsuarios   La lista de usuarios con los que se tiene una conversación.
     * @param usuarioActualId El ID del usuario actualmente logueado.
     */
    public AdaptadorConversaciones(Context context, List<Usuario> listaUsuarios, String usuarioActualId) {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
        this.usuarioActualId = usuarioActualId;
    }

    /**
     * Crea nuevas vistas (invocado por el layout manager).
     */
    @NonNull
    @Override
    public ConversacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversacion, parent, false);
        return new ConversacionViewHolder(view);
    }

    /**
     * Reemplaza el contenido de una vista (invocado por el layout manager).
     */
    @Override
    public void onBindViewHolder(@NonNull ConversacionViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);


        holder.textoUsername.setText(usuario.getUsername());


        FirebaseFirestore.getInstance().collection("Usuarios")
                .document(usuario.getIdUsuario())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imagenPerfil = documentSnapshot.getString("imagenPerfilUrl");
                        if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
                            Glide.with(context)
                                    .load(imagenPerfil)
                                    .circleCrop()
                                    .into(holder.imagenPerfil);
                        } else {
                            holder.imagenPerfil.setImageResource(R.drawable.icon_me);
                        }
                    }
                });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConversacionUser.class);
            intent.putExtra("idUsuarioDestino", usuario.getIdUsuario());
            intent.putExtra("usernameDestino", usuario.getUsername());
            context.startActivity(intent);
        });


    }

    /**
     * Devuelve el tamaño de tu conjunto de datos (invocado por el layout manager).
     */
    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    /**
     * ViewHolder para los elementos de la conversación.
     * Mantiene las referencias a las vistas de cada elemento.
     */
    public static class ConversacionViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenPerfil;
        TextView textoUsername;



        public ConversacionViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenPerfil = itemView.findViewById(R.id.imagenPerfilConversacion);
            textoUsername = itemView.findViewById(R.id.textoUsernameConversacion);
        }
    }
}
