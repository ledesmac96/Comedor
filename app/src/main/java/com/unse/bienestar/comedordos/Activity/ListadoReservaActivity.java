package com.unse.bienestar.comedordos.Activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.unse.bienestar.comedordos.Adapter.ReservasAdapter;
import com.unse.bienestar.comedordos.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedordos.Modelo.Menu;
import com.unse.bienestar.comedordos.Modelo.Reserva;
import com.unse.bienestar.comedordos.Modelo.ReservaEspecial;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.RecyclerListener.ItemClickSupport;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListadoReservaActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtAlmuerzo, txtCena, txtPostre, txtPorciones;
    RecyclerView mRecyclerView, mRecyclerViewEspecial;
    RecyclerView.LayoutManager mLayoutManager, mLayoutManagerEspecial;
    ReservasAdapter mAdapter, mReservasAdapterEspecial;;
    CardView cardEstadisticas;
    Menu mMenu;
    ArrayList<Reserva> mReservas, mReservasEspeciales;;
    LinearLayout latVacio, latEspeciales;;
    ImageView btnBack;
    BarChart barCantidad;
    ProgressBar mProgressBar;
    DialogoProcesamiento dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_reserva);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getIntent().getParcelableExtra(ABC.DATA_RESERVA) != null) {
            mMenu = getIntent().getParcelableExtra(ABC.DATA_RESERVA);
        }

        loadViews();

        loadData();

        loadListener();

        setToolbar();

    }

    private void loadListener() {
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {

            }
        });
        btnBack.setOnClickListener(this);
    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText(ABC.getInfoDate(mMenu.getDia(), mMenu.getMes(), mMenu.getAnio()));
    }


    private void loadData() {
        mReservasEspeciales = new ArrayList<>();
        latVacio.setVisibility(View.GONE);
        cardEstadisticas.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mLayoutManagerEspecial = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewEspecial.setLayoutManager(mLayoutManagerEspecial);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerViewEspecial.setNestedScrollingEnabled(true);
        loadInfo();

    }

    private void loadInfo() {
        mProgressBar.setVisibility(View.VISIBLE);
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&m=%s&d=%s&me=%s&a=%s", ABC.URL_RESERVA_HOY,
                id, key, 1, mMenu.getDia(), mMenu.getMes(), mMenu.getAnio());
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                latVacio.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                ABC.showCustomToast(ListadoReservaActivity.this, getApplicationContext(),
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
            mProgressBar.setVisibility(View.GONE);
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    latVacio.setVisibility(View.VISIBLE);
                    ABC.showCustomToast(ListadoReservaActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    loadInfo(jsonObject);
                    break;
                case 2:
                    latVacio.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    latVacio.setVisibility(View.VISIBLE);
                    ABC.showCustomToast(ListadoReservaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    latVacio.setVisibility(View.VISIBLE);
                    //No autorizado
                    ABC.showCustomToast(ListadoReservaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            latVacio.setVisibility(View.VISIBLE);
            e.printStackTrace();
            ABC.showCustomToast(ListadoReservaActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje")) {

                JSONArray jsonArray = jsonObject.getJSONArray("datos");

                mReservas = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Reserva reserva = Reserva.mapper(o, Reserva.HISTORIAL_TOTAL);

                    mReservas.add(reserva);

                }

                mReservasEspeciales = new ArrayList<>();

                jsonArray = jsonObject.getJSONArray("especial");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    ReservaEspecial reserva = ReservaEspecial.mapper(o, ReservaEspecial.MEDIUM);

                    mReservasEspeciales.add(reserva);

                }

                mMenu = Menu.mapper(jsonObject.getJSONArray("mensaje").getJSONObject(0), Menu.COMPLETE);
                mAdapter = new ReservasAdapter(mReservas, getApplicationContext(), ReservasAdapter.ADMIN);
                mReservasAdapterEspecial = new ReservasAdapter(mReservasEspeciales, getApplicationContext(), ReservasAdapter.ESPECIAL);
                if (mReservas.size() == 0) {
                    latVacio.setVisibility(View.VISIBLE);
                } else {
                    loadEstadisticas();
                }
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerViewEspecial.setAdapter(mReservasAdapterEspecial);
                if (mReservasEspeciales.size() > 0) {
                    mReservasAdapterEspecial.change(mReservasEspeciales);
                } else {
                    latEspeciales.setVisibility(View.GONE);
                }
                if (mMenu != null) {
                    String[] comida = ABC.getComidas(mMenu.getDescripcion());
                    txtAlmuerzo.setText(comida[0]);
                    txtCena.setText(comida[1]);
                    txtPostre.setText(comida[2]);
                    txtPorciones.setText(String.valueOf(mMenu.getPorcion()));
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
            latVacio.setVisibility(View.VISIBLE);
        }

    }

    private void loadEstadisticas() {
        //Reservas Ultimos 7 dias
        final ArrayList<BarEntry> entries;
        final ArrayList<String> entryLabels;
        XAxis xAxis2;
        YAxis leftAxis, rightAxis;

        barCantidad.getDescription().setEnabled(false);
        barCantidad.getLegend().setEnabled(false);
        xAxis2 = barCantidad.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setTextSize(12f);
        //Habilita los labels
        xAxis2.setDrawAxisLine(true);
        xAxis2.setDrawGridLines(false);

        leftAxis = barCantidad.getAxisLeft();
        rightAxis = barCantidad.getAxisRight();

        leftAxis.setTextSize(12f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);


        entries = new ArrayList<>();
        entryLabels = new ArrayList<String>();
        int cantidadRes = 0, cantidadReti = 0, cantidadCancelado = 0, cantidadNoRetirados = 0;
        for (Reserva reserva : mReservas) {
            if (reserva.getDescripcion().equals("RESERVADO")) {
                cantidadRes++;
            } else if (reserva.getDescripcion().equals("CANCELADO")) {
                cantidadCancelado++;
            } else if (reserva.getDescripcion().equals("RETIRADO")) {

                cantidadReti++;
            } else if (reserva.getDescripcion().equals("NO RETIRADO")) {

                cantidadNoRetirados++;
            }
        }
        entries.add(new BarEntry(1, mReservas.size()));
        entryLabels.add("Total");
        entries.add(new BarEntry(2, cantidadReti));
        entryLabels.add("Retiros");
        entries.add(new BarEntry(3, cantidadRes));
        entryLabels.add("Reservas");
        entries.add(new BarEntry(4, cantidadCancelado));
        entryLabels.add("Cancelos");
        entries.add(new BarEntry(5, cantidadNoRetirados));
        entryLabels.add("No Retirados");
        BarDataSet barDataSet2 = new BarDataSet(entries, "");
        barDataSet2.setColors(new int[]{R.color.colorLightBlue, R.color.colorGreen,
                R.color.colorOrange, R.color.colorRed, R.color.colorPink}, getApplicationContext());
        barDataSet2.setValueTextSize(13);
        barDataSet2.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        barDataSet2.setValueTextColor(Color.rgb(155, 155, 155));
        BarData barData2 = new BarData(barDataSet2);
        barData2.setBarWidth(0.9f); // set custom bar width
        barCantidad.setData(barData2);
        barCantidad.setFitBars(true);
        barCantidad.invalidate();
        barCantidad.setScaleEnabled(true);
        barCantidad.setDoubleTapToZoomEnabled(false);
        barCantidad.setBackgroundColor(Color.rgb(255, 255, 255));
        xAxis2.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";
            }
        });
        cardEstadisticas.setVisibility(View.VISIBLE);
    }

    private void loadViews() {
        latEspeciales = findViewById(R.id.latEspecial);
        mRecyclerViewEspecial = findViewById(R.id.recyclerEspecial);
        barCantidad = findViewById(R.id.barCantidad);
        latVacio = findViewById(R.id.latVacio);
        cardEstadisticas = findViewById(R.id.cardEstadistica);
        btnBack = findViewById(R.id.imgFlecha);
        txtAlmuerzo = findViewById(R.id.txtAlmuerzo);
        txtCena = findViewById(R.id.txtCena);
        txtPostre = findViewById(R.id.txtPostre);
        txtPorciones = findViewById(R.id.txtPorcion);
        mRecyclerView = findViewById(R.id.recycler);
        mProgressBar = findViewById(R.id.progress_bar);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFlecha:
                onBackPressed();
                break;
        }
    }
}
