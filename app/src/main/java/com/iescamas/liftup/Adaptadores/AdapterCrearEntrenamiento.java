package com.iescamas.liftup.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemEntrenamiento;
import com.iescamas.liftup.pojo.ItemEntrenoCompleto;
import com.iescamas.liftup.pojo.ItemSerie;

import java.util.List;

/**
 * Clase AdapterCrearEntrenamiento para mostrar una lista de entrenamientos en un RecyclerView.
 * Esta clase maneja la creación de vistas para cada elemento de la lista y el enlace de datos
 * a esas vistas. También maneja las interacciones del usuario, como hacer clic en un elemento
 * para ver detalles o hacer un clic largo para seleccionar un entrenamiento.
 */
public class AdapterCrearEntrenamiento extends RecyclerView.Adapter<AdapterCrearEntrenamiento.ViewHolderEntreno> {
    private final List<ItemEntrenamiento> listaEntrenamientos;
    private final Context context;

    /**
     * Constructor para AdapterCrearEntrenamiento.
     *
     * @param listaEntrenamientos Lista de objetos ItemEntrenamiento que se mostrarán.
     * @param context             Contexto de la aplicación o actividad.
     *                            Este constructor inicializa el adaptador con la lista de entrenamientos
     *                            y el contexto necesarios para inflar vistas y realizar otras operaciones
     *                            dependientes del contexto.
     */
    public AdapterCrearEntrenamiento(List<ItemEntrenamiento> listaEntrenamientos, Context context) {
        this.listaEntrenamientos = listaEntrenamientos;
        this.context = context;
    }

    @NonNull
    @Override
    /**
     * Se llama cuando RecyclerView necesita una nueva ViewHolder del tipo dado para representar un elemento.
     *
     * @param parent   El ViewGroup en el que se agregará la nueva vista después de que se vincule a una posición de adaptador.
     * @param viewType El tipo de vista de la nueva Vista.
     * @return Un nuevo ViewHolder que contiene una Vista del tipo de vista dado.
     */
    public ViewHolderEntreno onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrenamiento, parent, false);
        return new ViewHolderEntreno(view);
    }

    @Override
    /**
     * Se llama por RecyclerView para mostrar los datos en la posición especificada.
     * Este método debe actualizar el contenido de itemView para reflejar el elemento en la posición dada.
     *
     * @param holder   La ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    public void onBindViewHolder(@NonNull ViewHolderEntreno holder, int position) {
        ItemEntrenamiento item = listaEntrenamientos.get(position);

        holder.nombre.setText(item.getNombreEntrenamiento());

        holder.itemView.setOnClickListener(v -> {
            StringBuilder mensaje = new StringBuilder();

            List<ItemEntrenoCompleto> ejercicios = item.getEjercicios();

            if (ejercicios != null && !ejercicios.isEmpty()) {
                mensaje.append("Ejercicios:\n");

                for (ItemEntrenoCompleto ejercicio : ejercicios) {
                    mensaje.append("• ").append(ejercicio.getMusculo()).append(" - ").append(ejercicio.getEjercicio()).append(":\n");

                    List<ItemSerie> series = ejercicio.getSeries();
                    if (series != null && !series.isEmpty()) {
                        for (ItemSerie serie : series) {
                            mensaje.append("   • ")
                                    .append(serie.getRepeticiones()).append(" reps x ")
                                    .append(serie.getPeso()).append(" kg\n");
                        }
                    } else {
                        mensaje.append("   • Sin series registradas.\n");
                    }
                }
            } else {
                mensaje.append("Sin ejercicios registrados.");
            }

            new AlertDialog.Builder(context)
                    .setTitle(item.getNombreEntrenamiento())
                    .setMessage(mensaje.toString())
                    .setPositiveButton("Cerrar", null)
                    .show();
        });

        holder.itemView.setOnLongClickListener(v -> {
            Intent resultado = new Intent();
            resultado.putExtra("plan_entrenamiento", item);

            ((Activity) context).setResult(Activity.RESULT_OK, resultado);
            ((Activity) context).finish();

            Toast.makeText(context, "Entrenamiento seleccionado", Toast.LENGTH_SHORT).show();

            return true;
        });
    }

    @Override
    /**
     * Devuelve el número total de elementos en el conjunto de datos que tiene el adaptador.
     *
     * @return El número total de elementos en este adaptador.
     */
    public int getItemCount() {
        return listaEntrenamientos.size();
    }

    /**
     * Clase ViewHolderEntreno que describe una vista de elemento y metadatos sobre su lugar dentro del RecyclerView.
     */
    public static class ViewHolderEntreno extends RecyclerView.ViewHolder {
        TextView nombre;

        /**
         * Constructor para ViewHolderEntreno.
         *
         * @param itemView La vista de elemento para este ViewHolder.
         *                 Este constructor inicializa el ViewHolder y encuentra la vista de texto
         *                 para mostrar el nombre del entrenamiento.
         */
        public ViewHolderEntreno(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtNombreEntrenamientoListaId);
        }
    }
}