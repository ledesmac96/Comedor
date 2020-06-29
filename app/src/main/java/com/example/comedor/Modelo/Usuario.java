package com.example.comedor.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable {

    private int idUsuario, validez;
    private String nombre, apellido, fechaNac, pais, provincia, localidad, domicilio, barrio,
            telefono, mail , fechaRegistro, fechaModificacion;

    public Usuario(int idUsuario, int validez, String nombre, String apellido, String fechaNac,
                  String pais, String provincia, String localidad, String domicilio, String barrio,
                  String telefono, String mail, String fechaRegistro, String fechaModificacion) {
        this.idUsuario = idUsuario;
        this.validez = validez;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNac = fechaNac;
        this.pais = pais;
        this.provincia = provincia;
        this.localidad = localidad;
        this.domicilio = domicilio;
        this.barrio = barrio;
        this.telefono = telefono;
        this.mail = mail;
        this.fechaRegistro = fechaRegistro;
        this.fechaModificacion = fechaModificacion;
    }

    public Usuario() {
        this.idUsuario = -1;
        this.validez = -1;
        this.nombre = "";
        this.apellido = "";
        this.fechaNac = "";
        this.pais = "";
        this.provincia = "";
        this.localidad = "";
        this.domicilio = "";
        this.barrio = "";
        this.telefono = "";
        this.mail = "";
        this.fechaRegistro = "";
        this.fechaModificacion = "";
    }

    protected Usuario(Parcel in) {
        idUsuario = in.readInt();
        validez = in.readInt();
        nombre = in.readString();
        apellido = in.readString();
        fechaNac = in.readString();
        pais = in.readString();
        provincia = in.readString();
        localidad = in.readString();
        domicilio = in.readString();
        barrio = in.readString();
        telefono = in.readString();
        mail = in.readString();
        fechaRegistro = in.readString();
        fechaModificacion = in.readString();
    }

    public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsuario);
        dest.writeInt(validez);
        dest.writeString(nombre);
        dest.writeString(apellido);
        dest.writeString(fechaNac);
        dest.writeString(pais);
        dest.writeString(provincia);
        dest.writeString(localidad);
        dest.writeString(domicilio);
        dest.writeString(barrio);
        dest.writeString(telefono);
        dest.writeString(mail);
        dest.writeString(fechaRegistro);
        dest.writeString(fechaModificacion);
    }

}
