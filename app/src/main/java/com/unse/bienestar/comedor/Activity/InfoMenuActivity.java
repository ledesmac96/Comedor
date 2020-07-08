package com.unse.bienestar.comedor.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unse.bienestar.comedor.Dialogos.DatePickerFragment;
import com.unse.bienestar.comedor.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedor.Modelo.Menu;
import com.unse.bienestar.comedor.R;
import com.unse.bienestar.comedor.Utils.PreferenciasManager;
import com.unse.bienestar.comedor.Utils.Utils;
import com.unse.bienestar.comedor.Utils.Validador;
import com.unse.bienestar.comedor.Utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoMenuActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    Menu mMenu;
    EditText edtPorcion, edtAlmuerzo, edtCena, edtPostre, edtFecha;
    ImageView imgIcono;
    Button btnGuardar, btnEditar;
    DialogoProcesamiento dialog;
    EditText[] campos;
    boolean isEdit = false;
    int mode = 0, TIPO_USER = -1;
    int diaF = -1, mesF = -1, anioF = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getIntent().getParcelableExtra(Utils.ID_MENU) != null) {
            mMenu = getIntent().getParcelableExtra(Utils.ID_MENU);
        }

        if (mMenu != null) {
            loadViews();

            loadListener();

            loadData();

            setToolbar();

            editMode(0);
        } else {
            Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                    "Error al cargar la información", R.drawable.ic_error);
            finish();
        }
    }


    private void loadData() {
        btnGuardar.setVisibility(View.GONE);
        String[] comida = null;
        comida = Utils.getComidas(mMenu.getDescripcion());
        String fecha = mMenu.getDia() + "/" + mMenu.getMes() + "/" + mMenu.getAnio();
        edtFecha.setText(fecha);
        edtPorcion.setText(String.valueOf(mMenu.getPorcion()));
        edtAlmuerzo.setText(comida[0]);
        edtCena.setText(comida[1]);
        edtPostre.setText(comida[2]);
    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        edtFecha.setOnClickListener(this);
    }

    private void loadViews() {
        edtFecha = findViewById(R.id.edtFecha);
        edtPorcion = findViewById(R.id.edtPorcion);
        edtAlmuerzo = findViewById(R.id.edtAlmuerzo);
        edtCena = findViewById(R.id.edtCena);
        edtPostre = findViewById(R.id.edtPostre);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnEditar = findViewById(R.id.btnEditar);
        imgIcono = findViewById(R.id.imgFlecha);

        campos = new EditText[]{edtPorcion, edtAlmuerzo, edtCena, edtPostre, edtFecha};
    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText("Info menú");
        Utils.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEditar:
                btnGuardar.setVisibility(View.VISIBLE);
                btnEditar.setVisibility(View.GONE);
                activateEditMode();
                break;
            case R.id.btnGuardar:
                btnGuardar.setVisibility(View.GONE);
                btnEditar.setVisibility(View.VISIBLE);
                break;
            case R.id.edtFecha:
                elegirFecha();
                break;
            case R.id.imgIcon:
                onBackPressed();
                break;
        }
    }

    private void elegirFecha() {
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

    private void editMode(int mode) {
        for (EditText e : campos) {
            if (mode == 0) {
                e.setEnabled(false);
                e.setBackgroundColor(getResources().getColor(R.color.transparente));
                e.removeTextChangedListener(null);
            } else {
                e.setEnabled(true);
                e.setBackground(getResources().getDrawable(R.drawable.edit_text_logreg));
                e.addTextChangedListener(this);
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isEdit = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void activateEditMode() {
        if (isEdit) {
            Toast.makeText(this, "FUNCO", Toast.LENGTH_SHORT).show();
            //save();
            return;
        }
        if (mode == 0)
            mode = 1;
        else
            mode = 0;
        editMode(mode);
    }

    private void save() {
        String almuerzo = edtAlmuerzo.getText().toString();
        String cena = edtCena.getText().toString();
        String postre = edtPostre.getText().toString();
        if (almuerzo.equals(""))
            almuerzo = " ";
        if (cena.equals(""))
            cena = " ";
        if (postre.equals(""))
            postre = " ";
        String descripcion = almuerzo + "$" + cena + "$" + postre;
        String porcion = edtPorcion.getText().toString();
        Validador validador = new Validador(getApplicationContext());
        if (!validador.noVacio(descripcion) && validador.validarNumero(edtPorcion)) {
            if (diaF != -1 && mesF != -1 && anioF != -1) {
                sendServer(descripcion, porcion, diaF, mesF, anioF);
            } else {
                //Utils.showToast(getApplicationContext(), getString(R.string.eligeFecha));
                Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                        getString(R.string.eligeFecha), R.drawable.ic_advertencia);
            }
        } else {
            //Utils.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
            Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                    getString(R.string.camposInvalidos), R.drawable.ic_error);
        }
    }

    //AGREGAR EL LINK MENU_MODIFICAR

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
                Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
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
                    Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                            getString(R.string.menuCreado), R.drawable.ic_exito);
                    finish();
                    break;
                case 2:
                    Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                            getString(R.string.menuError), R.drawable.ic_error);
                    break;
                case 4:
                    Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_advertencia);
                    break;
                case 5:
                    Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                            getString(R.string.menuExistente), R.drawable.ic_error);
                    break;
                case 3:
                    Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showCustomToast(InfoMenuActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }


}