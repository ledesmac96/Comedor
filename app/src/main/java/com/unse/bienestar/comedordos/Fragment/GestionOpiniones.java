package com.unse.bienestar.comedordos.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unse.bienestar.comedordos.Adapter.OpinionesAdapter;
import com.unse.bienestar.comedordos.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedordos.Modelo.Sugerencia;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GestionOpiniones extends Fragment {

    View view;
    RecyclerView.LayoutManager mLayoutManager;
    FragmentManager mFragmentManager;
    LinearLayout latVacio;
    DialogoProcesamiento dialog;
    TextView txtTitulo;
    ArrayList<Sugerencia> mSugerencias;
    RecyclerView recyclerOpiniones;
    OpinionesAdapter mOpinionesAdapter;
    ProgressBar mProgressBar;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_gestion_opiniones, container, false);

        loadViews();

        loadData();

        loadListener();

        return view;
    }


    private void loadListener() {

    }

    private void loadViews() {
        txtTitulo = view.findViewById(R.id.txtTitulo);
        latVacio = view.findViewById(R.id.latVacio);
        recyclerOpiniones = view.findViewById(R.id.recycler);
        mProgressBar = view.findViewById(R.id.progress_bar);
    }


    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(mContext);
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s", ABC.URL_SUGERENCIA_LISTA, id, key);
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
                ABC.showToast(mContext, getString(R.string.servidorOff));
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
            mProgressBar.setVisibility(View.GONE);
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    latVacio.setVisibility(View.VISIBLE);
                    ABC.showToast(getContext(), getString(R.string.errorInternoAdmin));
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
                    ABC.showToast(getContext(), getString(R.string.tokenInvalido));
                    break;
                case 100:
                    //No autorizado
                    latVacio.setVisibility(View.VISIBLE);
                    ABC.showToast(getContext(), getString(R.string.tokenInexistente));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            latVacio.setVisibility(View.VISIBLE);
            ABC.showToast(getContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private void loadInfo(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje")) {

                JSONArray jsonArray = jsonObject.getJSONArray("mensaje");

                mSugerencias = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Sugerencia sugerencia = Sugerencia.mapper(o, Sugerencia.MEDIUM);

                    mSugerencias.add(sugerencia);

                }
                mOpinionesAdapter.change(mSugerencias);
                mOpinionesAdapter.notifyDataSetChanged();
                txtTitulo.setVisibility(View.VISIBLE);
            } else {
                latVacio.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadData() {
        txtTitulo.setVisibility(View.GONE);
        latVacio.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mSugerencias = new ArrayList<>();
        mOpinionesAdapter = new OpinionesAdapter(mSugerencias, getContext());
        mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerOpiniones.setNestedScrollingEnabled(true);
        recyclerOpiniones.setLayoutManager(mLayoutManager);
        recyclerOpiniones.setAdapter(mOpinionesAdapter);

        loadInfo();

    }

    @Override
    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Nullable
    public FragmentManager getManagerFragment() {
        return mFragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }
}