package com.example.comedor.Fragment;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.comedor.Activity.ReservaActivity;
import com.example.comedor.R;

public class InicioFragment extends Fragment implements View.OnClickListener {

    View view;
    CardView btnReserva;

    public InicioFragment() {
        // Metodo necesario
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Crea la vista de Inicio
        view = inflater.inflate(R.layout.fragment_inicio, container, false);

        loadViews();

        loadDataRecycler();

        return view;
    }

    private void loadViews() {
        btnReserva = view.findViewById(R.id.cardReservar);

    }

    private void loadDataRecycler() {
        btnReserva.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardReservar:
                startActivity(new Intent(getContext(), ReservaActivity.class));
                break;
        }
    }
}
