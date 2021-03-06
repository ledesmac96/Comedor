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

public class ReservaDiaActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtDescripcion;
    DialogoProcesamiento dialog;
    Menu mMenus;
    RecyclerView mRecyclerView, mRecyclerViewEspecial;
    RecyclerView.LayoutManager mLayoutManager, mLayoutManagerEspecial;
    LinearLayout latVacio, latEspeciales;
    CardView cardEstadisticas;
    ReservasAdapter mReservasAdapter, mReservasAdapterEspecial;
    ArrayList<Reserva> mReservas, mReservasEspeciales;
    ImageView imgIcono;
    ProgressBar mProgressBar;
    BarChart barCantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_dia);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadView();

        loadData();

        loadListener();

        setToolbar();

    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText("Reservas del día");
        ABC.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                // Intent i = new Intent(getApplicationContext(), InfoReservaActivity.class);
                //i.putExtra(Utils.RESERVA, mReservas.get(position));
                //startActivity(i);
            }
        });

    }

    private void loadData() {
        mReservas = new ArrayList<>();
        mReservasEspeciales = new ArrayList<>();
        latVacio.setVisibility(View.GONE);
        cardEstadisticas.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mReservasAdapter = new ReservasAdapter(mReservas, getApplicationContext(), ReservasAdapter.ADMIN);
        mReservasAdapterEspecial = new ReservasAdapter(mReservasEspeciales, getApplicationContext(), ReservasAdapter.ESPECIAL);
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mLayoutManagerEspecial = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerViewEspecial.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewEspecial.setLayoutManager(mLayoutManagerEspecial);
        mRecyclerView.setAdapter(mReservasAdapter);
        mRecyclerViewEspecial.setAdapter(mReservasAdapterEspecial);

        loadInfo();


    }

    private void loadInfo() {
        mProgressBar.setVisibility(View.VISIBLE);
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&m=%s", ABC.URL_RESERVA_HOY, id, key, -1);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mProgressBar.setVisibility(View.GONE);
                ABC.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
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
            mProgressBar.setVisibility(View.GONE);
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    ABC.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    loadInfo(jsonObject);
                    break;
                case 2:
                    ABC.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.noReservas), R.drawable.ic_error);
                    break;
                case 4:
                    ABC.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_error);
                    break;
                case 3:
                    ABC.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    ABC.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje") && jsonObject.has("datos")) {

                mMenus = Menu.mapper(jsonObject.getJSONArray("mensaje").getJSONObject(0), Menu.COMPLETE);

                mReservas = new ArrayList<>();

                JSONArray jsonArray = jsonObject.getJSONArray("datos");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Reserva reserva = Reserva.mapper(o, Reserva.MEDIUM);

                    mReservas.add(reserva);

                }

                mReservasEspeciales = new ArrayList<>();

                jsonArray = jsonObject.getJSONArray("especial");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    ReservaEspecial reserva = ReservaEspecial.mapper(o, ReservaEspecial.MEDIUM);

                    mReservasEspeciales.add(reserva);

                }

                if (mReservas.size() > 0) {
                    mReservasAdapter.change(mReservas);
                    loadEstadisticas();
                } else {
                    latVacio.setVisibility(View.VISIBLE);
                }

                if (mReservasEspeciales.size() > 0) {
                    mReservasAdapterEspecial.change(mReservasEspeciales);
                } else {
                    latEspeciales.setVisibility(View.GONE);
                }

                if (mReservasEspeciales.size() > 0 || mReservas.size() > 0) {
                    latVacio.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadView() {
        latEspeciales = findViewById(R.id.latEspecial);
        mRecyclerViewEspecial = findViewById(R.id.recyclerEspecial);
        barCantidad = findViewById(R.id.barCantidad);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        imgIcono = findViewById(R.id.imgFlecha);
        mProgressBar = findViewById(R.id.progress_bar);
        mRecyclerView = findViewById(R.id.recycler);
        cardEstadisticas = findViewById(R.id.cardEstadistica);
        latVacio = findViewById(R.id.latVacio);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFlecha:
                onBackPressed();
                break;
        }
    }
}
