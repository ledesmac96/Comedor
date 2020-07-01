package com.example.comedor.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Activity.NuevoAlumnoActivity;
import com.example.comedor.Activity.PerfilActivity;
import com.example.comedor.Adapter.UsuariosAdapter;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.Alumno;
import com.example.comedor.Modelo.Usuario;
import com.example.comedor.R;
import com.example.comedor.RecyclerListener.ItemClickSupport;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.VolleySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GestionUsuarioFragment extends Fragment implements View.OnClickListener {

    View view;
    FloatingActionButton fabAdd;
    ArrayList<Usuario> mUsuarios;
    RecyclerView.LayoutManager mLayoutManager;
    DialogoProcesamiento dialog;
    RecyclerView recyclerUsuarios;
    UsuariosAdapter mUsuariosAdapter;
    FragmentManager mFragmentManager;
    ProgressBar mProgressBar;
    Context mContext;
    EditText edtBuscar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_gestion_usuario, container, false);

        loadViews();

        loadData();

        loadListener();

        return view;
    }


    private void loadListener() {
        fabAdd.setOnClickListener(this);
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                buscar(s.toString());
            }
        });

    }

    private void loadViews() {
        edtBuscar = view.findViewById(R.id.edtBuscar);
        recyclerUsuarios = view.findViewById(R.id.recycler);
        fabAdd = view.findViewById(R.id.fabAdd);
        mProgressBar = view.findViewById(R.id.progress_bar);
    }

    private void buscar(String txt) {
        Pattern pattern;
        pattern = Pattern.compile("([0-9]+){1,8}");
        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()) {
            mUsuariosAdapter.filtrar(txt, Utils.LIST_DNI);
            return;
        } else {
            pattern = Pattern.compile("[a-zA-Z_ ]+");
            matcher = pattern.matcher(txt);
            if (matcher.find()) {
                mUsuariosAdapter.filtrar(txt, Utils.LIST_NOMBRE);
                return;
            } else {
                mUsuariosAdapter.filtrar(txt, Utils.LIST_RESET);
                return;
            }
        }

    }

    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(mContext);
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?id=%s&key=%s", Utils.URL_USUARIOS_LISTA, id, key);
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
                    mProgressBar.setVisibility(View.GONE);
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

                JSONArray jsonArray = jsonObject.getJSONArray("mensaje");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Usuario usuario = Usuario.mapper(o, Usuario.BASIC);

                    mUsuarios.add(usuario);

                }
                mUsuariosAdapter.change(mUsuarios);
                mUsuariosAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadData() {
        mUsuarios = new ArrayList<>();
        mProgressBar.setVisibility(View.VISIBLE);
        mUsuariosAdapter = new UsuariosAdapter(mUsuarios, getContext());
        mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerUsuarios.setNestedScrollingEnabled(true);
        recyclerUsuarios.setLayoutManager(mLayoutManager);
        recyclerUsuarios.setAdapter(mUsuariosAdapter);

        loadInfo();

        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerUsuarios);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                processClick(search(position, (int) id));
            }
        });


    }

    public Usuario search(int position, int id) {
        for (Usuario usuario : mUsuarios) {
            if (usuario.getIdUsuario() == id)
                return usuario;
        }
        return null;
    }

    private void processClick(Usuario usuario) {
        PreferenciasManager manager = new PreferenciasManager(getContext());
        String key = manager.getValueString(Utils.TOKEN);
        int idLocal = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?key=%s&id=%s&idU=%s", Utils.URL_USUARIO_BY_ID, key, idLocal, usuario.getIdUsuario());
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuestaUsuario(response);


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
        dialog.show(mFragmentManager, "dialog_process");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void procesarRespuestaUsuario(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    Utils.showToast(getContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    loadInfoUsuario(jsonObject);
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

    private void loadInfoUsuario(JSONObject jsonObject) {
        Usuario usuario = Usuario.mapper(jsonObject, Usuario.COMPLETE);
        Alumno alumno = Alumno.mapper(jsonObject, usuario);
        Intent i = new Intent(getContext(), PerfilActivity.class);
        i.putExtra(Utils.IS_ADMIN_MODE, true);
        i.putExtra(Utils.USER_INFO, alumno != null ? alumno : usuario);
        startActivity(i);

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
                Intent i = new Intent(getContext(), NuevoAlumnoActivity.class);
                startActivity(i);
                break;

        }
    }
}
