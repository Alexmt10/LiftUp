package com.iescamas.liftup.Adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemAlimento;
import com.iescamas.liftup.pojo.ItemEntrenoCompleto;
import com.iescamas.liftup.pojo.ItemPost;
import com.iescamas.liftup.pojo.ItemSerie;


import java.util.List;

public class AdaptadorPubli extends RecyclerView.Adapter<AdaptadorPubli.ViewHolder> {

    private final List<ItemPost> listaPost;
    private final Context context;

    public AdaptadorPubli(List<ItemPost> listaPost, Context context) {
        this.listaPost = listaPost;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorPubli.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPubli.ViewHolder holder, int position) {
        ItemPost itemPost = listaPost.get(position);
        Log.d("AdaptadorPubli", "onBindViewHolder: Cargando post en posición " + position);

        holder.NombreUsuarioPostId.setText("Cargando...");

        if (itemPost.getUidUsuario() != null && !itemPost.getUidUsuario().isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("Usuarios")
                    .document(itemPost.getUidUsuario())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreUsuario = documentSnapshot.getString("username");

                            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                                holder.NombreUsuarioPostId.setText(nombreUsuario);
                            } else {
                                holder.NombreUsuarioPostId.setText("Usuario desconocido");
                            }
                        } else {
                            holder.NombreUsuarioPostId.setText("Usuario no encontrado");
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.NombreUsuarioPostId.setText("Error al cargar");
                        Log.e("AdaptadorPubli", "Error al leer Firestore", e);
                    });

        } else {
            holder.NombreUsuarioPostId.setText("UID no válido");
        }


        if (itemPost.getDescripcion() != null) {
            Log.d("AdaptadorPubli", "Descripción: " + itemPost.getDescripcion());
            holder.DescripcionPostId.setText(itemPost.getDescripcion());
        } else {
            Log.w("AdaptadorPubli", "Descripción es null");
            holder.DescripcionPostId.setText("");
        }

        holder.NumeroMegustaPostId.setText("0");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uidUsuario = itemPost.getUidUsuario();

        if (uidUsuario != null && !uidUsuario.isEmpty()) {
            db.collection("Usuarios").document(uidUsuario)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imagenUrl = documentSnapshot.getString("imagenPerfilUrl");

                            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                                Glide.with(holder.itemView.getContext())
                                        .load(imagenUrl)
                                        .transform(new CircleCrop())
                                        .placeholder(R.drawable.messi)
                                        .error(R.drawable.cristiano)
                                        .into(holder.IconoPostId);

                                Log.d("AdaptadorPubli", "Imagen de usuario cargada correctamente con Glide");
                            } else {
                                Log.w("AdaptadorPubli", "URL de imagen vacía o null");
                            }
                        } else {
                            Log.w("AdaptadorPubli", "No existe el documento del usuario en Firestore");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AdaptadorPubli", "Error al obtener imagen del usuario desde Firestore", e);
                    });
        } else {
            Log.e("AdaptadorPubli", "El UID del usuario en itemPost es null o vacío");
        }



        // Imagen de la publicación
        try {
            if (itemPost.getImagenPostUrl() != null && !itemPost.getImagenPostUrl().isEmpty()) {
                Log.d("AdaptadorPubli", "Cargando imagen desde URL: " + itemPost.getImagenPostUrl());
                Glide.with(context)
                        .load(itemPost.getImagenPostUrl())
                        .into(holder.PublicaionPostId);
            } else if (itemPost.getImagenPost() != 0) {
                Log.d("AdaptadorPubli", "Cargando imagen de publicación con ID: " + itemPost.getImagenPost());
                Glide.with(context)
                        .load(itemPost.getImagenPost())
                        .into(holder.PublicaionPostId);
            } else {
                Log.w("AdaptadorPubli", "No hay imagen de publicación disponible, usando imagen por defecto");
                holder.PublicaionPostId.setImageResource(R.drawable.icon_match);
            }
        } catch (Exception e) {
            Log.e("AdaptadorPubli", "Error al cargar imagen de publicación", e);
        }

        holder.MegustaPostId.setImageResource(R.drawable.icon_match);
        holder.MensajesPostId.setImageResource(R.drawable.icon_comentario);
        holder.RutinaPostId.setImageResource(R.drawable.icon_mancuerna);
        holder.ComidaPostId.setImageResource(R.drawable.icon_cubiertoo);



        holder.RutinaPostId.setOnClickListener(v -> {
            if (itemPost.getEntrenamiento() != null && itemPost.getEntrenamiento().getEjercicios() != null) {
                StringBuilder mensaje = new StringBuilder();
                for (ItemEntrenoCompleto ejercicio : itemPost.getEntrenamiento().getEjercicios()) {
                    mensaje.append("- ").append(ejercicio.getEjercicio()).append(":\n");
                    for (ItemSerie serie : ejercicio.getSeries()) {
                        mensaje.append("   • ").append(serie.getRepeticiones())
                                .append(" reps x ").append(serie.getPeso()).append(" kg\n");
                    }
                }
                new AlertDialog.Builder(context)
                        .setTitle("Rutina: " + itemPost.getEntrenamiento().getNombreEntrenamiento())
                        .setMessage(mensaje.toString())
                        .setPositiveButton("Cerrar", null)
                        .show();
            } else {
                Toast.makeText(context, "No hay rutina en esta publicación", Toast.LENGTH_SHORT).show();
            }
        });

        // Mostrar comida
        holder.ComidaPostId.setOnClickListener(v -> {
            if (itemPost.getComida() != null && itemPost.getComida().getAlimentos() != null) {
                StringBuilder mensaje = new StringBuilder();
                for (ItemAlimento alimento : itemPost.getComida().getAlimentos()) {
                    mensaje.append("\n• ").append(alimento.getNombre())
                            .append(" (").append(alimento.getGramos()).append("g)")
                            .append("\n  Calorías: ").append(alimento.getCalorias()).append(" kcal")
                            .append("\n  Proteínas: ").append(alimento.getProteinas()).append("g")
                            .append("\n  Grasas: ").append(alimento.getGrasas()).append("g")
                            .append("\n  Carbohidratos: ").append(alimento.getCarbohidratos()).append("g\n");
                }

                new AlertDialog.Builder(context)
                        .setTitle("Comida: " + itemPost.getComida().getNombre())
                        .setMessage(mensaje.toString())
                        .setPositiveButton("Cerrar", null)
                        .show();
            } else {
                Toast.makeText(context, "No hay comida en esta publicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView NombreUsuarioPostId;
        ImageView IconoPostId;
        ImageView PublicaionPostId;
        ImageView MegustaPostId;
        TextView NumeroMegustaPostId;
        ImageView MensajesPostId;
        ImageView RutinaPostId;
        ImageView ComidaPostId;
        TextView DescripcionPostId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            NombreUsuarioPostId = itemView.findViewById(R.id.txtNombreUsuarioPostId);
            IconoPostId = itemView.findViewById(R.id.ImgIconoPostId);
            PublicaionPostId = itemView.findViewById(R.id.ImgPublicaionPostId);
            MegustaPostId = itemView.findViewById(R.id.iconMegustaPostId);
            NumeroMegustaPostId = itemView.findViewById(R.id.txtNumeroMegustaPostId);
            MensajesPostId = itemView.findViewById(R.id.iconMensajesPostId);
            RutinaPostId = itemView.findViewById(R.id.iconRutinaPostId);
            ComidaPostId = itemView.findViewById(R.id.iconComidaPostId);
            DescripcionPostId = itemView.findViewById(R.id.txtComentarioPostId);
        }
    }
}
