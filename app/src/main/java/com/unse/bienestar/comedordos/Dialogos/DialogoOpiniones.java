package com.unse.bienestar.comedordos.Dialogos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.Validador;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class DialogoOpiniones extends DialogFragment implements View.OnClickListener {

    View view;
    DialogoProcesamiento dialog;
    EditText edtDescripcion;
    Button btnEnviar;
    Context mContext;
    FragmentManager mFragmentManager;

    @Nullable
    @Override
    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }


    public FragmentManager getManagerFragment() {
        return mFragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.dialog_sugerencia, container, false);
       getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Esto es lo nuevoooooooo, evita los bordes cuadrados
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        loadViews();

        loadListener();

        return view;
    }

    private void loadViews() {
        edtDescripcion = view.findViewById(R.id.edtDescripcion);
        btnEnviar = view.findViewById(R.id.btnAceptar);
    }

    private void loadData() {
    }


    private void loadListener() {
        btnEnviar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnviar:
                checkOpinion();
                break;
        }
    }

    private void checkOpinion() {
        String descripcion = edtDescripcion.getText().toString();
        Validador validador = new Validador(getContext());
        if (validador.validarNombres(edtDescripcion)) {
            sendOpinion(descripcion);
        } else {
            ABC.showToast(getContext(), getString(R.string.camposInvalidos));
        }
    }

    private void sendOpinion(String descripcion) {
        PreferenciasManager manager = new PreferenciasManager(getContext());
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&iu=%s&d=%s", ABC.URL_SUGERENCIA_INSERTAR,
                id, key, id, descripcion);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ABC.showToast(getContext(), getString(R.string.servidorOff));
                dialog.dismiss();

            }
        });
        //Abro dialogo para congelar pantalla
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(getManagerFragment(), "dialog_process");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void procesarRespuesta(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    ABC.showToast(getContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    ABC.showToast(getContext(), getString(R.string.enviado));
                    dismiss();
                    break;
                case 2:
                    ABC.showToast(getContext(), getString(R.string.errorSugerencia));
                    break;
                case 3:
                    ABC.showToast(getContext(), getString(R.string.tokenInvalido));
                    break;
                case 4:
                    ABC.showToast(getContext(), getString(R.string.camposInvalidos));
                    break;
                case 100:
                    //No autorizado
                    ABC.showToast(getContext(), getString(R.string.tokenInexistente));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showToast(getContext(), getString(R.string.errorInternoAdmin));
        }
    }
}
