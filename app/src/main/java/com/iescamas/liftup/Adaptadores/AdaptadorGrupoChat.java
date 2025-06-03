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
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemGrupoChat;
import com.iescamas.liftup.tipos.ConversacionGrupo;

import java.util.List;

public class AdaptadorGrupoChat extends RecyclerView.Adapter<AdaptadorGrupoChat.GrupoChatViewHolder> {

    private Context context;
    private List<ItemGrupoChat> listaGrupos;

    public AdaptadorGrupoChat(Context context, List<ItemGrupoChat> listaGrupos) {
        this.context = context;
        this.listaGrupos = listaGrupos;
    }

    @NonNull
    @Override
    public GrupoChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_grupo_chat, parent, false);
        return new GrupoChatViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoChatViewHolder holder, int position) {
        ItemGrupoChat grupo = listaGrupos.get(position);
        Log.d("AdaptadorGrupoChat", "Cargando grupo en posición " + position + ": " + grupo.getNombreGrupo() + ", ID: " + grupo.getIdGrupo());

        holder.nombreGrupo.setText(grupo.getNombreGrupo());

        if (grupo.getImagenGrupoUrl() != null && !grupo.getImagenGrupoUrl().isEmpty()) {
            Log.d("AdaptadorGrupoChat", "Cargando imagen del grupo: " + grupo.getImagenGrupoUrl());
            Glide.with(context).load(grupo.getImagenGrupoUrl()).circleCrop().into(holder.imagenGrupo);
        } else {
            Log.d("AdaptadorGrupoChat", "Grupo sin imagen, uso imagen por defecto");
            holder.imagenGrupo.setImageResource(R.drawable.icon_grupo);
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d("AdaptadorGrupoChat", "Grupo clicado: " + grupo.getNombreGrupo() + ", ID: " + grupo.getIdGrupo());
            Intent intent = new Intent(context, ConversacionGrupo.class);
            intent.putExtra("idGrupo", grupo.getIdGrupo());
            intent.putExtra("nombreGrupo", grupo.getNombreGrupo());
            intent.putExtra("urlImagen", grupo.getImagenGrupoUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaGrupos != null ? listaGrupos.size() : 0;
    }

    public static class GrupoChatViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenGrupo;
        TextView nombreGrupo;

        public GrupoChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenGrupo = itemView.findViewById(R.id.imagen_grupo);
            nombreGrupo = itemView.findViewById(R.id.nombre_grupo);
        }
    }


}
