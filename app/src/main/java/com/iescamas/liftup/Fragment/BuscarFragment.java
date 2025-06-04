package com.iescamas.liftup.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.Adaptadores.AdaptadorBusquedaUsuario;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento para buscar usuarios en la aplicación.
 * Permite a los usuarios buscar a otros usuarios por su nombre de usuario.
 * Muestra los resultados de la búsqueda en una lista y permite interactuar con ellos.
 */
public class BuscarFragment extends Fragment {
    private TextInputEditText entradaBusqueda;
    private RecyclerView recyclerUsuarios;
    private ProgressBar indicadorCarga;
    private TextView textoVacio;

    private FirebaseFirestore db;
    private FirebaseAuth autenticacion;
    private AdaptadorBusquedaUsuario adaptadorUsuarios;
    private List<Usuario> listaUsuarios;

    /**
     * Se llama cuando el fragmento debe crear su vista de usuario.
     * Infla el diseño del fragmento, inicializa las vistas y configura los listeners.
     *
     * @param inflater El LayoutInflater que se puede usar para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, este es el grupo de vistas principal al que se debe adjuntar la interfaz de usuario del fragmento.
     *                  El fragmento no debe agregar la vista en sí, pero esto se puede usar para generar los LayoutParams de la vista.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anteriormente.
     * @return Devuelve la Vista para la interfaz de usuario del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_buscar, container, false);

        db = FirebaseFirestore.getInstance();
        autenticacion = FirebaseAuth.getInstance();

        entradaBusqueda = vista.findViewById(R.id.entrada_busqueda);
        recyclerUsuarios = vista.findViewById(R.id.recycler_usuarios);
        indicadorCarga = vista.findViewById(R.id.indicador_carga);
        textoVacio = vista.findViewById(R.id.texto_vacio);

        listaUsuarios = new ArrayList<>();
        adaptadorUsuarios = new AdaptadorBusquedaUsuario(listaUsuarios, getContext());
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUsuarios.setAdapter(adaptadorUsuarios);

        configurarListenerBusqueda();

        return vista;
    }

    /**
     * Configura el listener para el campo de entrada de búsqueda.
     * Cuando el texto en el campo de entrada cambia, se llama al método {@link #buscarUsuarios(String)}.
     */
    private void configurarListenerBusqueda() {
        entradaBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarUsuarios(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Busca usuarios en Firestore según el texto de búsqueda proporcionado.
     * Muestra los resultados en el RecyclerView o un mensaje si no se encuentran usuarios.
     *
     * @param textoBusqueda El texto utilizado para buscar usuarios.
     */
    private void buscarUsuarios(String textoBusqueda) {
        Log.d("BUSQUEDA_USUARIOS", "Texto de búsqueda: " + textoBusqueda);

        if (textoBusqueda.isEmpty()) {
            Log.d("BUSQUEDA_USUARIOS", "Texto vacío, limpiando resultados.");
            listaUsuarios.clear();
            adaptadorUsuarios.notifyDataSetChanged();
            textoVacio.setVisibility(View.VISIBLE);
            textoVacio.setText("Busca usuarios por nombre o username");
            return;
        }

        indicadorCarga.setVisibility(View.VISIBLE);
        textoVacio.setVisibility(View.GONE);

        db.collection("Usuarios")
                .orderBy("username")
                .startAt(textoBusqueda)
                .endAt(textoBusqueda + "\uf8ff")
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    indicadorCarga.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        listaUsuarios.clear();
                        Log.d("BUSQUEDA_USUARIOS", "Consulta exitosa.");

                        for (DocumentSnapshot documento : task.getResult()) {
                            Log.d("BUSQUEDA_USUARIOS", "Documento encontrado: " + documento.getId());

                            if (!documento.getId().equals(autenticacion.getCurrentUser().getUid())) {
                                Usuario usuario = documento.toObject(Usuario.class);
                                usuario.setIdUsuario(documento.getId());
                                listaUsuarios.add(usuario);
                                Log.d("BUSQUEDA_USUARIOS", "Usuario agregado: " + usuario.getUsername());
                            } else {
                                Log.d("BUSQUEDA_USUARIOS", "Usuario actual ignorado: " + documento.getId());
                            }
                        }

                        adaptadorUsuarios.notifyDataSetChanged();

                        if (listaUsuarios.isEmpty()) {
                            textoVacio.setVisibility(View.VISIBLE);
                            textoVacio.setText("No se encontraron usuarios");
                            Log.d("BUSQUEDA_USUARIOS", "Lista vacía después de la búsqueda.");
                        }

                    } else {
                        Log.e("BUSQUEDA_USUARIOS", "Error en la consulta: ", task.getException());
                        Toast.makeText(getContext(), "Error al buscar usuarios", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}