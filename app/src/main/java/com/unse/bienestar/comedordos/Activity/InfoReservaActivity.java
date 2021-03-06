package com.unse.bienestar.comedordos.Activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.unse.bienestar.comedordos.Dialogos.DialogoGeneral;
import com.unse.bienestar.comedordos.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedordos.Modelo.Reserva;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;
import com.unse.bienestar.comedordos.Utils.YesNoDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class InfoReservaActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgIcono, imgQR;
    TextView txtCodigoQR;
    Button btnCancelar;
    TextView txtIdRes, txtAlmuerzo, txtCena, txtPostre, txtFechaRes, txtEstado,
            txtFechaRetirada, txtPorcion, txtFechaResDescripcion;
    Reserva mReserva;
    //ReservaViewModel mReservaViewModel;
    //Menu mMenu;
    LinearLayout latEstado, latQR, latFecha, latRetiro;
    //int estado = 0;
    DialogoProcesamiento dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            setContentView(R.layout.activity_perfil_reserva);
        else
            setContentView(R.layout.activity_perfil_reserva_21);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getIntent().getParcelableExtra(ABC.RESERVA) != null) {
            mReserva = getIntent().getParcelableExtra(ABC.RESERVA);
        }

        if (mReserva != null) {
            loadViews();

            loadListener();

            loadData();

            setToolbar();

            updateInfo();
        } else {
            ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                    "Error al abrir la reserva", R.drawable.ic_error);
            finish();
        }


    }

    private void updateInfo() {
        if (mReserva != null) {
            PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
            String key = manager.getValueString(ABC.TOKEN);
            int id = manager.getValueInt(ABC.MY_ID);
            String URL = "";
            URL = String.format("%s?idU=%s&key=%s&ir=%s", ABC.URL_RESERVA_BY_ID, id, key, mReserva.getIdReserva());
            StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    procesarRespuestaInfo(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }
    }

    private void procesarRespuestaInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case 1:
                    //Exito
                    Reserva reserva = Reserva.mapper(jsonObject.getJSONObject("mensaje"), Reserva.COMPLETE);
                    if (reserva.getEstado() == 3) {
                        latQR.setVisibility(View.GONE);
                        latRetiro.setVisibility(View.VISIBLE);
                        txtFechaRetirada.setText(ABC.getFechaOrder(ABC.getFechaDateWithHour(reserva.getFechaModificacion())));
                        txtEstado.setText("RETIRADO");
                        btnCancelar.setVisibility(View.GONE);
                    }
                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText("Mi reserva");
        ABC.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
    }

    private void loadData() {
        String[] comida = null;
        //mReservaViewModel = new ReservaViewModel(getApplicationContext());
        txtIdRes.setText(String.format("RESERVA # %s", mReserva.getIdReserva()));
        comida = ABC.getComidas(mReserva.getMenu());
        txtAlmuerzo.setText(comida[0]);
        txtCena.setText(comida[1]);
        txtPostre.setText(comida[2]);
        txtFechaRes.setText(ABC.getFechaOrder(ABC.getFechaDateWithHour(mReserva.getFechaReserva())));
        txtEstado.setText(mReserva.getDescripcion());
        btnCancelar.setText(mReserva.getDescripcion().equals("RESERVADO") ? "CANCELAR" : "RESERVAR");
        txtPorcion.setText(String.valueOf(mReserva.getPorcion()));
        if (mReserva.getDescripcion().equals("RETIRADO")) {
            latRetiro.setVisibility(View.VISIBLE);
            txtFechaRetirada.setText(mReserva.getFechaModificacion() != null ?
                    ABC.getFechaOrder(ABC.getFechaDateWithHour(mReserva.getFechaModificacion())) :
                    "NO FECHA");
            btnCancelar.setVisibility(View.GONE);
            latQR.setVisibility(View.GONE);
        } else if (mReserva.getDescripcion().equals("RESERVADO")) {
            generateQR(mReserva);
        } else if (mReserva.getDescripcion().equals("NO RETIRADO")) {
            latRetiro.setVisibility(View.VISIBLE);
            txtFechaResDescripcion.setText("Fecha de Cancelación: ");
            txtFechaRetirada.setText(mReserva.getFechaModificacion() != null ?
                    ABC.getFechaOrder(ABC.getFechaDateWithHour(mReserva.getFechaModificacion())) :
                    "NO FECHA");
            btnCancelar.setVisibility(View.GONE);
            latQR.setVisibility(View.GONE);
        } else {
            latQR.setVisibility(View.GONE);
        }
    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    private void loadViews() {
        txtCodigoQR = findViewById(R.id.txtQR);
        txtFechaResDescripcion = findViewById(R.id.txtFechaRet);
        txtPorcion = findViewById(R.id.txtPorcion);
        latRetiro = findViewById(R.id.latFechaRetirada);
        txtFechaRetirada = findViewById(R.id.txtFechaRetiro);
        btnCancelar = findViewById(R.id.btnReservar);
        imgIcono = findViewById(R.id.imgFlecha);
        imgQR = findViewById(R.id.imgQR);
        txtIdRes = findViewById(R.id.txtIdReserva);
        txtAlmuerzo = findViewById(R.id.txtAlmuerzo);
        txtCena = findViewById(R.id.txtCena);
        txtPostre = findViewById(R.id.txtPostre);
        txtFechaRes = findViewById(R.id.txtFechaRes);
        txtEstado = findViewById(R.id.txtEstado);
        latEstado = findViewById(R.id.latEstado);
        latFecha = findViewById(R.id.latFechaReserva);
        latQR = findViewById(R.id.latQR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.btnReservar:
                //FUNCIÓN
                if (mReserva.getDescripcion().equals("RESERVADO")) {
                    //Dialogo
                    dialogoSeguro(mReserva.getIdReserva());

                } else {
                    reservarCancelar(mReserva.getIdReserva());
                }

                break;
        }
    }

    private void dialogoSeguro(final int id) {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo(getString(R.string.advertencia))
                .setDescripcion(getString(R.string.cancelarReserva))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        reservarCancelar(id);
                    }

                    @Override
                    public void no() {

                    }
                })
                .setIcono(R.drawable.ic_advertencia)
                .setTipo(DialogoGeneral.TIPO_SI_NO);
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.show(getSupportFragmentManager(), "dialog_ad");
    }

    private void reservarCancelar(int idReserva) {
        btnCancelar.setEnabled(false);
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = "";
        int reserva = mReserva.getDescripcion().equals("RESERVADO") ? 2 : 1;
        URL = String.format("%s?idU=%s&key=%s&idR=%s&val=%s", ABC.URL_RESERVA_CANCELAR, id, key, idReserva, reserva);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                btnCancelar.setEnabled(true);
                ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
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
            btnCancelar.setEnabled(true);
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    loadInfo(jsonObject);
                    break;
                case 2:
                    ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.reservaNoCancelada), R.drawable.ic_error);
                    break;
                case 3:
                    ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 4:
                    ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_error);
                    break;
                case 5:
                    ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.yaRetiro), R.drawable.ic_error);
                    latQR.setVisibility(View.GONE);
                    btnCancelar.setEnabled(false);
                    break;
                case 6:
                    ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.reservaNoExiste), R.drawable.ic_error);
                    finish();
                    break;
                case 7:
                case 8:
                    ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.reservaAnterior), R.drawable.ic_advertencia);
                    latQR.setVisibility(View.GONE);
                    btnCancelar.setVisibility(View.GONE);
                    txtEstado.setText("CANCELADO");
                    break;
                case 100:
                    //No autorizado
                    ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje")) {

                String mensaje = jsonObject.getString("mensaje");

                if (mensaje.contains("cancelada")) {
                    latEstado.setVisibility(View.VISIBLE);
                    latFecha.setVisibility(View.VISIBLE);
                    latQR.setVisibility(View.GONE);
                    btnCancelar.setText("RESERVAR");
                    mReserva.setEstado(2);
                    mReserva.setDescripcion("CANCELADO");
                    txtEstado.setText(mReserva.getDescripcion());
                } else if (mensaje.contains("habilitada")) {
                    latEstado.setVisibility(View.VISIBLE);
                    latFecha.setVisibility(View.VISIBLE);
                    latQR.setVisibility(View.VISIBLE);
                    mReserva.setEstado(1);
                    mReserva.setDescripcion("RESERVADO");
                    txtEstado.setText(mReserva.getDescripcion());
                    btnCancelar.setText("CANCELAR");
                    generateQR(mReserva);
                } else if (mensaje.contains("retirada")) {
                    latQR.setVisibility(View.GONE);
                    latRetiro.setVisibility(View.VISIBLE);
                    txtFechaRetirada.setText(ABC.getFechaOrder(ABC.getFechaDateWithHour(mReserva.getFechaModificacion())));
                    txtEstado.setText("RETIRADO");
                    btnCancelar.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void generateQR(Reserva reserva) {
        MultiFormatWriter formatWriter = new MultiFormatWriter();
        try {
            StringBuilder builder = new StringBuilder();
            // builder.append("COMEDOR UNIVERSITARIO - BIENESTAR ESTUDIANTIL");
            //builder.append("\n");
            builder.append("¡MUCHAS GRACIAS POR RESERVAR!");
            builder.append("\n");
            String[] dni = new String[String.valueOf(reserva.getIdUsuario()).length()];
            for (int i = 0; i < dni.length; i++) {
                char valor = ABC.encode(String.valueOf(reserva.getIdUsuario()).charAt(i));
                dni[i] = String.valueOf(valor);
            }
            StringBuilder dniModif = new StringBuilder();
            for (int i = 0; i < dni.length; i++) {
                dniModif.append(dni[i]);
            }
            builder.append(String.format("#%s-%s#", dniModif, reserva.getIdReserva()));
            txtCodigoQR.setText(String.format("COM-%s-%s", dniModif, reserva.getIdReserva()));
            BitMatrix matrix = formatWriter.encode(builder.toString(), BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(matrix);
            if (bitmap != null) {
                Glide.with(imgQR.getContext()).load(bitmap).into(imgQR);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

}
