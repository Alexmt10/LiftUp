package com.iescamas.liftup.Mensajes;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class Token {

    public static void guardarTokenEnFirestore(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TokenManager", "No se pudo obtener el token FCM", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    FirebaseFirestore.getInstance()
                            .collection("Usuarios")
                            .document(userId)
                            .update("fcmToken", token)
                            .addOnSuccessListener(aVoid -> Log.d("TokenManager", "Token guardado correctamente: " + token))
                            .addOnFailureListener(e -> Log.e("TokenManager", "Error guardando token", e));
                });
    }
}
