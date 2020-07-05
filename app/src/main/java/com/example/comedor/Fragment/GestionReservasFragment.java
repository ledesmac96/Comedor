package com.example.comedor.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.comedor.Activity.HistorialReservasActivity;
import com.example.comedor.Activity.ReservaDiaActivity;
import com.example.comedor.Adapter.OpcionesAdapter;
import com.example.comedor.Database.RolViewModel;
import com.example.comedor.Modelo.Opciones;
import com.example.comedor.Modelo.Rol;
import com.example.comedor.R;
import com.example.comedor.RecyclerListener.ItemClickSupport;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GestionReservasFragment extends Fragment {

    View view;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    OpcionesAdapter mAdapter;
    ArrayList<Opciones> mOpciones;
    RolViewModel mRolViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_gestion_reservas, container, false);

        loadViews();

        loadListener();

        loadData();

        return view;
    }


    private void loadListener() {
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                switch ((int) id) {
                    case 1:
                        startActivity(new Intent(getContext(), HistorialReservasActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getContext(), ReservaDiaActivity.class));
                        break;
                }
            }
        });

    }

    private void loadData() {
        mRolViewModel = new RolViewModel(getContext());
        mOpciones = new ArrayList<>();
        mOpciones.add(new Opciones(true, LinearLayout.VERTICAL, 2, "Reservas del día", R.drawable.ic_home, R.color.colorPrimaryDark));
        Rol rol = mRolViewModel.getByPermission(401);
        if (rol != null)
            mOpciones.add(new Opciones(true, LinearLayout.VERTICAL, 1, "Historial de reservas", R.drawable.ic_home, R.color.colorPrimaryDark));

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new OpcionesAdapter(mOpciones, getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadViews() {
        mRecyclerView = view.findViewById(R.id.recycler);
    }

}
