package com.iescamas.liftup.Adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log; // <--- IMPORTANTE
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
 * Adaptador para mostrar las publicaciones del usuario en un RecyclerView.
 *
 * Este adaptador se encarga de inflar el diseño de cada elemento de la lista
 * y vincular los datos de las publicaciones (imágenes, rutinas, comidas)
 * a las vistas correspondientes.
 */
public class AdaptadorYo extends RecyclerView.Adapter<AdaptadorYo.ViewHolder> {

    private final List<ItemPost> listaPost;
    private final Context context;

    /**
     * Constructor del adaptador.
     *
     * @param listaPost Lista de objetos {@link ItemPost} que se mostrarán.
     * @param context   Contexto de la aplicación.
     */
    public AdaptadorYo(List<ItemPost> listaPost, Context context) {
        this.listaPost = listaPost;
        this.context = context;
    }

    /**
     * Crea y devuelve un nuevo ViewHolder para un elemento de la lista.
     *
     * Este método se llama cuando el RecyclerView necesita un nuevo ViewHolder
     * para representar un elemento.
     *
     * @param parent   El ViewGroup al que se añadirá la nueva vista después de que se enlace a una posición del adaptador.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que contiene la vista para el elemento.
     */
    @NonNull
    @Override
    public AdaptadorYo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorYo.ViewHolder holder, int position) {
        /**
         * Vincula los datos de un elemento de la lista a las vistas del ViewHolder.
         *
         * Este método se llama para mostrar los datos en la posición especificada.
         * Actualiza el contenido de las vistas del ViewHolder para reflejar el elemento
         * en la posición dada.
         * @param holder   El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
         * @param position La posición del elemento dentro del conjunto de datos del adaptador.
         */
        ItemPost post = listaPost.get(position);

        Log.d("AdaptadorYo", "Cargando post en posición: " + position);
        Log.d("AdaptadorYo", "Post: " + post.toString());

        // Cargar imagen de publicación
        if (post.getImagenPostUrl() != null && !post.getImagenPostUrl().isEmpty()) {
            Log.d("AdaptadorYo", "Cargando imagen desde URL: " + post.getImagenPostUrl());
            Glide.with(context).load(post.getImagenPostUrl()).into(holder.PublicaionPostId);
        } else if (post.getImagenPost() != 0) {
            Log.d("AdaptadorYo", "Cargando imagen desde recurso local: " + post.getImagenPost());
            Glide.with(context).load(post.getImagenPost()).into(holder.PublicaionPostId);
        } else {
            Log.d("AdaptadorYo", "No hay imagen. Se muestra imagen por defecto.");
            holder.PublicaionPostId.setImageResource(R.drawable.icon_match);
        }

        holder.RutinaPostId.setOnClickListener(v -> {
            if (post.getEntrenamiento() != null && post.getEntrenamiento().getEjercicios() != null) {
                Log.d("AdaptadorYo", "Rutina encontrada para el post");
                StringBuilder mensaje = new StringBuilder();
                for (ItemEntrenoCompleto ejercicio : post.getEntrenamiento().getEjercicios()) {
                    mensaje.append("- ").append(ejercicio.getEjercicio()).append(":\n");
                    for (ItemSerie serie : ejercicio.getSeries()) {
                        mensaje.append("   • ").append(serie.getRepeticiones()).append(" reps x ").append(serie.getPeso()).append(" kg\n");
                    }
                }
                new AlertDialog.Builder(context).setTitle("Rutina: " + post.getEntrenamiento().getNombreEntrenamiento()).setMessage(mensaje.toString()).setPositiveButton("Cerrar", null).show();
            } else {
                Log.w("AdaptadorYo", "No hay rutina en esta publicación");
                Toast.makeText(context, "No hay rutina en esta publicación", Toast.LENGTH_SHORT).show();
            }
        });

        holder.ComidaPostId.setOnClickListener(v -> {
            if (post.getComida() != null && post.getComida().getAlimentos() != null) {
                Log.d("AdaptadorYo", "Comida encontrada para el post");
                StringBuilder mensaje = new StringBuilder();
                for (ItemAlimento alimento : post.getComida().getAlimentos()) {
                    mensaje.append("\n• ").append(alimento.getNombre()).append(" (").append(alimento.getGramos()).append("g)").append("\n  Calorías: ").append(alimento.getCalorias()).append(" kcal").append("\n  Proteínas: ").append(alimento.getProteinas()).append("g").append("\n  Grasas: ").append(alimento.getGrasas()).append("g").append("\n  Carbohidratos: ").append(alimento.getCarbohidratos()).append("g\n");
                }

                new AlertDialog.Builder(context).setTitle("Comida: " + post.getComida().getNombre()).setMessage(mensaje.toString()).setPositiveButton("Cerrar", null).show();
            } else {
                Log.w("AdaptadorYo", "No hay comida en esta publicación");
                Toast.makeText(context, "No hay comida en esta publicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos que maneja el adaptador.
     *
     * @return El número total de elementos en este adaptador.
     */
    @Override
    public int getItemCount() {
        Log.d("AdaptadorYo", "Total de publicaciones: " + listaPost.size());
        return listaPost.size();
    }

    /**
     * Clase ViewHolder que representa cada elemento de la lista.
     *
     * Contiene las referencias a las vistas (ImageView, Button)
     * que se utilizan para mostrar los datos de una publicación.
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView PublicaionPostId;
        Button RutinaPostId;
        Button ComidaPostId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PublicaionPostId = itemView.findViewById(R.id.imgpublicacionPerfilId);
            RutinaPostId = itemView.findViewById(R.id.btnmostrarentrenamientoPerfilid);
            ComidaPostId = itemView.findViewById(R.id.btnmostrarcomidaPerfilid);
            Log.d("AdaptadorYo", "ViewHolder creado");
        }
    }
}
