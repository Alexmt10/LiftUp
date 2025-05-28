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

public class AdapterCrearEntrenamiento extends RecyclerView.Adapter<AdapterCrearEntrenamiento.ViewHolderEntreno> {
    private final List<ItemEntrenamiento> listaEntrenamientos;
    private final Context context;

    public AdapterCrearEntrenamiento(List<ItemEntrenamiento> listaEntrenamientos, Context context) {
        this.listaEntrenamientos = listaEntrenamientos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderEntreno onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrenamiento, parent, false);
        return new ViewHolderEntreno(view);
    }

    @Override
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
            resultado.putExtra("id_entreno", item.getId());
            resultado.putExtra("titulo_entreno", item.getNombreEntrenamiento());

            ((Activity) context).setResult(Activity.RESULT_OK, resultado);
            ((Activity) context).finish();

            Toast.makeText(context, "Entrenamiento seleccionado", Toast.LENGTH_SHORT).show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaEntrenamientos.size();
    }

    public static class ViewHolderEntreno extends RecyclerView.ViewHolder {
        TextView nombre;

        public ViewHolderEntreno(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtNombreEntrenamientoListaId);
        }
    }
}
