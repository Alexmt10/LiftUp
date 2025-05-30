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
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.UsuarioChat;
import com.iescamas.liftup.tipos.ConversacionUser;

import java.util.List;

public class AdaptadorConversaciones extends RecyclerView.Adapter<AdaptadorConversaciones.ConversacionViewHolder> {

    private Context context;
    private List<UsuarioChat> listaUsuarios;
    private String usuarioActualId; // Nuevo campo para identificar el usuario actual

    // Constructor modificado para incluir el ID del usuario actual
    public AdaptadorConversaciones(Context context, List<UsuarioChat> listaUsuarios, String usuarioActualId) {
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
        UsuarioChat usuario = listaUsuarios.get(position);

        holder.textoNombre.setText(usuario.getNombreCompleto());
        holder.textoUsername.setText("@" + usuario.getUsername());

        // Cargar imagen de perfil con Glide
        if (usuario.getImagenPerfil() != null && !usuario.getImagenPerfil().isEmpty()) {
            Glide.with(context)
                    .load(usuario.getImagenPerfil())
                    .circleCrop()
                    .into(holder.imagenPerfil);
        } else {
            holder.imagenPerfil.setImageResource(R.drawable.icon_me);
        }

        // Configurar el clic en el item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConversacionUser.class);
            intent.putExtra("usuarioId", usuario.getId());
            intent.putExtra("usuarioNombre", usuario.getNombreCompleto());
            context.startActivity(intent);
        });

        // Aquí podrías añadir lógica adicional como mostrar el último mensaje, etc.
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class ConversacionViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenPerfil;
        TextView textoNombre;
        TextView textoUsername;

        public ConversacionViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenPerfil = itemView.findViewById(R.id.imagenPerfilConversacion);
            textoNombre = itemView.findViewById(R.id.textoNombreConversacion);
            textoUsername = itemView.findViewById(R.id.textoUsernameConversacion);
        }
    }
}
