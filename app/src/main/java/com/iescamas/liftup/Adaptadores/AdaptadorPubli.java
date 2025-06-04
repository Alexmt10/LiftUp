package com.iescamas.liftup.Adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

/**
 * Adaptador para el RecyclerView que muestra las publicaciones.
 */
public class AdaptadorPubli extends RecyclerView.Adapter<AdaptadorPubli.ViewHolder> {

    private final List<ItemPost> listaPost;
    private final Context context;

    /**
     * Constructor del adaptador.
     *
     * @param listaPost Lista de publicaciones a mostrar.
     * @param context   Contexto de la aplicación.
     */
    public AdaptadorPubli(List<ItemPost> listaPost, Context context) {
        this.listaPost = listaPost;
        this.context = context;
    }

    /**
     * Crea una nueva vista para un elemento del RecyclerView.
     *
     * @param parent   El ViewGroup al que se añadirá la nueva vista después de que se vincule a una posición del adaptador.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que contiene la vista para el elemento.
     */
    @NonNull
    @Override
    public AdaptadorPubli.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Vincula los datos de un elemento del RecyclerView con la vista correspondiente.
     *
     * @param holder   El ViewHolder que debe ser actualizado para representar el contenido del elemento en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull AdaptadorPubli.ViewHolder holder, int position) {
        ItemPost itemPost = listaPost.get(position);
        Log.d("AdaptadorPubli", "onBindViewHolder: Cargando post en posición " + position);

        holder.NombreUsuarioPostId.setText("Cargando...");
        if (itemPost.isLeGusta()) {
            holder.MegustaPostId.setImageResource(R.drawable.icon_corazon_rojo);
        } else {
            holder.MegustaPostId.setImageResource(R.drawable.icon_match);
        }


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
        holder.MegustaPostId.setOnClickListener(v -> {
            boolean leGustaAntes = itemPost.isLeGusta();
            boolean leGustaNuevo = !leGustaAntes;
            itemPost.setLeGusta(leGustaNuevo);

            if (leGustaNuevo) {
                holder.MegustaPostId.setImageResource(R.drawable.icon_corazon_rojo);
                Toast.makeText(context, "¡Te gusta esta publicación!", Toast.LENGTH_SHORT).show();
                Log.d("AdaptadorPubli", "Like activado en posición " + holder.getAdapterPosition());
            } else {
                holder.MegustaPostId.setImageResource(R.drawable.icon_match);
                Toast.makeText(context, "Ya no te gusta esta publicación", Toast.LENGTH_SHORT).show();
                Log.d("AdaptadorPubli", "Like desactivado en posición " + holder.getAdapterPosition());
            }

            try {
                int numActual = Integer.parseInt(holder.NumeroMegustaPostId.getText().toString());
                int nuevoValor = leGustaNuevo ? numActual + 1 : Math.max(0, numActual - 1);
                holder.NumeroMegustaPostId.setText(String.valueOf(nuevoValor));
            } catch (NumberFormatException e) {
                Log.e("AdaptadorPubli", "Error al parsear número de me gustas", e);
            }
        });

    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos que tiene el adaptador.
     *
     * @return El número total de elementos en este adaptador.
     */
    @Override
    public int getItemCount() {
        return listaPost.size();
    }

    /**
     * ViewHolder para los elementos del RecyclerView.
     * <p>
     * Contiene las vistas que se mostrarán para cada publicación.
     * </p>
     */
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

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista del elemento.
         */
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
