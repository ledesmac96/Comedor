package com.unse.bienestar.comedordos.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.unse.bienestar.comedordos.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedordos.Modelo.Usuario;

import java.util.ArrayList;
import java.util.HashMap;

public class GeneratePDFTask extends AsyncTask<String, String, String> {

    ArrayList<Usuario> mUsuarios;
    DialogoProcesamiento dialogoProcesamiento;
    private Context mContext;
    int tipo = 0;

    String mes;
    int diasMes;
    HashMap<String, Integer[]> reservasAlumnosDias;
    Bitmap graficoBarra, graficoTorta;

    public GeneratePDFTask(int tipo, ArrayList<Usuario> usuarios, String mes,
                           Bitmap graficoBarra, Bitmap graficoTorta,
                           int diasMes, HashMap<String, Integer[]> reservasAlumnosDias,
                           DialogoProcesamiento dialogoProcesamiento, Context context) {
        this.tipo = tipo;
        mUsuarios = usuarios;
        this.graficoBarra = graficoBarra;
        this.graficoTorta = graficoTorta;
        this.mes = mes;
        this.dialogoProcesamiento = dialogoProcesamiento;
        mContext = context;
        this.diasMes = diasMes;
        this.reservasAlumnosDias = reservasAlumnosDias;
    }

    public GeneratePDFTask(int tipo, ArrayList<Usuario> usuarios, DialogoProcesamiento dialogoProcesamiento, Context context) {
        mUsuarios = usuarios;
        this.dialogoProcesamiento = dialogoProcesamiento;
        mContext = context;
        this.tipo = tipo;
    }

    public ArrayList<Usuario> getUsuarios() {
        return mUsuarios;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        mUsuarios = usuarios;
    }

    public GeneratePDFTask(DialogoProcesamiento dialogoProcesamiento) {
        this.dialogoProcesamiento = dialogoProcesamiento;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        if (tipo == 1)
            ABC.createPDF(mUsuarios, mContext);
        else if (tipo == 2)
            ABC.createReportMensualPDF(mUsuarios, mContext, mes, diasMes, reservasAlumnosDias, graficoBarra, graficoTorta);
        return null;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dialogoProcesamiento.dismiss();
        ABC.showToast(mContext, "¡Listado creado!");
    }
}
