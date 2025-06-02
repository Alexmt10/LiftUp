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

public class AdaptadorYo extends RecyclerView.Adapter<AdaptadorYo.ViewHolder> {

    private final List<ItemPost> listaPost;
    private final Context context;

    public AdaptadorYo(List<ItemPost> listaPost, Context context) {
        this.listaPost = listaPost;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorYo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorYo.ViewHolder holder, int position) {
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
                Log.w("AdaptadorYo", "No hay rutina en esta publicación");
                Toast.makeText(context, "No hay rutina en esta publicación", Toast.LENGTH_SHORT).show();
            }
        });

        holder.ComidaPostId.setOnClickListener(v -> {
            if (post.getComida() != null && post.getComida().getAlimentos() != null) {
                Log.d("AdaptadorYo", "Comida encontrada para el post");
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
                Log.w("AdaptadorYo", "No hay comida en esta publicación");
                Toast.makeText(context, "No hay comida en esta publicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("AdaptadorYo", "Total de publicaciones: " + listaPost.size());
        return listaPost.size();
    }

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
