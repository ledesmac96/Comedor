package com.example.comedor.Activity;

import android.content.Intent;
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
import com.example.comedor.Database.ReservaViewModel;
import com.example.comedor.Dialogos.DialogoGeneral;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.Menu;
import com.example.comedor.Modelo.Reserva;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.VolleySingleton;
import com.example.comedor.Utils.YesNoDialogListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InfoReservaActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgIcono, imgQR;
    Button btnCancelar;
    TextView txtIdRes, txtPlato, txtFechaRes, txtEstado;
    Reserva mReserva;
    ReservaViewModel mReservaViewModel;
    Menu mMenu;
    LinearLayout latEstado, latQR, latFecha;
    int estado = 0;
    DialogoProcesamiento dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_reserva);

        if (getIntent().getParcelableExtra(Utils.RESERVA) != null) {
            mReserva = getIntent().getParcelableExtra(Utils.RESERVA);
        }

        if (getIntent().getParcelableExtra(Utils.DATA_RESERVA) != null) {
            mMenu = getIntent().getParcelableExtra(Utils.DATA_RESERVA);
        }

        loadViews();

        loadListener();

        loadData();

        setToolbar();

    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText("Mi reserva");
        Utils.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
    }

    private void loadData() {
        String[] comida = null;
        mReservaViewModel = new ReservaViewModel(getApplicationContext());
        if (mReserva != null) {
            txtIdRes.setText(String.format("# %s", mReserva.getIdReserva()));
            comida = Utils.getComidas(mReserva.getDescripcion());
            txtFechaRes.setText(mReserva.getFechaReserva());
            txtEstado.setText(String.valueOf(mReserva.getDescripcion()));
            estado = 2; //Del alumno
        } else {
            mReserva = mReservaViewModel.getAllByMenuID(mMenu.getIdMenu());
            if (mReserva == null) {
                txtIdRes.setText("INFO");
                btnCancelar.setText("RESERVAR");
                comida = Utils.getComidas(mMenu.getDescripcion());
                latQR.setVisibility(View.GONE);
                latFecha.setVisibility(View.GONE);
                latEstado.setVisibility(View.GONE);
                estado = 1; //Nueva
            } else {
                txtEstado.setText(mReserva.getEstado() == 1 ? "RESERVADO" : mReserva.getEstado() == 2 ? "CANCELADO" : "RETIRADO");
                txtIdRes.setText(String.format("# %s", mReserva.getIdReserva()));
                btnCancelar.setText("CANCELAR");
                comida = Utils.getComidas(mMenu.getDescripcion());
                txtFechaRes.setText(Utils.getFechaName(Utils.getFechaDateWithHour(mReserva.getFechaReserva())));
                estado = 3;//Ya reservada
                generateQR(mReserva);
            }
        }
        txtPlato.setText(String.format("Almuerzo: %s\nCena: %s\nPostre: %s", comida[0], comida[1], comida[2]));

    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        latQR.setOnClickListener(this);
    }

    private void loadViews() {
        btnCancelar = findViewById(R.id.btnReservar);
        imgIcono = findViewById(R.id.imgFlecha);
        imgQR = findViewById(R.id.imgQR);
        txtIdRes = findViewById(R.id.txtIdReserva);
        txtPlato = findViewById(R.id.txtPlato);
        txtFechaRes = findViewById(R.id.txtFechaRes);
        txtEstado = findViewById(R.id.txtEstado);
        latEstado = findViewById(R.id.latEstado);
        latFecha = findViewById(R.id.latFechaReserva);
        latQR = findViewById(R.id.latQR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.latQR:
                scanQR();
                break;
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.btnReservar:
                //FUNCIÓN
                if (estado == 3) {
                    //Dialogo
                    dialogoSeguro(mReserva.getIdReserva());

                } else {
                    reservarCancelar(0);
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
        if (estado == 3) {
            URL = String.format("%s?idU=%s&key=%s&idR=%s&val=%s", Utils.URL_RESERVA_CANCELAR, id, key, idReserva, 0);
        } else if (estado == 1) {
            URL = String.format("%s?idU=%s&key=%s&i=%s&im=%s&t=%s", Utils.URL_RESERVA_INSERTAR, id, key, id,
                    mMenu.getIdMenu(), 4);
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                btnCancelar.setEnabled(true);
                Utils.showToast(getApplicationContext(), getString(R.string.servidorOff));
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
                    Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    loadInfo(jsonObject);
                    break;
                case 2:
                    Utils.showToast(getApplicationContext(), getString(R.string.noData));
                    break;
                case 3:
                    Utils.showToast(getApplicationContext(), getString(R.string.tokenInvalido));
                    break;
                case 100:
                    //No autorizado
                    Utils.showToast(getApplicationContext(), getString(R.string.tokenInexistente));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje") && jsonObject.has("dato")) {

                if (estado == 3) {
                    latEstado.setVisibility(View.VISIBLE);
                    txtEstado.setText("CANCELADO");
                    latFecha.setVisibility(View.VISIBLE);
                    latQR.setVisibility(View.INVISIBLE);

                } else if (estado == 1) {

                    Reserva reserva = Reserva.mapper(jsonObject.getJSONObject("dato"), Reserva.COMPLETE);

                    mReservaViewModel.insert(reserva);

                    btnCancelar.setText("CANCELAR");
                    latEstado.setVisibility(View.VISIBLE);
                    txtEstado.setText(reserva.getEstado() == 1 ? "RESERVADO" : reserva.getEstado() == 2 ? "CANCELADO" : "RETIRADO");
                    latFecha.setVisibility(View.VISIBLE);
                    txtFechaRes.setText(Utils.getFechaName(Utils.getFechaDate(reserva.getFechaReserva())));
                    txtIdRes.setText(String.format("RESERVA #%s", reserva.getIdReserva()));
                    latQR.setVisibility(View.VISIBLE);
                    generateQR(reserva);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void scanQR() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(InfoReservaActivity.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt("Scan");
        intentIntegrator.setCameraId(0);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentIntegrator = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentIntegrator != null) {
            if (intentIntegrator.getContents() == null) {
                Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(), "Cancelaste", R.drawable.ic_error);

            } else {
                Utils.showCustomToast(InfoReservaActivity.this, getApplicationContext(),
                        intentIntegrator.getContents(), R.drawable.ic_exito);
            }
        }else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void generateQR(Reserva reserva) {
        MultiFormatWriter formatWriter = new MultiFormatWriter();
        try {
            BitMatrix matrix = formatWriter.encode(String.format("BIENESTAR ESTUDIANTIL #%s",
                    reserva.getIdReserva()), BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            Bitmap bitmap = barcodeEncoder.createBitmap(matrix);
            if (bitmap != null) {
                Glide.with(imgQR.getContext()).load(bitmap).into(imgQR);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

        /*FileStorageManager.saveQR(
                QRCode.from(String.format("BIENESTAR ESTUDIANTIL #%s",
                        reserva.getIdReserva()))
                        .withSize(250, 250).stream(),
                getApplicationContext(), "RESERVAS", "R_" + reserva.getIdReserva());
        Bitmap bitmap = FileStorageManager.getBitmap(getApplicationContext(), "RESERVAS", "R_" + reserva.getIdReserva(), false);
        if (bitmap != null) {

        }*/

    }

}
