package com.iescamas.liftup.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemMensajesGrupo;;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdaptadorMensajesGrupo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_MENSAJE_PROPIO = 0;
    private static final int TIPO_MENSAJE_OTRO = 1;

    private List<ItemMensajesGrupo> mensajes;
    private String usuarioActualId;

    public AdaptadorMensajesGrupo(List<ItemMensajesGrupo> mensajes, String usuarioActualId) {
        this.mensajes = mensajes;
        this.usuarioActualId = usuarioActualId;
    }

    @Override
    public int getItemViewType(int position) {
        ItemMensajesGrupo mensaje = mensajes.get(position);
        return mensaje.getEmisorId().equals(usuarioActualId) ? TIPO_MENSAJE_PROPIO : TIPO_MENSAJE_OTRO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista;
        if (viewType == TIPO_MENSAJE_PROPIO) {
            vista = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_propio, parent, false);
            return new MensajePropioViewHolder(vista);
        } else {
            vista = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_otro_grupo, parent, false);
            return new MensajeOtroViewHolder(vista);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemMensajesGrupo mensaje = mensajes.get(position);
        if (holder.getItemViewType() == TIPO_MENSAJE_PROPIO) {
            ((MensajePropioViewHolder) holder).bind(mensaje);
        } else {
            ((MensajeOtroViewHolder) holder).bind(mensaje);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }


    static class MensajePropioViewHolder extends RecyclerView.ViewHolder {
        TextView textoMensaje, textoFecha;

        public MensajePropioViewHolder(@NonNull View itemView) {
            super(itemView);
            textoMensaje = itemView.findViewById(R.id.texto_mensaje);

        }

        void bind(ItemMensajesGrupo mensaje) {
            textoMensaje.setText(mensaje.getTexto());

        }
    }

    static class MensajeOtroViewHolder extends RecyclerView.ViewHolder {
        TextView textoMensaje, textoRemitente;

        public MensajeOtroViewHolder(@NonNull View itemView) {
            super(itemView);
            textoMensaje = itemView.findViewById(R.id.texto_mensaje);
            textoRemitente = itemView.findViewById(R.id.texto_remitente_grupoId);
        }

        void bind(ItemMensajesGrupo mensaje) {
            textoMensaje.setText(mensaje.getTexto());
            textoRemitente.setText(mensaje.getEmisorUsername());

        }
    }
}
