package com.example.comedor.Activity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.ItemBase;
import com.example.comedor.Modelo.ItemDato;
import com.example.comedor.Modelo.ItemFecha;
import com.example.comedor.Modelo.Menu;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class HistorialReservasActivity extends AppCompatActivity {

    DialogoProcesamiento dialog;
    ArrayList<Menu> mMenus;
    ArrayList<ItemBase> mItems;
    ArrayList<ItemBase> mListOficial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_reserva);

        loadViews();

        loadListener();

        loadData();

        setToolbar();

    }

    private void setToolbar() {

    }

    private void loadData() {
        loadInfo();

    }

    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&m=%s", Utils.URL_RESERVA_HISTORIAL, id, key, 1);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //mProgressBar.setVisibility(View.GONE);
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
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    //mProgressBar.setVisibility(View.GONE);
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
            if (jsonObject.has("mensaje")) {

                JSONArray jsonArray = jsonObject.getJSONArray("mensaje");

                mMenus = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Menu menu = Menu.mapper(o, Menu.SIMPLE);

                    mMenus.add(menu);

                }
                Utils.showToast(getApplicationContext(), "HAY " + mMenus.size());
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
            List<String> anios = new ArrayList<>();
            anios.addAll(datos.keySet());
            for (String date : anios) {
                ItemFecha dateItem = new ItemFecha(Utils.getMonth(Integer.parseInt(date)));
                mListOficial.add(dateItem);
                for (ItemBase item : datos.get(date)) {
                    ItemDato generalItem = (ItemDato) item;
                    mListOficial.add(generalItem);
                }
            }
            Utils.showToast(getApplicationContext(), String.valueOf(mListOficial.size()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadListener() {
    }

    private void loadViews() {
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

}
