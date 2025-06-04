package com.iescamas.liftup.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemMensajesGrupo;

import java.util.List;

/**
 * Adaptador para mostrar mensajes en un RecyclerView, diferenciando entre mensajes propios y de otros usuarios en un grupo.
 */
public class AdaptadorMensajesGrupo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Constante para identificar el tipo de vista de un mensaje propio.
     */
    private static final int TIPO_MENSAJE_PROPIO = 0;
    /**
     * Constante para identificar el tipo de vista de un mensaje de otro usuario.
     */
    private static final int TIPO_MENSAJE_OTRO = 1;

    /**
     * Lista de objetos {@link ItemMensajesGrupo} que representan los mensajes a mostrar.
     */
    private List<ItemMensajesGrupo> mensajes;
    /**
     * ID del usuario actual para diferenciar los mensajes propios de los de otros.
     */
    private String usuarioActualId;

    /**
     * Constructor del adaptador.
     * @param mensajes Lista de mensajes a mostrar.
     * @param usuarioActualId ID del usuario actual.
     */
    public AdaptadorMensajesGrupo(List<ItemMensajesGrupo> mensajes, String usuarioActualId) {
        this.mensajes = mensajes;
        this.usuarioActualId = usuarioActualId;
    }

    @Override
    public int getItemViewType(int position) {
        /**
         * Determina el tipo de vista para un elemento en una posición específica.
         * @param position La posición del elemento en la lista.
         * @return {@link #TIPO_MENSAJE_PROPIO} si el mensaje es del usuario actual, {@link #TIPO_MENSAJE_OTRO} en caso contrario.
         */
        ItemMensajesGrupo mensaje = mensajes.get(position);
        return mensaje.getEmisorId().equals(usuarioActualId) ? TIPO_MENSAJE_PROPIO : TIPO_MENSAJE_OTRO;
    }

    @NonNull
    @Override
    /**
     * Crea un nuevo ViewHolder según el tipo de vista.
     * @param parent El ViewGroup padre al que se adjuntará la nueva vista.
     * @param viewType El tipo de vista del nuevo ViewHolder.
     * @return Un nuevo ViewHolder que contiene una Vista del tipo dado.
     */
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
        /**
         * Vincula los datos de un mensaje a un ViewHolder.
         * @param holder El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
         * @param position La posición del elemento dentro del conjunto de datos del adaptador.
         */
        ItemMensajesGrupo mensaje = mensajes.get(position);
        if (holder.getItemViewType() == TIPO_MENSAJE_PROPIO) {
            ((MensajePropioViewHolder) holder).bind(mensaje);
        } else {
            ((MensajeOtroViewHolder) holder).bind(mensaje);
        }
    }

    @Override
    public int getItemCount() {
        /**
         * Devuelve el número total de elementos en el conjunto de datos que tiene el adaptador.
         * @return El número total de elementos en este adaptador.
         */
        return mensajes.size();
    }


    /**
     * ViewHolder para representar un mensaje propio del usuario.
     */
    static class MensajePropioViewHolder extends RecyclerView.ViewHolder {
        /**
         * TextView para mostrar el texto del mensaje.
         */
        TextView textoMensaje;

        /**
         * Constructor del ViewHolder para mensajes propios.
         * @param itemView La vista raíz del elemento.
         */
        public MensajePropioViewHolder(@NonNull View itemView) {
            super(itemView);
            textoMensaje = itemView.findViewById(R.id.texto_mensaje);

        }

        /**
         * Vincula los datos de un mensaje propio a los elementos de la vista.
         * @param mensaje El objeto {@link ItemMensajesGrupo} que contiene los datos del mensaje.
         */
        void bind(ItemMensajesGrupo mensaje) {
            textoMensaje.setText(mensaje.getTexto());

        }
    }

    /**
     * ViewHolder para representar un mensaje de otro usuario en el grupo.
     */
    static class MensajeOtroViewHolder extends RecyclerView.ViewHolder {
        /**
         * TextView para mostrar el texto del mensaje y el nombre del remitente.
         */
        TextView textoMensaje, textoRemitente;

        /**
         * Constructor del ViewHolder para mensajes de otros usuarios.
         * @param itemView La vista raíz del elemento.
         */
        public MensajeOtroViewHolder(@NonNull View itemView) {
            super(itemView);
            textoMensaje = itemView.findViewById(R.id.texto_mensaje);
            textoRemitente = itemView.findViewById(R.id.texto_remitente_grupoId);
        }

        /**
         * Vincula los datos de un mensaje de otro usuario a los elementos de la vista.
         * @param mensaje El objeto {@link ItemMensajesGrupo} que contiene los datos del mensaje.
         */
        void bind(ItemMensajesGrupo mensaje) {
            textoMensaje.setText(mensaje.getTexto());
            textoRemitente.setText(mensaje.getEmisorUsername());
        }
    }
}
