package com.iescamas.liftup.Fragment;

import android.content.Intent;
import com.google.firebase.storage.StorageReference;

import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import com.google.firebase.storage.FirebaseStorage;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iescamas.liftup.R;
import com.iescamas.liftup.SplashSubirFoto;
import com.iescamas.liftup.pojo.ItemEntrenamiento;
import com.iescamas.liftup.pojo.ItemPost;
import com.iescamas.liftup.pojo.PlanComida;
import com.iescamas.liftup.tipos.ListaEntrenamiento;
import com.iescamas.liftup.tipos.ListaPlanComida;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class NuevaFragment extends Fragment {

    ImageView imgAnadirFoto;
    ImageButton imgAnadirEntreno;
    ImageButton imgAnadirComida;
    Button btnAnadirPublicacion;
    MultiAutoCompleteTextView txtAnadirDescripcion;
    TextView txtEntrenoElegidoMostrar;
    TextView txtComidaElegidaMostrar;

    private Uri cropDestinationUri;

    // Lanza UCrop y recoge el resultado
    private final ActivityResultLauncher<Intent> ucropLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    // Obtenemos la URI de la imagen recortada
                    final Uri resultUri = UCrop.getOutput(result.getData());
                    if (resultUri != null) {
                        imgAnadirFoto.setImageURI(resultUri); // Mostramos la imagen recortada
                        imgAnadirFoto.setScaleType(ImageButton.ScaleType.CENTER_CROP); // Ajuste visual
                        imgAnadirFoto.setPadding(0, 0, 0, 0); // Quitamos padding para que se vea bien
                    } else {
                        Toast.makeText(getContext(), "Error: la imagen recortada es nula", Toast.LENGTH_SHORT).show();
                    }
                } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                    Throwable cropError = UCrop.getError(result.getData());
                    Toast.makeText(getContext(), "Error al recortar: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {


                        String fileName = "recorte_" + System.currentTimeMillis() + ".jpg";
                        File destino = new File(requireContext().getExternalFilesDir(null), fileName);
                        cropDestinationUri = Uri.fromFile(destino);

                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionQuality(100);
                        options.setFreeStyleCropEnabled(true);
                        options.setHideBottomControls(true);

                        Intent cropIntent = UCrop.of(selectedImageUri, cropDestinationUri)
                                .withOptions(options)
                                .withMaxResultSize(2000, 2000)
                                .getIntent(requireContext());

                        ucropLauncher.launch(cropIntent);
                    } else {
                        Toast.makeText(getContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nueva, container, false);

        imgAnadirFoto = view.findViewById(R.id.imgAnadirFotoid);
        imgAnadirFoto.setOnClickListener(v -> openGallery());
        imgAnadirEntreno = view.findViewById(R.id.imgAnadirEntrenoId);
        imgAnadirComida = view.findViewById(R.id.imganadirComidaid);
        btnAnadirPublicacion = view.findViewById(R.id.btnAnadirPublicacionid);
        txtAnadirDescripcion = view.findViewById(R.id.txtAnadirDescripcionid);
        txtEntrenoElegidoMostrar = view.findViewById(R.id.txtEntrenoElegidoMostrarId);
        txtComidaElegidaMostrar = view.findViewById(R.id.txtComidaElegidaMostrarId);

        txtEntrenoElegidoMostrar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarEstadoEntreno();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        txtComidaElegidaMostrar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarEstadoComida();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        imgAnadirEntreno.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ListaEntrenamiento.class);
            launcherEntrenamientoSeleccionado.launch(intent);
        });


        imgAnadirComida.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ListaPlanComida.class);
            launcherComidaSeleccionada.launch(intent);
        });

        btnAnadirPublicacion.setOnClickListener(v -> {
            String descripcion = txtAnadirDescripcion.getText().toString();



            if (descripcion.isEmpty()) {
                Toast.makeText(getContext(), "Escribe una descripción", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                Toast.makeText(getContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cropDestinationUri == null) {
                Toast.makeText(getContext(), "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getContext(), SplashSubirFoto.class);
            intent.putExtra("descripcion", descripcion);
            intent.putExtra("imagenUri", cropDestinationUri.toString());

            intent.putExtra("planEntreno", planEntrenamientoSeleccionado);
            intent.putExtra("planComida", planComidaSeleccionado);

            startActivity(intent);

            String userId = user.getUid();
            String userName = user.getDisplayName() != null ? user.getDisplayName() : "Anónimo";
            String fileName = "post_" + System.currentTimeMillis() + ".jpg";

            StorageReference storageRef = FirebaseStorage.getInstance().getReference("imagenes_posts/" + fileName);
            storageRef.putFile(cropDestinationUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String urlImagen = uri.toString();

                                    ItemPost post = new ItemPost(
                                            userName,
                                            descripcion,
                                            userId,
                                            urlImagen,
                                            planEntrenamientoSeleccionado,
                                            planComidaSeleccionado
                                    );

                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("publicaciones");
                                    dbRef.push()
                                            .setValue(post)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(getContext(), "Publicado con éxito", Toast.LENGTH_SHORT).show();
                                                txtAnadirDescripcion.setText("");
                                                imgAnadirFoto.setImageResource(R.drawable.icon_me);
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());


        });



        return view;
    }

    private void actualizarEstadoEntreno() {
        if (txtEntrenoElegidoMostrar != null && txtEntrenoElegidoMostrar.getText() != null) {
            String textoEntreno = txtEntrenoElegidoMostrar.getText().toString().trim();

            if (!textoEntreno.isEmpty()) {
                imgAnadirEntreno.setBackgroundColor(getResources().getColor(R.color.botones_check)); // Asegúrate de tener un color azul en colors.xml
            } else {
                imgAnadirEntreno.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }
    }
    private void actualizarEstadoComida() {
        if (txtComidaElegidaMostrar != null && txtComidaElegidaMostrar.getText() != null) {
            String textoEntreno = txtComidaElegidaMostrar.getText().toString().trim();

            if (!textoEntreno.isEmpty()) {
                imgAnadirComida.setBackgroundColor(getResources().getColor(R.color.botones_check)); // Asegúrate de tener un color azul en colors.xml
            } else {

                imgAnadirComida.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }
    }
    private PlanComida planComidaSeleccionado;

    private final ActivityResultLauncher<Intent> launcherComidaSeleccionada =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                            planComidaSeleccionado = (PlanComida) result.getData().getSerializableExtra("plan_comida");

                            if (planComidaSeleccionado != null) {
                                txtComidaElegidaMostrar.setText(planComidaSeleccionado.getNombre());
                            }
                        }
                    });

    private ItemEntrenamiento planEntrenamientoSeleccionado;

    private final ActivityResultLauncher<Intent> launcherEntrenamientoSeleccionado =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                            planEntrenamientoSeleccionado = (ItemEntrenamiento) result.getData().getSerializableExtra("plan_entrenamiento");

                            if (planEntrenamientoSeleccionado != null) {
                                txtEntrenoElegidoMostrar.setText(planEntrenamientoSeleccionado.getNombreEntrenamiento());
                            }
                        }
                    });



    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al abrir la galería", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }




}