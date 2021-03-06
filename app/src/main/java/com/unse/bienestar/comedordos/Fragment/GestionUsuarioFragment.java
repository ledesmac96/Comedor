package com.unse.bienestar.comedordos.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unse.bienestar.comedordos.Activity.NuevoAlumnoActivity;
import com.unse.bienestar.comedordos.Activity.PerfilActivity;
import com.unse.bienestar.comedordos.Adapter.UsuariosAdapter;
import com.unse.bienestar.comedordos.Database.RolViewModel;
import com.unse.bienestar.comedordos.Dialogos.DialogoOpciones;
import com.unse.bienestar.comedordos.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedordos.Modelo.Alumno;
import com.unse.bienestar.comedordos.Modelo.Opciones;
import com.unse.bienestar.comedordos.Modelo.Usuario;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.RecyclerListener.ItemClickSupport;
import com.unse.bienestar.comedordos.Utils.GeneratePDFTask;
import com.unse.bienestar.comedordos.Utils.OnClickOptionListener;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GestionUsuarioFragment extends Fragment implements View.OnClickListener {

    View view;
    Activity mActivity;
    FloatingActionButton fabAdd, fabPDF;
    ArrayList<Usuario> mUsuarios;
    RecyclerView.LayoutManager mLayoutManager;
    DialogoProcesamiento dialog;
    LinearLayout latVacio;
    RecyclerView recyclerUsuarios;
    UsuariosAdapter mUsuariosAdapter;
    FragmentManager mFragmentManager;
    ProgressBar mProgressBar;
    Context mContext;
    EditText edtBuscar;
    RolViewModel mRolViewModel;

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
        fabPDF.setOnClickListener(this);
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
        fabPDF = view.findViewById(R.id.fabPDF);
        latVacio = view.findViewById(R.id.latVacio);
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
            mUsuariosAdapter.filtrar(txt, ABC.LIST_DNI);
            return;
        } else {
            pattern = Pattern.compile("[a-zA-Z_ ]+");
            matcher = pattern.matcher(txt);
            if (matcher.find()) {
                mUsuariosAdapter.filtrar(txt, ABC.LIST_NOMBRE);
                return;
            } else {
                mUsuariosAdapter.filtrar(txt, ABC.LIST_RESET);
                return;
            }
        }

    }

    @Nullable
    public Activity getFActivity() {
        return mActivity;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(mContext);
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?id=%s&key=%s", ABC.URL_USUARIOS_LISTA, id, key);
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

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Usuario usuario = Usuario.mapper(o, Usuario.BASIC);

                    mUsuarios.add(usuario);

                }
                mUsuariosAdapter.change(mUsuarios);
                mUsuariosAdapter.notifyDataSetChanged();
            } else {
                latVacio.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadData() {
        mRolViewModel = new RolViewModel(getContext());
        latVacio.setVisibility(View.GONE);
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
        String key = manager.getValueString(ABC.TOKEN);
        int idLocal = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?key=%s&id=%s&idU=%s", ABC.URL_USUARIO_BY_ID, key, idLocal, usuario.getIdUsuario());
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuestaUsuario(response);


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
                    ABC.showToast(getContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    loadInfoUsuario(jsonObject);
                    break;
                case 2:
                    ABC.showToast(getContext(), getString(R.string.noData));
                    break;
                case 3:
                    ABC.showToast(getContext(), getString(R.string.tokenInvalido));
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

    private void loadInfoUsuario(JSONObject jsonObject) {
        Usuario usuario = Usuario.mapper(jsonObject, Usuario.COMPLETE);
        Alumno alumno = Alumno.mapper(jsonObject, usuario);
        Intent i = new Intent(getContext(), PerfilActivity.class);
        i.putExtra(ABC.IS_ADMIN_MODE, true);
        i.putExtra(ABC.USER_INFO, alumno.getCarrera() != null ? alumno : usuario);
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
            case R.id.fabPDF:
                if (mUsuarios != null && mUsuarios.size() > 0) {
                    if (ABC.isPermissionGranted(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (ABC.isPermissionGranted(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            ArrayList<Opciones> opciones = new ArrayList<>();
                            opciones.add(new Opciones("TODOS LOS USUARIOS"));
                            opciones.add(new Opciones("INCLUIR INACTIVOS"));
                            DialogoOpciones dialogoOpciones = new DialogoOpciones(new OnClickOptionListener() {
                                @Override
                                public void onClick(int pos) {
                                    procesarClick(pos);

                                }
                            }, opciones, getContext());
                            dialogoOpciones.show(getManagerFragment(), "opciones");
                        } else {
                            showPermission();
                        }

                    } else {
                        showPermission();
                    }
                } else {
                    ABC.showToast(getContext(), getString(R.string.noUsuarios));
                }

                break;

        }
    }

    private void showPermission() {
        ActivityCompat.requestPermissions(mActivity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private void procesarClick(int pos) {
        DialogoProcesamiento dialogoProcesamiento = new DialogoProcesamiento();
        ArrayList<Usuario> usuarios = new ArrayList<>();
        if (pos == 0) {
            for (Usuario usuario : mUsuarios) {
                if (usuario.getValidez() == 1)
                    usuarios.add(usuario);
            }
            new GeneratePDFTask(1, usuarios, dialogoProcesamiento, getContext()).execute();
        } else {
            new GeneratePDFTask(1, mUsuarios, dialogoProcesamiento, getContext()).execute();
        }
        dialogoProcesamiento.show(getManagerFragment(), "jeje");

    }
}
