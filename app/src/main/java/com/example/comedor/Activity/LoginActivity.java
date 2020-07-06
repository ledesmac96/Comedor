package com.example.comedor.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button mInicio;
    DialogoProcesamiento dialog;
    EditText edtUser, edtPass;
    PreferenciasManager mPreferenciasManager;
    TextView txtWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferenciasManager = new PreferenciasManager(getApplicationContext());

        if (mPreferenciasManager.getValue(Utils.IS_LOGIN)) {
            setContentView(R.layout.activity_login);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            loadViews();

            loadListener();

            loadData();

            changeTextWelcome();

        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }

    public void changeTextWelcome() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay <= 12) {
            txtWelcome.setText("¡Buen día!");
        } else if (timeOfDay >= 13 && timeOfDay <= 19) {
            txtWelcome.setText("¡Buenas tardes!");
        } else if (timeOfDay >= 20)
            txtWelcome.setText("¡Buenas noches!");
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
        txtWelcome = findViewById(R.id.txtWelcome);
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
            Utils.showCustomToast(LoginActivity.this, getApplicationContext(),
                    getString(R.string.camposIncompletos), R.drawable.ic_advertencia);
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
                Utils.showCustomToast(LoginActivity.this, getApplicationContext(),
                        "Error de conexión o servidor fuera de rango", R.drawable.ic_error);

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
                    Utils.showCustomToast(LoginActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    Utils.showCustomToast(LoginActivity.this, getApplicationContext(),
                            getString(R.string.sesionIniciada), R.drawable.ic_exito);
                    String token = jsonObject.getJSONObject("token").getString("token");

                    //Insertar BD
                    Usuario user = guardarDatos(jsonObject);
                    //Roles
                    saveRoles(jsonObject, user.getIdUsuario());
                    PreferenciasManager preferenceManager = new PreferenciasManager(getApplicationContext());
                    preferenceManager.setValue(Utils.IS_LOGIN, false);
                    int dni = user.getIdUsuario();
                    preferenceManager.setValue(Utils.IS_LOCK, false);
                    preferenceManager.setValue(Utils.MY_ID, dni);
                    preferenceManager.setValue(Utils.TOKEN, token);
                    //Main
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finishAffinity();
                    break;
                case 2:
                    //Usuario invalido
                    Utils.showCustomToast(LoginActivity.this, getApplicationContext(),
                            getString(R.string.usuarioInvalido), R.drawable.ic_error);
                    break;
                case 5:
                    //Usuario invalido
                    Utils.showCustomToast(LoginActivity.this, getApplicationContext(),
                            getString(R.string.usuarioInhabilitado), R.drawable.ic_error);
                    break;
                case 4:
                    //Usuario invalido
                    Utils.showCustomToast(LoginActivity.this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showCustomToast(LoginActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
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

                    String rol = object.getString("idrol");

                    Rol ro = new Rol(Integer.parseInt(rol), id, "test");

                    rolViewModel.insert(ro);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
