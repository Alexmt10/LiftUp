package com.iescamas.liftup.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.iescamas.liftup.Fragment.NuevaFragment;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemAlimento;
import com.iescamas.liftup.pojo.PlanComida;

import java.util.List;

public class AdaptadorListaComida extends RecyclerView.Adapter<AdaptadorListaComida.PlanComidaViewHolder> {

    private final List<PlanComida> planesComida;
    private final Context context;

    public AdaptadorListaComida(List<PlanComida> planesComida, Context context) {
        this.planesComida = planesComida;
        this.context = context;
    }

    @NonNull
    @Override
    public PlanComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comida, parent, false);
        return new PlanComidaViewHolder(view);
    }

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
            }
            return false;
        });
    }


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

    @Override
    public int getItemCount() {
        return planesComida.size();
    }

    public static class PlanComidaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombrePlan; TextView tvCantidadAlimentos;

        public PlanComidaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombrePlan = itemView.findViewById(R.id.tvNombrePlan);

        }
    }
}