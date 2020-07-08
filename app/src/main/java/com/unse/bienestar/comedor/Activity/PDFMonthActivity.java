package com.unse.bienestar.comedor.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.unse.bienestar.comedor.R;

public class PDFMonthActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgIcono;
    Button btnPdf;
    BarChart barMes;
    PieChart pieFacultad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfmonth);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadViews();

        loadListener();

        loadData();

        setToolbar();
    }

    private void loadData() {
    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        btnPdf.setOnClickListener(this);

    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setText("Informe del mes");
    }

    private void loadViews() {
        imgIcono = findViewById(R.id.imgFlecha);
        btnPdf = findViewById(R.id.btnPdf);
        barMes = findViewById(R.id.barMeses);
        pieFacultad = findViewById(R.id.pieFacultad);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.btnPdf:
                break;

        }
    }
}