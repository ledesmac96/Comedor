package com.example.comedor.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "reserva")
public class Reserva implements Parcelable {

    @Ignore
    public static final int COMPLETE = 1;
    @Ignore
    public static final int MEDIUM = 2;
    @Ignore
    public static final int HISTORIAL = 3;
    @Ignore
    public static final int HISTORIAL_TOTAL = 4;
    @Ignore
    public static final String TABLE = "reserva";
    @Ignore
    public static final String KEY_ID_ALU = "idReserva";
    @Ignore
    public static final String KEY_MENU = "idMenu";

    @PrimaryKey
    @NonNull
    private int idReserva;
    private int idUsuario, idEmpleado, idMenu, estado, validez;
    private String fechaReserva, fechaModificacion, nombre, apellido;
    private String descripcion, tipoEntrega;
    private int dia, mes, anio;

    @Ignore
    public Reserva(int idReserva, int idMenu, String fechaReserva, String descripcion,
                   String tipoEntrega, int dia, int mes, int anio) {
        this.idReserva = idReserva;
        this.idMenu = idMenu;
        this.fechaReserva = fechaReserva;
        this.descripcion = descripcion;
        this.tipoEntrega = tipoEntrega;
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
    }

    public Reserva(int idReserva, int idUsuario, int idEmpleado, int idMenu, int estado,
                   String fechaReserva, String fechaModificacion, int validez, String descripcion) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idEmpleado = idEmpleado;
        this.idMenu = idMenu;
        this.descripcion = descripcion;
        this.estado = estado;
        this.validez = validez;
        this.fechaReserva = fechaReserva;
        this.fechaModificacion = fechaModificacion;
    }

    @Ignore
    public Reserva(int idReserva, int idUsuario, int idMenu, String estado, int validez, String nombre, String apellido) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idMenu = idMenu;
        this.descripcion = estado;
        this.validez = validez;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    @Ignore
    public Reserva() {
        this.idReserva = -1;
        this.idUsuario = -1;
        this.idEmpleado = -1;
        this.idMenu = -1;
        this.estado = -1;
        this.fechaReserva = "";
        this.fechaModificacion = "";
    }

    @Ignore
    protected Reserva(Parcel in) {
        idReserva = in.readInt();
        idUsuario = in.readInt();
        idEmpleado = in.readInt();
        idMenu = in.readInt();
        estado = in.readInt();
        fechaReserva = in.readString();
        fechaModificacion = in.readString();
        validez = in.readInt();
        nombre = in.readString();
        apellido = in.readString();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
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

    public static final Creator<Reserva> CREATOR = new Creator<Reserva>() {
        @Override
        public Reserva createFromParcel(Parcel in) {
            return new Reserva(in);
        }

        @Override
        public Reserva[] newArray(int size) {
            return new Reserva[size];
        }
    };

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Ignore
    public static Reserva mapper(JSONObject object, int tipo) {
        int idReserva, idUsuario, idEmpleado, idMenu, estado, validez, dia, mes, anio;
        String fechaReserva, fechaModificacion, nombre, apellido, estadoDescripcion, tipoEntrega;
        Reserva reserva = new Reserva();
        try {
            switch (tipo) {
                case COMPLETE:
                    idReserva = Integer.parseInt(object.getString("idreserva"));
                    idUsuario = Integer.parseInt(object.getString("idusuario"));
                    idMenu = Integer.parseInt(object.getString("idmenu"));
                    estado = Integer.parseInt(object.getString("estado"));
                    fechaReserva = object.getString("fechareserva");
                    fechaModificacion = object.getString("fechamodificacion");
                    validez = Integer.parseInt(object.getString("validez"));
                    reserva = new Reserva(idReserva,idUsuario, 0, idMenu, estado, fechaReserva, fechaModificacion, validez, "");
                    break;
                case MEDIUM:
                    idReserva = Integer.parseInt(object.getString("idreserva"));
                    idUsuario = Integer.parseInt(object.getString("idusuario"));
                    idMenu = Integer.parseInt(object.getString("idmenu"));
                    estadoDescripcion = object.getString("descripcion");
                    validez = Integer.parseInt(object.getString("validez"));
                    nombre = object.getString("nombre");
                    apellido = object.getString("apellido");
                    reserva = new Reserva(idReserva, idUsuario, idMenu, estadoDescripcion, validez, nombre, apellido);
                    break;
                case HISTORIAL:
                    idReserva = Integer.parseInt(object.getString("idreserva"));
                    idMenu = Integer.parseInt(object.getString("idmenu"));
                    dia = Integer.parseInt(object.getString("dia"));
                    mes = Integer.parseInt(object.getString("mes"));
                    anio = Integer.parseInt(object.getString("anio"));
                    fechaReserva = object.getString("fechareserva");
                    estadoDescripcion = object.getString("descripcion");
                    tipoEntrega = object.getString("tiporeserva");
                    reserva = new Reserva(idReserva, idMenu, fechaReserva, estadoDescripcion, tipoEntrega, dia, mes, anio);
                    break;
                case HISTORIAL_TOTAL:
                    idReserva = Integer.parseInt(object.getString("idreserva"));
                    idMenu = Integer.parseInt(object.getString("idmenu"));
                    estadoDescripcion = object.getString("descripcion");
                    tipoEntrega = object.getString("tiporeserva");
                    validez = Integer.parseInt(object.getString("validez"));
                    nombre = object.getString("nombre");
                    apellido = object.getString("apellido");
                    reserva = new Reserva(idReserva, idMenu, validez, "", nombre, apellido, estadoDescripcion, tipoEntrega);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reserva;

    }

    @Ignore
    public Reserva(int idReserva, int idMenu, int validez, String fechaReserva, String nombre, String apellido, String descripcion, String tipoEntrega) {
        this.idReserva = idReserva;
        this.idMenu = idMenu;
        this.validez = validez;
        this.fechaReserva = fechaReserva;
        this.nombre = nombre;
        this.apellido = apellido;
        this.descripcion = descripcion;
        this.tipoEntrega = tipoEntrega;
    }

    public int getValidez() {
        return validez;
    }

    public void setValidez(int validez) {
        this.validez = validez;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    @Override
    @Ignore
    public int describeContents() {
        return 0;
    }

    @Override
    @Ignore
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idReserva);
        dest.writeInt(idUsuario);
        dest.writeInt(idEmpleado);
        dest.writeInt(idMenu);
        dest.writeInt(estado);
        dest.writeString(fechaReserva);
        dest.writeString(fechaModificacion);
        dest.writeInt(validez);
        dest.writeString(nombre);
        dest.writeString(apellido);
    }
}
