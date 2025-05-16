package com.iescamas.liftup.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iescamas.liftup.Adaptadores.AdaptadorPubli;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemPost;

import java.util.ArrayList;
import java.util.List;


public class InicioFragment extends Fragment {

    RecyclerView recyclerPostId;
    AdaptadorPubli adaptadorPubli;
    List<ItemPost> lista_post;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     
        
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        
        recyclerPostId = view.findViewById(R.id.recyclerPostId);
        
        recyclerPostId.setLayoutManager(new LinearLayoutManager(getContext()));
        
        lista_post = cargarItem();
        
        adaptadorPubli = new AdaptadorPubli(lista_post, getContext());
        
        recyclerPostId.setAdapter(adaptadorPubli);
        
        
        
        
        
        
        return view;
    }

    private List<ItemPost> cargarItem() {
        List<ItemPost> lista = new ArrayList<>();



        return lista;
    }
}