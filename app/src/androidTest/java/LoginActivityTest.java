import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import com.iescamas.liftup.R;
import com.iescamas.liftup.registro_inicio.LoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Test
    public void testComponentesVisibles() {
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            System.out.println("Actividad lanzada correctamente: " + activity.getLocalClassName());
        });

        onView(withId(R.id.edit_usuario)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_contrasena)).check(matches(isDisplayed()));
        onView(withId(R.id.btnInicioSesion)).check(matches(isDisplayed()));
        onView(withId(R.id.txtregistro)).check(matches(isDisplayed()));
        onView(withId(R.id.txtOlvidoContrasena)).check(matches(isDisplayed()));

        scenario.close(); // Cierra explícitamente después del test
    }

    @Test
    public void testBotonInicioSesionCamposVacios() {
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.btnInicioSesion)).perform(click());

        onView(withId(R.id.edit_usuario)).check(matches(isDisplayed()));

        scenario.close();
    }
}
