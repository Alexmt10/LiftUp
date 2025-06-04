package com.iescamas.liftup.Adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemPost;
import com.iescamas.liftup.pojo.ItemEntrenoCompleto;
import com.iescamas.liftup.pojo.ItemSerie;
import com.iescamas.liftup.pojo.ItemAlimento;

import java.util.List;

/**
 * AdapterOtroPerfil es un adaptador personalizado para un RecyclerView que muestra
 * una lista de publicaciones ({@link ItemPost}) de otro perfil.
 */
public class AdapterOtroPerfil extends RecyclerView.Adapter<AdapterOtroPerfil.ViewHolder> {

    private final List<ItemPost> listaPostOtro;
    private final Context context;

    /**
     * Constructor para el adaptador.
     *
     * @param listaPost Lista de objetos {@link ItemPost} que se mostrarán.
     * @param context   Contexto de la aplicación.
     */
    public AdapterOtroPerfil(List<ItemPost> listaPost, Context context) {
        this.listaPostOtro = listaPost;
        this.context = context;
    }

    /**
     * Crea nuevas vistas (invocado por el layout manager).
     * @return Nuevo ViewHolder que contiene la vista para cada ítem.
     */
    @NonNull
    @Override
    public AdapterOtroPerfil.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Reemplaza el contenido de una vista (invocado por el layout manager).
     * Este método actualiza la vista del ViewHolder con los datos del ítem en la posición dada.
     *
     * @param holder   El ViewHolder que debe ser actualizado.
     * @param position La posición del ítem dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull AdapterOtroPerfil.ViewHolder holder, int position) {
        ItemPost post = listaPostOtro.get(position);

        Log.d("AdapterOtroPerfil", "Cargando post en posición: " + position);
        Log.d("AdapterOtroPerfil", "Post: " + post.toString());

        if (post.getImagenPostUrl() != null && !post.getImagenPostUrl().isEmpty()) {
            Log.d("AdapterOtroPerfil", "Cargando imagen desde URL: " + post.getImagenPostUrl());
            Glide.with(context).load(post.getImagenPostUrl()).into(holder.PublicaionPostId);
        } else if (post.getImagenPost() != 0) {
            Log.d("AdapterOtroPerfil", "Cargando imagen desde recurso local: " + post.getImagenPost());
            Glide.with(context).load(post.getImagenPost()).into(holder.PublicaionPostId);
        } else {
            Log.d("AdapterOtroPerfil", "No hay imagen. Se muestra imagen por defecto.");
            holder.PublicaionPostId.setImageResource(R.drawable.icon_match);
        }

        holder.RutinaPostId.setOnClickListener(v -> {
            if (post.getEntrenamiento() != null && post.getEntrenamiento().getEjercicios() != null) {
                Log.d("AdapterOtroPerfil", "Rutina encontrada para el post");
                StringBuilder mensaje = new StringBuilder();
                for (ItemEntrenoCompleto ejercicio : post.getEntrenamiento().getEjercicios()) {
                    mensaje.append("- ").append(ejercicio.getEjercicio()).append(":\n");
                    for (ItemSerie serie : ejercicio.getSeries()) {
                        mensaje.append("   • ")
                                .append(serie.getRepeticiones()).append(" reps x ")
                                .append(serie.getPeso()).append(" kg\n");
                    }
                }
                new AlertDialog.Builder(context)
                        .setTitle("Rutina: " + post.getEntrenamiento().getNombreEntrenamiento())
                        .setMessage(mensaje.toString())
                        .setPositiveButton("Cerrar", null)
                        .show();
            } else {
                Log.w("AdapterOtroPerfil", "No hay rutina en esta publicación");
                Toast.makeText(context, "No hay rutina en esta publicación", Toast.LENGTH_SHORT).show();
            }
        });

        holder.ComidaPostId.setOnClickListener(v -> {
            if (post.getComida() != null && post.getComida().getAlimentos() != null) {
                Log.d("AdapterOtroPerfil", "Comida encontrada para el post");
                StringBuilder mensaje = new StringBuilder();
                for (ItemAlimento alimento : post.getComida().getAlimentos()) {
                    mensaje.append("\n• ").append(alimento.getNombre())
                            .append(" (").append(alimento.getGramos()).append("g)")
                            .append("\n  Calorías: ").append(alimento.getCalorias()).append(" kcal")
                            .append("\n  Proteínas: ").append(alimento.getProteinas()).append("g")
                            .append("\n  Grasas: ").append(alimento.getGrasas()).append("g")
                            .append("\n  Carbohidratos: ").append(alimento.getCarbohidratos()).append("g\n");
                }

                new AlertDialog.Builder(context)
                        .setTitle("Comida: " + post.getComida().getNombre())
                        .setMessage(mensaje.toString())
                        .setPositiveButton("Cerrar", null)
                        .show();
            } else {
                Log.w("AdapterOtroPerfil", "No hay comida en esta publicación");
                Toast.makeText(context, "No hay comida en esta publicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Devuelve el número total de ítems en el conjunto de datos que tiene el adaptador.
     *
     * @return El número total de ítems.
     */
    @Override
    public int getItemCount() {
        Log.d("AdapterOtroPerfil", "Total de publicaciones: " + listaPostOtro.size());
        return listaPostOtro.size();
    }

    /**
     * ViewHolder describe una vista de ítem y metadatos sobre su lugar dentro del RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView PublicaionPostId;
        Button RutinaPostId;
        Button ComidaPostId;

        /**
         * Constructor para el ViewHolder.
         * @param itemView La vista que representa un solo ítem en la lista.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PublicaionPostId = itemView.findViewById(R.id.imgpublicacionPerfilId);
            RutinaPostId = itemView.findViewById(R.id.btnmostrarentrenamientoPerfilid);
            ComidaPostId = itemView.findViewById(R.id.btnmostrarcomidaPerfilid);
            Log.d("AdapterOtroPerfil", "ViewHolder creado");
        }
    }
}
