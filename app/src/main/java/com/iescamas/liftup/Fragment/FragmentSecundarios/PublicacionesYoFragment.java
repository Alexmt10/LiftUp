package com.iescamas.liftup.Fragment.FragmentSecundarios;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iescamas.liftup.Adaptadores.AdaptadorYo;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemYoPerfil;

import java.util.ArrayList;
import java.util.List;

public class PublicacionesYoFragment extends Fragment {
    RecyclerView recyclerPerfilId;
    AdaptadorYo adaptadorYo;
    List<ItemYoPerfil> lista_perfil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_publicaciones_yo, container, false);
        recyclerPerfilId = view.findViewById(R.id.recyclerPerfilYoId);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerPerfilId.setLayoutManager(gridLayoutManager);
        lista_perfil = cargarYoItem();
        adaptadorYo = new AdaptadorYo(lista_perfil, getContext());
        recyclerPerfilId.setAdapter(adaptadorYo);
        return view;
    }
    private List<ItemYoPerfil> cargarYoItem() {
        List<ItemYoPerfil> lista = new ArrayList<>();

        lista.add(new ItemYoPerfil(R.drawable.candado));
        lista.add(new ItemYoPerfil(R.drawable.email));
        lista.add(new ItemYoPerfil(R.drawable.icon_mensajes));
        lista.add(new ItemYoPerfil(R.drawable.icon_comentario));
        lista.add(new ItemYoPerfil(R.drawable.candado));




        return lista;
    }

}
