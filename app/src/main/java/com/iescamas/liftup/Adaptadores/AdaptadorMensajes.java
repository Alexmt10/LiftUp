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

/**
 * Adaptador para mostrar una lista de mensajes en un RecyclerView.
 * Este adaptador maneja dos tipos de vistas: mensajes propios y mensajes de otros usuarios.
 */
public class AdaptadorMensajes extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Constante para identificar el tipo de vista de un mensaje propio.
     */
    private static final int TIPO_MENSAJE_PROPIO = 0;
    /**
     * Constante para identificar el tipo de vista de un mensaje de otro usuario.
     */
    private static final int TIPO_MENSAJE_OTRO = 1;

    private List<Mensaje> listaMensajes;
    private String idUsuarioActual;

    /**
     * Constructor del adaptador.
     *
     * @param listaMensajes   La lista de mensajes a mostrar.
     * @param idUsuarioActual El ID del usuario actual para diferenciar los mensajes propios de los de otros.
     */
    public AdaptadorMensajes(List<Mensaje> listaMensajes, String idUsuarioActual) {
        this.listaMensajes = listaMensajes;
        this.idUsuarioActual = idUsuarioActual;
    }

    /**
     * Determina el tipo de vista para un elemento en una posición específica.
     *
     * @param position La posición del elemento.
     * @return El tipo de vista ({@link #TIPO_MENSAJE_PROPIO} o {@link #TIPO_MENSAJE_OTRO}).
     */
    @Override
    public int getItemViewType(int position) {
        return listaMensajes.get(position).getEmisor().equals(idUsuarioActual) ? TIPO_MENSAJE_PROPIO : TIPO_MENSAJE_OTRO;
    }

    /**
     * Crea un nuevo ViewHolder para un tipo de vista específico.
     *
     * @param parent   El ViewGroup al que se añadirá la nueva vista después de que se enlace a una posición del adaptador.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que contiene una vista del tipo de vista dado.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == TIPO_MENSAJE_PROPIO ? R.layout.item_mensaje_propio : R.layout.item_mensaje_otro, parent, false);
        return new MensajeViewHolder(view);
    }
    /**
     * Vincula los datos de un mensaje específico a un ViewHolder.
     * @param holder La instancia de ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MensajeViewHolder) holder).bind(listaMensajes.get(position));
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    /**
     * ViewHolder para representar un mensaje en la lista.
     */
    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        /**
         * TextView que muestra el contenido del mensaje.
         */
        TextView texto;

        /**
         * Constructor del ViewHolder.
         *
         * @param view La vista del elemento del mensaje.
         */
        MensajeViewHolder(View view) {
            super(view);
            texto = view.findViewById(R.id.texto_mensaje);
        }

        /**
         * Vincula un objeto Mensaje a este ViewHolder.
         * Establece el contenido del mensaje en el TextView.
         *
         * @param mensaje El objeto Mensaje que contiene los datos a mostrar.
         */
        void bind(Mensaje mensaje) {
            texto.setText(mensaje.getContenido());
        }
    }
}
