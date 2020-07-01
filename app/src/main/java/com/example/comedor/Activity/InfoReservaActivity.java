package com.example.comedor.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.comedor.Modelo.Reserva;
import com.example.comedor.R;
import com.example.comedor.Utils.Utils;

public class InfoReservaActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgIcono;
    Button btnCancelar;
    TextView txtIdRes, txtPlato, txtFechaRes, txtEstado;
    Reserva mReserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_reserva);

        if (getIntent().getParcelableExtra(Utils.RESERVA) != null) {
            mReserva = getIntent().getParcelableExtra(Utils.RESERVA);
        }

        loadViews();

        loadListener();

        loadData();

        setToolbar();

    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText("Mi reserva");
        Utils.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
    }

    private void loadData() {
        txtIdRes.setText("#"+ mReserva.getIdReserva());
        txtPlato.setText(mReserva.getDescripcion());
        txtFechaRes.setText(mReserva.getFechaReserva());
        txtEstado.setText(String.valueOf(mReserva.getDescripcion()));

       if (mReserva.getDescripcion().equals("RETIRADO") ||
               mReserva.getDescripcion().equals("CANCELADO")) {
           btnCancelar.setVisibility(View.GONE);
       }

    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    private void loadViews() {
        btnCancelar = findViewById(R.id.btnCancelar);
        imgIcono = findViewById(R.id.imgFlecha);

        txtIdRes = findViewById(R.id.txtIdReserva);
        txtPlato = findViewById(R.id.txtPlato);
        txtFechaRes = findViewById(R.id.txtFechaRes);
        txtEstado = findViewById(R.id.txtEstado);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.btnCancelar:
                //FUNCIÓN
                break;
        }
    }

}
