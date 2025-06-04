package com.iescamas.liftup;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class testFirestoreGuardarMensaje {

    @Test
    public void testFirestoreGuardarMensaje() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] fallo = {false};

        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String mensaje = "Mensaje de prueba integración";
                String uid = auth.getCurrentUser().getUid();

                Map<String, Object> datos = new HashMap<>();
                datos.put("emisor", uid);
                datos.put("receptor", "usuario_destino_id");
                datos.put("mensaje", mensaje);

                db.collection("chats").add(datos).addOnSuccessListener(doc -> {
                    Log.d("Test", "Mensaje guardado: " + doc.getId());
                    latch.countDown();
                }).addOnFailureListener(e -> {
                    fallo[0] = true;
                    Log.e("Test", "Error guardando en Firestore: ", e);
                    latch.countDown();
                });
            } else {
                fallo[0] = true;
                Log.e("Test", "Error autenticando usuario anónimo");
                latch.countDown();
            }
        });

        boolean completado = latch.await(10, TimeUnit.SECONDS);

        assertTrue("La operación no se completó a tiempo", completado);
        assertFalse("Falló la operación de guardar mensaje", fallo[0]);
    }
}
