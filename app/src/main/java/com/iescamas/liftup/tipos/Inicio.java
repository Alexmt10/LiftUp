package com.iescamas.liftup.tipos;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.iescamas.liftup.Fragment.BuscarFragment;
import com.iescamas.liftup.Fragment.InicioFragment;
import com.iescamas.liftup.Fragment.MatchFragment;
import com.iescamas.liftup.Fragment.NuevaFragment;
import com.iescamas.liftup.Fragment.YoFragment;
import com.iescamas.liftup.R;

/**
 * Clase Inicio que representa la actividad principal de la aplicación.
 * Esta actividad contiene una barra de navegación inferior para cambiar entre diferentes fragmentos.
 */
public class Inicio extends AppCompatActivity {


    /**
     * Método llamado cuando la actividad es creada por primera vez.
     * Configura la vista de la actividad y la barra de navegación inferior.
     *
     * @param savedInstanceState Si la actividad está siendo recreada después de haber sido destruida previamente, este Bundle contiene los datos que suministró más recientemente en onSaveInstanceState(Bundle). De lo contrario, es nulo.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewid);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.iconInicioId) loadfragment(new InicioFragment());
                if (item.getItemId() == R.id.iconGrupoId) loadfragment(new BuscarFragment());
                if (item.getItemId() == R.id.iconMatchId) loadfragment(new MatchFragment());
                if (item.getItemId() == R.id.iconMeId) loadfragment(new YoFragment());
                if (item.getItemId() == R.id.iconNuevaId) loadfragment(new NuevaFragment());

                return true;
            }
        });
        loadfragment(new InicioFragment());

    }


    /**
     * Método para cargar un fragmento en el contenedor de fragmentos.
     *
     * @param fragment El fragmento que se va a cargar.
     */
    public void loadfragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameid, fragment);
        fragmentTransaction.commit();
    }
}