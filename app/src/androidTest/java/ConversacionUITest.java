

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.iescamas.liftup.R;
import com.iescamas.liftup.tipos.ConversacionUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ConversacionUITest {

    @Rule
    public ActivityScenarioRule<ConversacionUser> activityRule =
            new ActivityScenarioRule<>(ConversacionUser.class);

    @Test
    public void testEnviarMensaje() {
        onView(withId(R.id.campo_mensaje)).perform(typeText("Hola Alejandro"), closeSoftKeyboard());

        onView(withId(R.id.boton_enviar)).perform(click());

        onView(withId(R.id.campo_mensaje)).check(matches(withText("")));
    }
}
