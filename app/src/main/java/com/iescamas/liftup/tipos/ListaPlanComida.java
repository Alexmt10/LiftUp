package com.iescamas.liftup.tipos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iescamas.liftup.Adaptadores.AdaptadorListaComida;
import com.iescamas.liftup.R;

import com.iescamas.liftup.pojo.PlanComida;

import java.util.ArrayList;
import java.util.List;

public class ListaPlanComida extends AppCompatActivity {
    FloatingActionButton floatBtnAnadirPlanComidaId;
    private FirebaseFirestore db;
    private AdaptadorListaComida adaptador;
    private List<PlanComida> planesComida = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_plan_comida);

        db = FirebaseFirestore.getInstance();

        floatBtnAnadirPlanComidaId = findViewById(R.id.floatBtnAnadirComidaId);
        Intent intent = new Intent(this, CrearPlanComida.class);
        floatBtnAnadirPlanComidaId.setOnClickListener(v -> startActivity(intent));

        recyclerView = findViewById(R.id.recyclerListComidaId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdaptadorListaComida(planesComida, this);
        recyclerView.setAdapter(adaptador);

        cargarPlanesDesdeFirebase();
    }





    private void cargarPlanesDesdeFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("planes_comida");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                planesComida.clear();

                for (DataSnapshot doc : snapshot.getChildren()) {
                    PlanComida plan = doc.getValue(PlanComida.class);
                    if (plan != null) {
                        planesComida.add(plan);
                    }
                }

                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaPlanComida.this, "Error al cargar planes: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}