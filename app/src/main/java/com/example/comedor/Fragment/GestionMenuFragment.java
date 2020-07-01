package com.example.comedor.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Activity.NuevoMenuActivity;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.Menu;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class GestionMenuFragment extends Fragment implements View.OnClickListener {

    FragmentManager mFragmentManager;
    ProgressBar mProgressBar;
    Context mContext;
    DialogoProcesamiento dialog;
    ArrayList<Menu> mMenus;
    View view;
    ArrayAdapter<String> mesesAdapter;
    Spinner mSpinnerMeses;
    String[] meses = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
            "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    Button btnNuevo, btnBuscar;

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
        mesesAdapter = new ArrayAdapter<String>(getContext(), R.layout.style_spinner, meses);
        mesesAdapter.setDropDownViewResource(R.layout.style_spinner);
        mSpinnerMeses.setAdapter(mesesAdapter);
    }

    private void loadListener() {
        btnNuevo.setOnClickListener(this);
        btnBuscar.setOnClickListener(this);
    }

    private void loadView() {
        btnBuscar = view.findViewById(R.id.btnBuscar);
        btnNuevo = view.findViewById(R.id.btnGuardar);
        mSpinnerMeses = view.findViewById(R.id.spinner1);
    }

    private void loadInfo() {
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
            case R.id.btnGuardar:
                startActivity(new Intent(getContext(), NuevoMenuActivity.class));
                break;
            case R.id.btnBuscar:
                loadInfo();
                break;
        }

    }
}
