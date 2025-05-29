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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iescamas.liftup.Fragment.FragmentSecundarios.PubliGuardadasYoFragment;
import com.iescamas.liftup.Fragment.FragmentSecundarios.PublicacionesYoFragment;
import com.iescamas.liftup.R;
import com.iescamas.liftup.tipos.Configuraciones;
import com.iescamas.liftup.tipos.ListaEntrenamiento;
import com.iescamas.liftup.tipos.ListaPlanComida;


public class YoFragment extends Fragment {

    FloatingActionButton crearComida;
    FloatingActionButton crearEntrenamiento;
    ImageView iconMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_yo, container, false);

        crearComida = view.findViewById(R.id.flobtnCrearComidaYoId);
        crearEntrenamiento = view.findViewById(R.id.flobtnCrearEntrenamientoYoId);
        iconMenu = view.findViewById(R.id.IconMenuYoid);

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
}