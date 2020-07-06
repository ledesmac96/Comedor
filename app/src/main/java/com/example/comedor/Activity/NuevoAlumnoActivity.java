package com.example.comedor.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.Validador;
import com.example.comedor.Utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class NuevoAlumnoActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtNombre, edtApellido, edtDNI, edtLegajo, edtAnioIngreso;
    Spinner mSpinnerFacultad, mSpinnerCarrera;
    ImageView btnBack;
    ArrayAdapter<String> carreraAdapter;
    ArrayAdapter<String> facultadAdapter;
    DialogoProcesamiento dialog;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alumno);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setToolbar();

        loadViews();

        loadData();

        loadListener();
    }

    private void loadListener() {
        btnRegister.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        mSpinnerFacultad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                mSpinnerFacultad.setSelection(position);
                switch (position) {
                    case 0:
                        //FAyA
                        carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.style_spinner, Utils.faya);
                        carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                        mSpinnerCarrera.setAdapter(carreraAdapter);
                        break;
                    case 1:
                        //FCEyT
                        carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.style_spinner, Utils.fceyt);
                        carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                        mSpinnerCarrera.setAdapter(carreraAdapter);
                        break;
                    case 2:
                        //FCF
                        carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.style_spinner, Utils.fcf);
                        carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                        mSpinnerCarrera.setAdapter(carreraAdapter);
                        break;
                    case 3:
                        //FCM
                        carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.style_spinner, Utils.fcm);
                        carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                        mSpinnerCarrera.setAdapter(carreraAdapter);
                        break;
                    case 4:
                        //FHyCS
                        carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.style_spinner, Utils.fhcys);
                        carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                        mSpinnerCarrera.setAdapter(carreraAdapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
        mSpinnerCarrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                facultadAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.style_spinner, Utils.facultad);
                facultadAdapter.setDropDownViewResource(R.layout.style_spinner);
                mSpinnerFacultad.setAdapter(facultadAdapter);
                carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.style_spinner, Utils.faya);
                carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                mSpinnerCarrera.setAdapter(carreraAdapter);
            }
        }).start();
    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setText("");
    }

    private void loadViews() {
        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtDNI = findViewById(R.id.edtDNI);
        edtAnioIngreso = findViewById(R.id.edtAnioIngrAlu);
        edtLegajo = findViewById(R.id.edtLegajo);
        btnBack = findViewById(R.id.imgFlecha);
        mSpinnerCarrera = findViewById(R.id.spinner2);
        mSpinnerFacultad = findViewById(R.id.spinner1);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private String getCarrera(int selectedItemPosition) {
        switch (selectedItemPosition) {
            case 0:
                return Utils.faya[mSpinnerCarrera.getSelectedItemPosition()];
            case 1:
                return Utils.fceyt[mSpinnerCarrera.getSelectedItemPosition()];
            case 2:
                return Utils.fcf[mSpinnerCarrera.getSelectedItemPosition()];
            case 3:
                return Utils.fcm[mSpinnerCarrera.getSelectedItemPosition()];
            case 4:
                return Utils.fhcys[mSpinnerCarrera.getSelectedItemPosition()];
        }
        return "";
    }


    private void save() {
        Validador validador = new Validador(getApplicationContext());

        String nombre = edtNombre.getText().toString().trim();
        String apellido = edtApellido.getText().toString().trim();
        String dni = edtDNI.getText().toString().trim();
        String faculta = Utils.facultad[mSpinnerFacultad.getSelectedItemPosition()].trim();
        String carrera = getCarrera(mSpinnerFacultad.getSelectedItemPosition()).trim();
        String anioIngreso2 = edtAnioIngreso.getText().toString().trim();
        String legajo = edtLegajo.getText().toString().trim();

        if (validador.validarDNI(edtDNI)
                && !validador.validarNombresEdt(edtNombre, edtApellido)) {
            sendServer(processString(dni, nombre, apellido, carrera, faculta, anioIngreso2
                    , legajo));

        } else Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                getString(R.string.camposInvalidos), R.drawable.ic_error);
    }

    public String processString(String dni, String nombre, String apellido, String carrera,
                                String facultad, String anioIng, String legajo) {
        String resp = "";
        resp = String.format(Utils.dataUser, dni, nombre, apellido, carrera, facultad,
                anioIng, legajo);

        return resp;
    }

    public void sendServer(String data) {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(Utils.TOKEN);
        int idLocal = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s%s&key=%s&id=%s", Utils.URL_USUARIO_INSERTAR, data, key, idLocal);
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                        getString(R.string.servidorOff), R.drawable.ic_error);
                dialog.dismiss();

            }
        });
        //Abro dialogo para congelar pantalla
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
                    Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                            getString(R.string.usuarioCreado), R.drawable.ic_exito);
                    finish();
                    break;
                case 2:
                    Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                            getString(R.string.usuarioErrorCrear), R.drawable.ic_error);
                    break;
                case 4:
                    Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_error);
                    break;
                case 5:
                    Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                            getString(R.string.usuarioYaExiste), R.drawable.ic_error);
                    break;
                case 3:
                    Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showCustomToast(NuevoAlumnoActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                save();
                break;
            case R.id.imgFlecha:
                onBackPressed();
                break;
        }
    }
}
