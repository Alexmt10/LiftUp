

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.iescamas.liftup.Fragment.NuevaFragment;
import com.iescamas.liftup.R;
import com.iescamas.liftup.Tester.ToastMatcher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NuevaFragmentTest {

    private NuevaFragment nuevaFragment;

    @Before
    public void setUp() {
        nuevaFragment = NuevaFragment.newInstance("test_user_id");
    }

    @Test
    public void testCrearRutina() {
        String nombreRutina = "Rutina de prueba";
        String ejercicio1 = "Ejercicio 1";
        String series1 = "3";
        String repeticiones1 = "10";

        onView(withId(R.id.txtTituloEntrenamiento)).perform(typeText(nombreRutina), closeSoftKeyboard());
        onView(withId(R.id.spinner_ejercicio)).perform(typeText(ejercicio1), closeSoftKeyboard());
        onView(withId(R.id.layoutSeries)).perform(typeText(series1), closeSoftKeyboard());
        onView(withId(R.id.editRepeticionesid)).perform(typeText(repeticiones1), closeSoftKeyboard());

        onView(withId(R.id.btnGuardarEntrenamientoid)).perform(click());

    }

}
