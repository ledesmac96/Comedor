package com.example.comedor.Activity;

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

public class ReservaDiaActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtDescripcion;
    DialogoProcesamiento dialog;
    Menu mMenus;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ReservasAdapter mReservasAdapter;
    ArrayList<Reserva> mReservas;
    ImageView imgIcono;
    ProgressBar mProgressBar;

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
        Utils.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
    }

    private void loadData() {
        mReservas = new ArrayList<>();

        mProgressBar.setVisibility(View.VISIBLE);
        mReservasAdapter = new ReservasAdapter(mReservas, getApplicationContext());
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mReservasAdapter);

        loadInfo();

        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), InfoReservaActivity.class);
                i.putExtra(Utils.RESERVA, mReservas.get(position));
                startActivity(i);
            }
        });

    }

    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&m=%s", Utils.URL_RESERVA_HOY, id, key, 1);
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
                //Utils.showToast(getApplicationContext(), getString(R.string.servidorOff));
                Utils.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
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
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    //Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    Utils.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    mProgressBar.setVisibility(View.GONE);
                    loadInfo(jsonObject);
                    break;
                case 2:
                    //Utils.showToast(getApplicationContext(), getString(R.string.noReservas));
                    Utils.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.noReservas), R.drawable.ic_error);
                    break;
                case 4:
                    //Utils.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
                    Utils.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_error);
                    break;
                case 3:
                    //Utils.showToast(getApplicationContext(), getString(R.string.tokenInvalido));
                    Utils.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    //Utils.showToast(getApplicationContext(), getString(R.string.tokenInexistente));
                    Utils.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
            Utils.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje") && jsonObject.has("datos")) {

                mMenus = Menu.mapper(jsonObject.getJSONArray("mensaje").getJSONObject(0), Menu.COMPLETE);

                //mReservas = new ArrayList<>();

                JSONArray jsonArray = jsonObject.getJSONArray("datos");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Reserva reserva = Reserva.mapper(o, Reserva.MEDIUM);

                    mReservas.add(reserva);

                }
                //Utils.showToast(getApplicationContext(), "HAY " + mReservas.size());
                Utils.showCustomToast(ReservaDiaActivity.this, getApplicationContext(),
                        "HAY " + mReservas.size(), R.drawable.ic_exito);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadView() {
        txtDescripcion = findViewById(R.id.txtDescripcion);
        imgIcono = findViewById(R.id.imgFlecha);
        mProgressBar = findViewById(R.id.progress_bar);
        mRecyclerView = findViewById(R.id.recycler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgFlecha:
                onBackPressed();
                break;
        }
    }
}
