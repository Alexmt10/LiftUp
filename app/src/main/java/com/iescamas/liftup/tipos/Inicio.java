package com.iescamas.liftup.tipos;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.iescamas.liftup.Fragment.GrupoFragment;
import com.iescamas.liftup.Fragment.InicioFragment;
import com.iescamas.liftup.Fragment.MatchFragment;
import com.iescamas.liftup.Fragment.NuevaFragment;
import com.iescamas.liftup.Fragment.YoFragment;
import com.iescamas.liftup.R;

public class Inicio extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewid);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()==R.id.iconInicioId) loadfragment(new InicioFragment());
                if (item.getItemId()==R.id.iconGrupoId) loadfragment(new GrupoFragment());
                if (item.getItemId()==R.id.iconMatchId) loadfragment(new MatchFragment());
                if (item.getItemId()==R.id.iconMeId) loadfragment(new YoFragment());
                if (item.getItemId()==R.id.iconNuevaId) loadfragment(new NuevaFragment());

                return true;
            }
        });
        loadfragment(new InicioFragment());

    }


    private void loadfragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameid, fragment);
        fragmentTransaction.commit();
    }
}