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
import com.iescamas.liftup.pojo.ItemAlimento;
import com.iescamas.liftup.pojo.PlanComida;

import java.util.List;

/**
 * Adaptador para mostrar una lista de planes de comida en un RecyclerView.
 * Permite seleccionar un plan de comida y mostrar sus detalles.
 */
public class AdaptadorListaComida extends RecyclerView.Adapter<AdaptadorListaComida.PlanComidaViewHolder> {

    private final List<PlanComida> planesComida;
    private final Context context;

    /**
     * Constructor del adaptador.
     *
     * @param planesComida Lista de planes de comida a mostrar.
     * @param context      Contexto de la aplicación.
     */
    public AdaptadorListaComida(List<PlanComida> planesComida, Context context) {
        this.planesComida = planesComida;
        this.context = context;
    }

    /**
     * Crea una nueva vista para un elemento de la lista.
     *
     * @param parent   El grupo de vistas al que se adjuntará la nueva vista después de que se enlace a una posición del adaptador.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Una nueva instancia de PlanComidaViewHolder que contiene la vista para un elemento de la lista.
     */
    @NonNull
    @Override
    public PlanComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comida, parent, false);
        return new PlanComidaViewHolder(view);
    }

    /**
     * Vincula los datos de un plan de comida a una vista de elemento de la lista.
     *
     * @param holder   El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull PlanComidaViewHolder holder, int position) {
        PlanComida plan = planesComida.get(position);
        holder.tvNombrePlan.setText(plan.getNombre());

        holder.itemView.setOnClickListener(v -> mostrarDetallePlan(plan));

        holder.itemView.setOnLongClickListener(v -> {
            if (context instanceof Activity) {
                Intent intent = new Intent();
                intent.putExtra("plan_comida", plan);
                ((Activity) context).setResult(Activity.RESULT_OK, intent);
                ((Activity) context).finish();
                Toast.makeText(context, "Plan de comida seleccionado", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }

    /**
     * Muestra los detalles de un plan de comida en un diálogo de alerta.
     *
     * @param plan El plan de comida cuyos detalles se mostrarán.
     */
    private void mostrarDetallePlan(PlanComida plan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(plan.getNombre());

        StringBuilder mensaje = new StringBuilder();
        for (ItemAlimento alimento : plan.getAlimentos()) {
            mensaje.append("\n• ").append(alimento.getNombre())
                    .append(" (").append(alimento.getGramos()).append("g)")
                    .append("\n  Calorías: ").append(alimento.getCalorias()).append(" kcal")
                    .append("\n  Proteínas: ").append(alimento.getProteinas()).append("g")
                    .append("\n  Grasas: ").append(alimento.getGrasas()).append("g")
                    .append("\n  Carbohidratos: ").append(alimento.getCarbohidratos()).append("g\n");
        }

        builder.setMessage(mensaje.toString());
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos que tiene el adaptador.
     *
     * @return El número total de elementos en este adaptador.
     */
    @Override
    public int getItemCount() {
        return planesComida.size();
    }

    /**
     * ViewHolder para los elementos de la lista de planes de comida.
     * Contiene las vistas que se mostrarán para cada elemento.
     */
    public static class PlanComidaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombrePlan;

        /**
         * Constructor del ViewHolder.
         * @param itemView La vista raíz del elemento de la lista.
         */
        public PlanComidaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombrePlan = itemView.findViewById(R.id.tvNombrePlan);

        }
    }
}