package com.example.comedor.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comedor.Activity.NuevoMenuActivity;
import com.example.comedor.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class EstadisticasFragment extends Fragment implements View.OnClickListener {

    View view;
    BarChart mBarChart;
    PieChart mPieChart;
    Context mContext;
    FragmentManager mFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        loadView();

        loadData();

        loadListener();

        loadAnimation();


        return view;
    }

    private void loadData() {
        ArrayList<BarEntry> entries;
        final ArrayList<String> entryLabels;
        BarDataSet barDataSet;
        BarData barData;
        XAxis xAxis;
        YAxis leftAxis, rightAxis;

        mBarChart.getDescription().setEnabled(false);
        mBarChart.getLegend().setEnabled(false);
        xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        //Habilita los labels
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        leftAxis = mBarChart.getAxisLeft();
        rightAxis = mBarChart.getAxisRight();

        leftAxis.setTextSize(10f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        /*ArrayList<Gasto> gastos = gastoRepo.getAll();
        ArrayList<Datos> datos = new ArrayList<>();
        for (int i = 0; i < Util.TIPOS.length; i++) {
            datos.add(new Datos(i, 0f));
        }
        for (Gasto r : gastos) {

            datos.get(r.getTipo()).setCant(datos.get(r.getTipo()).getCant() + r.getMonto());

        }*/

        entries = new ArrayList<>();
        entryLabels = new ArrayList<String>();
        entries.add(new BarEntry(1, 3));
        entryLabels.add("10");
        entries.add(new BarEntry(2, 14));
        entryLabels.add("11");
        entries.add(new BarEntry(3, 22));
        entryLabels.add("12");
        entries.add(new BarEntry(4, 10));
        entryLabels.add("13");
        entries.add(new BarEntry(5, 18));
        entryLabels.add("14");
        entries.add(new BarEntry(6, 30));
        entryLabels.add("15");
        entries.add(new BarEntry(7, 23));
        entryLabels.add("16");


        barDataSet = new BarDataSet(entries, "Cantidad de Reservas en la semana");

        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.rgb(155, 155, 155));

        barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f); // set custom bar width

        mBarChart.setData(barData);
        mBarChart.setFitBars(true);
        mBarChart.invalidate();
        mBarChart.setScaleEnabled(true);
        mBarChart.setDoubleTapToZoomEnabled(false);
        mBarChart.setBackgroundColor(Color.rgb(255, 255, 255));

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return entryLabels.get((int) value-1);
            }
        });

        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);
        mPieChart.getLegend().setEnabled(false);
        mPieChart.setDragDecelerationFrictionCoef(0.99f);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);
        mPieChart.setTransparentCircleRadius(61f);
        mPieChart.setEntryLabelColor(Color.BLACK);

        ArrayList<PieEntry> yEntry = new ArrayList<>();
        yEntry.add(new PieEntry(50, "Ingresos"));
        yEntry.add(new PieEntry(32, "Egresos"));

        PieDataSet dataSet = new PieDataSet(yEntry, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(new int[]{R.color.colorGreen, R.color.colorRed}, getContext());

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.BLACK);

        mPieChart.setData(pieData);

    }

    private void loadListener() {

    }

    private void loadView() {
        mBarChart = view.findViewById(R.id.barChart);
        mPieChart = view.findViewById(R.id.pieChart);
    }

    public void loadAnimation() {
        mPieChart.animateY(1500, Easing.EasingOption.EaseInCirc);
        mBarChart.animateY(1500, Easing.EasingOption.EaseInOutExpo);

    }


    /*private void loadInfo() {
        int mes = mSpinnerMeses.getSelectedItemPosition() + 1;
        PreferenciasManager manager = new PreferenciasManager(getContext());
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&m=%s", Utils.URL_MENU_BY_RANGE, id, key, mes);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // mProgressBar.setVisibility(View.GONE);
                Utils.showToast(getContext(), getString(R.string.servidorOff));
                dialog.dismiss();

            }
        });
        //Abro dialogo para congelar pantalla
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(mFragmentManager, "dialog_process");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void procesarRespuesta(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    // mProgressBar.setVisibility(View.GONE);
                    loadInfo(jsonObject);
                    break;
                case 2:
                    Utils.showToast(getContext(), getString(R.string.noMenu));
                    break;
                case 3:
                    Utils.showToast(getContext(), getString(R.string.tokenInvalido));
                    break;
                case 100:
                    //No autorizado
                    Utils.showToast(getContext(), getString(R.string.tokenInexistente));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje")) {

                mMenus = new ArrayList<>();

                JSONArray jsonArray = jsonObject.getJSONArray("mensaje");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Menu menu = Menu.mapper(o, Menu.COMPLETE);


                    mMenus.add(menu);

                }
                Utils.showToast(getContext(), "HAY " + mMenus.size());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/


    public void setFragmentManager(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    public FragmentManager getManagerFragment() {
        return this.mFragmentManager;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGuardar:
                startActivity(new Intent(getContext(), NuevoMenuActivity.class));
                break;
            case R.id.btnBuscar:
                //loadInfo();
                break;
        }

    }
}
