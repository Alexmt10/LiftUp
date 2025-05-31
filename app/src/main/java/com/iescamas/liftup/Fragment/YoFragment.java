package com.iescamas.liftup.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.Fragment.FragmentSecundarios.PubliGuardadasYoFragment;
import com.iescamas.liftup.Fragment.FragmentSecundarios.PublicacionesYoFragment;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemPost;
import com.iescamas.liftup.tipos.Configuraciones;
import com.iescamas.liftup.tipos.ListaEntrenamiento;
import com.iescamas.liftup.tipos.ListaPlanComida;

public class YoFragment extends Fragment {

    FloatingActionButton crearComida;
    FloatingActionButton crearEntrenamiento;
    ImageView iconMenu, iconoperfil;
    TextView nombreusuario, seguidores, seguidos, publicaciones, descripcion;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference dbRefRT;
    private String uidUsuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yo, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dbRefRT = FirebaseDatabase.getInstance().getReference();

        crearComida = view.findViewById(R.id.flobtnCrearComidaYoId);
        crearEntrenamiento = view.findViewById(R.id.flobtnCrearEntrenamientoYoId);
        iconMenu = view.findViewById(R.id.IconMenuYoid);
        iconoperfil = view.findViewById(R.id.IconPerfilYoid);
        nombreusuario = view.findViewById(R.id.txtNombreUsuarioYoid);
        descripcion = view.findViewById(R.id.txtDescripcionYoId);

        seguidores = view.findViewById(R.id.txtNumeroSeguidoresYoId);
        seguidos = view.findViewById(R.id.txtNumeroSeguidosYoId);
        publicaciones = view.findViewById(R.id.txtNumeroPublicaiconesYoId);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            uidUsuario = user.getUid();

            ViewPager2 paginator = view.findViewById(R.id.viewPagerYoid);
            FragmentStateAdapter pageadapter = new deslizador(this);
            paginator.setAdapter(pageadapter);

            TabLayout tabla = view.findViewById(R.id.tabLayoutYoid);
            new TabLayoutMediator(tabla, paginator, (tab, position) -> {
                switch (position) {
                    case 0: tab.setText("Publicaciones"); break;
                    case 1: tab.setText("Guardadas"); break;
                }
            }).attach();

            crearEntrenamiento.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), ListaEntrenamiento.class));
            });

            crearComida.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), ListaPlanComida.class));
            });

            iconMenu.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), Configuraciones.class));
            });

            // Cargar datos del usuario
            cargarDatosUsuario();
            cargarSeguidores();
            cargarSeguidos();
            cargarPublicaciones();

        } else {
            Log.e("YoFragment", "Usuario no autenticado");
            Toast.makeText(getContext(), "Por favor, inicia sesión", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), Configuraciones.class); // O LoginActivity si la tienes
            startActivity(intent);
            requireActivity().finish();
        }

        return view;
    }

    private class deslizador extends FragmentStateAdapter {
        public deslizador(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return new PublicacionesYoFragment();
            else return new PubliGuardadasYoFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    private void cargarDatosUsuario() {
        db.collection("Usuarios").document(uidUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("username");
                        String descripcionUsuario = documentSnapshot.getString("descripcion");
                        String imagenUrl = documentSnapshot.getString("imagenPerfilUrl");

                        if (nombre != null) nombreusuario.setText(nombre);
                        if (descripcionUsuario != null) descripcion.setText(descripcionUsuario);
                        if (imagenUrl != null) {
                            Glide.with(requireContext())
                                    .load(imagenUrl)
                                    .transform(new CircleCrop())
                                    .placeholder(R.drawable.messi)
                                    .error(R.drawable.cristiano)
                                    .into(iconoperfil);
                        }
                    }
                });
    }

    private void cargarSeguidores() {
        db.collection("seguidores").document(uidUsuario).collection("usuarios")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) return;
                    if (querySnapshot != null) {
                        seguidores.setText(String.valueOf(querySnapshot.size()));
                    }
                });
    }

    private void cargarSeguidos() {
        db.collection("seguidos").document(uidUsuario).collection("usuarios")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) return;
                    if (querySnapshot != null) {
                        seguidos.setText(String.valueOf(querySnapshot.size()));
                    }
                });
    }

    private void cargarPublicaciones() {
        dbRefRT.child("publicaciones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int contador = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ItemPost post = postSnapshot.getValue(ItemPost.class);
                    if (post != null && uidUsuario.equals(post.getUidUsuario())) {
                        contador++;
                    }
                }
                publicaciones.setText(String.valueOf(contador));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                publicaciones.setText("0");
            }
        });
    }
}
