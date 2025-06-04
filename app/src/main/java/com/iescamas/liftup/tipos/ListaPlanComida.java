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

/**
 * Actividad que muestra una lista de planes de comida.
 * <p>
 * Esta actividad recupera los planes de comida desde Firebase Realtime Database y los muestra en un RecyclerView.
 * También proporciona un botón flotante para añadir nuevos planes de comida.
 */
public class ListaPlanComida extends AppCompatActivity {
    FloatingActionButton floatBtnAnadirPlanComidaId;
    private FirebaseFirestore db;
    private AdaptadorListaComida adaptador;
    private List<PlanComida> planesComida = new ArrayList<>();
    private RecyclerView recyclerView;

    /**
     * Se llama cuando la actividad está iniciando.
     * <p>
     * Aquí es donde se debe realizar la inicialización, como inflar la interfaz de usuario, inicializar variables y configurar listeners.
     * @param savedInstanceState Si la actividad se está reiniciando después de haber sido previamente cerrada, este Bundle contiene los datos que suministró más recientemente en {@link #onSaveInstanceState}. De lo contrario, es nulo.
     */
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

    /**
     * Carga los planes de comida desde Firebase Realtime Database.
     * <p>
     * Este método configura un {@link ValueEventListener} para escuchar los cambios en la referencia "planes_comida" en la base de datos. Cuando se producen cambios, actualiza la lista local de planes de comida y notifica al adaptador para que actualice la vista.
     */
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