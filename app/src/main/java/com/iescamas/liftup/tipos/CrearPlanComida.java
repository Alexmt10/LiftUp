package com.iescamas.liftup.tipos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemAlimento;
import com.iescamas.liftup.pojo.PlanComida;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad para crear un nuevo plan de comida.
 * Permite al usuario añadir varios alimentos, especificar sus detalles y guardar el plan.
 */
public class CrearPlanComida extends AppCompatActivity {

    private LinearLayout containerAlimentos;
    private FloatingActionButton btnAddAlimento;
    private Button btnGuardarAlimento;
    public static boolean isTestMode = false;

    @Override
    /**
     * Inicializa la actividad, establece el layout y configura los listeners de los botones.
     *
     * @param savedInstanceState Si la actividad se reinicia, este Bundle contiene los datos más recientes.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_plan_comida);

        containerAlimentos = findViewById(R.id.containerAlimentos);
        btnAddAlimento = findViewById(R.id.btnAddAlimento);
        btnGuardarAlimento = findViewById(R.id.btnGuardar);

        agregarNuevoAlimento();

        btnAddAlimento.setOnClickListener(v -> agregarNuevoAlimento());
        btnGuardarAlimento.setOnClickListener(v -> guardarAlimentos());
    }

    /**
     * Añade una nueva vista de alimento al layout.
     * Infla el layout `item_crear_alimento` y lo añade al `containerAlimentos`.
     */
    private void agregarNuevoAlimento() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View alimentoView = inflater.inflate(R.layout.item_crear_alimento, containerAlimentos, false);
        containerAlimentos.addView(alimentoView);
    }
    /**
     * Recopila los datos de los alimentos ingresados y guarda el plan de comida en Firebase.
     */
    private void guardarAlimentos() {
        List<ItemAlimento> listaAlimentos = new ArrayList<>();

        for (int i = 0; i < containerAlimentos.getChildCount(); i++) {
            View alimentoView = containerAlimentos.getChildAt(i);

            try {
                ItemAlimento alimento = new ItemAlimento(
                        getTextFromEditText(alimentoView, R.id.CrearAlimentoNombreId),
                        getDoubleFromEditText(alimentoView, R.id.CrearAlimentoGramosId),
                        getDoubleFromEditText(alimentoView, R.id.CrearAlimentoCaloriasId),
                        getDoubleFromEditText(alimentoView, R.id.CrearAlimentoProteinasId),
                        getDoubleFromEditText(alimentoView, R.id.CrearAlimentoGrasasId),
                        getDoubleFromEditText(alimentoView, R.id.CrearAlimentoCarbohidratosId)
                );
                listaAlimentos.add(alimento);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error en los valores del alimento " + (i + 1), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        EditText etNombreComida = findViewById(R.id.CrearAlimentoTituloPlatoId);
        String nombrePlan = etNombreComida.getText().toString().trim();
        if (nombrePlan.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce el nombre del plan de comida", Toast.LENGTH_SHORT).show();
            return;
        }

        PlanComida plan = new PlanComida(nombrePlan, listaAlimentos);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("planes_comida");
        String id = dbRef.push().getKey();

        if (id != null) {
            dbRef.child(id).setValue(plan)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Plan guardado correctamente", Toast.LENGTH_SHORT).show();
                        if (!isTestMode) {
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar plan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(this, "No se pudo generar ID para el plan", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Obtiene el texto de un EditText dentro de una vista padre.
     *
     * @param parentView La vista padre que contiene el EditText.
     * @param id El ID del EditText.
     * @return El texto del EditText.
     */
    private String getTextFromEditText(View parentView, int id) {
        return ((EditText) parentView.findViewById(id)).getText().toString();
    }
    /**
     * Obtiene el valor numérico de un EditText.
     * @param parentView La vista padre que contiene el EditText.
     * @param id El ID del EditText.
     * @return El valor numérico.
     * @throws NumberFormatException Si el texto no es un número válido.
     */
    private double getDoubleFromEditText(View parentView, int id) throws NumberFormatException {
        String text = getTextFromEditText(parentView, id);
        if (text.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(text);
    }
}
