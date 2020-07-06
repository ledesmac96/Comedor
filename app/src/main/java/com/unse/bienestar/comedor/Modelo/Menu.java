package com.unse.bienestar.comedor.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "menu")
public class Menu implements Parcelable {

    @Ignore
    public static final int COMPLETE = 1;
    @Ignore
    public static final int SIMPLE = 2;
    @Ignore
    public static final int ESTADISTICA = 3;
    @Ignore
    public static final String TABLE = "menu";
    @Ignore
    public static final String KEY_ID = "idMenu";

    @PrimaryKey
    @NonNull
    private int idMenu;
    private int dia, mes, anio, validez, disponible, porcion;
    private String fechaRegistro, fechaModificacion, descripcion;

    @Ignore
    public Menu(int idMenu, int dia, int mes, int anio) {
        this.idMenu = idMenu;
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
    }

    public Menu(int idMenu, int dia, int mes, int anio, int validez, int disponible,
                String fechaRegistro, String fechaModificacion, String descripcion, int porcion) {
        this.idMenu = idMenu;
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
        this.validez = validez;
        this.disponible = disponible;
        this.fechaRegistro = fechaRegistro;
        this.fechaModificacion = fechaModificacion;
        this.descripcion = descripcion;
        this.porcion = porcion;
    }

    @Ignore
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

    @Ignore
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
        porcion = in.readInt();
    }

    @Ignore
    public static Menu mapper(JSONObject datos, int tipo) {
        Menu menu = new Menu();
        int idMenu, dia, mes, anio, validez, disponible, porcion;
        String fechaRegistro, fechaModificacion, descripcion;
        try {
            switch (tipo) {
                case COMPLETE:
                    idMenu = Integer.parseInt(datos.getString("idmenu"));
                    dia = Integer.parseInt(datos.getString("dia"));
                    mes = Integer.parseInt(datos.getString("mes"));
                    anio = Integer.parseInt(datos.getString("anio"));
                    validez = Integer.parseInt(datos.getString("validez"));
                    disponible = Integer.parseInt(datos.getString("disponible"));
                    porcion = Integer.parseInt(datos.getString("porcion"));

                    descripcion = datos.getString("descripcion");
                    fechaModificacion = datos.getString("fechamodificacion");
                    fechaRegistro = datos.getString("fecharegistro");
                    menu = new Menu(idMenu, dia, mes, anio, validez, disponible, fechaRegistro, fechaModificacion, descripcion, porcion);
                    break;
                case SIMPLE:
                    idMenu = Integer.parseInt(datos.getString("idmenu"));
                    dia = Integer.parseInt(datos.getString("dia"));
                    mes = Integer.parseInt(datos.getString("mes"));
                    anio = Integer.parseInt(datos.getString("anio"));
                    menu = new Menu(idMenu, dia, mes, anio);
                    break;
                case ESTADISTICA:
                    idMenu = Integer.parseInt(datos.getString("idmenu"));
                    dia = Integer.parseInt(datos.getString("dia"));
                    mes = Integer.parseInt(datos.getString("mes"));
                    anio = Integer.parseInt(datos.getString("anio"));
                    porcion = Integer.parseInt(datos.getString("porcion"));
                    menu = new Menu(idMenu, dia, mes, anio);
                    menu.setPorcion(porcion);
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return menu;
    }

    @Ignore
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

    public int getPorcion() {
        return porcion;
    }

    public void setPorcion(int porcion) {
        this.porcion = porcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    @Ignore
    public int describeContents() {
        return 0;
    }

    @Override
    @Ignore
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
        dest.writeInt(porcion);
    }
}
