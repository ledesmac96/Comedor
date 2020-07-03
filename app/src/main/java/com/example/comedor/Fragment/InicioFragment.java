package com.example.comedor.Fragment;

import android.app.Activity;
import android.content.Intent;
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
import com.example.comedor.Activity.InfoReservaActivity;
import com.example.comedor.Activity.MainActivity;
import com.example.comedor.Database.MenuViewModel;
import com.example.comedor.Dialogos.DialogoGeneral;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.Menu;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.VolleySingleton;
import com.example.comedor.Utils.YesNoDialogListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class InicioFragment extends Fragment implements View.OnClickListener {

    View view;
    ProgressBar mProgressBar;
    LinearLayout latAdmin;
    CardView cardInicio, cardReservar, cardNo, cardScanear, cardTerminar;
    DialogoProcesamiento dialog;
    Menu mMenu;
    PreferenciasManager mPreferenciasManager;
    MenuViewModel mMenuViewModel;
    Activity mActivity;

    public InicioFragment() {
        // Metodo necesario
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_inicio, container, false);

        loadViews();

        loadData();

        loadListener();


        return view;
    }

    private void loadListener() {
        cardReservar.setOnClickListener(this);
        cardScanear.setOnClickListener(this);
        cardTerminar.setOnClickListener(this);
    }

    private void loadViews() {
        latAdmin = view.findViewById(R.id.latAdmin);
        cardScanear = view.findViewById(R.id.cardScan);
        cardTerminar = view.findViewById(R.id.cardTerminar);
        cardNo = view.findViewById(R.id.cardNo);
        mProgressBar = view.findViewById(R.id.progress_bar);
        cardInicio = view.findViewById(R.id.card_one);
        cardReservar = view.findViewById(R.id.cardReservar);

    }

    private void loadData() {
        mPreferenciasManager = new PreferenciasManager(getContext());
        mMenuViewModel = new MenuViewModel(getContext());
        if (true) {
            latAdmin.setVisibility(View.VISIBLE);
        }
        cardInicio.setVisibility(View.GONE);
        cardReservar.setVisibility(View.GONE);
        cardNo.setVisibility(View.GONE);
        loadInfo();

    }

    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(getContext());
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&d=%s", Utils.URL_MENU_HOY, id, key, 1);
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
                loadInternal();
                //Utils.showToast(getContext(), getString(R.string.servidorOff));
                dialog.dismiss();


            }
        });
        //Abro dialogo para congelar pantalla
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "dialog_process");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void loadInternal() {
        int id = mPreferenciasManager.getValueInt(Utils.ID_MENU);
        if (id != 0) {
            mMenu = mMenuViewModel.getAllByMenuID(id);
            loadMenu(mMenu);
        } else {
            cardNo.setVisibility(View.VISIBLE);
            cardReservar.setVisibility(View.GONE);
        }
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
                    cardNo.setVisibility(View.VISIBLE);
                    cardReservar.setVisibility(View.GONE);
                    break;
                case 1:
                    //Exito

                    loadInfo(jsonObject);
                    break;
                case 2:
                    cardNo.setVisibility(View.VISIBLE);
                    cardReservar.setVisibility(View.GONE);
                    break;
                case 3:
                    Utils.showToast(getContext(), getString(R.string.tokenInvalido));
                    cardNo.setVisibility(View.VISIBLE);
                    cardReservar.setVisibility(View.GONE);
                    break;
                case 100:
                    //No autorizado
                    Utils.showToast(getContext(), getString(R.string.tokenInexistente));
                    cardNo.setVisibility(View.VISIBLE);
                    cardReservar.setVisibility(View.GONE);
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

                Menu menu = Menu.mapper(jsonObject.getJSONArray("mensaje").getJSONObject(0), Menu.COMPLETE);

                loadMenu(menu);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadMenu(Menu menu) {
        if (menu != null) {
            if (mMenuViewModel.getAllByMenuID(menu.getIdMenu()) == null)
                mMenuViewModel.insert(menu);
            mMenu = menu;
            String hora = Utils.getHora(new Date(System.currentTimeMillis()));
            hora = hora.substring(0,hora.indexOf(":"));
            int max = 24;
            int horaNum = 15;
            try {
                horaNum = Integer.parseInt(hora);
            } catch (NumberFormatException e) {

            }
            if (mMenu.getDisponible() == 1 && (horaNum < max && horaNum > 0)) {
                cardInicio.setVisibility(View.VISIBLE);
                TextView txtFecha = view.findViewById(R.id.txtFecha);
                TextView txtPlato = view.findViewById(R.id.txtPlato);
                TextView txtPostre = view.findViewById(R.id.txtPostre);

                mPreferenciasManager.setValue(Utils.ID_MENU, mMenu.getIdMenu());

                txtFecha.setText(Utils.getDate(menu.getDia(), menu.getMes(), menu.getAnio()));
                String[] comida = Utils.getComidas(menu.getDescripcion());
                String food = comida[0] + " " + comida[1];
                txtPlato.setText(food.length() > 20 ? food.substring(20) : food);
                txtPostre.setText(comida[2]);
                cardReservar.setVisibility(View.VISIBLE);
            } else {
                cardNo.setVisibility(View.VISIBLE);
                cardReservar.setVisibility(View.GONE);
            }

        } else {
            cardNo.setVisibility(View.VISIBLE);
            cardReservar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardReservar:
                openInfo();
                break;
            case R.id.cardScan:
                scanQR();
                break;
            case R.id.cardTerminar:
                terminar();
                break;
        }
    }

    private void terminar() {
    }



    private void changeEstado(String idReserva) {
        PreferenciasManager manager = new PreferenciasManager(getContext());
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&idr=%s&ie=%s&e=%s", Utils.URL_RESERVA_ACTUALIZAR, id, key, idReserva, id, 3);
        StringRequest request = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuestaActualizar(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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

    private void procesarRespuestaActualizar(String response) {
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
                    dialogo(true);
                    break;
                case 2:
                    Utils.showToast(getContext(), getString(R.string.errorActualizar));
                    dialogo(false);
                    break;
                case 4:
                    Utils.showToast(getContext(), getString(R.string.camposInvalidos));
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

    private void dialogo(boolean estado) {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getContext())
                .setTitulo(getString(estado ? R.string.confirmado : R.string.noConfirmado))
                .setDescripcion(getString(estado ? R.string.confirmadoReserva : R.string.confirmadoReservaError))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {

                    }

                    @Override
                    public void no() {

                    }
                })
                .setIcono(R.drawable.ic_advertencia)
                .setTipo(DialogoGeneral.TIPO_ACEPTAR);
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.show(getFragmentManager(), "dialog_ad");
    }

    public void scanQR() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(mActivity);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt("Scan");
        intentIntegrator.setCameraId(0);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }


    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    private void openInfo() {
        Intent intent = new Intent(getContext(), InfoReservaActivity.class);
        intent.putExtra(Utils.DATA_RESERVA, mMenu);
        startActivity(intent);
    }
}
