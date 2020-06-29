package com.example.comedor.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Empleado extends Usuario implements Parcelable {

    private int idEmpleado, anio;
    private String fechaRegistro;

    public Empleado(int idUsuario, int validez, String nombre, String apellido, String fechaNac,
                    String pais, String provincia, String localidad, String domicilio, String barrio,
                    String telefono, String mail, String fechaRegistro, String fechaModificacion,
                    int idEmpleado, int anio, String fechaReg) {
        super(idUsuario, validez, nombre, apellido, fechaNac, pais, provincia, localidad, domicilio,
                barrio, telefono, mail, fechaRegistro, fechaModificacion);
        this.idEmpleado = idEmpleado;
        this.anio = anio;
        this.fechaRegistro = fechaReg;
    }

    public Empleado() {
    }


    protected Empleado(Parcel in) {
        super(in);

        setIdUsuario(in.readInt());
        setValidez(in.readInt());
        setNombre(in.readString());
        setApellido(in.readString());
        setPais(in.readString());
        setProvincia(in.readString());
        setLocalidad(in.readString());
        setDomicilio(in.readString());
        setBarrio(in.readString());
        setTelefono(in.readString());
        setMail(in.readString());
        setFechaNac(in.readString());
        setFechaRegistro(in.readString());
        setFechaModificacion(in.readString());

        idEmpleado = in.readInt();
        anio = in.readInt();
        fechaRegistro = in.readString();
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    @Override
    public String getFechaRegistro() {
        return fechaRegistro;
    }

    @Override
    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Empleado> CREATOR = new Creator<Empleado>() {
        @Override
        public Empleado createFromParcel(Parcel in) {
            return new Empleado(in);
        }

        @Override
        public Empleado[] newArray(int size) {
            return new Empleado[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(getIdUsuario());
        dest.writeInt(getValidez());
        dest.writeString(getNombre());
        dest.writeString(getApellido());
        dest.writeString(getPais());
        dest.writeString(getProvincia());
        dest.writeString(getLocalidad());
        dest.writeString(getDomicilio());
        dest.writeString(getBarrio());
        dest.writeString(getTelefono());
        dest.writeString(getMail());
        dest.writeString(getFechaNac());
        dest.writeString(getFechaRegistro());
        dest.writeString(getFechaModificacion());

        dest.writeInt(idEmpleado);
        dest.writeInt(anio);
        dest.writeString(fechaRegistro);
    }


}
