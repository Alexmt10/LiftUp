package com.iescamas.liftup.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.Mensaje;

import java.util.List;

public class AdaptadorMensajes extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TIPO_MENSAJE_PROPIO = 0;
    private static final int TIPO_MENSAJE_OTRO = 1;

    private List<Mensaje> listaMensajes;
    private String idUsuarioActual;

    public AdaptadorMensajes(List<Mensaje> listaMensajes, String idUsuarioActual) {
        this.listaMensajes = listaMensajes;
        this.idUsuarioActual = idUsuarioActual;
    }

    @Override
    public int getItemViewType(int position) {
        return listaMensajes.get(position).getEmisor().equals(idUsuarioActual) ?
                TIPO_MENSAJE_PROPIO : TIPO_MENSAJE_OTRO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                viewType == TIPO_MENSAJE_PROPIO ? R.layout.item_mensaje_propio : R.layout.item_mensaje_otro,
                parent, false
        );
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MensajeViewHolder) holder).bind(listaMensajes.get(position));
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView texto;

        MensajeViewHolder(View view) {
            super(view);
            texto = view.findViewById(R.id.texto_mensaje);
        }

        void bind(Mensaje mensaje) {
            texto.setText(mensaje.getContenido());
        }
    }
}
