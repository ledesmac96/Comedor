package com.unse.bienestar.comedor.Activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
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
import com.unse.bienestar.comedor.Dialogos.DialogoGeneral;
import com.unse.bienestar.comedor.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedor.Modelo.Reserva;
import com.unse.bienestar.comedor.R;
import com.unse.bienestar.comedor.Utils.PreferenciasManager;
import com.unse.bienestar.comedor.Utils.Utils;
import com.unse.bienestar.comedor.Utils.VolleySingleton;
import com.unse.bienestar.comedor.Utils.YesNoDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class InfoReservaActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgIcono, imgQR;
    Button btnCancelar;
    TextView txtIdRes, txtAlmuerzo, txtCena, txtPostre, txtFechaRes, txtEstado,
            txtFechaRetirada, txtPorcion;
    Reserva mReserva;
    //ReservaViewModel mReservaViewModel;
    //Menu mMenu;
    LinearLayout latEstado, latQR, latFecha, latRetiro;
    //int estado = 0;
    DialogoProcesamiento dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_reserva);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getIntent().getParcelableExtra(Utils.RESERVA) != null) {
            mReserva = getIntent().getParcelableExtra(Utils.RESERVA);
        }

        if (mReserva != null) {
            loadViews();

            loadListener();

            loadData();

            setToolbar();

            updateInfo();
        } else {
            Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                    "Error al abrir la reserva", R.drawable.ic_error);
            finish();
        }


    }

    private void updateInfo() {
        if (mReserva != null) {
            PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
            String key = manager.getValueString(Utils.TOKEN);
            int id = manager.getValueInt(Utils.MY_ID);
            String URL = "";
            URL = String.format("%s?idU=%s&key=%s&ir=%s", Utils.URL_RESERVA_BY_ID, id, key, mReserva.getIdReserva());
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
                        txtFechaRetirada.setText(Utils.getFechaOrder(Utils.getFechaDateWithHour(reserva.getFechaModificacion())));
                        txtEstado.setText("RETIRADO");
                        btnCancelar.setVisibility(View.GONE);
                    }
                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText("Mi reserva");
        Utils.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
    }

    private void loadData() {
        String[] comida = null;
        //mReservaViewModel = new ReservaViewModel(getApplicationContext());
        txtIdRes.setText(String.format("RESERVA # %s", mReserva.getIdReserva()));
        comida = Utils.getComidas(mReserva.getMenu());
        txtAlmuerzo.setText(comida[0]);
        txtCena.setText(comida[1]);
        txtPostre.setText(comida[2]);
        txtFechaRes.setText(Utils.getFechaOrder(Utils.getFechaDateWithHour(mReserva.getFechaReserva())));
        txtEstado.setText(mReserva.getDescripcion());
        //txtEstado.setText(mReserva.getEstado() == 1 ? "RESERVADO" : mReserva.getEstado() == 2 ? "CANCELADO" : "RETIRADO");
        btnCancelar.setText(mReserva.getDescripcion().equals("RESERVADO") ? "CANCELAR" : "RESERVAR");
        txtPorcion.setText(String.valueOf(mReserva.getPorcion()));
        if (mReserva.getDescripcion().equals("RETIRADO")) {
            latRetiro.setVisibility(View.VISIBLE);
            txtFechaRetirada.setText(mReserva.getFechaModificacion() != null ?
                    Utils.getFechaOrder(Utils.getFechaDateWithHour(mReserva.getFechaModificacion())) :
                    "NO FECHA");
            btnCancelar.setVisibility(View.GONE);
            latQR.setVisibility(View.GONE);
        } else if (mReserva.getDescripcion().equals("RESERVADO")) {
            generateQR(mReserva);
        } else {
            latQR.setVisibility(View.GONE);
        }

        /*if (mReserva != null) {
            txtIdRes.setText(String.format("RESERVA # %s", mReserva.getIdReserva()));
            comida = Utils.getComidas(mReserva.getDescripcion());

            txtEstado.setText(mReserva.getDescripcion());
            //
            btnCancelar.setText(mReserva.getDescripcion().equals("RESERVADO") ? "CANCELAR" : "RESERVAR");
            if (mReserva.getDescripcion().equals("RETIRADO")) {
                latRetiro.setVisibility(View.VISIBLE);
                txtFechaRetirada.setText(mReserva.getFechaModificacion() != null ?
                        Utils.getFechaName(Utils.getFechaDateWithHour(mReserva.getFechaModificacion())) :
                        "NO FECHA");
                btnCancelar.setVisibility(View.GONE);
                latQR.setVisibility(View.GONE);
            } else {
                generateQR(mReserva);
            }
            btnCancelar.setEnabled(false);
            txtPorcion.setText(mMenu != null ? String.valueOf(mMenu.getPorcion()) : "NO INFO");
            estado = 2; //Del alumno
        } else {
            mReserva = mReservaViewModel.getByMenuID(mMenu.getIdMenu());
            if (mReserva == null) {
                txtIdRes.setText("INFO");
                btnCancelar.setText("RESERVAR");
                comida = Utils.getComidas(mMenu.getDescripcion());
                txtPorcion.setText(String.valueOf(mMenu.getPorcion()));
                latQR.setVisibility(View.GONE);
                latFecha.setVisibility(View.GONE);
                latEstado.setVisibility(View.GONE);
                estado = 1; //Nueva
            } else {
                txtEstado.setText(mReserva.getEstado() == 1 ? "RESERVADO" : mReserva.getEstado() == 2 ? "CANCELADO" : "RETIRADO");
                txtIdRes.setText(String.format("RESERVA # %s", mReserva.getIdReserva()));
                if (mReserva.getEstado() == 3) {
                    latRetiro.setVisibility(View.VISIBLE);
                    txtFechaRetirada.setText(Utils.getFechaName(Utils.getFechaDateWithHour(mReserva.getFechaModificacion())));
                }
                btnCancelar.setText(mReserva.getEstado() == 1 ? "CANCELAR" : "RESERVAR");
                comida = Utils.getComidas(mMenu.getDescripcion());
                txtPorcion.setText(String.valueOf(mMenu.getPorcion()));
                txtFechaRes.setText(Utils.getFechaName(Utils.getFechaDateWithHour(mReserva.getFechaReserva())));
                estado = 3;//Ya reservada
                if (mReserva.getEstado() != 2) generateQR(mReserva);
                else latQR.setVisibility(View.GONE);
            }
        }*/
        //txtPlato.setText(String.format("Almuerzo: %s\nCena: %s\nPostre: %s", comida[0], comida[1], comida[2]));


    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    private void loadViews() {
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
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = "";
        int reserva = mReserva.getDescripcion().equals("RESERVADO") ? 2 : 1;
        URL = String.format("%s?idU=%s&key=%s&idR=%s&val=%s", Utils.URL_RESERVA_CANCELAR, id, key, idReserva, reserva);
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
                Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
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
                    Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    loadInfo(jsonObject);
                    break;
                case 2:
                    Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.reservaNoExiste), R.drawable.ic_error);
                    break;
                case 3:
                    Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 5:
                    Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.yaReservo), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje")) {

                String mensaje = jsonObject.getString("mensaje");

                if (mensaje.contains("cancelada")) {
                    latEstado.setVisibility(View.VISIBLE);
                    btnCancelar.setText("RESERVAR");
                    latFecha.setVisibility(View.VISIBLE);
                    latQR.setVisibility(View.GONE);
                    mReserva.setEstado(2);
                    mReserva.setDescripcion("CANCELADO");
                    txtEstado.setText(mReserva.getDescripcion());
                } else if (mensaje.contains("habilitada")) {
                    latEstado.setVisibility(View.VISIBLE);
                    mReserva.setEstado(1);
                    mReserva.setDescripcion("RESERVADO");
                    txtEstado.setText(mReserva.getDescripcion());
                    btnCancelar.setText("CANCELAR");
                    latFecha.setVisibility(View.VISIBLE);
                    latQR.setVisibility(View.VISIBLE);
                    generateQR(mReserva);

                }
            }

               /* if (3 == 1) {

                    Reserva reserva = Reserva.mapper(jsonObject.getJSONObject("dato"), Reserva.COMPLETE);

                    // mReservaViewModel.insert(reserva);

                    btnCancelar.setText("CANCELAR");
                    latEstado.setVisibility(View.VISIBLE);
                    txtEstado.setText(reserva.getEstado() == 1 ? "RESERVADO" : reserva.getEstado() == 2 ? "CANCELADO" : "RETIRADO");
                    latFecha.setVisibility(View.VISIBLE);
                    txtFechaRes.setText(Utils.getFechaName(Utils.getFechaDate(reserva.getFechaReserva())));
                    txtIdRes.setText(String.format("RESERVA #%s", reserva.getIdReserva()));
                    latQR.setVisibility(View.VISIBLE);
                    generateQR(reserva);
                }
            } else {

            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void generateQR(Reserva reserva) {
        MultiFormatWriter formatWriter = new MultiFormatWriter();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("COMEDOR UNIVERSITARIO - BIENESTAR ESTUDIANTIL");
            builder.append("\n");
            builder.append("¡MUCHAS GRACIAS POR RESERVAR!");
            builder.append("\n");
            builder.append(String.format("#%s-%s#", mReserva.getIdUsuario(), mReserva.getIdReserva()));
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
