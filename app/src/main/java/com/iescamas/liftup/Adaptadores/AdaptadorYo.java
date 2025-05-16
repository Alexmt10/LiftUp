package com.iescamas.liftup.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemYoPerfil;

import java.util.List;

public class AdaptadorYo extends RecyclerView.Adapter<AdaptadorYo.ViewHolderYo> {


    private final List<ItemYoPerfil> listaPerfil;
    private final Context context;

    public AdaptadorYo(List<ItemYoPerfil> listaPerfil, Context context) {
        this.listaPerfil = listaPerfil;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorYo.ViewHolderYo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_perfil, parent, false);
        return  new ViewHolderYo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorYo.ViewHolderYo holder, int position) {
        ItemYoPerfil itemYoPerfil = listaPerfil.get(position);
        holder.imagenPerfilYoId.setImageResource(itemYoPerfil.getImagenPerfil());
        holder.btnmonstrarentrenamiento.setOnClickListener(v -> {
            // Aquí puedes manejar la lógica cuando se hace clic en el botón "Entrenamiento"
        }
        );
        holder.btnmonstrarcomida.setOnClickListener(v -> {
            // Aquí puedes manejar la lógica cuando se hace clic en el botón "Comida"
                }
        );
    }

    @Override
    public int getItemCount() {
        return listaPerfil.size();
    }

    public class ViewHolderYo extends RecyclerView.ViewHolder {
        ImageView imagenPerfilYoId;
        Button btnmonstrarentrenamiento;
        Button btnmonstrarcomida;
        public ViewHolderYo(@NonNull View itemView) {
            super(itemView);

            imagenPerfilYoId = itemView.findViewById(R.id.imgpublicacionPerfilId);
            btnmonstrarentrenamiento = itemView.findViewById(R.id.btnmostrarentrenamientoPerfilid);
            btnmonstrarcomida = itemView.findViewById(R.id.btnmostrarcomidaPerfilid);
        }
    }
}