package com.unse.bienestar.comedor.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuario")
public class Usuario implements Parcelable {

    @Ignore
    public static final int COMPLETE = 1;
    @Ignore
    public static final int BASIC = 2;
    @Ignore
    public static final int MEDIUM = 3;
    @Ignore
    public static final int ESTADISTICA = 4;
    @Ignore
    public static final String TABLE_USER = "usuario";
    @Ignore
    public static final String KEY_ID_USER = "idUsuario";

    @PrimaryKey
    @NonNull
    private int idUsuario;
    @NonNull
    private String nombre;
    @NonNull
    private String apellido;
    @NonNull
    private String fechaNac;
    @NonNull
    private String pais;
    @NonNull
    private String provincia;
    @NonNull
    private String localidad;
    @NonNull
    private String domicilio;
    @NonNull
    private String barrio;
    @NonNull
    private String telefono;
    @NonNull
    private String mail;
    @NonNull
    private String fechaRegistro;
    @NonNull
    private String fechaModificacion;
    @NonNull
    private int validez;

    public Usuario(int idUsuario, @NonNull String nombre, @NonNull String apellido,
                   @NonNull String fechaNac, @NonNull String pais, @NonNull String provincia,
                   @NonNull String localidad, @NonNull String domicilio, @NonNull String barrio,
                   @NonNull String telefono, @NonNull String mail,
                   @NonNull String fechaRegistro, @NonNull String fechaModificacion,
                   int validez) {
        this.idUsuario = idUsuario;
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
        this.validez = validez;
    }

    @Ignore
    public Usuario(int idUsuario, @NonNull String nombre, @NonNull String apellido, int validez) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.validez = validez;
    }

    @Ignore
    public Usuario() {
    }

    @Ignore
    protected Usuario(Parcel in) {
        idUsuario = in.readInt();
        validez = in.readInt();
        nombre = in.readString();
        apellido = in.readString();
        pais = in.readString();
        provincia = in.readString();
        localidad = in.readString();
        domicilio = in.readString();
        barrio = in.readString();
        telefono = in.readString();
        mail = in.readString();
        fechaNac = in.readString();
        fechaRegistro = in.readString();
        fechaModificacion = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public static Usuario mapper(JSONObject object, int tipe) {
        Usuario usuario = new Usuario();
        String nombre, apellido, pais, provincia, localidad, domicilio,
                barrio, telefono, sexo, mail, fechaRegistro, fechaNac, fechaModificacion;
        int idUsuario, tipoUsuario, validez;

        try {

            switch (tipe) {
                case COMPLETE:
                    JSONObject datos = object.getJSONObject("mensaje");
                    idUsuario = Integer.parseInt(datos.getString("idusuario"));
                    nombre = datos.getString("nombre");
                    apellido = datos.getString("apellido");
                    pais = datos.getString("pais").equals("null") ? "" : datos.getString("pais");
                    provincia = datos.getString("provincia").equals("null") ? "" : datos.getString("provincia");
                    localidad = datos.getString("localidad").equals("null") ? "" : datos.getString("localidad");
                    domicilio = datos.getString("domicilio").equals("null") ? "" : datos.getString("domicilio");
                    barrio = datos.getString("barrio").equals("null") ? "" : datos.getString("barrio");
                    telefono = datos.getString("telefono").equals("null") ? "" : datos.getString("telefono");
                    ;
                    mail = datos.getString("mail").equals("null") ? "" : datos.getString("mail");
                    fechaRegistro = datos.getString("fecharegistro").equals("null") ? "" : datos.getString("fecharegistro");
                    fechaNac = datos.getString("fechanac").equals("null") ? "" : datos.getString("fechanac");
                    fechaModificacion = datos.getString("fechamodificacion").equals("null") ? "" : datos.getString("fechamodificacion");
                    validez = Integer.parseInt(datos.getString("validez"));
                    String[] check = new String[]{pais, provincia, localidad, domicilio, barrio, telefono, mail, fechaRegistro, fechaModificacion, fechaNac};
                    usuario = new Usuario(idUsuario, nombre, apellido, fechaNac, pais, provincia,
                            localidad, domicilio, barrio, telefono, mail, fechaRegistro,
                            fechaModificacion, validez);
                    break;
                case BASIC:
                    idUsuario = Integer.parseInt(object.getString("idusuario"));
                    nombre = object.getString("nombre");
                    apellido = object.getString("apellido");
                    validez = Integer.parseInt(object.getString("validez"));
                    usuario = new Usuario(idUsuario, nombre, apellido, validez);
                    break;
                case ESTADISTICA:
                    idUsuario = Integer.parseInt(object.getString("idusuario"));
                    fechaModificacion = object.getString("fechamodificacion");
                    usuario = new Usuario();
                    usuario.setIdUsuario(idUsuario);
                    usuario.setFechaModificacion(fechaModificacion);
                    break;
                case MEDIUM:
                    idUsuario = Integer.parseInt(object.getString("idusuario"));
                    telefono = object.getString("telefono");
                    nombre = object.getString("nombre");
                    apellido = object.getString("apellido");
                    fechaNac = object.getString("fechaNac");
                    pais = object.getString("pais");
                    provincia = object.getString("provincia");
                    localidad = object.getString("localidad");
                    domicilio = object.getString("domicilio");
                    barrio = object.getString("barrio");
                    mail = object.getString("mail");
                    usuario = new Usuario(idUsuario, nombre, apellido, fechaNac, pais, provincia, localidad, domicilio,
                            barrio, telefono, mail, null, null, -1);
                    break;
            }

            return usuario;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return usuario;
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
        dest.writeString(pais);
        dest.writeString(provincia);
        dest.writeString(localidad);
        dest.writeString(domicilio);
        dest.writeString(barrio);
        dest.writeString(telefono);
        dest.writeString(mail);
        dest.writeString(fechaNac);
        dest.writeString(fechaRegistro);
        dest.writeString(fechaModificacion);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    @NonNull
    public String getApellido() {
        return apellido;
    }

    public void setApellido(@NonNull String apellido) {
        this.apellido = apellido;
    }

    @NonNull
    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(@NonNull String fechaNac) {
        this.fechaNac = fechaNac;
    }

    @NonNull
    public String getPais() {
        return pais;
    }

    public void setPais(@NonNull String pais) {
        this.pais = pais;
    }

    @NonNull
    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(@NonNull String provincia) {
        this.provincia = provincia;
    }

    @NonNull
    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(@NonNull String localidad) {
        this.localidad = localidad;
    }

    @NonNull
    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(@NonNull String domicilio) {
        this.domicilio = domicilio;
    }

    @NonNull
    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(@NonNull String barrio) {
        this.barrio = barrio;
    }

    @NonNull
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(@NonNull String telefono) {
        this.telefono = telefono;
    }


    @NonNull
    public String getMail() {
        return mail;
    }

    public void setMail(@NonNull String mail) {
        this.mail = mail;
    }

    @NonNull
    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(@NonNull String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @NonNull
    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(@NonNull String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public int getValidez() {
        return validez;
    }

    public void setValidez(int validez) {
        this.validez = validez;
    }
}

