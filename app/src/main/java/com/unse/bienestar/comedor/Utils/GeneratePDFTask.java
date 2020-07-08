package com.unse.bienestar.comedor.Utils;

import android.content.Context;
import android.os.AsyncTask;

import com.unse.bienestar.comedor.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedor.Modelo.Usuario;

import java.util.ArrayList;

public class GeneratePDFTask extends AsyncTask<String, String, String> {

    ArrayList<Usuario> mUsuarios;
    DialogoProcesamiento dialogoProcesamiento;
    private Context mContext;
    int tipo = 0;

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
            Utils.createPDF(mUsuarios, mContext);
        else if (tipo == 2)
            Utils.createReportMensualPDF(mUsuarios, mContext, "Julio", 31);
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
        Utils.showToast(mContext, "¡Listado creado!");
    }
}
