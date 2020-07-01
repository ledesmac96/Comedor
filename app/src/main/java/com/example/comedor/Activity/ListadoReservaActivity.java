package com.example.comedor.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Adapter.ReservasAdapter;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.Menu;
import com.example.comedor.Modelo.Reserva;
import com.example.comedor.R;
import com.example.comedor.RecyclerListener.ItemClickSupport;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListadoReservaActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtPlato, txtPorciones;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ReservasAdapter mAdapter;
    Menu mMenu;
    ArrayList<Reserva> mReservas;
    ImageView btnBack;
    ProgressBar mProgressBar;
    DialogoProcesamiento dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_reserva);

        if (getIntent().getParcelableExtra(Utils.DATA_RESERVA) != null){
            mMenu = getIntent().getParcelableExtra(Utils.DATA_RESERVA);
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
        ((TextView) findViewById(R.id.txtTitulo)).setText(Utils.getInfoDate(mMenu.getDia(), mMenu.getMes(), mMenu.getAnio()));
    }


    private void loadData() {
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        loadInfo();

    }

    private void loadInfo() {
        mProgressBar.setVisibility(View.GONE);
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&m=%s&d=%s&me=%s&a=%s", Utils.URL_RESERVA_HOY,
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
                mProgressBar.setVisibility(View.GONE);
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
            mProgressBar.setVisibility(View.GONE);
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
            if (jsonObject.has("mensaje")) {

                JSONArray jsonArray = jsonObject.getJSONArray("datos");

                mReservas = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Reserva reserva = Reserva.mapper(o, Reserva.HISTORIAL_TOTAL);

                    mReservas.add(reserva);

                }

                mMenu = Menu.mapper(jsonObject.getJSONArray("mensaje").getJSONObject(0), Menu.COMPLETE);
                mAdapter = new ReservasAdapter(mReservas, getApplicationContext(), ReservasAdapter.ADMIN);
                mRecyclerView.setAdapter(mAdapter);
                if (mMenu != null) {
                    txtPlato.setText(mMenu.getDescripcion());
                    txtPorciones.setText(String.valueOf(mMenu.getPorcion()));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void loadViews() {
        btnBack = findViewById(R.id.imgFlecha);
        txtPlato = findViewById(R.id.txtPlato);
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
