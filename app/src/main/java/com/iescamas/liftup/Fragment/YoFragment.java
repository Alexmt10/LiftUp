package com.iescamas.liftup.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iescamas.liftup.Fragment.FragmentSecundarios.PubliGuardadasYoFragment;
import com.iescamas.liftup.Fragment.FragmentSecundarios.PublicacionesYoFragment;
import com.iescamas.liftup.R;
import com.iescamas.liftup.pojo.ItemPost;
import com.iescamas.liftup.tipos.Configuraciones;
import com.iescamas.liftup.tipos.ListaEntrenamiento;
import com.iescamas.liftup.tipos.ListaPlanComida;

/**
 * Fragmento que muestra el perfil del usuario, sus publicaciones, seguidores,
 * seguidos y opciones para crear contenido y acceder a la configuración.
 * Utiliza Firebase para la autenticación y el almacenamiento de datos.
 */
public class YoFragment extends Fragment {

    FloatingActionButton crearComida;
    FloatingActionButton crearEntrenamiento;
    ImageView iconMenu, iconoperfil;
    TextView nombreusuario, seguidores, seguidos, publicaciones, descripcion;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference dbRefRT;
    private String uidUsuario;

    /**
     * Se llama cuando el fragmento debe crear su vista.
     * Infla el layout y inicializa los componentes de la interfaz de usuario,
     * configura los listeners de los botones y carga los datos del usuario.
     * @param inflater El LayoutInflater que se puede usar para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, esta es la vista principal a la que se debe adjuntar la interfaz de usuario del fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anteriormente.
     * @return Devuelve la Vista para la interfaz de usuario del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yo, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dbRefRT = FirebaseDatabase.getInstance().getReference();

        crearComida = view.findViewById(R.id.flobtnCrearComidaYoId);
        crearEntrenamiento = view.findViewById(R.id.flobtnCrearEntrenamientoYoId);
        iconMenu = view.findViewById(R.id.IconMenuYoid);
        iconoperfil = view.findViewById(R.id.IconPerfilYoid);
        nombreusuario = view.findViewById(R.id.txtNombreUsuarioYoid);
        descripcion = view.findViewById(R.id.txtDescripcionYoId);

        seguidores = view.findViewById(R.id.txtNumeroSeguidoresYoId);
        seguidos = view.findViewById(R.id.txtNumeroSeguidosYoId);
        publicaciones = view.findViewById(R.id.txtNumeroPublicaiconesYoId);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            uidUsuario = user.getUid();

            ViewPager2 paginator = view.findViewById(R.id.viewPagerYoid);
            FragmentStateAdapter pageadapter = new deslizador(this);
            paginator.setAdapter(pageadapter);

            TabLayout tabla = view.findViewById(R.id.tabLayoutYoid);
            new TabLayoutMediator(tabla, paginator, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("publication");
                        break;
                    case 1:
                        tab.setText("save");
                        break;
                }
            }).attach();

            crearEntrenamiento.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), ListaEntrenamiento.class));
            });

            crearComida.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), ListaPlanComida.class));
            });

            iconMenu.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), Configuraciones.class));
            });

            cargarDatosUsuario();
            cargarSeguidores();
            cargarSeguidos();
            cargarPublicaciones();

        } else {
            Log.e("YoFragment", "Usuario no autenticado");
            Toast.makeText(getContext(), "Por favor, inicia sesión", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), Configuraciones.class); // O LoginActivity si la tienes
            startActivity(intent);
            requireActivity().finish();
        }

        return view;
    }

    /**
     * Adaptador para el ViewPager2 que maneja los fragmentos de publicaciones y guardados.
     */
    private class deslizador extends FragmentStateAdapter {
        /**
         * Constructor para el adaptador.
         * @param fragment El fragmento padre.
         */
        public deslizador(@NonNull Fragment fragment) {
            super(fragment);
        }

        /**
         * Crea el fragmento para la posición dada.
         * @param position La posición del fragmento a crear.
         * @return El fragmento creado.
         */
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return new PublicacionesYoFragment();
            else return new PubliGuardadasYoFragment();
        }
        /**
         * Devuelve el número total de fragmentos.
         * @return El número de fragmentos.
         */
        @Override
        public int getItemCount() {
            return 2;
        }
    }

    /**
     * Carga los datos del usuario desde Firestore y actualiza la interfaz de usuario.
     * Obtiene el nombre de usuario, la descripción y la URL de la imagen de perfil.
     * Utiliza Glide para cargar la imagen de perfil de forma asíncrona.
     * Muestra imágenes de placeholder y error en caso de que la carga falle.
     * Este método se llama después de que el usuario se haya autenticado correctamente.
     * @see FirebaseFirestore
     */
    private void cargarDatosUsuario() {
        db.collection("Usuarios").document(uidUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("username");
                        String descripcionUsuario = documentSnapshot.getString("descripcion");
                        String imagenUrl = documentSnapshot.getString("imagenPerfilUrl");

                        if (nombre != null) nombreusuario.setText(nombre);
                        if (descripcionUsuario != null) descripcion.setText(descripcionUsuario);
                        if (imagenUrl != null) {
                            Glide.with(requireContext())
                                    .load(imagenUrl)
                                    .transform(new CircleCrop())
                                    .placeholder(R.drawable.messi)
                                    .error(R.drawable.cristiano)
                                    .into(iconoperfil);
                        }
                    }
                });
    }

    /**
     * Carga el número de seguidores del usuario desde Firestore y actualiza el TextView correspondiente.
     * Escucha los cambios en tiempo real en la colección de seguidores del usuario.
     * Si ocurre un error durante la carga, no se realiza ninguna acción.
     * @see FirebaseFirestore
     */
    private void cargarSeguidores() {
        db.collection("seguidores").document(uidUsuario).collection("usuarios")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) return;
                    if (querySnapshot != null) {
                        seguidores.setText(String.valueOf(querySnapshot.size()));
                    }
                });
    }

    /**
     * Carga el número de usuarios a los que sigue el usuario actual desde Firestore y actualiza el TextView correspondiente.
     * Escucha los cambios en tiempo real en la colección de seguidos del usuario.
     * Si ocurre un error durante la carga, no se realiza ninguna acción.
     * @see FirebaseFirestore
     */
    private void cargarSeguidos() {
        db.collection("seguidos").document(uidUsuario).collection("usuarios")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) return;
                    if (querySnapshot != null) {
                        seguidos.setText(String.valueOf(querySnapshot.size()));
                    }
                });
    }

    /**
     * Carga el número de publicaciones realizadas por el usuario desde Firebase Realtime Database
     * y actualiza el TextView correspondiente.
     * Itera sobre todas las publicaciones y cuenta aquellas cuyo UID de usuario coincide con el del usuario actual.
     * En caso de error al cargar los datos, establece el contador de publicaciones en "0".
     * @see FirebaseDatabase
     */
    private void cargarPublicaciones() {
        dbRefRT.child("publicaciones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int contador = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ItemPost post = postSnapshot.getValue(ItemPost.class);
                    if (post != null && uidUsuario.equals(post.getUidUsuario())) {
                        contador++;
                    }
                }
                publicaciones.setText(String.valueOf(contador));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                publicaciones.setText("0");
            }
        });
    }
}
