package com.iescamas.liftup.tipos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iescamas.liftup.Base_de_datos.DBHelper;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemEntrenamiento;
import com.iescamas.liftup.pojo.ItemEntrenoCompleto;
import com.iescamas.liftup.pojo.ItemSerie;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CrearEntrenamiento extends AppCompatActivity {

    DatabaseReference dbReference;
    EditText tituloentrenamiento;


    Spinner spinnerMusculo, spinnerEjercicio;
    LinearLayout layoutSeries;
    FloatingActionButton floatBtnAgregarSerie;
    int numeroSerie = 1;
    Button btnAgregarEjercicio, btnGuardarEnternamiento;
    List<ItemEntrenoCompleto> listaEjercicios  = new ArrayList<>();
    TextView txtMonstrarEjerciciosId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_entrenamiento);

        dbReference = FirebaseDatabase.getInstance().getReference("entrenamientos");


        tituloentrenamiento = findViewById(R.id.txtTituloEntrenamiento);
        spinnerMusculo = findViewById(R.id.spinnerMusculo);
        spinnerEjercicio = findViewById(R.id.spinnerEjercicio);
        layoutSeries = findViewById(R.id.layoutSeries);
        floatBtnAgregarSerie = findViewById(R.id.fabAgregarSerie);
        btnAgregarEjercicio = findViewById(R.id.btnAgregarEjercicio);
        btnGuardarEnternamiento = findViewById(R.id.btnGuardarEntrenamientoid);
        txtMonstrarEjerciciosId = findViewById(R.id.txtMonstrarEjerciciosId);


        ArrayAdapter<CharSequence> musculoAdapter = ArrayAdapter.createFromResource( this,
                R.array.musculos,
                android.R.layout.simple_spinner_item
        );
        musculoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMusculo.setAdapter(musculoAdapter);


        spinnerMusculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                actualizarSpinnerEjercicios(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });


        floatBtnAgregarSerie.setOnClickListener(v -> agregarSerie());


        btnAgregarEjercicio.setOnClickListener(v -> {

            String musculo = spinnerMusculo.getSelectedItem().toString();
            String ejercicio = spinnerEjercicio.getSelectedItem().toString();

            List<ItemSerie> series = new ArrayList<>();
            for (int i = 0; i < layoutSeries.getChildCount(); i++) {
                View fila = layoutSeries.getChildAt(i);
                EditText rep = fila.findViewById(R.id.editRepeticionesid);
                EditText peso = fila.findViewById(R.id.editPesoid);

                int repeticiones = Integer.parseInt(rep.getText().toString());
                int pesoKg = Integer.parseInt(peso.getText().toString());

                series.add(new ItemSerie(repeticiones, pesoKg));
            }
            listaEjercicios.add(new ItemEntrenoCompleto(musculo, ejercicio, series));
            String textoActual = txtMonstrarEjerciciosId.getText().toString();
            txtMonstrarEjerciciosId.setText(textoActual + "\n" + musculo + " - " + ejercicio);


            layoutSeries.removeAllViews();
            spinnerMusculo.setSelection(0);
            spinnerEjercicio.setSelection(0);
            numeroSerie = 1;

        });

        btnGuardarEnternamiento.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("entrenamientos");

            String id = ref.push().getKey();
            String nombreEntreno = tituloentrenamiento.getText().toString().trim();
            int imagenPorDefecto = R.drawable.icon_mancuerna;

            ItemEntrenamiento entrenamientoCompleto = new ItemEntrenamiento(
                    id,
                    nombreEntreno,
                    imagenPorDefecto,
                    listaEjercicios
            );

            if (id != null) {
                ref.child(id).setValue(entrenamientoCompleto).addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Entrenamiento guardado en Firebase", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, ListaEntrenamiento.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                });
            }
        });


    }

    private void actualizarSpinnerEjercicios(int posicionMusculo) {
        int idArrayEjercicios;

        switch (posicionMusculo) {
            case 0:
                idArrayEjercicios = R.array.ejercicios_pecho;
                break;
            case 1:
                idArrayEjercicios = R.array.ejercicios_espalda;
                break;
            case 2:
                idArrayEjercicios = R.array.ejercicios_piernas;
                break;
            case 3:
                idArrayEjercicios = R.array.ejercicios_hombros;
                break;
            case 4:
                idArrayEjercicios = R.array.ejercicios_biceps;
                break;
            case 5:
                idArrayEjercicios = R.array.ejercicios_triceps;
                break;
            default:
                idArrayEjercicios = R.array.ejercicios_pecho;
        }

        ArrayAdapter<CharSequence> ejerciciosAdapter = ArrayAdapter.createFromResource(
                this,
                idArrayEjercicios,
                android.R.layout.simple_spinner_item
        );
        ejerciciosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEjercicio.setAdapter(ejerciciosAdapter);
    }

    private void agregarSerie() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View filaSerie = inflater.inflate(R.layout.item_serie, layoutSeries, false);

        TextView txtNumero = filaSerie.findViewById(R.id.txtSerieNumero);
        txtNumero.setText(String.valueOf(numeroSerie));

        layoutSeries.addView(filaSerie);
        numeroSerie++;
    }
}
