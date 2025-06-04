

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.iescamas.liftup.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChatMessageTest {

    @Before
    public void launchActivity() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.iescamas.liftup", "com.iescamas.liftup.tipos.ConversacionUser");
        ActivityScenario.launch(intent);
    }

    @Test
    public void testEnviarMensaje() {
        String mensajeDePrueba = "Hola desde el test!";

        onView(withId(R.id.campo_mensaje))
                .perform(typeText(mensajeDePrueba));
        closeSoftKeyboard();

        onView(withId(R.id.boton_enviar)).perform(click());

        onView(withText(mensajeDePrueba)).check(matches(isDisplayed()));
    }
}
