package com.example.comedor.Fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.comedor.Activity.AddAlumnoActivity;
import com.example.comedor.Activity.PerfilReservaActivity;
import com.example.comedor.Activity.ReservaActivity;
import com.example.comedor.Adapter.OpcionesAdapter;
import com.example.comedor.Adapter.ReservasAdapter;
import com.example.comedor.Adapter.UsuariosAdapter;
import com.example.comedor.Modelo.Opciones;
import com.example.comedor.Modelo.Reserva;
import com.example.comedor.R;
import com.example.comedor.RecyclerListener.ItemClickSupport;
import com.example.comedor.Utils.Utils;

import java.util.ArrayList;

public class MisReservasFragment extends Fragment {

    View view;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ReservasAdapter mReservasAdapter;
    ArrayList<Reserva> mReservas;
    FragmentManager mFragmentManager;
    ProgressBar mProgressBar;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_mis_reservas, container, false);

        loadViews();

        loadListener();

        loadData();

        return view;
    }


    private void loadListener() {


    }

    private void loadData() {
        mReservas = new ArrayList<>();

        mProgressBar.setVisibility(View.VISIBLE);
        mReservasAdapter = new ReservasAdapter(mReservas, getContext());
        mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mReservasAdapter);

        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Intent i = new Intent(getContext(), PerfilReservaActivity.class);
                i.putExtra(Utils.RESERVA, mReservas.get(position));
                startActivity(i);
            }
        });

    }

    private void loadViews() {
        mProgressBar = view.findViewById(R.id.progress_bar);
        mRecyclerView = view.findViewById(R.id.recycler);

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

}
