package com.example.comedor.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Database.UsuarioViewModel;
import com.example.comedor.Modelo.Usuario;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class LockedActivity extends AppCompatActivity {

    PreferenciasManager mPreferenciasManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPreferenciasManager = new PreferenciasManager(getApplicationContext());

        checkInfo();
    }

    private void checkInfo() {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(Utils.TOKEN);
        final int idLocal = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?key=%s&id=%s&idU=%s", Utils.URL_USUARIO_CHECK, key, idLocal, idLocal);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuesta(response, idLocal);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //Utils.showToast(getApplicationContext(), getString(R.string.servidorOff));
                Utils.showCustomToast(LockedActivity.this, getApplicationContext(),
                        getString(R.string.servidorOff), R.drawable.ic_error);

            }
        });
        //Abro dialogo para congelar pantalla
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void procesarRespuesta(String response, int idUser) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case 1:
                    UsuarioViewModel usuarioViewModel = new UsuarioViewModel(getApplicationContext());
                    Usuario usuario = usuarioViewModel.getById(idUser);
                    int validez = jsonObject.getInt("mensaje");
                    usuario.setValidez(validez);
                    usuarioViewModel.update(usuario);
                    if (validez == 1) {
                        mPreferenciasManager.setValue(Utils.IS_LOCK, false);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finishAffinity();
                    }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
