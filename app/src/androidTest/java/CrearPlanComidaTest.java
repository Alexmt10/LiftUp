import static org.junit.Assert.assertNotNull;

import androidx.test.core.app.ActivityScenario;

import com.iescamas.liftup.R;
import com.iescamas.liftup.tipos.CrearPlanComida;

import org.junit.Test;

public class CrearPlanComidaTest {

    @Test
    public void crearPlanComida_abreActivity_correctamente() {
        ActivityScenario<CrearPlanComida> scenario = ActivityScenario.launch(CrearPlanComida.class);

        scenario.onActivity(activity -> {
            assertNotNull(activity.findViewById(R.id.CrearAlimentoNombreId));
            assertNotNull(activity.findViewById(R.id.CrearAlimentoGramosId));
            assertNotNull(activity.findViewById(R.id.CrearAlimentoCaloriasId));
            assertNotNull(activity.findViewById(R.id.CrearAlimentoProteinasId));
            assertNotNull(activity.findViewById(R.id.CrearAlimentoGrasasId));
            assertNotNull(activity.findViewById(R.id.CrearAlimentoCarbohidratosId));

            assertNotNull(activity.findViewById(R.id.CrearAlimentoTituloPlatoId));

            assertNotNull(activity.findViewById(R.id.btnAddAlimento));
            assertNotNull(activity.findViewById(R.id.btnGuardar));

            assertNotNull(activity.findViewById(R.id.containerAlimentos));
        });
    }
}