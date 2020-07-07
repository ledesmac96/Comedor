package com.unse.bienestar.comedor.Firebase;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.unse.bienestar.comedor.Utils.PreferenciasManager;
import com.unse.bienestar.comedor.Utils.Utils;
import com.unse.bienestar.comedor.Utils.VolleySingleton;

public class FCMInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = FCMInstanceIdService.class.getSimpleName();

    public FCMInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Token: " + fcmToken);
        sendTokenToServer(fcmToken);
    }

    private void sendTokenToServer(String fcmToken) {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        final int idLocal = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?tok=%s&id=%s", Utils.URL_USUARIO_TEST, fcmToken, idLocal);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        //Abro dialogo para congelar pantalla
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

}