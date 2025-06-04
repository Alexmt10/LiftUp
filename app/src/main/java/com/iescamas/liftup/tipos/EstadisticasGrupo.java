package com.iescamas.liftup.tipos;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.iescamas.liftup.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Actividad para mostrar y gestionar las estadísticas de los mejores pesos de los usuarios en diferentes ejercicios.
 * Permite a los usuarios guardar sus pesos y ver un gráfico con el top 3 de los mejores pesos para cada ejercicio.
 */

public class EstadisticasGrupo extends AppCompatActivity {

    private Spinner spinnerEjercicio;
    private EditText etKilos;
    private Button btnGuardar;
    private BarChart barChart;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String[] ejercicios = {"Press banca", "Sentadilla", "Peso muerto"};

    /**
     * Método llamado cuando la actividad es creada.
     * Inicializa los componentes de la interfaz de usuario, la base de datos Firestore y la autenticación de Firebase.
     * Configura el Spinner para seleccionar el ejercicio y el botón para guardar el peso.
     * @param savedInstanceState Estado previamente guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_grupo);

        spinnerEjercicio = findViewById(R.id.spinner_ejercicio);
        etKilos = findViewById(R.id.et_kilos);
        btnGuardar = findViewById(R.id.btn_guardar);
        barChart = findViewById(R.id.bar_chart);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ejercicios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEjercicio.setAdapter(adapter);

        spinnerEjercicio.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                cargarTop3(ejercicios[position]);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        btnGuardar.setOnClickListener(v -> {
            String ejercicio = ejercicios[spinnerEjercicio.getSelectedItemPosition()];
            String uid = auth.getCurrentUser().getUid();
            String pesoStr = etKilos.getText().toString().trim();

            if (pesoStr.isEmpty()) {
                Toast.makeText(this, "Ingresa un peso", Toast.LENGTH_SHORT).show();
                return;
            }

            double nuevoPeso = Double.parseDouble(pesoStr);

            db.collection("Usuarios").document(uid).get().addOnSuccessListener(usuarioDoc -> {
                String username = usuarioDoc.getString("username");
                if (username == null || username.isEmpty()) {
                    username = "Anónimo";
                }
                guardarPeso(ejercicio, uid, username, nuevoPeso);
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al obtener el nombre de usuario", Toast.LENGTH_SHORT).show();
            });
        });
    }

    /**
     * Guarda el peso del usuario para un ejercicio específico en la base de datos Firestore.
     * @param ejercicio El ejercicio para el cual se guarda el peso.
     * @param uid El ID del usuario.
     * @param username El nombre de usuario.
     * @param nuevoPeso El nuevo peso a guardar.
     */
    private void guardarPeso(String ejercicio, String uid, String username, double nuevoPeso) {
        DocumentReference docRef = db.collection("mejores_pesos")
                .document(ejercicio)
                .collection("Usuarios")
                .document(uid);

        docRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Double pesoActual = doc.getDouble("peso");
                if (pesoActual != null && nuevoPeso > pesoActual) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("peso", nuevoPeso);
                    datos.put("username", username);
                    docRef.set(datos).addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Nuevo récord guardado", Toast.LENGTH_SHORT).show();
                        etKilos.setText("");
                        cargarTop3(ejercicio);
                    });
                } else {
                    Toast.makeText(this, "Ya tienes un peso igual o mayor", Toast.LENGTH_SHORT).show();
                }
            } else {
                Map<String, Object> datos = new HashMap<>();
                datos.put("peso", nuevoPeso);
                datos.put("username", username);
                docRef.set(datos).addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Peso guardado", Toast.LENGTH_SHORT).show();
                    etKilos.setText("");
                    cargarTop3(ejercicio);
                });
            }
        });
    }

    /**
     * Carga y muestra el top 3 de los mejores pesos para un ejercicio seleccionado en un gráfico de barras.
     * @param ejercicioSeleccionado El ejercicio para el cual se cargan las estadísticas.
     */
    private void cargarTop3(String ejercicioSeleccionado) {
        db.collection("mejores_pesos")
                .document(ejercicioSeleccionado)
                .collection("Usuarios")
                .orderBy("peso", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> nombres = new ArrayList<>();
                    List<Float> pesos = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("username");
                        if (nombre == null || nombre.isEmpty()) {
                            nombre = "Desconocido";
                        }
                        Double pesoD = doc.getDouble("peso");
                        float peso = pesoD != null ? pesoD.floatValue() : 0f;

                        nombres.add(nombre);
                        pesos.add(peso);
                    }

                    while (nombres.size() < 3) {
                        nombres.add("-");
                        pesos.add(0f);
                    }

                    List<BarEntry> entries = new ArrayList<>();
                    List<String> labels = new ArrayList<>();

                    entries.add(new BarEntry(0, pesos.get(1)));
                    labels.add(nombres.get(1));

                    entries.add(new BarEntry(1, pesos.get(0)));
                    labels.add(nombres.get(0));

                    entries.add(new BarEntry(2, pesos.get(2)));
                    labels.add(nombres.get(2));

                    BarDataSet dataSet = new BarDataSet(entries, "Top 3 - " + ejercicioSeleccionado);
                    dataSet.setValueTextSize(16f);
                    List<Integer> customColors = new ArrayList<>();
                    customColors.add(Color.parseColor("#C0C0C0"));
                    customColors.add(Color.parseColor("#FFD700"));
                    customColors.add(Color.parseColor("#CD7F32"));

                    dataSet.setColors(customColors);

                    BarData data = new BarData(dataSet);
                    data.setBarWidth(0.9f);

                    barChart.setData(data);
                    barChart.getDescription().setEnabled(false);
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.getXAxis().setGranularity(1f);
                    barChart.getXAxis().setGranularityEnabled(true);
                    barChart.getAxisRight().setEnabled(false);
                    barChart.setFitBars(true);
                    barChart.invalidate();
                });
    }
}
