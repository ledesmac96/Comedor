package com.unse.bienestar.comedor.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.unse.bienestar.comedor.Database.AlumnoViewModel;
import com.unse.bienestar.comedor.Database.MenuViewModel;
import com.unse.bienestar.comedor.Database.ReservaViewModel;
import com.unse.bienestar.comedor.Database.RolViewModel;
import com.unse.bienestar.comedor.Database.UsuarioViewModel;
import com.unse.bienestar.comedor.Modelo.Usuario;
import com.unse.bienestar.comedor.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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
    public static final String ID_MENU = "id_menu";
    //Constantes para activities
    public static final String USER_INFO = "user_info";
    //public static final String RESERVA = "user_info";
    public static final String IS_ADMIN_MODE = "is_admin_mode";

    //Constantes para busqueda
    public static final String PATRON_LEGAJO = "[0-9]{1,5}(-|/)[0-9]{2,4}";
    public static final String PATRON_DNI = "([0-9]){5,8}";
    public static final String PATRON_NOMBRES = "[a-zA-Z_ ]+";
    public static final String PATRON_NUMEROS = "[0-9]+";
    //Constante para Volley
    public static final int MY_DEFAULT_TIMEOUT = 15000;


    public static final String TYPE_RANGE = "type_range";

    public static final String MONSERRAT = "Montserrat-Regular.ttf";
    public static final String MONTSERRAT_BOLD = "Montserrat-Black.ttf";

    public static final String RESERVA = "reserva";
    public static final String DATA_RESERVA = "dato_reserva";
    public static final String ALUMNO_NAME = "dato_alumno";
    public static final int LIST_RESET = 0;
    public static final int LIST_DNI = 2;
    public static final int LIST_NOMBRE = 3;

    public static final String IS_TOKEN = "is_token_firebase";

    private static final String IP = "bienestar.unse.edu.ar";
    //USUARIO
    public static final String URL_USUARIO_INSERTAR = "http://" + IP + "/bienestar/comedor/beneficiario/insertarUsuario.php";
    public static final String URL_USUARIO_ACTUALIZAR = "http://" + IP + "/bienestar/comedor/beneficiario/actualizarUsuario.php";
    public static final String URL_USUARIO_LOGIN = "http://" + IP + "/bienestar/comedor/beneficiario/login.php";
    public static final String URL_USUARIO_CHECK = "http://" + IP + "/bienestar/comedor/beneficiario/getValid.php";
    public static final String URL_USUARIO_TEST = "http://" + IP + "/bienestar/comedor/menu/test.php";
    public static final String URL_USUARIOS_LISTA = "http://" + IP + "/bienestar/comedor/beneficiario/getUsuarios.php";
    public static final String URL_USUARIO_BY_ID = "http://" + IP + "/bienestar/comedor/beneficiario/getUsuario.php";
    public static final String URL_USUARIO_ELIMINAR = "http://" + IP + "/bienestar/comedor/beneficiario/eliminarUsuario.php";

    public static final String URL_MENU_BY_RANGE = "http://" + IP + "/bienestar/comedor/menu/getMenuByRange.php";
    public static final String URL_MENU_HOY = "http://" + IP + "/bienestar/comedor/menu/getMenuToday.php";
    public static final String URL_MENU_TERMINAR = "http://" + IP + "/bienestar/comedor/menu/terminarMenu.php";
    public static final String URL_MENU_RESTRINGIR = "http://" + IP + "/bienestar/comedor/menu/restringirMenu.php";
    public static final String URL_MENU_NUEVO = "http://" + IP + "/bienestar/comedor/menu/insertarMenu.php";

    public static final String URL_RESERVA_HOY = "http://" + IP + "/bienestar/comedor/reserva/getReservaByDay.php";
    public static final String URL_RESERVA_BY_ID = "http://" + IP + "/bienestar/comedor/reserva/getReserva.php";
    public static final String URL_RESERVA_INSERTAR = "http://" + IP + "/bienestar/comedor/reserva/insertarReserva.php";
    public static final String URL_RESERVA_CANCELAR = "http://" + IP + "/bienestar/comedor/reserva/cancelarReservaById.php";
    public static final String URL_RESERVA_ACTUALIZAR = "http://" + IP + "/bienestar/comedor/reserva/actualizarReserva.php";
    public static final String URL_RESERVA_HISTORIAL = "http://" + IP + "/bienestar/comedor/reserva/getReservas.php";
    public static final String URL_RESERVA_USUARIO = "http://" + IP + "/bienestar/comedor/reserva/getReservaByUser.php";

    public static final String URL_DATOS = "http://" + IP + "/bienestar/comedor/getReportes.php";
    public static final String URL_DATOS_RESERVA_MENSUAL = "http://" + IP + "/bienestar/comedor/getReporteMensualReservas.php";


    public static final String[] facultad = {"FAyA", "FCEyT", "FCF", "FCM", "FHCSyS"};
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


    public static void changeColorDrawable(ImageView view, Context context, int color) {
        DrawableCompat.setTint(
                DrawableCompat.wrap(view.getDrawable()),
                ContextCompat.getColor(context, color));
    }


    public static void showToast(Context c, String msj) {
        Toast.makeText(c, msj, Toast.LENGTH_LONG).show();
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


    public static String getDirectoryPath() {
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/BIENESTAR_ESTUDIANTIL/";
        File directorio = new File(directory_path);
        if (!directorio.exists())
            directorio.mkdirs();
        return directory_path;
    }


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
        if (fecha != null) {
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
        } else return null;


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

    public static String getFechaOrder(Date date) {

        Calendar cal = Calendar.getInstance();
        if (date != null) {
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

            return String.format("%s/%s/%s - %s:%s:%s", diaS, mesS, cal.get(Calendar.YEAR), horasS, minutosS, segS);
        } else return "NO FECHA";

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
        new MenuViewModel(context).deleteAll();
        new ReservaViewModel(context).deleteAll();
    }

    public static String[] getComidas(String descripcion) {
        String[] food = new String[]{"NO INFO", "NO INFO", "NO INFO"};
        if (descripcion != null && descripcion.contains("$")) {

            int index = descripcion.indexOf("$");
            food[0] = descripcion.substring(0, index).trim();

            Pattern pattern = Pattern.compile("\\$[a-zA-Z  ]+");
            Matcher matcher = pattern.matcher(descripcion);
            int i = 1;
            while (matcher.find()) {
                food[i] = matcher.group().substring(1);
                i++;
            }

            /*int index = descripcion.indexOf("$");
            int finIndex = descripcion.lastIndexOf("$");

            try {
                food[0] = descripcion.substring(0, index).trim();
            } catch (IndexOutOfBoundsException e) {
            }
            try {
                food[1] = descripcion.substring(index + 1, finIndex).trim();
            } catch (IndexOutOfBoundsException e) {
            }
            try {
                food[2] = descripcion.substring(finIndex + 1, descripcion.length() - 1).trim();
            } catch (IndexOutOfBoundsException e) {

            }*/

        }
        return food;
    }

    public static void createPDF(List<Usuario> usuarios, Context context) {
        Document document = null;
        FileOutputStream fileOutputStream = null;
        try {
            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/BIENESTAR_ESTUDIANTIL_COMEDOR/";
            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File filePath = new File(getNameFile(directory_path, 1, ""));
            if (!file.exists())
                file.createNewFile();
            }

            fileOutputStream = new FileOutputStream(filePath);
            PdfWriter pdfWriter = new PdfWriter(fileOutputStream);
            document = new Document(new PdfDocument(pdfWriter));
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            addHeadder(document, font);
            document.add(getText("LISTADO DE USUARIOS", 10, true).setFont(font));
            Image image = loadImage(context, R.drawable.encabezado, 179, 79);
            document.add(image.setTextAlignment(TextAlignment.CENTER));
            //image = loadImage(context, R.drawable.logo_unse, 179, 76);
            //document.add(image.setTextAlignment(TextAlignment.RIGHT));

            //PdfFont font = PdfFontFactory.createFont();
            //PdfFont font = PdfFontFactory.createFont("assets/product_sans_regular.ttf", PdfEncodings.IDENTITY_H, true);
            document.add(getText("BIENESTAR ESTUDIANTIL", 12, true));
            document.add(getText("SISTEMA DE GESTIÓN - COMEDOR UNIVERSITARIO", 11, true));
            // document.add(getText("----------------------------------------------------------------------------------------------------------------------", 11, true));
            document.add(getText("LISTADO DE USUARIOS", 10, true));
            Table table = new Table(new UnitValue[]{
                    new UnitValue(UnitValue.PERCENT, 20f),
                    new UnitValue(UnitValue.PERCENT, 6f),
                    new UnitValue(UnitValue.PERCENT, 10f),
                    new UnitValue(UnitValue.PERCENT, 10f),
                    new UnitValue(UnitValue.PERCENT, 12f),
                    new UnitValue(UnitValue.PERCENT, 10f),
                    new UnitValue(UnitValue.PERCENT, 10f),
                    new UnitValue(UnitValue.PERCENT, 10f),
                    new UnitValue(UnitValue.PERCENT, 11f)
            })
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(10).setMarginBottom(10);
            table.addHeaderCell(createCell("Apellido y Nombre", true, 8).setFont(font));
            table.addHeaderCell(createCell("D.N.I", true, 8).setFont(font));
            table.addHeaderCell(createCell("Lunes", true, 8).setFont(font));
            table.addHeaderCell(createCell("Martes", true, 8).setFont(font));
            table.addHeaderCell(createCell("Miércoles", true, 8).setFont(font));
            table.addHeaderCell(createCell("Jueves", true, 8).setFont(font));
            table.addHeaderCell(createCell("Viernes", true, 8).setFont(font));
            table.addHeaderCell(createCell("Sábado", true, 8).setFont(font));
            table.addHeaderCell(createCell("Domingo", true, 8).setFont(font));
            for (Usuario usuario : usuarios) {
                table.addCell(getText(String.format("%s %s", usuario.getApellido(), usuario.getNombre()),
                        8, true));
                table.addCell(getText(String.valueOf(usuario.getIdUsuario()), 8, true));
                table.addCell(createCell("          ", true, 9));
                table.addCell(createCell("          ", true, 9));
                table.addCell(createCell("          ", true, 9));
                table.addCell(createCell("          ", true, 9));
                table.addCell(createCell("          ", true, 9));
                table.addCell(createCell("          ", true, 9));
                table.addCell(createCell("          ", true, 9));
            }
            document.add(table);
            addFooter(document, font);
            document.close();


        } catch (IOException e) {
            e.printStackTrace();
            showToast(context, context.getString(R.string.errorPDF));
        }

    }

    public static void createReportMensualPDF(List<Usuario> usuarios, Context context, String mes, int dias) {
        Document document = null;
        FileOutputStream fileOutputStream = null;
        try {
            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/BIENESTAR_ESTUDIANTIL_COMEDOR/";
            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File filePath = new File(getNameFile(directory_path, 2, mes.toUpperCase()));
            if (!file.exists())
                file.createNewFile();
            fileOutputStream = new FileOutputStream(filePath);
            PdfWriter pdfWriter = new PdfWriter(fileOutputStream);
            document = new Document(new PdfDocument(pdfWriter));
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            addHeadder(document, font);
            document.add(getText(String.format("REPORTE MENSUAL: %s", mes.toUpperCase()), 10, true).setFont(font));
            UnitValue[] columnas = new UnitValue[2 + dias + 1];
            columnas[0] = new UnitValue(UnitValue.PERCENT, 18f);
            columnas[1] = new UnitValue(UnitValue.PERCENT, 6f);
            for (int i = 2; i < 11; i++) {
                columnas[i] = new UnitValue(UnitValue.PERCENT, 1.6f);
            }
            for (int i = 11; i < (dias + 2); i++) {
                columnas[i] = new UnitValue(UnitValue.PERCENT, 2f);
            }
            columnas[columnas.length - 1] = new UnitValue(UnitValue.PERCENT, 3f);
            Table table = new Table(columnas)
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(10)
                    .setMarginBottom(10);
            table.addHeaderCell(createCell("Apellido y Nombre", true, 8).setFont(font));
            table.addHeaderCell(createCell("D.N.I", true, 8).setFont(font));
            for (int i = 1; i <= dias; i++) {
                table.addHeaderCell(createCell(String.valueOf(i), true, 8).setFont(font));
            }
            table.addHeaderCell(createCell("T", true, 8).setFont(font));
            for (Usuario usuario : usuarios) {
                table.addCell(getText(String.format("%s %s", usuario.getApellido(), usuario.getNombre()),
                        8, true));
                table.addCell(getText(String.valueOf(usuario.getIdUsuario()), 8, true));
                int total = 0;
                for (int i = 1; i <= dias; i++) {
                    if (new Random().nextInt(3) == 1)
                        table.addCell(createCell(" ", true, 7));
                    else {
                        table.addCell(createCell("X", true, 7));
                        total++;
                    }
                }
                table.addCell(createCell(String.valueOf(total), true, 7));
            }
           /* table.addCell(createCell("TOTAL", true, 7).setFont(font));
            table.addCell(createCell("     ", true, 7).setFont(font));
            for (int i = 1; i <= dias; i++) {
                table.addCell(createCell(String.valueOf(new Random().nextInt(30)), true, 7));
            }*/
            document.add(table);
            addFooter(document, font);
            document.close();


        } catch (IOException e) {
            e.printStackTrace();
            showToast(context, context.getString(R.string.errorPDF));
        }

    }

    public static void addHeadder(Document document, PdfFont font) {
        document.add(getText("BIENESTAR ESTUDIANTIL", 12, true).setFont(font));
        document.add(getText("SISTEMA DE GESTIÓN - COMEDOR UNIVERSITARIO", 11, true).setFont(font));

    }

    public static void addFooter(Document document, PdfFont font) {
        document.add(getText("Generado desde App Comedor Universitario", 10, false)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(font));
        document.add(getText("Fecha de Generación: " +
                getFechaOrder(new Date(System.currentTimeMillis())), 10, false)
                .setTextAlignment(TextAlignment.RIGHT));


    }

    private static Image loadImage(Context context, int img, int width, int height) {
        Drawable drawable = context.getResources().getDrawable(img);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
       // bitmap = resize(bitmap, width, height);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream);
        Image image = null;
        byte[] bytes = stream.toByteArray();
        image = new Image(ImageDataFactory.create(bytes));
        return image;
    }

    private static Paragraph getText(String text, float size, boolean center) {
        return center ? new Paragraph(text).setFontSize(size).setTextAlignment(TextAlignment.CENTER)
                : new Paragraph(text).setFontSize(size);
    }

    public static String getNameFile(String directory_path, int tipo, String extra) {
        return directory_path + (tipo == 1 ? "LISTADO_USUARIOS_" : String.format("REPORTE_MENSUAL_%s_", extra)) +
                getFechaName(new Date(System.currentTimeMillis())) + ".pdf";
    }

    private static Cell createCell(String text, boolean center, int size) {
        return center ? new Cell().setPadding(0.8f)
                .add(new Paragraph(text).setFontSize(size)
                        .setMultipliedLeading(1)).setTextAlignment(TextAlignment.CENTER)
                : new Cell().setPadding(0.8f)
                .add(new Paragraph(text).setFontSize(size)
                        .setMultipliedLeading(1));
    }

    private static Cell createCell(String text, PdfFont font) {
        return new Cell().setPadding(0.8f)
                .add(new Paragraph(text).setFont(font)
                        .setMultipliedLeading(1));
    }

    public static boolean isShowByHour(int max) {
        String hora = Utils.getHora(new Date(System.currentTimeMillis()));
        hora = hora.substring(0, hora.indexOf(":"));
        int horaNum = 13;
        try {
            horaNum = Integer.parseInt(hora);
        } catch (NumberFormatException e) {

        }
        return (horaNum < max && horaNum > 0);
    }

    public static char encode(char charAt) {
        if (charAt % 2 == 0) {

            switch (charAt) {
                case '0':
                    return 'M';
                case '2':
                    return 'U';
                case '4':
                    return 'T';
                case '6':
                    return 'W';
                case '8':
                    return 'X';
                default:
                    return charAt;
            }

        } else return charAt;
    }

    public static char decode(char charAt) {
        switch (charAt) {
            case 'M':
                return '0';
            case 'U':
                return '2';
            case 'T':
                return '4';
            case 'W':
                return '6';
            case 'X':
                return '8';
            default:
                return charAt;
        }
    }
}

