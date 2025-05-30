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


public class BuscarFragment extends Fragment {
    private TextInputEditText entradaBusqueda;
    private RecyclerView recyclerUsuarios;
    private ProgressBar indicadorCarga;
    private TextView textoVacio;

    private FirebaseFirestore db;
    private FirebaseAuth autenticacion;
    private AdaptadorBusquedaUsuario adaptadorUsuarios;
    private List<Usuario> listaUsuarios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_buscar, container, false);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        autenticacion = FirebaseAuth.getInstance();

        // Inicializar vistas
        entradaBusqueda = vista.findViewById(R.id.entrada_busqueda);
        recyclerUsuarios = vista.findViewById(R.id.recycler_usuarios);
        indicadorCarga = vista.findViewById(R.id.indicador_carga);
        textoVacio = vista.findViewById(R.id.texto_vacio);

        // Configurar RecyclerView
        listaUsuarios = new ArrayList<>();
        adaptadorUsuarios = new AdaptadorBusquedaUsuario(listaUsuarios, getContext());
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUsuarios.setAdapter(adaptadorUsuarios);

        // Configurar búsqueda en tiempo real
        configurarListenerBusqueda();

        return vista;
    }

    private void configurarListenerBusqueda() {
        entradaBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarUsuarios(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

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