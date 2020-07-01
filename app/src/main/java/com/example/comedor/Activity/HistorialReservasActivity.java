package com.example.comedor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Adapter.HistorialReservasAdapter;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.ItemBase;
import com.example.comedor.Modelo.ItemDato;
import com.example.comedor.Modelo.ItemFecha;
import com.example.comedor.Modelo.Menu;
import com.example.comedor.R;
import com.example.comedor.RecyclerListener.ItemClickSupport;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistorialReservasActivity extends AppCompatActivity implements View.OnClickListener {

    DialogoProcesamiento dialog;
    ArrayList<Menu> mMenus;
    ArrayList<ItemBase> mItems;
    ArrayList<ItemBase> mListOficial;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    HistorialReservasAdapter adapter;

    ImageView imgIcono;
    ProgressBar mProgressBar;

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
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText("Historial de reservas");
        Utils.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
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
                mProgressBar.setVisibility(View.GONE);
                Utils.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
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
                    //Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    Utils.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito

                    loadInfo(jsonObject);
                    break;
                case 2:
                    //Utils.showToast(getApplicationContext(), getString(R.string.noData));
                    Utils.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
                            getString(R.string.noData), R.drawable.ic_error);
                    break;
                case 3:
                    //Utils.showToast(getApplicationContext(), getString(R.string.tokenInvalido));
                    Utils.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    //Utils.showToast(getApplicationContext(), getString(R.string.tokenInexistente));
                    Utils.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
            Utils.showCustomToast(HistorialReservasActivity.this, getApplicationContext(),
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
                ItemFecha dateItem = new ItemFecha(Utils.getMonth(Integer.parseInt(date)));
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
        imgIcono.setOnClickListener(this);

        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                if (mListOficial.get(position) instanceof ItemDato){
                    Intent i = new Intent(getApplicationContext(), ListadoReservaActivity.class);
                    i.putExtra(Utils.DATA_RESERVA, ((ItemDato) mListOficial.get(position)).getMenu());
                    startActivity(i);
                }

            }
        });

    }

    private void loadViews() {
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
        }
    }
}
