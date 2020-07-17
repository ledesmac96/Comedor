package com.unse.bienestar.comedordos.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "alumno")
public class Alumno extends Usuario implements Parcelable {

    @Ignore
    public static final String TABLE_ALUMNO = "alumno";
    @Ignore
    public static final String KEY_ID_ALU = "idAlumno";

    @NonNull
    private int idAlumno;
    @NonNull
    private String carrera;
    @NonNull
    private String facultad;
    @NonNull
    private String anio;
    @NonNull
    private String legajo;

    public Alumno(int idUsuario, @NonNull String nombre, @NonNull String apellido,
                  @NonNull String fechaNac, @NonNull String pais, @NonNull String provincia,
                  @NonNull String localidad, @NonNull String domicilio, @NonNull String barrio,
                  @NonNull String telefono, @NonNull String mail,
                  @NonNull String fechaRegistro, @NonNull String fechaModificacion,
                  int validez, int idAlumno, @NonNull String carrera, @NonNull String facultad,
                  @NonNull String anio, @NonNull String legajo) {
        super(idUsuario, nombre, apellido, fechaNac, pais, provincia, localidad, domicilio,
                barrio, telefono, mail, fechaRegistro,
                fechaModificacion, validez);
        this.idAlumno = idAlumno;
        this.carrera = carrera;
        this.facultad = facultad;
        this.anio = anio;
        this.legajo = legajo;
    }

    @Ignore
    public Alumno() {

    }

    @Ignore
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

    public static Alumno mapper(JSONObject o, Usuario usuario) {
        Alumno alumno = new Alumno();
        try {
            if (o.has("datos")) {
                JSONObject object = o.getJSONObject("datos");
                String carrera = object.getString("carrera");
                String facultad = object.getString("facultad");
                String anio = object.getString("anio");
                String legajo = object.getString("legajo");

                alumno = new Alumno(usuario.getIdUsuario(), usuario.getNombre(), usuario.getApellido(),
                        usuario.getFechaNac(), usuario.getPais(), usuario.getProvincia(), usuario.getLocalidad(),
                        usuario.getDomicilio(), usuario.getBarrio(), usuario.getTelefono(),
                        usuario.getMail(), usuario.getFechaRegistro(), usuario.getFechaModificacion(),
                        usuario.getValidez(), usuario.getIdUsuario(), carrera, facultad, anio, legajo
                );
            } else {
                String carrera = o.getString("carrera");
                String facultad = o.getString("facultad");
                String anio = o.getString("anio");
                String legajo = o.getString("legajo");

                alumno = new Alumno(usuario.getIdUsuario(), usuario.getNombre(), usuario.getApellido(),
                        usuario.getFechaNac(), usuario.getPais(), usuario.getProvincia(), usuario.getLocalidad(),
                        usuario.getDomicilio(), usuario.getBarrio(), usuario.getTelefono(),
                        usuario.getMail(), usuario.getFechaRegistro(), usuario.getFechaModificacion(),
                        usuario.getValidez(), usuario.getIdUsuario(), carrera, facultad, anio, legajo);
            }
            return alumno;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return alumno;
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

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }


    @Override
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
