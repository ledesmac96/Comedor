package com.example.comedor.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Reserva implements Parcelable {

    private int idReserva, idUsuario, idEmpleado, idMenu, estado;
    private String fechaReserva, fechaModificacion;

    public Reserva(int idReserva, int idUsuario, int idEmpleado, int idMenu, int estado,
                   String fechaReserva, String fechaModificacion) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idEmpleado = idEmpleado;
        this.idMenu = idMenu;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
        this.fechaModificacion = fechaModificacion;
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
    }
}
