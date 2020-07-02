package com.example.comedor.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comedor.Database.AlumnoViewModel;
import com.example.comedor.Database.RolViewModel;
import com.example.comedor.Database.UsuarioViewModel;
import com.example.comedor.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class Utils {

    //Constantes de preferencias
    public static final String IS_FIRST_TIME_LAUNCH = "is_first";
    public static final String PREF_NAME = "comedor";
    public static final String IS_LOGIN = "login_yes";
    public static final String IS_LOCK = "lock_user";
    public static final String MY_ID = "my_id_user";
    public static final String TOKEN = "my_token";
    public static final String IS_VISIT = "visit";
    //Constantes para activities
    public static final String USER_INFO = "user_info";
    //public static final String RESERVA = "user_info";
    public static final String DEPORTE_NAME = "dato_deporte";
    public static final String DEPORTE_ID = "id_deporte";
    public static final String TIPO_CREDENCIAL = "tipo_cred";
    public static final String TIPO_CREDENCIAL_DATO = "tipo_cred_dato";
    public static final String CREDENCIAL = "credencial";
    public static final String URI_IMAGE = "uri_image";
    public static final String DEPORTE_NAME_PROF = "dato_deporte_prof";
    public static final String IS_ADMIN_MODE = "is_admin_mode";
    public static final String NAME_GENERAL = "name_general";
    public static final String ANIO = "anio_temp";
    public static final String ROLES = "roles_all";
    public static final String ROLES_USER = "roles_user";
    public static final String ASISTENCIA = "asistencia";
    public static final String FECHA = "fecha";
    public static final String LINEA_NAME = "linea_name";
    //Constantes para activities
    public static final int PICK_IMAGE = 9090;
    public static final int EDIT_IMAGE = 9091;
    //Constantes para tipos de usuario
    public static final int TIPO_USUARIO = 1;
    public static final int TIPO_ESTUDIANTE = 2;
    public static final int TIPO_ROLES = 10;
    //Constantes para busqueda
    public static final String PATRON_LEGAJO = "[0-9]{1,5}(-|/)[0-9]{2,4}";
    public static final String PATRON_DNI = "([0-9]){5,8}";
    public static final String PATRON_NOMBRES = "[a-zA-Z_ ]+";
    public static final String PATRON_NUMEROS = "[0-9]+";
    //Constante para tipo de usuario
    public static final int TIPO_ALUMNO = 1;
    public static final int TIPO_PROFESOR = 2;
    public static final int TIPO_NODOCENTE = 3;
    public static final int TIPO_EGRESADO = 4;
    public static final int TIPO_PARTICULAR = 5;
    //Constante para Volley
    public static final int MY_DEFAULT_TIMEOUT = 15000;
    //Constante de nombres de archivos
    public static final String PROFILE_PIC = "%s.jpg";
    //Constantes de permisos
    public static final int[] LIST_PERMISOS = new int[]{999, 998};

    public static final int PERMISSION_ALL = 1010;

    public static final int NOTICIA_NORMAL = 3030;
    public static final int NOTICIA_BUTTON_WEB = 3131;
    public static final int NOTICIA_BUTTON_TIENDA = 3132;
    public static final int NOTICIA_BUTTON_APP = 3133;

    public static final int TIPO_COMEDOR = 1;
    public static final int TIPO_DEPORTE = 2;
    public static final int TIPO_TRANSPORTE = 3;
    public static final int TIPO_BECA = 4;
    public static final int TIPO_RESIDENCIA = 5;
    public static final int TIPO_CYBER = 6;
    public static final int TIPO_UPA = 7;

    public static final int TIPO_CANCHA = 1010;
    public static final int TIPO_QUINCHO = 1011;

    public static final String TYPE_RANGE = "type_range";

    public static final String MONSERRAT = "Montserrat-Regular.ttf";
    public static final String MONTSERRAT_BOLD = "Montserrat-Black.ttf";

    public static final String BECA_NAME = "dato_deporte";
    public static final String BARCODE = "dato_barcode";
    public static final String TORNEO = "dato_torneo";
    public static final String NOTICIA = "dato_noticias";
    public static final String RESERVA = "reserva";
    public static final String DATA_RESERVA = "dato_reserva";
    public static final String ALUMNO_NAME = "dato_alumno";
    public static final String USER_NAME = "dato_user";
    public static final String NUM_INST = "num_inst";
    public static final int LIST_RESET = 0;
    public static final int LIST_LEGAJO = 1;
    public static final int LIST_DNI = 2;
    public static final int LIST_NOMBRE = 3;

    public static final int GET_FROM_GALLERY = 1011;
    public static final int GET_FROM_DNI = 1010;

    private static final String IP = "bienestar.unse.edu.ar";
    //USUARIO
    public static final String URL_USUARIO_INSERTAR = "http://" + IP + "/bienestar/comedor/beneficiario/insertarUsuario.php";
    public static final String URL_USUARIO_ACTUALIZAR = "http://" + IP + "/bienestar/comedor/beneficiario/actualizarUsuario.php";
    public static final String URL_USUARIO_LOGIN = "http://" + IP + "/bienestar/comedor/beneficiario/login.php";
    public static final String URL_USUARIO_CHECK = "http://" + IP + "/bienestar/comedor/beneficiario/getValid.php";
    public static final String URL_USUARIOS_LISTA = "http://" + IP + "/bienestar/comedor/beneficiario/getUsuarios.php";
    public static final String URL_USUARIO_BY_ID = "http://" + IP + "/bienestar/comedor/beneficiario/getUsuario.php";
    public static final String URL_USUARIO_ELIMINAR = "http://" + IP + "/bienestar/comedor/beneficiario/eliminarUsuario.php";

    public static final String URL_MENU_BY_RANGE = "http://" + IP + "/bienestar/comedor/menu/getMenuByRange.php";
    public static final String URL_MENU_HOY = "http://" + IP + "/bienestar/comedor/menu/getMenuToday.php";
    public static final String URL_MENU_NUEVO = "http://" + IP + "/bienestar/comedor/menu/insertarMenu.php";

    public static final String URL_RESERVA_HOY = "http://" + IP + "/bienestar/comedor/reserva/getReservaByDay.php";
    public static final String URL_RESERVA_HISTORIAL = "http://" + IP + "/bienestar/comedor/reserva/getReservas.php";
    public static final String URL_RESERVA_USUARIO = "http://" + IP + "/bienestar/comedor/reserva/getReservaByUser.php";


    //ROLES
    public static final String URL_ROLES_LISTA = "http://" + IP + "/bienestar/general/getRoles.php";
    public static final String URL_ROLES_INSERTAR = "http://" + IP + "/bienestar/general/insertarRol.php";
    public static final String URL_ROLES_USER_LISTA = "http://" + IP + "/bienestar/general/getRolesByUsuario.php";

    //GENERALES
    public static final String URL_CATEGORIAS = "http://" + IP + "/bienestar/general/getArchivos.php";
    public static final String URL_ARCHIVOS = "http://" + IP + "/bienestar/archivos/";


    public static final long SECONS_TIMER = 15000;

    //CARPETAS
    public static final String FOLDER = "BIENESTAR_ESTUDIANTIL/";
    public static final String FOLDER_CREDENCIALES = FOLDER + "CREDENCIALES/";

    public static String[] facultad = {"FAyA", "FCEyT", "FCF", "FCM", "FHCSyS"};
    public static String[] faya = {"Ingeniería Agronómica", "Ingeniería en Alimentos", "Licenciatura en Biotecnología",
            "Licenciatura en Química", "Profesorado en Química", "Tecnicatura en Apicultura"};
    public static String[] fceyt = {"Ingeniería Civil", "Ingeniería Electromecánica", "Ingeniería Electrónica",
            "Ingeniería Eléctrica", "Ingeniería en Agrimensura", "Ingeniería Hidráulica",
            "Ingeniería Industrial", "Ingeniería Vial", "Licenciatura en Hidrología Subterránea",
            "Licenciatura en Matemática", "Licenciatura en Sistemas de Información",
            "Profesorado en Física", "Profesorado en Informática", "Profesorado en Matemática",
            "Programador Universitario en Informática", "Tecnicatura Universitaria Vial",
            "Tecnicatura Universitaria en Construcciones",
            "Tecnicatura Universitaria en Hidrología Subterránea",
            "Tecnicatura Universitaria en Organización y Control de la Producción"};
    public static String[] fcf = {"Ingeniería Forestal", "Ingeniería en Industrias Forestales",
            "Licenciatura en Ecología y Conservación del Ambiente",
            "Tecnicatura Universitaria Fitosanitarista",
            "Tecnicatura Universitaria en Viveros y Plantaciones Forestales",
            "Tecnicatura Universitaria en Aserraderos y Carpintería Industrial"};

    public static String[] fcm = {"Medicina"};

    public static String[] fhcys = {"Licenciatura en Administración", "Contador Público Nacional",
            "Licenciatura en Letras", "Licenciatura en Sociología", "Licenciatura en Enfermería",
            "Licenciatura en Educación para la Salud", "Licenciatura en Obstetricia",
            "Licenciatura en Filosofía", "Licenciatura en Trabajo Social",
            "Licenciatura en Periodismo", "Profesorado en Educación para la Salud",
            "Tecnicatura Sup. Adm. y Gestión Universitaria",
            "Tecnicatura en Educación Intercultural Bilingue"};

    public static String dataAlumno = "?idU=%s&nom=%s&ape=%s&fechan=%s&pais=%s&prov=%s&local=%s" +
            "&dom=%s&car=%s&fac=%s&anio=%s&leg=%s" +
            "&mail=%s&tel=%s&barr=%s";

    public static String dataUser = "?idU=%s&nom=%s&ape=%s&car=%s&fac=%s&anio=%s&leg=%s";

    public static String dataProfesor = "?idU=%s&nom=%s&ape=%s&fechan=%s&pais=%s&prov=%s&local=%s" +
            "&dom=%s&sex=%s&tipo=%s&mail=%s&tel=%s" +
            "&prof=%s&fechain=%s&barr=%s&fecham=%s";

    public static String dataEgresado = "?idU=%s&nom=%s&ape=%s&fechan=%s&pais=%s&prov=%s&local=%s" +
            "&dom=%s&sex=%s&tipo=%s&mail=%s&tel=%s" +
            "&prof=%s&fechaeg=%s&barr=%s&fecham=%s";

    public static String dataPartiNoDoc = "?idU=%s&nom=%s&ape=%s&fechan=%s&pais=%s&prov=%s&local=%s" +
            "&dom=%s&sex=%s&tipo=%s&mail=%s&tel=%s&barr=%s&fecham=%s";


    public static void changeColorDrawable(ImageView view, Context context, int color) {
        DrawableCompat.setTint(
                DrawableCompat.wrap(view.getDrawable()),
                ContextCompat.getColor(context, color));
    }

    public static String getStringCamel(String string) {
        Pattern pattern = Pattern.compile("[A-Za-z]+");
        Matcher matcher = pattern.matcher(string);
        StringBuilder resp = new StringBuilder();
        while (matcher.find()) {
            if (matcher.group(0).length() > 1)
                resp.append(matcher.group(0).charAt(0)).append(matcher.group(0).substring(1).toLowerCase()).append(" ");
            else
                resp.append(matcher.group(0));
        }
        return resp.toString();
    }


    public static void showToast(Context c, String msj) {
        Toast.makeText(c, msj, Toast.LENGTH_LONG).show();
    }

    public static void showLog(String title, String msj) {
        Log.e(title, msj);
    }

    public static Bitmap resize(Bitmap bitmapToScale, float newWidth, float newHeight) {
        if (bitmapToScale == null)
            return null;
        int width = bitmapToScale.getWidth();
        int height = bitmapToScale.getHeight();

        Matrix matrix = new Matrix();

        matrix.postScale(newWidth / width, newHeight / height);

        return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.getWidth(), bitmapToScale.getHeight(), matrix, true);
    }

    /**
     * Método que muestra un Toast personalizado
     *
     * @param activity actividad actual
     * @param context  contexto de la actividad de donde se lo convoca
     * @param text     mensaje del toast
     * @param icon     icono
     */
    public static void showCustomToast(Activity activity, Context context, String text, int icon) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) activity.findViewById(R.id.toast_layout));

        ImageView image = layout.findViewById(R.id.image);
        image.setImageResource(icon);
        TextView text2 = layout.findViewById(R.id.text);
        text2.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 30);
        toast.setDuration(Toast.LENGTH_LONG + 4);
        toast.setView(layout);
        toast.show();
    }

    //Metodo para saber si un permiso esta autorizado o no
    public static boolean isPermissionGranted(Context ctx, String permision) {
        int permission = ActivityCompat.checkSelfPermission(
                ctx,
                permision);
        return permission == PackageManager.PERMISSION_GRANTED;

    }

    public static String getTwoDecimals(double value) {
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(value);
    }

    public static String obtainEstado(String imc) {
        double auxImc = Double.parseDouble(imc);
//        String aux = getTwoDecimals(iMC);
//        double auxImc = Double.parseDouble(aux);
        String estado = " ";
        if (auxImc <= 15) {
            estado = "Delgadez muy severa";
        } else if (auxImc > 15 && auxImc <= 15.9) {
            estado = "Delgadez severa";
        } else if (auxImc >= 16 && auxImc <= 18.4) {
            estado = "Delgadez";
        } else if (auxImc >= 18.5 && auxImc <= 24.9) {
            estado = "Peso saludable";
        } else if (auxImc >= 25 && auxImc <= 29.9) {
            estado = "Sobrepeso";
        } else if (auxImc >= 30 && auxImc <= 34.9) {
            estado = "Obesidad moderada";
        } else if (auxImc >= 35 && auxImc <= 39.9) {
            estado = "Obesidad severa";
        } else if (auxImc >= 40) {
            estado = "Obesidad mórbida";
        }

        return estado;
    }

    public static String obtainIMC(String peso, String altura) {
        String imc = " ", aux = " ";
        double auximc = 0;
        if (!peso.equals(" ") && !altura.equals(" ")) {
            double pso = Double.parseDouble(peso);
            double alt = Double.parseDouble(altura);
            auximc = pso / (alt * alt);
            imc = Double.toString(auximc);
            double iMC = Double.parseDouble(imc);
            aux = getTwoDecimals(iMC);
        }
        return aux;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getAppName(Context context, ComponentName name) {
        try {
            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(
                    name, PackageManager.GET_META_DATA);
            return activityInfo.loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void crearArchivoProvisorio(InputStream inputStream, File file) {

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String crypt(String text) {

        MessageDigest crypt = null;
        try {
            crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(text.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new BigInteger(1, crypt.digest()).toString(16);
    }

    public static String generateToken(String... data) {
        String sha = "";
        if (data.length == 3) {
            sha = data[0] + data[1] + data[2];
            return crypt(sha);
        } else {
            return sha;
        }

    }

    public static String getDirectoryPath() {
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/BIENESTAR_ESTUDIANTIL/";
        File directorio = new File(directory_path);
        if (!directorio.exists())
            directorio.mkdirs();
        return directory_path;
    }

    /*public static Object[] exist(Archivo archivo) {
        File file = new File(getDirectoryPath() + archivo.getNombreArchivo());
        Object[] a = new Object[2];
        a[0] = file.exists();
        a[1] = file.exists() ? file.lastModified() : 0;

        return a;
    }*/

    public static Date getFechaDate(String fecha) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaI = null;
        if (fecha != null)
            try {
                fechaI = simpleDateFormat.parse(fecha);

            } catch (ParseException e) {
                simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    fechaI = simpleDateFormat.parse(fecha);

                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }

        return fechaI;

    }

    public static Date getFechaDateDMA(String fecha) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaI = null;
        if (fecha != null)
            try {
                fechaI = simpleDateFormat.parse(fecha);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        return fechaI;

    }

    public static Date getFechaDateWithHour(String fecha) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fechaI = null;
        if (fecha != null)
            try {
                fechaI = simpleDateFormat.parse(fecha);

            } catch (ParseException e) {
                simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    fechaI = simpleDateFormat.parse(fecha);

                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }

        return fechaI;

    }

    public static String getHora(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String value = "";
        if (cal.get(Calendar.MINUTE) <= 9) {
            value = cal.get(Calendar.HOUR_OF_DAY) + ":0" +
                    cal.get(Calendar.MINUTE);
        } else {
            value = cal.get(Calendar.HOUR_OF_DAY) + ":" +
                    cal.get(Calendar.MINUTE);
        }

        return value;
    }

    public static String getHoraWithSeconds(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String value = "";
        if (cal.get(Calendar.MINUTE) <= 9) {
            value = cal.get(Calendar.HOUR_OF_DAY) + ":0" +
                    cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        } else {
            value = cal.get(Calendar.HOUR_OF_DAY) + ":" +
                    cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        }

        return value;
    }

    public static String getFechaName(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String mesS, diaS, minutosS, segS, horasS;
        int mes = cal.get(Calendar.MONTH) + 1;
        if (mes < 10) {
            mesS = "0" + mes;
        } else
            mesS = String.valueOf(mes);

        int dia = cal.get(Calendar.DAY_OF_MONTH);
        if (dia < 10) {
            diaS = "0" + dia;
        } else
            diaS = String.valueOf(dia);

        int minutos = cal.get(Calendar.MINUTE);
        if (minutos < 10) {
            minutosS = "0" + minutos;
        } else
            minutosS = String.valueOf(minutos);

        int seg = cal.get(Calendar.SECOND);
        if (seg < 10) {
            segS = "0" + seg;
        } else
            segS = String.valueOf(seg);

        int horas = cal.get(Calendar.HOUR_OF_DAY);
        if (horas < 10)
            horasS = "0" + horas;
        else
            horasS = String.valueOf(horas);

        String value = cal.get(Calendar.YEAR) + "-" + mesS + "-"
                + diaS + " " + horasS + ":" +
                minutosS + ":" + segS;

        return value;
    }

    public static String getBirthday(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String mesS, diaS;
        int mes = cal.get(Calendar.MONTH) + 1;
        if (mes < 10) {
            mesS = "0" + mes;
        } else
            mesS = String.valueOf(mes);

        int dia = cal.get(Calendar.DAY_OF_MONTH);
        if (dia < 10) {
            diaS = "0" + dia;
        } else
            diaS = String.valueOf(dia);
        String value = diaS + "/" + mesS + "/" + cal.get(Calendar.YEAR);

        return value;
    }

    public static String getFechaNameWithinHour(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String mesS, diaS;
        int mes = cal.get(Calendar.MONTH) + 1;
        if (mes < 10) {
            mesS = "0" + mes;
        } else
            mesS = String.valueOf(mes);

        int dia = cal.get(Calendar.DAY_OF_MONTH);
        if (dia < 10) {
            diaS = "0" + dia;
        } else
            diaS = String.valueOf(dia);
        String value = cal.get(Calendar.YEAR) + "-" + mesS + "-"
                + diaS;

        return value;
    }

    public static String getInfoDate(int dia, int mes, int anio) {
        Date fecha = getFechaDateDMA(String.format("%02d-%02d-%02d", dia, mes, anio));
        if (fecha != null) {
            String diaSemana = getDayWeek(fecha);
            return String.format("%s %02d", diaSemana, dia);
        }
        return "";

    }

    public static String getDate(int dia, int mes, int anio) {
        Date fecha = getFechaDateDMA(String.format("%02d-%02d-%02d", dia, mes, anio));
        return getBirthday(fecha);

    }

    public static String getDayWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "Lunes";
            case Calendar.TUESDAY:
                return "Martes";
            case Calendar.WEDNESDAY:
                return "Miércoles";
            case Calendar.THURSDAY:
                return "Jueves";
            case Calendar.FRIDAY:
                return "Viernes";
            case Calendar.SATURDAY:
                return "Sábado";
            case Calendar.SUNDAY:
                return "Domingo";
        }
        return "";
    }

    public static String getMonth(int mont) {
        /*Date date = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, mont-1);
        String mes = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        char in = mes.charAt(0);

        return String.valueOf(in).toUpperCase() + mes.substring(1);*/
        switch (mont) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
            default:
                return "Desconocido";
        }
    }

    public static String getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String mes = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        char in = mes.charAt(0);

        return in + mes.substring(1);
    }

    public static boolean isDateHabilited(Calendar calendar) {

        if (getDayWeek(calendar.getTime()).equals("Sábado") || getDayWeek(calendar.getTime()).equals("Domingo")) {
            return true;
        }
        return false;
    }

    public static int getEdad(Date fechaNac) {
        Date hoy = new Date(System.currentTimeMillis());
        long tiempo = hoy.getTime() - fechaNac.getTime();
        double years = tiempo / 3.15576e+10;
        int age = (int) Math.floor(years);
        return age;
    }

    public static void changeColor(Drawable drawable, Context mContext, int colorNo) {
        if (drawable instanceof ShapeDrawable)
            ((ShapeDrawable) drawable).getPaint().setColor(ContextCompat.getColor(mContext, colorNo));
        else if (drawable instanceof GradientDrawable)
            ((GradientDrawable) drawable).setColor(ContextCompat.getColor(mContext, colorNo));
        else if (drawable instanceof ColorDrawable)
            ((ColorDrawable) drawable).setColor(ContextCompat.getColor(mContext, colorNo));
    }


    public static String limpiarAcentos(String cadena) {
        String limpio = null;
        if (cadena != null) {
            String valor = cadena;
            valor = valor.toUpperCase();
            // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
            limpio = Normalizer.normalize(valor, Normalizer.Form.NFD);
            // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
            limpio = limpio.replaceAll("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
            // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
            limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC);
        }
        return limpio;
    }

    public static String convertImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] img = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(img, Base64.DEFAULT);

    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static String getTipoUser(int i) {
        switch (i) {
            case 1:
                return "Alumno";
            case 2:
                return "Profesor";
            case 3:
                return "Nodocente";
            case 4:
                return "Egresado";
            case 5:
                return "Particular";
        }
        return "";
    }

    public static String getFechaFormat(String fechaRegistro) {
        Calendar calendar = new GregorianCalendar();
        Date date = getFechaDateWithHour(fechaRegistro);
        if (date != null) {
            calendar.setTime(date);
            String dia = getDayWeek(date);
            String mes = getMonth(date);
            String anio = String.valueOf(calendar.get(Calendar.YEAR));
            String minutosS, segS, horasS;
            int minutos = calendar.get(Calendar.MINUTE);
            if (minutos < 10) {
                minutosS = "0" + minutos;
            } else
                minutosS = String.valueOf(minutos);

            int seg = calendar.get(Calendar.SECOND);
            if (seg < 10) {
                segS = "0" + seg;
            } else
                segS = String.valueOf(seg);

            int horas = calendar.get(Calendar.HOUR_OF_DAY);
            if (horas < 10)
                horasS = "0" + horas;
            else {
                horasS = String.valueOf(horas);
            }
            String hora = String.format("%s:%s:%s", horasS, minutosS, segS);
            String fecha = String.format("%s, %s de %s de %s - %s", dia, calendar.get(Calendar.DAY_OF_MONTH), mes, anio, hora);
            return fecha;
        }
        return "NO FECHA";

    }

    public static void resetData(Context context) {
        new UsuarioViewModel(context).deleteAll();
        new AlumnoViewModel(context).deleteAll();
        new RolViewModel(context).deleteAll();
    }

    public static String[] getComidas(String descripcion) {
        String[] food = new String[3];
        int index = descripcion.indexOf("#");
        int finIndex = descripcion.lastIndexOf("#");
        food[0] = descripcion.substring(0, index).trim();
        food[1] = descripcion.substring(index+1, finIndex).trim();
        food[2] = descripcion.substring(finIndex+1, descripcion.length() - 1).trim();
        return food;
    }
}

