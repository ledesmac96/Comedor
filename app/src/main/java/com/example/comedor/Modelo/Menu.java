package com.example.comedor.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Menu implements Parcelable {

    private int idMenu, dia, mes, anio, validez, disponible;
    private String fechaRegistro, fechaModificacion, descripcion;

    public Menu(int idMenu, int dia, int mes, int anio, int validez, int disponible,
                String fechaRegistro, String fechaModificacion, String descripcion) {
        this.idMenu = idMenu;
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
        this.validez = validez;
        this.disponible = disponible;
        this.fechaRegistro = fechaRegistro;
        this.fechaModificacion = fechaModificacion;
        this.descripcion = descripcion;
    }

    public Menu() {
        this.idMenu = -1;
        this.dia = -1;
        this.mes = -1;
        this.anio = -1;
        this.validez = -1;
        this.disponible = -1;
        this.fechaRegistro = "";
        this.fechaModificacion = "";
        this.descripcion = "";
    }

    protected Menu(Parcel in) {
        idMenu = in.readInt();
        dia = in.readInt();
        mes = in.readInt();
        anio = in.readInt();
        validez = in.readInt();
        disponible = in.readInt();
        fechaRegistro = in.readString();
        fechaModificacion = in.readString();
        descripcion = in.readString();
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getValidez() {
        return validez;
    }

    public void setValidez(int validez) {
        this.validez = validez;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idMenu);
        dest.writeInt(dia);
        dest.writeInt(mes);
        dest.writeInt(anio);
        dest.writeInt(validez);
        dest.writeInt(disponible);
        dest.writeString(fechaRegistro);
        dest.writeString(fechaModificacion);
        dest.writeString(descripcion);
    }
}
