package com.iescamas.liftup.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.Fragment.FragmentSecundarios.PubliGuardadasYoFragment;
import com.iescamas.liftup.Fragment.FragmentSecundarios.PublicacionesYoFragment;
import com.iescamas.liftup.R;
import com.iescamas.liftup.tipos.Configuraciones;
import com.iescamas.liftup.tipos.ListaEntrenamiento;
import com.iescamas.liftup.tipos.ListaPlanComida;


public class YoFragment extends Fragment {

    FloatingActionButton crearComida;
    FloatingActionButton crearEntrenamiento;
    ImageView iconMenu, iconoperfil;
    TextView nombreusuario,seguidores,seguidos,publicaciones, descripcion;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_yo, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        crearComida = view.findViewById(R.id.flobtnCrearComidaYoId);
        crearEntrenamiento = view.findViewById(R.id.flobtnCrearEntrenamientoYoId);
        iconMenu = view.findViewById(R.id.IconMenuYoid);
        iconoperfil = view.findViewById(R.id.IconPerfilYoid);
        nombreusuario = view.findViewById(R.id.txtNombreUsuarioYoid);
        descripcion = view.findViewById(R.id.txtDescripcionYoId);


        ViewPager2 paginator = view.findViewById(R.id.viewPagerYoid);
        FragmentStateAdapter pageadapter = new deslizador(this);
        paginator.setAdapter(pageadapter);

        TabLayout tabla = view.findViewById(R.id.tabLayoutYoid);

        new TabLayoutMediator(tabla,paginator,(tab, position) -> {

            switch (position) {
                case 0 :
                    tab.setText("Publicaciones");
                    break;
                case 1:
                    tab.setText("Guardadas");
                    break;
            }
        }).attach();



        crearEntrenamiento.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListaEntrenamiento.class);
            startActivity(intent);

        });
        crearComida.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListaPlanComida.class);
            startActivity(intent);
        });


        iconMenu.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Configuraciones.class);
            startActivity(intent);
        });

        cargarDatosUsuario();
        return view;
    }

    private class deslizador extends FragmentStateAdapter {
        public deslizador(YoFragment fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            switch (position){
                case 0 :
                    fragment = new PublicacionesYoFragment();
                    break;
                case 1 :
                    fragment = new PubliGuardadasYoFragment();
                    break;
                default:
                    fragment = null;
                    break;
            }
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
    private void cargarDatosUsuario() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();

        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String descripcionUsuario = documentSnapshot.getString("descripcion");
                        String imagenUrl = documentSnapshot.getString("imagenPerfilUrl");

                        if (nombre != null) {
                            nombreusuario.setText(nombre);
                        }
                        if (descripcionUsuario != null) {
                            descripcion.setText(descripcionUsuario);
                        }
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

}