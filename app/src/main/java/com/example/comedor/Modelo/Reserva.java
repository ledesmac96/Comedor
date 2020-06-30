package com.example.comedor.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Reserva implements Parcelable {

    public static final int COMPLETE = 1;
    public static final int MEDIUM = 2;

    private int idReserva, idUsuario, idEmpleado, idMenu, estado, validez;
    private String fechaReserva, fechaModificacion, nombre, apellido;

    public Reserva(int idReserva, int idUsuario, int idEmpleado, int idMenu, int estado,
                   String fechaReserva, String fechaModificacion, int validez) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idEmpleado = idEmpleado;
        this.idMenu = idMenu;
        this.estado = estado;
        this.validez = validez;
        this.fechaReserva = fechaReserva;
        this.fechaModificacion = fechaModificacion;
    }

    public Reserva(int idReserva, int idUsuario, int idMenu, int estado, int validez, String nombre, String apellido) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idMenu = idMenu;
        this.estado = estado;
        this.validez = validez;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Reserva() {
        this.idReserva = -1;
        this.idUsuario = -1;
        this.idEmpleado = -1;
        this.idMenu = -1;
        this.estado = -1;
        this.fechaReserva = "";
        this.fechaModificacion = "";
    }

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

    public static Reserva mapper(JSONObject object, int tipo) {
        int idReserva, idUsuario, idEmpleado, idMenu, estado, validez;
        String fechaReserva, fechaModificacion, nombre, apellido;
        Reserva reserva = new Reserva();
        try {
            switch (tipo) {
                case MEDIUM:
                    idReserva = Integer.parseInt(object.getString("idreserva"));
                    idUsuario = Integer.parseInt(object.getString("idusuario"));
                    idMenu = Integer.parseInt(object.getString("idmenu"));
                    estado = Integer.parseInt(object.getString("estado"));
                    validez = Integer.parseInt(object.getString("validez"));
                    nombre = object.getString("nombre");
                    apellido = object.getString("apellido");
                    reserva = new Reserva(idReserva, idUsuario, idMenu, estado, validez, nombre, apellido);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reserva;

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
    public int describeContents() {
        return 0;
    }

    @Override
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
