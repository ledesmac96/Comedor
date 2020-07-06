package com.unse.bienestar.comedor.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unse.bienestar.comedor.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedor.Modelo.Alumno;
import com.unse.bienestar.comedor.Modelo.Menu;
import com.unse.bienestar.comedor.Modelo.Reserva;
import com.unse.bienestar.comedor.Modelo.Usuario;
import com.bienestar.comedor.R;
import com.unse.bienestar.comedor.Utils.PreferenciasManager;
import com.unse.bienestar.comedor.Utils.Utils;
import com.unse.bienestar.comedor.Utils.VolleySingleton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class EstadisticasFragment extends Fragment implements View.OnClickListener {

    View view;
    LinearLayout latProcesamiento, latDatos, latAlumno, latReserva, latVacio;
    BarChart barSieteDias, barMeses;
    PieChart mPieChart, pieDatos;
    Context mContext;
    FragmentManager mFragmentManager;
    DialogoProcesamiento dialog;
    ArrayList<Alumno> mAlumnos;
    ArrayList<Reserva> mReservas;
    ArrayList<Menu> mMenus;
    TextView txtCarreras, txtCantidad, txtCantidadReservas, txtPorciones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        loadView();

        loadData();

        loadListener();

        return view;
    }

    private void loadData() {
        latVacio.setVisibility(View.GONE);
        latDatos.setVisibility(View.GONE);
        barMeses.setVisibility(View.GONE);
        barSieteDias.setVisibility(View.GONE);
        loadInfo();
    }

    private void loadInfo() {
        latProcesamiento.setVisibility(View.VISIBLE);
        PreferenciasManager manager = new PreferenciasManager(mContext);
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?id=%s&key=%s", Utils.URL_DATOS, id, key);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                latProcesamiento.setVisibility(View.GONE);
                latVacio.setVisibility(View.VISIBLE);
                Utils.showToast(mContext, getString(R.string.servidorOff));
                dialog.dismiss();

            }
        });
        //Abro dialogo para congelar pantalla
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(mFragmentManager, "dialog_process");
        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void procesarRespuesta(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
                    latProcesamiento.setVisibility(View.GONE);
                    latVacio.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    //Exito
                    loadInfo(jsonObject);
                    break;
                case 2:
                    Utils.showToast(getContext(), getString(R.string.sinDatos));
                    latProcesamiento.setVisibility(View.GONE);
                    latVacio.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    Utils.showToast(getContext(), getString(R.string.tokenInvalido));
                    latProcesamiento.setVisibility(View.GONE);
                    latVacio.setVisibility(View.VISIBLE);
                    break;
                case 100:
                    //No autorizado
                    Utils.showToast(getContext(), getString(R.string.tokenInexistente));
                    latProcesamiento.setVisibility(View.GONE);
                    latVacio.setVisibility(View.VISIBLE);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            latProcesamiento.setVisibility(View.GONE);
            latVacio.setVisibility(View.VISIBLE);
            Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje") && jsonObject.has("reserva") && jsonObject.has("menu")) {

                //Usuario
                mAlumnos = new ArrayList<>();
                JSONArray usuarios = jsonObject.getJSONArray("mensaje");

                for (int i = 0; i < usuarios.length(); i++) {

                    JSONObject o = usuarios.getJSONObject(i);

                    Usuario usuario = Usuario.mapper(o, Usuario.ESTADISTICA);

                    Alumno alumno = Alumno.mapper(o, usuario);

                    mAlumnos.add(alumno);

                }

                mReservas = new ArrayList<>();
                JSONArray reservas = jsonObject.getJSONArray("reserva");
                for (int i = 0; i < reservas.length(); i++) {

                    JSONObject o = reservas.getJSONObject(i);

                    Reserva reserva = Reserva.mapper(o, Reserva.ESTADISTICA);

                    mReservas.add(reserva);

                }

                mMenus = new ArrayList<>();
                JSONArray menus = jsonObject.getJSONArray("menu");
                for (int i = 0; i < menus.length(); i++) {

                    JSONObject o = menus.getJSONObject(i);

                    Menu menu = Menu.mapper(o, Menu.ESTADISTICA);

                    mMenus.add(menu);

                }

                loadEstadisticas();

            } else {
                latVacio.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadEstadisticas() {
        int[] facultades = new int[5];
        int totalSinCompletar = 0, totalCompleto = 0;
        int cantidadTotal = 0;
        HashMap<String, Integer> carreras = new HashMap<>();
        for (Alumno alumno : mAlumnos) {
            if (!alumno.getCarrera().equals("null")) {
                cantidadTotal++;
                if (alumno.getFechaModificacion().equals("null")) {
                    totalSinCompletar++;
                } else {
                    totalCompleto++;
                }
                if (!carreras.containsKey(alumno.getCarrera())) {
                    carreras.put(alumno.getCarrera(), 1);
                } else {
                    carreras.put(alumno.getCarrera(), carreras.get(alumno.getCarrera()) + 1);
                }
                switch (alumno.getFacultad()) {
                    case "FAyA":
                        facultades[0] = facultades[0] + 1;
                        break;
                    case "FCEyT":
                        facultades[1] = facultades[1] + 1;
                        break;
                    case "FCF":
                        facultades[2] = facultades[2] + 1;
                        break;
                    case "FCM":
                        facultades[3] = facultades[3] + 1;
                        break;
                    case "FHCSyS":
                        facultades[4] = facultades[4] + 1;
                        break;
                }
            }
        }
        ArrayList<String> horasReserva = new ArrayList<>();
        ArrayList<String> horasRetirada = new ArrayList<>();
        HashMap<String, Integer> reservasPorMenu = new HashMap<>();
        for (Reserva reserva : mReservas) {
            int indexHora = reserva.getFechaReserva().indexOf(":");
            String hora = reserva.getFechaReserva().substring(indexHora - 2, indexHora + 3);
            horasReserva.add(hora);

            int indexHoraRet = reserva.getFechaModificacion().indexOf(":");
            String horaRet = reserva.getFechaModificacion().substring(indexHoraRet - 2, indexHoraRet + 3);
            horasRetirada.add(horaRet);

            if (!reservasPorMenu.containsKey(String.valueOf(reserva.getIdMenu()))) {
                reservasPorMenu.put(String.valueOf(reserva.getIdMenu()), 1);
            } else {
                reservasPorMenu.put(String.valueOf(reserva.getIdMenu()),
                        reservasPorMenu.get(String.valueOf(reserva.getIdMenu())) + 1);
            }
        }
        String[] fechasSiete = new String[]{"", "", "", "", "", "", ""};
        int[] idMenus = new int[]{0, 0, 0, 0, 0, 0, 0};
        int inicio = mMenus.size() - 7, fin = mMenus.size();
        int i = 6;
        List<Menu> ultimosSiete = mMenus.size() > 7 ? mMenus.subList(inicio, fin) : mMenus;
        Collections.reverse(ultimosSiete);
        for (Menu menu : ultimosSiete) {
            fechasSiete[i] = String.format("%02d/%02d", menu.getDia(), menu.getMes());
            idMenus[i] = menu.getIdMenu();
            i--;
        }
        int[] reservasMensuales = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int cantidadPorciones = 0;
        for (Menu menu : mMenus) {

            int reservas = reservasPorMenu.get(String.valueOf(menu.getIdMenu())) != null ?
                    reservasPorMenu.get(String.valueOf(menu.getIdMenu())) :
                    0;
            cantidadPorciones = cantidadPorciones + (reservas * menu.getPorcion());
            reservasMensuales[menu.getMes() - 1] = reservasMensuales[menu.getMes() - 1] + reservas;

        }
        loadResults(facultades, carreras, totalSinCompletar,
                totalCompleto, cantidadTotal, fechasSiete, reservasPorMenu, idMenus, cantidadPorciones,
                reservasMensuales, horasReserva, horasRetirada);

    }

    private void loadResults(int[] facultades, HashMap<String, Integer> carreras,
                             int incompleto, int completo, int total, String[] fechas,
                             HashMap<String, Integer> reservasMenu,
                             int[] menus, int cantidadPorciones,
                             int[] reservasMensuales,
                             ArrayList<String> horaReserva,
                             ArrayList<String> horaRetirada) {

        loadAlumnosPorFacultad(facultades);

        StringBuilder carrerasInfo = loadAlumnosPorCarrera(carreras);

        loadCapacidadInformacion(completo, incompleto);

        if (!fechas[0].equals("") && reservasMenu.size() > 0)
            loadReservasSieteDias(fechas, reservasMenu, menus);

        boolean isData = false;
        for (Integer integer : reservasMensuales) {
            if (integer != 0)
                isData = true;
        }
        if (isData)
            loadReservasPorMeses(reservasMensuales);

        if (horaReserva.size() > 0 && horaRetirada.size() > 0)
            loadHoraPromedio(horaReserva, horaRetirada);


        txtCarreras.setText(carrerasInfo.toString());
        txtPorciones.setText(String.valueOf(cantidadPorciones));
        txtCantidad.setText(String.valueOf(total));
        txtCantidadReservas.setText(String.valueOf(mReservas.size()));
        latProcesamiento.setVisibility(View.GONE);
        latDatos.setVisibility(View.VISIBLE);
        latReserva.setVisibility(View.VISIBLE);
        latAlumno.setVisibility(View.VISIBLE);

        loadAnimation();

    }

    private int[] loadHoraPromedio(ArrayList<String> horaReserva, ArrayList<String> horaRetirada) {
        int[] numeros = new int[2];
        ArrayList<Integer> horas = new ArrayList<>();
        for (String s : horaReserva) {
            s = s.replace(":", "");
            try {
                horas.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {

            }
        }
        int total = 0;
        for (Integer integer : horas) {
            total = total + integer;
        }
        int prom = total / horas.size();
        numeros[0] = prom;

        horas.clear();
        for (String s : horaRetirada) {
            s = s.replace(":", "");
            try {
                horas.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {

            }
        }
        total = 0;
        for (Integer integer : horas) {
            total = total + integer;
        }
        prom = total / horas.size();
        numeros[1] = prom;
        return numeros;
    }

    private void loadReservasPorMeses(int[] reservasMensuales) {
        //Reservas Ultimos 7 dias
        ArrayList<BarEntry> entries;
        final ArrayList<String> entryLabels;
        XAxis xAxis2;
        YAxis leftAxis, rightAxis;

        barMeses.getDescription().setEnabled(false);
        barMeses.getLegend().setEnabled(false);
        xAxis2 = barMeses.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setTextSize(12f);
        //Habilita los labels
        xAxis2.setDrawAxisLine(true);
        xAxis2.setDrawGridLines(false);

        leftAxis = barMeses.getAxisLeft();
        rightAxis = barMeses.getAxisRight();

        leftAxis.setTextSize(12f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);


        entries = new ArrayList<>();
        entryLabels = new ArrayList<String>();
        int i = 1;
        for (Integer cantidad : reservasMensuales) {
            entries.add(new BarEntry(i, cantidad));
            entryLabels.add(Utils.getMonth(i).substring(0, 3));
            i++;
        }
        BarDataSet barDataSet2 = new BarDataSet(entries, "Reservas por Meses");
        barDataSet2.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet2.setValueTextSize(13);
        barDataSet2.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        barDataSet2.setValueTextColor(Color.rgb(155, 155, 155));
        BarData barData2 = new BarData(barDataSet2);
        barData2.setBarWidth(0.9f); // set custom bar width
        barMeses.setData(barData2);
        barMeses.setFitBars(true);
        barMeses.invalidate();
        barMeses.setScaleEnabled(true);
        barMeses.setDoubleTapToZoomEnabled(false);
        barMeses.setBackgroundColor(Color.rgb(255, 255, 255));
        xAxis2.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return entryLabels.get((int) value - 1);
            }
        });

        barMeses.setVisibility(View.VISIBLE);

    }

    private void loadReservasSieteDias(String[] fechas,
                                       HashMap<String, Integer> reservasMenu,
                                       int[] menus) {
        //Reservas Ultimos 7 dias
        ArrayList<BarEntry> entries;
        final ArrayList<String> entryLabels;
        BarDataSet barDataSet;
        BarData barData;
        XAxis xAxis;
        YAxis leftAxis, rightAxis;


        barSieteDias.getDescription().setEnabled(false);
        barSieteDias.getLegend().setEnabled(false);
        xAxis = barSieteDias.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        //Habilita los labels
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        leftAxis = barSieteDias.getAxisLeft();
        rightAxis = barSieteDias.getAxisRight();

        leftAxis.setTextSize(12f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);


        entries = new ArrayList<>();
        entryLabels = new ArrayList<String>();
        int i = 1;
        for (Integer idMenus : menus) {
            Integer valor = reservasMenu.get(String.valueOf(idMenus));
            entries.add(new BarEntry(i, valor != null ? valor : 0));
            entryLabels.add(fechas[i - 1]);
            i++;
        }
        barDataSet = new BarDataSet(entries, "Reservas Ultimos 7 Días");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextSize(13);
        barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        barDataSet.setValueTextColor(Color.rgb(155, 155, 155));
        barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f); // set custom bar width
        barSieteDias.setData(barData);
        barSieteDias.setFitBars(true);
        barSieteDias.invalidate();
        barSieteDias.setScaleEnabled(true);
        barSieteDias.setDoubleTapToZoomEnabled(false);
        barSieteDias.setBackgroundColor(Color.rgb(255, 255, 255));
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return entryLabels.get((int) value - 1);
            }
        });

        barSieteDias.setVisibility(View.VISIBLE);

    }

    private void loadCapacidadInformacion(int completo, int incompleto) {
        //Informacion
        pieDatos.setUsePercentValues(true);
        pieDatos.getDescription().setEnabled(false);
        pieDatos.setExtraOffsets(5, 10, 5, 5);
        pieDatos.getLegend().setEnabled(true);
        pieDatos.setDragDecelerationFrictionCoef(0.99f);
        pieDatos.setDrawHoleEnabled(true);
        pieDatos.setHoleColor(Color.WHITE);
        pieDatos.setTransparentCircleRadius(61f);
        pieDatos.setEntryLabelTextSize(0);
        pieDatos.setEntryLabelColor(Color.BLACK);

        ArrayList<PieEntry> entradasInfo = new ArrayList<>();
        entradasInfo.add(new PieEntry(completo, "Estudiantes con info completa"));
        entradasInfo.add(new PieEntry(incompleto, "Estudiantes con info incompleta"));
        PieDataSet dataSetDatos = new PieDataSet(entradasInfo, "");
        dataSetDatos.setSliceSpace(3f);
        dataSetDatos.setSelectionShift(5f);
        dataSetDatos.setColors(new int[]{R.color.colorRed, R.color.colorFCEyT}, getContext());
        PieData pieDataDatos = new PieData(dataSetDatos);
        pieDataDatos.setValueTextSize(16f);
        pieDataDatos.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        pieDataDatos.setValueTextColor(Color.BLACK);
        pieDatos.setData(pieDataDatos);

        pieDatos.setVisibility(View.VISIBLE);

    }

    private StringBuilder loadAlumnosPorCarrera(HashMap<String, Integer> carreras) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : carreras.keySet()) {
            if (!s.equals("null")) {
                stringBuilder.append(String.format("%s - %s", carreras.get(s), s));
                stringBuilder.append("\n");
            }
        }
        return stringBuilder;
    }

    private void loadAlumnosPorFacultad(int[] facultades) {
        //Alumnos por Facultad
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);
        mPieChart.getLegend().setEnabled(true);
        mPieChart.setDragDecelerationFrictionCoef(0.99f);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);
        mPieChart.setTransparentCircleRadius(61f);
        //mPieChart.setEntryLabelColor(Color.BLACK);
        mPieChart.setEntryLabelTextSize(0); //Nuevo

        ArrayList<PieEntry> yEntry = new ArrayList<>();
        int i = 0;
        for (Integer integer : facultades) {
            yEntry.add(new PieEntry(integer, Utils.facultad[i]));
            i++;
        }
        PieDataSet dataSet = new PieDataSet(yEntry, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(new int[]{R.color.colorRed, R.color.colorFCEyT,
                R.color.colorGreen, R.color.colorLightBlue, R.color.colorYellow}, getContext());
        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(17f);
        pieData.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        pieData.setValueTextColor(Color.BLACK);
        mPieChart.setData(pieData);
    }

    private void loadListener() {

    }

    private void loadView() {
        latVacio = view.findViewById(R.id.latVacio);
        barMeses = view.findViewById(R.id.barMeses);
        txtPorciones = view.findViewById(R.id.txtCantidadPorciones);
        txtCantidadReservas = view.findViewById(R.id.txtCantidadReservas);
        txtCantidad = view.findViewById(R.id.txtCantidad);
        latDatos = view.findViewById(R.id.latDatos);
        txtCarreras = view.findViewById(R.id.txtCarreras);
        barSieteDias = view.findViewById(R.id.barSieteDias);
        latAlumno = view.findViewById(R.id.latAlumno);
        latReserva = view.findViewById(R.id.latReserva);
        mPieChart = view.findViewById(R.id.pieFacultad);
        pieDatos = view.findViewById(R.id.pieDatos);
        latProcesamiento = view.findViewById(R.id.latProcesamiento);
    }

    public void loadAnimation() {
        mPieChart.animateY(800, Easing.EasingOption.EaseInCirc);
        //mBarChart.animateY(1500, Easing.EasingOption.EaseInOutExpo);

    }


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

    }
}
