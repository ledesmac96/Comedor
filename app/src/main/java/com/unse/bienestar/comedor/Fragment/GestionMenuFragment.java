package com.unse.bienestar.comedor.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unse.bienestar.comedor.Activity.NuevoMenuActivity;
import com.unse.bienestar.comedor.Adapter.MenuAdapter;
import com.unse.bienestar.comedor.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedor.Modelo.Menu;
import com.bienestar.comedor.R;
import com.unse.bienestar.comedor.Utils.PreferenciasManager;
import com.unse.bienestar.comedor.Utils.Utils;
import com.unse.bienestar.comedor.Utils.VolleySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GestionMenuFragment extends Fragment implements View.OnClickListener {

    FragmentManager mFragmentManager;
    CardView mCardView;
    ProgressBar mProgressBar;
    LinearLayout latNoData;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    MenuAdapter mMenuAdapter;
    Context mContext;
    DialogoProcesamiento dialog;
    FloatingActionButton fabAdd;
    ArrayList<Menu> mMenus;
    View view;
    ArrayAdapter<String> mesesAdapter;
    Spinner mSpinnerMeses;
    String[] meses = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
            "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    Button btnBuscar;
    TextView txtMes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_gestion_menu, container, false);

        loadView();

        loadData();

        loadListener();


        return view;
    }

    private void loadData() {
        mCardView.setVisibility(View.GONE);
        mMenus = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mProgressBar.setVisibility(View.GONE);
        latNoData.setVisibility(View.GONE);
        mesesAdapter = new ArrayAdapter<String>(getContext(), R.layout.style_spinner, meses);
        mesesAdapter.setDropDownViewResource(R.layout.style_spinner);
        mSpinnerMeses.setAdapter(mesesAdapter);
    }

    private void loadListener() {
        fabAdd.setOnClickListener(this);
        btnBuscar.setOnClickListener(this);
    }

    private void loadView() {
        txtMes = view.findViewById(R.id.txtFecha);
        mCardView = view.findViewById(R.id.cardFecha);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mRecyclerView = view.findViewById(R.id.recycler);
        latNoData = view.findViewById(R.id.latNoData);
        btnBuscar = view.findViewById(R.id.btnBuscar);
        fabAdd = view.findViewById(R.id.fabAdd);
        mSpinnerMeses = view.findViewById(R.id.spinner1);
    }

    private void loadInfo() {
        mMenus.clear();
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mCardView.setVisibility(View.GONE);
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
                mProgressBar.setVisibility(View.GONE);
                latNoData.setVisibility(View.VISIBLE);
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
            mCardView.setVisibility(View.VISIBLE);
            txtMes.setText(meses[mSpinnerMeses.getSelectedItemPosition()]);
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
                    Utils.showToast(getContext(), getString(R.string.noMenu));
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

                    Menu menu = Menu.mapper(o, Menu.COMPLETE);

                    mMenus.add(menu);

                }
                if (mMenus.size() > 0) {
                    mMenuAdapter = new MenuAdapter(mContext, mMenus);
                    mRecyclerView.setAdapter(mMenuAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    latNoData.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        switch (v.getId()) {
            case R.id.fabAdd:
                startActivity(new Intent(getContext(), NuevoMenuActivity.class));
                break;
            case R.id.btnBuscar:
                loadInfo();
                break;
        }

    }
}
