package com.example.comedor.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Activity.InfoReservaActivity;
import com.example.comedor.Adapter.ReservasAdapter;
import com.example.comedor.Database.ReservaViewModel;
import com.example.comedor.Dialogos.DialogoProcesamiento;
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
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MisReservasFragment extends Fragment {

    View view;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ReservasAdapter mReservasAdapter;
    ArrayList<Reserva> mReservas;
    FragmentManager mFragmentManager;
    ProgressBar mProgressBar;
    Context mContext;
    DialogoProcesamiento dialog;
    LinearLayout latNoData;
    ReservaViewModel mReservaViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_mis_reservas, container, false);

        loadViews();

        loadListener();

        loadData();

        return view;
    }


    private void loadListener() {
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Intent i = new Intent(getContext(), InfoReservaActivity.class);
                i.putExtra(Utils.RESERVA, mReservas.get(position));
                startActivity(i);
            }
        });


    }

    private void loadData() {
        mReservas = new ArrayList<>();
        mProgressBar.setVisibility(View.VISIBLE);
        latNoData.setVisibility(View.GONE);
        mReservaViewModel = new ReservaViewModel(getContext());
        mReservasAdapter = new ReservasAdapter(mReservas, getContext(), ReservasAdapter.USER);
        mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mReservasAdapter);

        loadInfo();

    }

    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(mContext);
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&id=%s", Utils.URL_RESERVA_USUARIO, id, key, id);
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
                latNoData.setVisibility(View.VISIBLE);
                loadInternal();
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

    private void loadInternal() {
        List<Reserva> internal = mReservaViewModel.getAll();
        if (internal != null) {
            mReservas.clear();
            mReservas.addAll(internal);
        }
        if (mReservas.size() > 0){
            latNoData.setVisibility(View.GONE);
        }

    }

    private void procesarRespuesta(String response) {
        try {
            dialog.dismiss();
            mProgressBar.setVisibility(View.GONE);
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    latNoData.setVisibility(View.VISIBLE);
                    Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    latNoData.setVisibility(View.GONE);
                    loadInfo(jsonObject);
                    break;
                case 2:
                    latNoData.setVisibility(View.VISIBLE);
                    Utils.showToast(getContext(), getString(R.string.noReservasUser));
                    break;
                case 3:
                    latNoData.setVisibility(View.VISIBLE);
                    Utils.showToast(getContext(), getString(R.string.tokenInvalido));
                    break;
                case 100:
                    latNoData.setVisibility(View.VISIBLE);
                    //No autorizado
                    Utils.showToast(getContext(), getString(R.string.tokenInexistente));
                    break;
            }

        } catch (JSONException e) {
            latNoData.setVisibility(View.VISIBLE);
            e.printStackTrace();
            Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje")) {

                JSONArray jsonArray = jsonObject.getJSONArray("mensaje");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Reserva reserva = Reserva.mapper(o, Reserva.HISTORIAL);

                    mReservas.add(reserva);

                }
                if (mReservas.size() > 0) {
                    Collections.reverse(mReservas);
                    mReservasAdapter.change(mReservas);
                    mReservasAdapter.notifyDataSetChanged();
                    saveInfo();
                } else {
                    latNoData.setVisibility(View.VISIBLE);
                }

            } else {
                latNoData.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void saveInfo() {
        for (Reserva reserva : mReservas) {
            Reserva exist = mReservaViewModel.getByReservaID(reserva.getIdReserva());
            if (exist == null) {
                mReservaViewModel.insert(reserva);
            }
        }
    }

    private void loadViews() {
        mProgressBar = view.findViewById(R.id.progress_bar);
        mRecyclerView = view.findViewById(R.id.recycler);
        latNoData = view.findViewById(R.id.latNoData);

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

}
