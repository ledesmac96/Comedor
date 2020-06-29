package com.example.comedor.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Alumno extends Usuario implements Parcelable {

    private int idAlumno;
    private String carrera, facultad, anio, legajo;

    public Alumno(int idUsuario, int validez, String nombre, String apellido, String fechaNac,
                  String pais, String provincia, String localidad, String domicilio, String barrio,
                  String telefono, String mail, String fechaRegistro, String fechaModificacion,
            int idAlumno, String carrera, String facultad, String anio, String legajo) {
        super(idUsuario, validez, nombre, apellido, fechaNac, pais, provincia, localidad, domicilio,
                barrio, telefono, mail, fechaRegistro, fechaModificacion);
        this.idAlumno = idAlumno;
        this.carrera = carrera;
        this.facultad = facultad;
        this.anio = anio;
        this.legajo = legajo;
    }

    public Alumno() {

    }

    protected Alumno(Parcel in) {
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

        carrera = in.readString();
        facultad = in.readString();
        legajo = in.readString();
        anio = in.readString();
        idAlumno = in.readInt();
    }

    public static final Creator<Alumno> CREATOR = new Creator<Alumno>() {
        @Override
        public Alumno createFromParcel(Parcel in) {
            return new Alumno(in);
        }

        @Override
        public Alumno[] newArray(int size) {
            return new Alumno[size];
        }
    };

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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

        dest.writeString(carrera);
        dest.writeString(facultad);
        dest.writeString(legajo);
        dest.writeString(anio);
        dest.writeInt(idAlumno);
    }
}
