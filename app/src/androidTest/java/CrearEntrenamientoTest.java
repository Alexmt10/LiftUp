import static org.junit.Assert.assertNotNull;

import androidx.test.core.app.ActivityScenario;

import com.iescamas.liftup.R;
import com.iescamas.liftup.tipos.CrearEntrenamiento;
import com.iescamas.liftup.tipos.CrearPlanComida;

import org.junit.Test;


public class CrearEntrenamientoTest {


    @Test
    public void crearEntrenamiento_abreActivity_correctamente() {

        ActivityScenario<CrearEntrenamiento> scenario = ActivityScenario.launch(CrearEntrenamiento.class);
        scenario.onActivity(activity -> {
            assertNotNull(activity.findViewById(R.id.txtTituloEntrenamiento));
            assertNotNull(activity.findViewById(R.id.spinnerMusculo));
            assertNotNull(activity.findViewById(R.id.btnGuardarEntrenamientoid));
        });
    }
}
