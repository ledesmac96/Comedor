package com.example.comedor.Activity;

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
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class InfoReservaActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgIcono, imgQR;
    Button btnCancelar;
    TextView txtIdRes, txtPlato, txtFechaRes, txtEstado, txtFechaRetirada;
    Reserva mReserva;
    ReservaViewModel mReservaViewModel;
    Menu mMenu;
    LinearLayout latEstado, latQR, latFecha, latRetiro;
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

        updateInfo();

    }

    private void updateInfo() {
        if (estado != 2 && mReserva != null) {
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
                        txtFechaRetirada.setText(Utils.getFechaName(Utils.getFechaDateWithHour(reserva.getFechaModificacion())));
                        txtEstado.setText("RETIRADO");
                        btnCancelar.setVisibility(View.GONE);
                        mReservaViewModel.update(reserva);
                    }
                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
        }
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
                txtIdRes.setText(String.format("RESERVA # %s", mReserva.getIdReserva()));
                if (mReserva.getEstado() == 3) {
                    latRetiro.setVisibility(View.VISIBLE);
                    txtFechaRetirada.setText(Utils.getFechaName(Utils.getFechaDateWithHour(mReserva.getFechaModificacion())));
                }
                btnCancelar.setText(mReserva.getEstado() == 1 ? "CANCELAR" : "RESERVAR");
                comida = Utils.getComidas(mMenu.getDescripcion());
                txtFechaRes.setText(Utils.getFechaName(Utils.getFechaDateWithHour(mReserva.getFechaReserva())));
                estado = 3;//Ya reservada
                if (mReserva.getEstado() != 2) generateQR(mReserva);
                else latQR.setVisibility(View.GONE);
            }
        }
        txtPlato.setText(String.format("Almuerzo: %s\nCena: %s\nPostre: %s", comida[0], comida[1], comida[2]));

    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    private void loadViews() {
        latRetiro = findViewById(R.id.latFechaRetirada);
        txtFechaRetirada = findViewById(R.id.txtFechaRetiro);
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
            int reserva = mReserva.getEstado() == 2 ? 1 : 2;
            URL = String.format("%s?idU=%s&key=%s&idR=%s&val=%s", Utils.URL_RESERVA_CANCELAR, id, key, idReserva, reserva);
        } else if (estado == 1) {
            URL = String.format("%s?idU=%s&key=%s&i=%s&im=%s&t=%s", Utils.URL_RESERVA_INSERTAR, id, key, id,
                    mMenu.getIdMenu(), 4);
        }

        StringRequest request = new StringRequest(estado == 1 ? Request.Method.POST : Request.Method.GET, URL, new Response.Listener<String>() {
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
                    Utils.showToast(getApplicationContext(), getString(R.string.reservaNoExiste));
                    break;
                case 3:
                    Utils.showToast(getApplicationContext(), getString(R.string.tokenInvalido));
                    break;
                case 5:
                    Utils.showToast(getApplicationContext(), getString(R.string.yaReservo));
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

                if (estado == 1) {

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
            } else {
                if (estado == 3 && mReserva.getEstado() == 1) {
                    latEstado.setVisibility(View.VISIBLE);
                    txtEstado.setText("CANCELADO");
                    btnCancelar.setText("RESERVAR");
                    latFecha.setVisibility(View.VISIBLE);
                    latQR.setVisibility(View.GONE);
                    mReserva.setEstado(2);
                    mReservaViewModel.update(mReserva);
                } else if (mReserva.getEstado() == 2) {
                    latEstado.setVisibility(View.VISIBLE);
                    txtEstado.setText("RESERVADO");
                    btnCancelar.setText("CANCELAR");
                    latFecha.setVisibility(View.VISIBLE);
                    latQR.setVisibility(View.VISIBLE);
                    generateQR(mReserva);
                    mReserva.setEstado(1);
                    mReservaViewModel.update(mReserva);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void generateQR(Reserva reserva) {
        MultiFormatWriter formatWriter = new MultiFormatWriter();
        try {
            BitMatrix matrix = formatWriter.encode(String.format("BIENESTAR ESTUDIANTIL #%s",
                    reserva.getIdReserva()), BarcodeFormat.QR_CODE, 300, 300);
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
