package com.unse.bienestar.comedor.Fragment;

import android.app.Activity;
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
import com.unse.bienestar.comedor.Database.MenuViewModel;
import com.unse.bienestar.comedor.Database.ReservaViewModel;
import com.unse.bienestar.comedor.Database.RolViewModel;
import com.unse.bienestar.comedor.Dialogos.DialogoGeneral;
import com.unse.bienestar.comedor.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedor.Modelo.Menu;
import com.unse.bienestar.comedor.Modelo.Reserva;
import com.unse.bienestar.comedor.Modelo.Rol;
import com.unse.bienestar.comedor.R;
import com.unse.bienestar.comedor.Utils.PreferenciasManager;
import com.unse.bienestar.comedor.Utils.Utils;
import com.unse.bienestar.comedor.Utils.VolleySingleton;
import com.unse.bienestar.comedor.Utils.YesNoDialogListener;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONException;
import org.json.JSONObject;

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
    RolViewModel mRolViewModel;
    ReservaViewModel mReservaViewModel;
    boolean isAdmin = false;

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
        mRolViewModel = new RolViewModel(getContext());
        mReservaViewModel = new ReservaViewModel(getContext());
        mPreferenciasManager = new PreferenciasManager(getContext());
        mMenuViewModel = new MenuViewModel(getContext());
        cardInicio.setVisibility(View.GONE);
        cardReservar.setVisibility(View.GONE);
        cardNo.setVisibility(View.GONE);
        loadAdmin();
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
            mMenu = mMenuViewModel.getByMenuID(id);
            loadMenu(mMenu);
        } else {
            cardNo.setVisibility(View.VISIBLE);
            cardReservar.setVisibility(View.GONE);
        }
    }

    private void loadAdmin() {
        Rol rol = mRolViewModel.getByPermission(402);
        if (rol != null) {
            latAdmin.setVisibility(View.VISIBLE);
            //cardReservar.setVisibility(View.GONE);
            isAdmin = true;
        } else {
            isAdmin = false;
            latAdmin.setVisibility(View.GONE);
            //cardReservar.setVisibility(View.VISIBLE);
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
            cardNo.setVisibility(View.VISIBLE);
            cardReservar.setVisibility(View.GONE);
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
            Menu exist = mMenuViewModel.getByMenuID(menu.getIdMenu());
            if (exist == null)
                mMenuViewModel.insert(menu);
            mMenu = menu;
            if (mMenu.getDisponible() == 1 && Utils.isShowByHour()) {

                TextView txtFecha = view.findViewById(R.id.txtFecha);
                TextView txtPlato = view.findViewById(R.id.txtPlato);
                TextView txtPostre = view.findViewById(R.id.txtPostre);

                mPreferenciasManager.setValue(Utils.ID_MENU, mMenu.getIdMenu());

                txtFecha.setText(Utils.getDate(menu.getDia(), menu.getMes(), menu.getAnio()));
                String[] comida = Utils.getComidas(menu.getDescripcion());
                String food = String.format("%s %s", comida[0], comida[1]);
                txtPlato.setText(food.length() > 20 ? food.substring(20) : food);
                txtPostre.setText(comida[2]);

                cardReservar.setVisibility(View.VISIBLE);
                cardInicio.setVisibility(View.VISIBLE);

            } else {
                cardNo.setVisibility(View.VISIBLE);
                cardReservar.setVisibility(View.GONE);
            }
            if (isAdmin) updateButton(mMenu.getDisponible() == 1);


        } else {
            cardNo.setVisibility(View.VISIBLE);
            cardReservar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardReservar:
                reservarDialogo();
                break;
            case R.id.cardScan:
                scanQR();
                break;
            case R.id.cardTerminar:
                terminarDialogo();
                break;
        }
    }

    private void terminarDialogo() {
        boolean b = mMenu.getDisponible() == 1;
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getContext())
                .setTitulo(getString(R.string.advertencia))
                .setDescripcion(getString(b ? R.string.finalizarMenu : R.string.rehabilitarMenu))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        terminar();
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

    private void reservarDialogo() {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getContext())
                .setTitulo(getString(R.string.advertencia))
                .setDescripcion(String.format(getString(R.string.reservaInfo), mMenu.getDia(),
                        mMenu.getMes(), mMenu.getAnio()))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        reservarMenu();

                    }

                    @Override
                    public void no() {

                    }
                })
                .setIcono(R.drawable.ic_advertencia)
                .setTipo(DialogoGeneral.TIPO_SI_NO);
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.show(getFragmentManager(), "dialog_ad");
    }

    private void reservarMenu() {
        PreferenciasManager manager = new PreferenciasManager(getContext());
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&i=%s&im=%s&t=%s", Utils.URL_RESERVA_INSERTAR, id, key, id,
                mMenu.getIdMenu(), 4);
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuestaReserva(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialogReserva(false, null);
                dialog.dismiss();

            }
        });
        //Abro dialogo para congelar pantalla
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "dialog_process");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void procesarRespuestaReserva(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
                    dialogReserva(false, null);
                    break;
                case 1:
                    loadInfoReserva(jsonObject);
                    break;
                case 2:
                    Utils.showToast(getContext(), getString(R.string.reservaNoExiste));
                    dialogReserva(false, null);
                    break;
                case 3:
                    Utils.showToast(getContext(), getString(R.string.tokenInvalido));
                    dialogReserva(false, null);
                    break;
                case 5:
                    yaExisteDialogo();
                    break;
                case 100:
                    //No autorizado
                    Utils.showToast(getContext(), getString(R.string.tokenInexistente));
                    dialogReserva(false, null);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private void yaExisteDialogo() {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getContext())
                .setTitulo(getString(R.string.yaReservo))
                .setDescripcion(getString(R.string.dirigeReserva))
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

    private void loadInfoReserva(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje") && jsonObject.has("dato")) {

                Reserva reserva = Reserva.mapper(jsonObject.getJSONObject("dato"), Reserva.COMPLETE);

                Reserva existe = mReservaViewModel.getByReservaID(reserva.getIdReserva());
                if (existe == null) {
                    mReservaViewModel.insert(reserva);
                }

                dialogReserva(true, reserva);
            } else {
                dialogReserva(false, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialogReserva(false, null);
        }

    }

    private void dialogReserva(boolean b, Reserva reserva) {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getContext())
                .setTitulo(getString(b ? R.string.reservado : R.string.salioMal))
                .setDescripcion(b ? String.format(getString(R.string.reservaExito), String.valueOf(reserva.getIdReserva()))
                        : getString(R.string.reservaError))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {

                    }

                    @Override
                    public void no() {

                    }
                })
                .setIcono(b ? R.drawable.ic_exito : R.drawable.ic_advertencia)
                .setTipo(DialogoGeneral.TIPO_ACEPTAR);
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.show(getFragmentManager(), "dialog_ad");
    }

    private void terminar() {
        PreferenciasManager manager = new PreferenciasManager(getContext());
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&idM=%s&val=%s", Utils.URL_MENU_TERMINAR, id, key, mMenu.getIdMenu(), mMenu.getDisponible() == 1 ? 0 : 1);
        StringRequest request = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuestaTerminar(response);


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

    private void procesarRespuestaTerminar(String response) {
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
                    String mensaje = jsonObject.getString("mensaje");
                    if (mensaje.contains("habilitado")) {
                        updateButton(true);
                        mMenu.setDisponible(1);
                    } else if (mensaje.contains("terminado")) {
                        updateButton(false);
                        mMenu.setDisponible(0);
                    }
                    mMenuViewModel.update(mMenu);
                    break;
                case 2:
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

    private void updateButton(boolean b) {
        ((TextView) view.findViewById(R.id.txtTerminar)).setText(b ? "FINALIZAR DIA" : "HABILITAR DIA");
        cardInicio.setEnabled(b);
        if (b) {
            cardInicio.setAlpha(1);
        } else {
            cardInicio.setAlpha(0.5f);
        }
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

}