package com.example.comedor.Utils;

import android.content.Context;
import android.widget.EditText;

import com.example.comedor.R;

public class Validador {

    Context mContext;

    public Validador(Context context) {
        mContext = context;
    }

    public boolean validarNumero(EditText editText) {
        if (noVacio(editText.getText().toString().trim())) {
            editText.setError(mContext.getString(R.string.campoVacio));
            return false;
        }
        if (validarNumero(editText.getText().toString().trim())) {
            return true;
        } else {
            editText.setError(mContext.getString(R.string.numeroNoValido));
            return false;
        }
    }


    public boolean validarNombres(EditText editText) {
        if (noVacio(editText.getText().toString().trim())) {
            editText.setError(mContext.getString(R.string.campoVacio));
            return false;
        }
        if (validarNombre(editText.getText().toString().trim())) {

            if (lengthMore(editText.getText().toString().trim())) {
                return true;
            }else{
                editText.setError(mContext.getString(R.string.campoNoGrande));
                return false;
            }
        } else {
            editText.setError(mContext.getString(R.string.campoNoFormato));
            return false;
        }
    }

    public boolean validarTelefono(EditText editText){
        if (noVacio(editText.getText().toString().trim())){
            editText.setError(mContext.getString(R.string.campoVacio));
            return false;
        }
        if (validarNumeroTelefono(editText.getText().toString().trim())){
            return true;
        }else{
            editText.setError(mContext.getString(R.string.numeroTelInvalido));
            return false;
        }

    }

    public boolean validarMail(EditText editText){
        if (noVacio(editText.getText().toString().trim())){
            editText.setError(mContext.getString(R.string.campoVacio));
            return false;
        }
        if (validarMail(editText.getText().toString().trim())){
            return true;
        }else{
            editText.setError(mContext.getString(R.string.emailInvalido));
            return false;
        }

    }

    public boolean validarContraseña(EditText editText, EditText editText2){
        if (noVacio(editText.getText().toString().trim())){
            editText.setError(mContext.getString(R.string.campoVacio));
            return false;
        }
        if (noVacio(editText2.getText().toString().trim())){
            editText2.setError(mContext.getString(R.string.campoVacio));
            return false;
        }
        if (!validarContraseña(editText.getText().toString().trim())){
            editText.setError(mContext.getString(R.string.contraseniaMinimo));
            return false;
        }
        if (!validarContraseña(editText2.getText().toString().trim())){
            editText2.setError(mContext.getString(R.string.contraseniaMinimo));
            return false;
        }
        if (editText.getText().toString().trim().equalsIgnoreCase(
                editText2.getText().toString().trim())){
            return true;
        }else{
            editText2.setError(mContext.getString(R.string.contraseniaNoIguales));
            return false;
        }

    }

    public boolean validarNumeroTelefono(String numero) {
        String regex = "[0-9]{8,}";
        return numero.matches(regex);
    }

    public boolean validarMail(String mail) {
        String regex = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return mail.matches(regex);
    }


    public boolean validarDNI(EditText editText) {
        if (noVacio(editText.getText().toString().trim())) {
            editText.setError(mContext.getString(R.string.campoVacioDNI));
            return false;
        }
        if (validarNumero(editText.getText().toString().trim()) &&
                editText.getText().toString().trim().length() >= 7) {
            return true;
        } else {
            editText.setError(mContext.getString(R.string.invalidoDNI));
            return false;
        }
    }

    public boolean validarContrasenia(EditText editText) {
        if (noVacio(editText.getText().toString().trim())) {
            editText.setError(mContext.getString(R.string.campoVacioPass));
            return false;
        }
        if (validarContraseña(editText.getText().toString().trim())) {
            return true;
        } else {
            editText.setError(mContext.getString(R.string.invalidoContrasenia));
            return false;
        }
    }

    public boolean validarNombre(String name) {
        String regex = "[ a-zA-ZÀ-ÿ\\u00f1\\u00d1]+";
        return name.matches(regex);
    }

    public boolean lengthMore(String string) {
        return string.length() >= 3;
    }

    public boolean noVacio(String string) {
        return string.equalsIgnoreCase("");
    }

    public boolean validarNumero(String numero) {
        String regex = "^[0-9]+$";
        return numero.matches(regex);
    }

    public boolean validarContraseña(String c) {
        return c.length() >= 4;
    }

}
