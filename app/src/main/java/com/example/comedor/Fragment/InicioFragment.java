package com.example.comedor.Fragment;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Activity.InfoReservaActivity;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.Menu;
import com.example.comedor.Modelo.Usuario;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class InicioFragment extends Fragment implements View.OnClickListener {

    View view;
    ProgressBar mProgressBar;
    CardView cardInicio;
    DialogoProcesamiento dialog;
    Menu mMenu;

    public InicioFragment() {
        // Metodo necesario
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_inicio, container, false);

        loadViews();

        loadData();

        return view;
    }

    private void loadViews() {
        mProgressBar = view.findViewById(R.id.progress_bar);
        cardInicio = view.findViewById(R.id.card_one);

    }

    private void loadData() {
        cardInicio.setVisibility(View.GONE);
        loadInfo();
        cardInicio.setOnClickListener(this);
    }

    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(getContext());
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&d=%s", Utils.URL_MENU_HOY, id, key,1);
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
                Utils.showToast(getContext(), getString(R.string.servidorOff));
                dialog.dismiss();

            }
        });
        //Abro dialogo para congelar pantalla
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "dialog_process");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void procesarRespuesta(String response) {
        try {
            mProgressBar.setVisibility(View.GONE);
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito

                    loadInfo(jsonObject);
                    break;
                case 2:
                    Utils.showToast(getContext(), getString(R.string.noData));
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

                Menu menu = Menu.mapper(jsonObject.getJSONArray("mensaje").getJSONObject(0),Menu.COMPLETE);

                loadMenu(menu);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadMenu(Menu menu) {
        if (menu != null){
            mMenu = menu;
            cardInicio.setVisibility(View.VISIBLE);
            TextView txtFecha = view.findViewById(R.id.txtFecha);
            TextView txtPlato = view.findViewById(R.id.txtPlato);
            TextView txtPostre = view.findViewById(R.id.txtPostre);

            txtFecha.setText(Utils.getDate(menu.getDia(), menu.getMes(), menu.getAnio()));
            String[] comida = Utils.getComidas(menu.getDescripcion());
            String food = comida[0] + " " + comida[1];
            txtPlato.setText(food.length() > 20 ? food.substring(20) : food);
            txtPostre.setText(comida[2]);

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_one:
                openInfo();
                break;
        }
    }

    private void openInfo() {
        Intent intent = new Intent(getContext(), InfoReservaActivity.class);
        intent.putExtra(Utils.DATA_RESERVA, mMenu);
        startActivity(intent);
    }
}
