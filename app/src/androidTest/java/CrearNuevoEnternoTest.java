package com.iescamas.liftup;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.iescamas.liftup.tipos.CrearEntrenamiento;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CrearNuevoEnternoTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(CrearEntrenamiento.class);
    }

    @Test
    public void testCrearYGuardarEntrenamiento() throws InterruptedException {
        onView(withId(R.id.txtTituloEntrenamiento)).perform(typeText("Entreno de prueba"), closeSoftKeyboard());

        onView(withId(R.id.spinnerMusculo)).perform(click());
        onView(withText("Pecho")).perform(click());

        Thread.sleep(500);

        onView(withId(R.id.spinnerEjercicio)).perform(click());
        onView(withText("Press banca")).perform(click());

        onView(withId(R.id.fabAgregarSerie)).perform(click());
        onView(withId(R.id.editRepeticionesid)).perform(typeText("10"), closeSoftKeyboard());
        onView(withId(R.id.editPesoid)).perform(typeText("50"), closeSoftKeyboard());

        onView(withId(R.id.btnAgregarEjercicio)).perform(click());

        onView(withId(R.id.btnGuardarEntrenamientoid)).perform(click());

        Thread.sleep(3000);


        onView(withText("Mis entrenamientos")).check(matches(isDisplayed()));
    }
}
