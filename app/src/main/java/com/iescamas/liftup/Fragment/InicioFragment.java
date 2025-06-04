package com.iescamas.liftup.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iescamas.liftup.Adaptadores.AdaptadorPubli;
import com.iescamas.liftup.Fragment.FragmentSecundarios.ChatFragment;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemPost;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que representa la pantalla de inicio de la aplicación.
 * Muestra una lista de publicaciones y un icono para acceder a la pantalla de chat.
 */
public class InicioFragment extends Fragment {

    ImageView iconoMensajes;
    RecyclerView recyclerPostId;
    AdaptadorPubli adaptadorPubli;
    List<ItemPost> lista_post;

    /**
     * Se llama para que el fragmento instancie su vista de interfaz de usuario.
     *
     * @param inflater           El LayoutInflater que se puede usar para inflar cualquier vista en el fragmento.
     * @param container          Si no es nulo, esta es la vista principal a la que se debe adjuntar la interfaz de usuario del fragmento.
     *                           El fragmento no debe agregar la vista por sí mismo, pero puede usarse para generar
     *                           los LayoutParams de la vista.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anterior.
     * @return Devuelve la Vista para la interfaz de usuario del fragmento, o nulo.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        iconoMensajes = view.findViewById(R.id.imageView2);
        iconoMensajes.setOnClickListener(v -> {
            ChatFragment chatFragment = new ChatFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameid, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerPostId = view.findViewById(R.id.recyclerPostId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerPostId.setLayoutManager(layoutManager);

        lista_post = new ArrayList<>();
        adaptadorPubli = new AdaptadorPubli(lista_post, getContext());
        recyclerPostId.setAdapter(adaptadorPubli);

        cargarPublicacionesDesdeFirebase();


        return view;
    }

    /**
     * Carga las publicaciones desde Firebase Realtime Database y las muestra en el RecyclerView.
     */
    private void cargarPublicacionesDesdeFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("publicaciones");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_post.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ItemPost post = postSnapshot.getValue(ItemPost.class);
                    if (post != null) {
                        lista_post.add(post);
                    }
                }
                adaptadorPubli.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("InicioFragment", "Error al leer publicaciones", error.toException());
            }
        });
    }
}
