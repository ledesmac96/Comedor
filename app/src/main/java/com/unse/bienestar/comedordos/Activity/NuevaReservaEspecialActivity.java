package com.unse.bienestar.comedordos.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unse.bienestar.comedordos.Dialogos.DialogoGeneral;
import com.unse.bienestar.comedordos.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedordos.Modelo.Reserva;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.Validador;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;
import com.unse.bienestar.comedordos.Utils.YesNoDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class NuevaReservaEspecialActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtPorcion, edtDescripcion, edtDNI;
    Button btnGuardar;
    int idMenu = -1;
    DialogoProcesamiento dialog;
    ImageView btnBack;
    EditText edtUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reserva_especial);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadIntent();

        setToolbar();

        loadView();

        loadData();

        loadListener();

    }

    private void loadIntent() {
        if (getIntent().getIntExtra(ABC.ID_MENU, -1) != -1) {
            idMenu = getIntent().getIntExtra(ABC.ID_MENU, -1);
        }
    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setText("Reserva especial");
    }

    private void loadListener() {
        btnGuardar.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void loadData() {
        edtPorcion.setText("2");
    }

    private void loadView() {
        edtUsuario = findViewById(R.id.edtNombre);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtPorcion = findViewById(R.id.edtPorcion);
        edtDNI = findViewById(R.id.edtDNI);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnBack = findViewById(R.id.imgFlecha);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.btnGuardar:
                save();
                break;
        }
    }

    private void save() {
        String descripcion = edtDescripcion.getText().toString();
        String dni = edtDNI.getText().toString();
        if (dni.equals("0")) dni = "1";
        String porcion = edtPorcion.getText().toString();
        Validador validador = new Validador(getApplicationContext());
        String nombre = edtUsuario.getText().toString();
        descripcion = descripcion + " - " + nombre;
        if (!validador.noVacio(descripcion) && validador.validarNumero(edtPorcion)
                && validador.validarNumero(edtDNI)) {
            if (idMenu != -1) {
                sendServer(descripcion, porcion, dni, idMenu);
            } else {
                ABC.showCustomToast(this, getApplicationContext(),
                        getString(R.string.elegirMenu), R.drawable.ic_advertencia);
            }
        } else {
            ABC.showCustomToast(this, getApplicationContext(),
                    getString(R.string.camposInvalidos), R.drawable.ic_error);
        }
    }

    private void sendServer(String descripcion, String porcion, String dni, int idmenu) {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int idLocal = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?key=%s&idU=%s&i=%s&im=%s&t=%s&ie=%s&des=%s&p=%s", ABC.URL_RESERVA_INSERTAR_ESPECIAL
                , key, idLocal, dni, idmenu, 4, idLocal, descripcion, porcion);
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ABC.showCustomToast(NuevaReservaEspecialActivity.this, getApplicationContext(),
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
                    ABC.showCustomToast(this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    loadInfo(jsonObject);
                    break;
                case 2:
                    ABC.showCustomToast(this, getApplicationContext(),
                            getString(R.string.reservaError), R.drawable.ic_error);
                    break;
                case 4:
                    ABC.showCustomToast(this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_advertencia);
                    break;
                case 5:
                    ABC.showCustomToast(this, getApplicationContext(),
                            getString(R.string.yaReservoEspecial), R.drawable.ic_error);
                    break;
                case 3:
                    ABC.showCustomToast(this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    ABC.showCustomToast(this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showCustomToast(this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje") && jsonObject.has("dato")) {

                Reserva reserva = Reserva.mapper(jsonObject.getJSONObject("dato"), Reserva.ESPECIALES);

                dialogReserva(true, reserva);
            } else {
                dialogReserva(false, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialogReserva(false, null);
        }
    }

    private void dialogReserva(boolean b, Reserva reserva) {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo(getString(b ? R.string.reservado : R.string.salioMal))
                .setDescripcion(b ? String.format(getString(R.string.reservaAdminExito), String.valueOf(reserva.getIdReserva()))
                        : getString(R.string.reservaError))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        finish();
                    }

                    @Override
                    public void no() {

                    }
                })
                .setIcono(b ? R.drawable.ic_exito : R.drawable.ic_advertencia)
                .setTipo(DialogoGeneral.TIPO_ACEPTAR);
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.show(getSupportFragmentManager(), "dialog_ad");
    }
}
