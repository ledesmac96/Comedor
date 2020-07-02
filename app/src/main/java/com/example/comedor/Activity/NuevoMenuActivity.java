package com.example.comedor.Activity;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Dialogos.DatePickerFragment;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.Validador;
import com.example.comedor.Utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class NuevoMenuActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtPorcion, edtAlmuerzo, edtCena, edtPostre, edtFecha;
    Button btnGuardar;
    int diaF = -1, mesF = -1, anioF = -1;
    DialogoProcesamiento dialog;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setToolbar();

        loadView();

        loadData();

        loadListener();

    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setText("Nuevo menú diario");
    }

    private void loadListener() {
        btnGuardar.setOnClickListener(this);
        edtFecha.setOnClickListener(this);
    }

    private void loadData() {
        edtPorcion.setText("2");
    }

    private void loadView() {
        edtFecha = findViewById(R.id.edtFecha);
        edtPorcion = findViewById(R.id.edtPorcion);
        edtAlmuerzo = findViewById(R.id.edtAlmuerzo);
        edtCena = findViewById(R.id.edtCena);
        edtPostre = findViewById(R.id.edtPostre);
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
            case R.id.edtFecha:
                elegirFechaNacimiento();
                break;
        }
    }

    private void elegirFechaNacimiento() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String mes, dia;
                month = month + 1;
                if (month < 10) {
                    mes = "0" + month;
                } else
                    mes = String.valueOf(month);
                if (day < 10)
                    dia = "0" + day;
                else
                    dia = String.valueOf(day);
                diaF = day;
                mesF = month;
                anioF = year;
                final String selectedDate = year + "-" + mes + "-" + dia;
                edtFecha.setText(selectedDate);
            }
        });
        newFragment.show(getFragmentManager(), "datePicker");

    }

    private void save() {
        String almuerzo = edtAlmuerzo.getText().toString();
        String cena = edtCena.getText().toString();
        String postre = edtPostre.getText().toString();
        String descripcion = almuerzo + "$" + cena + "$" + postre;
        String porcion = edtPorcion.getText().toString();
        Validador validador = new Validador(getApplicationContext());
        if (!validador.noVacio(descripcion) && validador.validarNumero(edtPorcion)) {
            if (diaF != -1 && mesF != -1 && anioF != -1) {
                sendServer(descripcion, porcion, diaF, mesF, anioF);
            } else {
                //Utils.showToast(getApplicationContext(), getString(R.string.eligeFecha));
                Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                        getString(R.string.eligeFecha), R.drawable.ic_advertencia);
            }
        } else {
            //Utils.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
            Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                    getString(R.string.camposInvalidos), R.drawable.ic_error);
        }
    }

    private void sendServer(String descripcion, String porcion, int diaF, int mesF, int anioF) {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(Utils.TOKEN);
        int idLocal = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?key=%s&idU=%s&d=%s&m=%s&a=%s&de=%s&p=%s", Utils.URL_MENU_NUEVO
                , key, idLocal, diaF, mesF, anioF, descripcion, porcion);
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //Utils.showToast(getApplicationContext(), getString(R.string.servidorOff));
                Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
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
                    //Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    //Utils.showToast(getApplicationContext(), getString(R.string.menuCreado));
                    Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                            getString(R.string.menuCreado), R.drawable.ic_exito);
                    finish();
                    break;
                case 2:
                    //Utils.showToast(getApplicationContext(), getString(R.string.menuError));
                    Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                            getString(R.string.menuError), R.drawable.ic_error);
                    break;
                case 4:
                    //Utils.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
                    Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_advertencia);
                    break;
                case 5:
                    //Utils.showToast(getApplicationContext(), getString(R.string.menuExistente));
                    Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                            getString(R.string.menuExistente), R.drawable.ic_error);
                    break;
                case 3:
                    //Utils.showToast(getApplicationContext(), getString(R.string.tokenInvalido));
                    Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    //Utils.showToast(getApplicationContext(), getString(R.string.tokenInexistente));
                    Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
            Utils.showCustomToast(NuevoMenuActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }
}
