package com.example.comedor.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.comedor.R;
import com.example.comedor.Utils.Utils;

public class InfoReservaActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgIcono;
    Button btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_reserva);

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

    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    private void loadViews() {
        btnCancelar = findViewById(R.id.btnReservar);
        imgIcono = findViewById(R.id.imgFlecha);
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
