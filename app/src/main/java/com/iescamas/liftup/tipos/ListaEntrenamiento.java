package com.iescamas.liftup.tipos;

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
import com.iescamas.liftup.Adaptadores.AdapterCrearEntrenamiento;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemEntrenamiento;

import java.util.ArrayList;
import java.util.List;

public class ListaEntrenamiento extends AppCompatActivity {

    private FloatingActionButton floatBtnAnadirEntrenamientoId;
    private RecyclerView recyclerListEntrenamientoId;
    private AdapterCrearEntrenamiento adapterCrearEntrenamiento;
    private final List<ItemEntrenamiento> listaEntrenamientos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_entrenamiento);

        floatBtnAnadirEntrenamientoId = findViewById(R.id.floatBtnAnadirEntrenamientoId);
        recyclerListEntrenamientoId = findViewById(R.id.recyclerListEntrenamientoId);
        recyclerListEntrenamientoId.setLayoutManager(new LinearLayoutManager(this));

        adapterCrearEntrenamiento = new AdapterCrearEntrenamiento(listaEntrenamientos, this);
        recyclerListEntrenamientoId.setAdapter(adapterCrearEntrenamiento);

        cargarEntrenamientosDesdeFirebase();

        floatBtnAnadirEntrenamientoId.setOnClickListener(v -> {
            startActivity(new Intent(this, CrearEntrenamiento.class));
        });
    }

    private void cargarEntrenamientosDesdeFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("entrenamientos");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEntrenamientos.clear();

                int contador = 1;
                for (DataSnapshot child : snapshot.getChildren()) {
                    ItemEntrenamiento entrenamiento = child.getValue(ItemEntrenamiento.class);
                    if (entrenamiento != null) {
                        if (entrenamiento.getNombreEntrenamiento() == null || entrenamiento.getNombreEntrenamiento().isEmpty()) {
                            entrenamiento.setNombreEntrenamiento("Entrenamiento " + contador++);
                        }
                        listaEntrenamientos.add(entrenamiento);
                    }
                }

                adapterCrearEntrenamiento.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaEntrenamiento.this, "Error al cargar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
