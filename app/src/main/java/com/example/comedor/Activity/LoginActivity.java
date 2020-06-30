package com.example.comedor.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Database.AlumnoViewModel;
import com.example.comedor.Database.RolViewModel;
import com.example.comedor.Database.UsuarioViewModel;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.Alumno;
import com.example.comedor.Modelo.Rol;
import com.example.comedor.Modelo.Usuario;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.Validador;
import com.example.comedor.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button mInicio;
    ImageView btnBack;
    LinearLayout layoutFondo;
    DialogoProcesamiento dialog;
    EditText edtUser, edtPass;
    PreferenciasManager mPreferenciasManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferenciasManager = new PreferenciasManager(getApplicationContext());

        if (mPreferenciasManager.getValue(Utils.IS_LOGIN)) {
            setContentView(R.layout.activity_login);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

          /*  Glide.with(this).load(R.drawable.img_unse2)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            layoutFondo.setBackground(resource);
                        }
                    });
*/
            loadViews();

            loadListener();

            loadData();

        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


    }

    private void loadData() {

    }

    private void loadListener() {
        mInicio.setOnClickListener(this);
    }

    private void loadViews() {
        mInicio = findViewById(R.id.sesionOn);
        edtPass = findViewById(R.id.edtPass);
        edtUser = findViewById(R.id.edtUser);
        layoutFondo = findViewById(R.id.backgroundlogin);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sesionOn:
                login();
                break;
            case R.id.txtPassMissed:
                //startActivity(new Intent(LoginActivity.this, RecuperarContraseniaActivity.class));
                break;
        }
    }

    private void login() {
        String usuario = edtUser.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        Validador validador = new Validador(getApplicationContext());

        if (validador.validarContraseña(edtPass) && validador.validarDNI(edtUser)) {
            sendServer(usuario, pass);
        } else {
            Utils.showToast(getApplication(), getString(R.string.camposIncompletos));
        }
    }

    private void sendServer(String usuario, String pass) {
        String URL = String.format("%s?id=%s&pass=%s", Utils.URL_USUARIO_LOGIN, usuario, pass);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Utils.showToast(getApplicationContext(), "Error de conexión o servidor fuera de rango");

            }
        });
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog_process");
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void procesarRespuesta(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    Utils.showToast(getApplicationContext(), getString(R.string.sesionIniciada));
                    String token = jsonObject.getJSONObject("token").getString("token");

                    //Insertar BD
                    Usuario user = guardarDatos(jsonObject);
                    //Roles
                    saveRoles(jsonObject, user.getIdUsuario());
                    PreferenciasManager preferenceManager = new PreferenciasManager(getApplicationContext());
                    preferenceManager.setValue(Utils.IS_LOGIN, false);
                    int dni = user.getIdUsuario();
                    preferenceManager.setValue(Utils.MY_ID, dni);
                    preferenceManager.setValue(Utils.TOKEN, token);
                    //Main
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finishAffinity();
                    break;
                case 2:
                    //Usuario invalido
                    Utils.showToast(getApplicationContext(), getString(R.string.usuarioInvalido));
                    break;
                case 5:
                    //Usuario invalido
                    Utils.showToast(getApplicationContext(), getString(R.string.usuarioInhabilitado));
                    break;
                case 4:
                    //Usuario invalido
                    Utils.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private Usuario guardarDatos(JSONObject datos) {
        Usuario usuario = null;
        try {
            usuario = Usuario.mapper(datos, Usuario.COMPLETE);
            UsuarioViewModel usuarioViewModel = new UsuarioViewModel(getApplicationContext());
            usuarioViewModel.insert(usuario);
            Alumno alumno = Alumno.mapper(datos, usuario);
            AlumnoViewModel alumnoViewModel = new AlumnoViewModel(getApplicationContext());
            alumnoViewModel.insert(alumno);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuario;
    }

    private void saveRoles(JSONObject jsonObject, int id) {
        RolViewModel rolViewModel = new RolViewModel(getApplicationContext());
        if (jsonObject.has("roles")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("roles");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    String rol = object.getString("idRol");

                    Rol ro = new Rol(Integer.parseInt(rol), id, "test");

                    rolViewModel.insert(ro);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
