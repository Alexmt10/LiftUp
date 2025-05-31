package com.iescamas.liftup.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.iescamas.liftup.pojo.UsuarioChat;
import com.iescamas.liftup.tipos.ConversacionUser;

import java.util.List;

public class AdaptadorConversaciones extends RecyclerView.Adapter<AdaptadorConversaciones.ConversacionViewHolder> {

    private Context context;
    private List<Usuario> listaUsuarios;
    private String usuarioActualId; // Nuevo campo para identificar el usuario actual

    // Constructor modificado para incluir el ID del usuario actual
    public AdaptadorConversaciones(Context context, List<Usuario> listaUsuarios, String usuarioActualId) {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
        this.usuarioActualId = usuarioActualId;
    }

    @NonNull
    @Override
    public ConversacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversacion, parent, false);
        return new ConversacionViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

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
