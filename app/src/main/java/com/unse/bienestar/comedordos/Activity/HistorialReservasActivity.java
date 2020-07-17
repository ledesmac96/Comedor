package com.unse.bienestar.comedordos.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unse.bienestar.comedordos.Adapter.HistorialReservasAdapter;
import com.unse.bienestar.comedordos.Dialogos.DialogoOpciones;
import com.unse.bienestar.comedordos.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedordos.Modelo.Alumno;
import com.unse.bienestar.comedordos.Modelo.ItemBase;
import com.unse.bienestar.comedordos.Modelo.ItemDato;
import com.unse.bienestar.comedordos.Modelo.ItemFecha;
import com.unse.bienestar.comedordos.Modelo.Menu;
import com.unse.bienestar.comedordos.Modelo.Opciones;
import com.unse.bienestar.comedordos.Modelo.Reserva;
import com.unse.bienestar.comedordos.Modelo.Usuario;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.RecyclerListener.ItemClickSupport;
import com.unse.bienestar.comedordos.Utils.OnClickOptionListener;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistorialReservasActivity extends AppCompatActivity implements View.OnClickListener {

    DialogoProcesamiento dialog;
    ArrayList<Menu> mMenus;
    ArrayList<ItemBase> mItems;
    ArrayList<ItemBase> mListOficial;
    FloatingActionButton fabPDF;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    HistorialReservasAdapter adapter;
    String[] meses = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
            "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    ImageView imgIcono;
    ProgressBar mProgressBar;
    String mes = "";
    int numberMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_reserva);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadViews();

        loadListener();

        loadData();

        setToolbar();

    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText("Historial de reservas");
        ABC.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
    }

    private void loadData() {
        loadInfo();

        adapter = new HistorialReservasAdapter(this, mListOficial);
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    private void loadInfo() {
        mProgressBar.setVisibility(View.VISIBLE);
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&m=%s", ABC.URL_RESERVA_HISTORIAL, id, key, 1);
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
                ABC.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
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
                    ABC.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito

                    loadInfo(jsonObject);
                    break;
                case 2:
                    ABC.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
                            getString(R.string.noReservasHistorial), R.drawable.ic_error);
                    break;
                case 3:
                    ABC.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    ABC.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje")) {

                JSONArray jsonArray = jsonObject.getJSONArray("mensaje");

                mMenus = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Menu menu = Menu.mapper(o, Menu.SIMPLE);

                    mMenus.add(menu);

                }
                //Utils.showToast(getApplicationContext(), "HAY " + mMenus.size());
            }

            mItems = new ArrayList<>();
            for (Menu m : mMenus) {
                ItemDato itemDato = new ItemDato();
                itemDato.setMenu(m);
                itemDato.setTipo(ItemDato.TIPO_MENU);
                mItems.add(itemDato);
            }
            mListOficial = new ArrayList<>();
            HashMap<String, List<ItemBase>> datos = filtrarPorMes(mItems);
            List<String> meses = new ArrayList<>();
            meses.addAll(datos.keySet());
            for (String date : meses) {
                ItemFecha dateItem = new ItemFecha(ABC.getMonth(Integer.parseInt(date)));
                mListOficial.add(dateItem);
                for (ItemBase item : datos.get(date)) {
                    ItemDato generalItem = (ItemDato) item;
                    mListOficial.add(generalItem);
                }
            }
            adapter.change(mListOficial);
            //Utils.showToast(getApplicationContext(), String.valueOf(mListOficial.size()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadListener() {
        fabPDF.setOnClickListener(this);
        imgIcono.setOnClickListener(this);

        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                if (mListOficial.get(position) instanceof ItemDato) {
                    Intent i = new Intent(getApplicationContext(), ListadoReservaActivity.class);
                    i.putExtra(ABC.DATA_RESERVA, ((ItemDato) mListOficial.get(position)).getMenu());
                    startActivity(i);
                }
            }
        });
    }

    private void loadViews() {
        fabPDF = findViewById(R.id.fabPDF);
        imgIcono = findViewById(R.id.imgFlecha);
        mProgressBar = findViewById(R.id.progress_bar);
        mRecyclerView = findViewById(R.id.recycler);
    }

    private HashMap<String, List<ItemBase>> filtrarPorMes(List<ItemBase> list) {

        HashMap<String, List<ItemBase>> groupedHashMap = new HashMap<>();

        for (ItemBase dato : list) {

            ItemDato itemDatoKey = (ItemDato) dato;

            if (itemDatoKey.getTipoDato() == ItemDato.TIPO_MENU) {

                String mes = String.valueOf(itemDatoKey.getMenu().getMes());

                if (!groupedHashMap.containsKey(mes)) {

                    for (ItemBase item : list) {

                        ItemDato itemDato = (ItemDato) item;

                        if (itemDato.getMenu().getMes() == itemDatoKey.getMenu().getMes()) {
                            if (groupedHashMap.containsKey(mes)) {
                                groupedHashMap.get(mes).add(itemDato);
                            } else {
                                List<ItemBase> nuevaLista = new ArrayList<>();
                                nuevaLista.add(itemDato);
                                groupedHashMap.put(mes, nuevaLista);
                            }
                        }
                    }
                }
            }

        }
        return groupedHashMap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.fabPDF:
                openDialog();
                break;
        }
    }

    private void openDialog() {
        ArrayList<Opciones> opciones = new ArrayList<>();
        opciones.add(new Opciones("REPORTE MENSUAL"));
        DialogoOpciones dialogoOpciones = new DialogoOpciones(new OnClickOptionListener() {
            @Override
            public void onClick(int pos) {
                procesarClick(pos);

            }
        }, opciones, getApplicationContext());
        dialogoOpciones.show(getSupportFragmentManager(), "opciones");
    }

    private void procesarClick(int pos) {
        switch (pos) {
            case 0:
                openMonthDialog();
                break;
        }
    }

    private void openMonthDialog() {
        final ArrayList<Opciones> opciones = new ArrayList<>();
        for (String s : meses) {
            opciones.add(new Opciones(s.toUpperCase()));
        }
        DialogoOpciones dialogoOpciones = new DialogoOpciones(new OnClickOptionListener() {
            @Override
            public void onClick(int pos) {
                mes = opciones.get(pos).getTitulo();
                procesarClickMes(pos);

            }
        }, opciones, getApplicationContext());
        dialogoOpciones.show(getSupportFragmentManager(), "opciones");
    }

    private void procesarClickMes(int pos) {
        numberMes = pos;
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?id=%s&key=%s&me=%s", ABC.URL_DATOS_RESERVA_MENSUAL, id, key, (pos + 1));
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuestaMes(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ABC.showToast(getApplicationContext(), getString(R.string.servidorOff));
                dialog.dismiss();

            }
        });
        //Abro dialogo para congelar pantalla
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog_process");
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void procesarRespuestaMes(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    ABC.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    loadInfoMes(jsonObject);
                    break;
                case 2:
                    ABC.showToast(getApplicationContext(), getString(R.string.noMenuMesDato));
                    break;
                case 3:
                    ABC.showToast(getApplicationContext(), getString(R.string.tokenInvalido));
                    break;
                case 100:
                    //No autorizado
                    ABC.showToast(getApplicationContext(), getString(R.string.tokenInexistente));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private void loadInfoMes(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje") && jsonObject.has("reserva") &&
                    jsonObject.has("alumnos")) {

                JSONArray jsonArray = jsonObject.getJSONArray("mensaje");

                ArrayList<Menu> menus = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Menu menu = Menu.mapper(o, Menu.REPORTE);

                    menus.add(menu);
                }

                jsonArray = jsonObject.getJSONArray("alumnos");

                ArrayList<Usuario> alumnos = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    String id = o.getString("idalumno");
                    String facultad = o.getString("facultad");
                    String nombre = o.getString("nombre");
                    String apellido = o.getString("apellido");
                    Alumno alumno = new Alumno();
                    alumno.setIdAlumno(Integer.parseInt(id));
                    alumno.setFacultad(facultad);
                    alumno.setNombre(nombre);
                    alumno.setApellido(apellido);
                    alumno.setIdUsuario(alumno.getIdAlumno());

                    alumnos.add(alumno);
                }

                jsonArray = jsonObject.getJSONArray("reserva");

                ArrayList<Reserva> reservas = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Reserva reserva = Reserva.mapper(o, Reserva.REPORTE_MENSUAL);

                    reservas.add(reserva);
                }

                if (menus.size() > 0 && reservas.size() > 0) {
                    openActivity(menus, alumnos, reservas);
                } else {
                    ABC.showToast(getApplicationContext(), getString(R.string.noMenuMesDato));
                }

            } else {
                ABC.showToast(getApplicationContext(), getString(R.string.noMenuMesDato));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openActivity(ArrayList<Menu> menus, ArrayList<Usuario> alumnos, ArrayList<Reserva> reservas) {
        Intent intent = new Intent(getApplicationContext(), PDFMonthActivity.class);
        intent.putExtra(ABC.ID_MENU, menus);
        intent.putExtra(ABC.ALUMNO_NAME, alumnos);
        intent.putExtra(ABC.RESERVA, reservas);
        intent.putExtra(ABC.MESNAME, mes);
        intent.putExtra(ABC.NUMERO_MES, numberMes);
        startActivity(intent);
    }
}
