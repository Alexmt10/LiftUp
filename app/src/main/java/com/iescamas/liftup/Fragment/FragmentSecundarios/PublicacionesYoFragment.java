package com.iescamas.liftup.Fragment.FragmentSecundarios;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iescamas.liftup.Adaptadores.AdaptadorYo;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemPost;


import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra las publicaciones del usuario actual.
 */
public class PublicacionesYoFragment extends Fragment {

    RecyclerView recyclerPerfilId;
    AdaptadorYo adaptadorYo;
    List<ItemPost> lista_perfil;

    /**
     * Se llama para que el fragmento instancie su vista de interfaz de usuario.
     * Este método infla el diseño del fragmento, inicializa el RecyclerView y su adaptador,
     * y carga las publicaciones del usuario actual desde Firebase.
     *
     * @param inflater El LayoutInflater que se puede usar para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, esta es la vista principal a la que se adjuntará la interfaz de usuario del fragmento.
     *                  El fragmento no debe agregar la vista en sí, pero puede usarse para generar
     *                  los LayoutParams de la vista.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anterior.
     * @return Devuelve la Vista para la interfaz de usuario del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publicaciones_yo, container, false);

        recyclerPerfilId = view.findViewById(R.id.recyclerPerfilYoId);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerPerfilId.setLayoutManager(gridLayoutManager);

        lista_perfil = new ArrayList<>();
        adaptadorYo = new AdaptadorYo(lista_perfil, getContext());
        recyclerPerfilId.setAdapter(adaptadorYo);

        cargarMisPublicacionesDesdeFirebase();

        return view;
    }

    /**
     * Carga las publicaciones del usuario actual desde la base de datos de Firebase.
     * Obtiene el ID del usuario actual, consulta la base de datos para las publicaciones
     * que coinciden con ese ID y actualiza el adaptador del RecyclerView.
     */
    private void cargarMisPublicacionesDesdeFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d("PublicacionesYoFragment", "Usuario no autenticado - No se cargarán publicaciones");
            return;
        }

        String miUserId = user.getUid();
        Log.d("PublicacionesYoFragment", "ID de usuario actual: " + miUserId);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("publicaciones");
        Log.d("PublicacionesYoFragment", "Referencia a DB obtenida. Consultando publicaciones...");

        dbRef.orderByChild("uidUsuario").equalTo(miUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("PublicacionesYoFragment", "onDataChange - Número de hijos encontrados: " + snapshot.getChildrenCount());

                lista_perfil.clear();
                int contador = 0;

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ItemPost post = postSnapshot.getValue(ItemPost.class);
                    if (post != null) {
                        lista_perfil.add(post);
                        contador++;
                        Log.v("PublicacionesYoFragment", "Post añadido: " + postSnapshot.getKey());
                    }
                }

                Log.d("PublicacionesYoFragment", "Total de publicaciones cargadas: " + contador);
                adaptadorYo.notifyDataSetChanged();

                if (contador == 0) {
                    Log.w("PublicacionesYoFragment", "No se encontraron publicaciones para este usuario");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PublicacionesYoFragment", "Error al cargar publicaciones", error.toException());
                Log.e("PublicacionesYoFragment", "Detalles del error: " + error.getMessage() + " | Código: " + error.getCode());
            }
        });
    }
}
