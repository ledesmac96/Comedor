package com.example.comedor.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.comedor.Database.ReservaViewModel;
import com.example.comedor.Modelo.Menu;
import com.example.comedor.Modelo.Reserva;
import com.example.comedor.R;
import com.example.comedor.Utils.Utils;

import androidx.appcompat.app.AppCompatActivity;

public class InfoReservaActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgIcono;
    Button btnCancelar;
    TextView txtIdRes, txtPlato, txtFechaRes, txtEstado;
    Reserva mReserva;
    ReservaViewModel mReservaViewModel;
    Menu mMenu;
    LinearLayout latEstado, latQR, latFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_reserva);

        if (getIntent().getParcelableExtra(Utils.RESERVA) != null) {
            mReserva = getIntent().getParcelableExtra(Utils.RESERVA);
        }

        if (getIntent().getParcelableExtra(Utils.DATA_RESERVA) != null) {
            mMenu = getIntent().getParcelableExtra(Utils.DATA_RESERVA);
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
        String[] comida = null;
        mReservaViewModel = new ReservaViewModel(getApplicationContext());
        if (mReserva != null) {
            txtIdRes.setText(String.format("# %s", mReserva.getIdReserva()));
            comida = Utils.getComidas(mReserva.getDescripcion());
            txtFechaRes.setText(mReserva.getFechaReserva());
            txtEstado.setText(String.valueOf(mReserva.getDescripcion()));
        } else {
            Reserva reserva = mReservaViewModel.getAllByMenuID(mMenu.getIdMenu());
            if (reserva == null) {
                txtIdRes.setText("INFO");
                btnCancelar.setText("RESERVAR");
                comida = Utils.getComidas(mMenu.getDescripcion());
                latQR.setVisibility(View.GONE);
                latFecha.setVisibility(View.GONE);
                latEstado.setVisibility(View.GONE);
            } else {
                txtEstado.setText(String.valueOf(reserva.getDescripcion()));
                txtIdRes.setText(String.format("# %s", reserva.getIdReserva()));
                btnCancelar.setText("CANCELAR");
                comida = Utils.getComidas(reserva.getDescripcion());
                txtFechaRes.setText(reserva.getFechaReserva());
            }
        }
        txtPlato.setText(String.format("Almuerzo: %s\nCena: %s\nPostre: %s", comida[0], comida[1], comida[2]));

    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    private void loadViews() {
        btnCancelar = findViewById(R.id.btnReservar);
        imgIcono = findViewById(R.id.imgFlecha);

        txtIdRes = findViewById(R.id.txtIdReserva);
        txtPlato = findViewById(R.id.txtPlato);
        txtFechaRes = findViewById(R.id.txtFechaRes);
        txtEstado = findViewById(R.id.txtEstado);
        latEstado = findViewById(R.id.latEstado);
        latFecha = findViewById(R.id.latFechaReserva);
        latQR = findViewById(R.id.latQR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.btnReservar:
                //FUNCIÓN
                break;
        }
    }

}
