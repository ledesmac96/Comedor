package com.example.comedor.Fragment;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.comedor.Activity.AddAlumnoActivity;
import com.example.comedor.Adapter.UsuariosAdapter;
import com.example.comedor.Modelo.Usuario;
import com.example.comedor.R;
import com.example.comedor.RecyclerListener.ItemClickSupport;
import com.example.comedor.Utils.Utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GestionUsuarioFragment extends Fragment implements View.OnClickListener {

    View view;
    CardView cardAddClient;
    ArrayList<Usuario> mUsuarios;
    RecyclerView.LayoutManager mLayoutManager;
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
        cardAddClient.setOnClickListener(this);
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
        cardAddClient = view.findViewById(R.id.cardAddAlumno);
        mProgressBar = view.findViewById(R.id.progress_bar);
    }

    private void buscar(String txt) {
        Pattern pattern;
        pattern = Pattern.compile("([0-9]+){1,8}");
        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()) {
            mUsuariosAdapter.filtrar(txt, Utils.LIST_DNI);
            return;
        }else{
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

    private void loadData() {
        mUsuarios = new ArrayList<>();

        mProgressBar.setVisibility(View.VISIBLE);
        mUsuariosAdapter = new UsuariosAdapter(mUsuarios, getContext());
        mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerUsuarios.setNestedScrollingEnabled(true);
        recyclerUsuarios.setLayoutManager(mLayoutManager);
        recyclerUsuarios.setAdapter(mUsuariosAdapter);

//        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerUsuarios);
//        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//            @Override
//            public void onItemClick(RecyclerView parent, View view, int position, long id) {
//                Intent i = new Intent(getContext(), InfoUsuarioActivity.class);
//                i.putExtra(Utils.USER_INFO, mUsuarios.get(position));
//                startActivity(i);
//            }
//        });


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
            case R.id.cardAddAlumno:
                Intent i = new Intent(getContext(), AddAlumnoActivity.class);
                startActivity(i);
                break;

        }
    }
}
