package com.unse.bienestar.comedordos.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unse.bienestar.comedordos.Database.UsuarioViewModel;
import com.unse.bienestar.comedordos.Modelo.Usuario;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;

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
        String key = manager.getValueString(ABC.TOKEN);
        final int idLocal = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?key=%s&id=%s&idU=%s", ABC.URL_USUARIO_CHECK, key, idLocal, idLocal);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuesta(response, idLocal);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ABC.showCustomToast(LockedActivity.this, getApplicationContext(),
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
                        ABC.resetData(getApplicationContext());
                        mPreferenciasManager.setValue(ABC.IS_LOGIN, true);
                        mPreferenciasManager.setValue(ABC.IS_LOCK, true);
                        mPreferenciasManager.setValue(ABC.MY_ID, 0);
                        mPreferenciasManager.setValue(ABC.TOKEN, "");
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finishAffinity();
                    }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
