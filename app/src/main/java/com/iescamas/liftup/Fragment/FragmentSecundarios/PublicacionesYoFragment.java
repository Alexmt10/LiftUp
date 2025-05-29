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
import com.iescamas.liftup.pojo.ItemYoPerfil;

import java.util.ArrayList;
import java.util.List;

public class PublicacionesYoFragment extends Fragment {

    RecyclerView recyclerPerfilId;
    AdaptadorYo adaptadorYo;
    List<ItemPost> lista_perfil;

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
