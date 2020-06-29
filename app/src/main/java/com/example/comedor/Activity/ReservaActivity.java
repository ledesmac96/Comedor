package com.example.comedor.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.comedor.Dialogos.DialogCancelar;
import com.example.comedor.R;
import com.example.comedor.Utils.Utils;

public class ReservaActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mPlato, mFecha;
    ImageView imgIcono;
    Button btnReservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadViews();

        loadListener();

        loadData();

        setToolbar();

    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.txtTitulo)).setText("Reserva");
        Utils.changeColorDrawable(imgIcono, getApplicationContext(), R.color.colorPrimary);
    }

    private void loadData() {

    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnReservar.setOnClickListener(this);
    }

    private void loadViews() {
        mPlato = findViewById(R.id.txtPlato);
        mFecha = findViewById(R.id.txtFechaRes);
        btnReservar = findViewById(R.id.btnReservar);
        imgIcono = findViewById(R.id.imgFlecha);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.btnReservar:
                //FUNCIÓN
                break;
        }
    }

}
