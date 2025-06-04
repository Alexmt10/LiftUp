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

/**
 * Adaptador para mostrar una lista de grupos de chat en un RecyclerView.
 * Cada ítem de la lista muestra el nombre y la imagen del grupo.
 * Al hacer clic en un ítem, se abre la actividad de conversación del grupo correspondiente.
 */
public class AdaptadorGrupoChat extends RecyclerView.Adapter<AdaptadorGrupoChat.GrupoChatViewHolder> {

    private Context context;
    private List<ItemGrupoChat> listaGrupos;

    /**
     * Constructor del adaptador.
     *
     * @param context     Contexto de la aplicación.
     * @param listaGrupos Lista de objetos {@link ItemGrupoChat} que representan los grupos.
     */
    public AdaptadorGrupoChat(Context context, List<ItemGrupoChat> listaGrupos) {
        this.context = context;
        this.listaGrupos = listaGrupos;
    }

    /**
     * Crea nuevas vistas (invocado por el layout manager).
     *
     * @param parent   El ViewGroup en el que se inflará la nueva vista después de que
     *                 se agregue a su hijo.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo {@link GrupoChatViewHolder} que contiene una vista para el tipo de elemento.
     */
    @NonNull
    @Override
    public GrupoChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_grupo_chat, parent, false);
        return new GrupoChatViewHolder(vista);
    }

    /**
     * Reemplaza el contenido de una vista (invocado por el layout manager).
     *
     * @param holder   El {@link GrupoChatViewHolder} que debe actualizarse para representar el
     *                 contenido del ítem en la posición dada en el conjunto de datos.
     * @param position La posición del ítem dentro del conjunto de datos del adaptador.
     */
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

    /**
     * Devuelve el número total de ítems en el conjunto de datos que tiene el adaptador.
     *
     * @return El número total de ítems en este adaptador.
     */
    @Override
    public int getItemCount() {
        return listaGrupos != null ? listaGrupos.size() : 0;
    }

    /**
     * ViewHolder para los ítems de grupo de chat.
     * Contiene las vistas para la imagen y el nombre del grupo.
     */
    public static class GrupoChatViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenGrupo;
        TextView nombreGrupo;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista del ítem del grupo de chat.
         */
        public GrupoChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenGrupo = itemView.findViewById(R.id.imagen_grupo);
            nombreGrupo = itemView.findViewById(R.id.nombre_grupo);
        }
    }

    public void actualizarLista(List<ItemGrupoChat> nuevosGrupos) {
        this.listaGrupos = nuevosGrupos;
        notifyDataSetChanged();
    }


}
