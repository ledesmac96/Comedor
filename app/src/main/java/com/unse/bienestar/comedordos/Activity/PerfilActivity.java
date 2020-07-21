package com.unse.bienestar.comedordos.Activity;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.unse.bienestar.comedordos.Adapter.ReservasAdapter;
import com.unse.bienestar.comedordos.Database.AlumnoViewModel;
import com.unse.bienestar.comedordos.Database.RolViewModel;
import com.unse.bienestar.comedordos.Database.UsuarioViewModel;
import com.unse.bienestar.comedordos.Dialogos.DatePickerFragment;
import com.unse.bienestar.comedordos.Dialogos.DialogoGeneral;
import com.unse.bienestar.comedordos.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedordos.Modelo.Alumno;
import com.unse.bienestar.comedordos.Modelo.Reserva;
import com.unse.bienestar.comedordos.Modelo.Rol;
import com.unse.bienestar.comedordos.Modelo.Usuario;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.Validador;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;
import com.unse.bienestar.comedordos.Utils.YesNoDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;

public class PerfilActivity extends AppCompatActivity
        implements View.OnClickListener, TextWatcher {

    ImageView btnBack;
    AppCompatImageView imgEditar, imgEditar2;
    CircleImageView imgUser;
    LinearLayout latGeneral, latAlumno, latAdmin, latUser, latVacio, latEstadisticas, latReserva, latInfoUser;
    CardView fabEditar, fabEditar2;
    EditText edtNombre, edtApellido, edtDNI, edtMail, edtAnioIngresoAlu, edtLegajoAlu, edtDomicilio,
            edtProvincia, edtTelefono, edtPais, edtLocalidad, edtBarrio, edtRegistro, edtModificacion,
            edtFechaNac;
    Button btnAltaBaja;
    Spinner spinnerFacultad, spinnerCarrera;
    EditText[] campos;
    ArrayAdapter<String> carreraAdapter;
    ArrayAdapter<String> facultadAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ReservasAdapter mReservasAdapter;
    ArrayList<Reserva> mReservas;
    BarChart barCantidad;
    RolViewModel mRolViewModel;

    DialogoProcesamiento dialog;
    UsuarioViewModel mUsuarioViewModel;
    Usuario mUsuario = null;

    FragmentManager manager = null;
    Object tipoUsuario;

    boolean isEditMode = false, isAdminMode = false, isOtherViste = false;
    int facultadUser = 0, carreraUser = 0, mode = 0, tipoUsuer = -1, idUser = 0, validez = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadViews();

        isAdmin();

        setToolbar();

        loadData();

        loadListener();

        editMode(0);

    }

    private void isAdmin() {
        if (getIntent().getBooleanExtra(ABC.IS_ADMIN_MODE, false)) {
            isAdminMode = getIntent().getBooleanExtra(ABC.IS_ADMIN_MODE, false);
        }
        if (isAdminMode) {
            if (getIntent().getParcelableExtra(ABC.USER_INFO) != null) {
                mUsuario = getIntent().getParcelableExtra(ABC.USER_INFO);
            }
            mRecyclerView.setVisibility(VISIBLE);
            latVacio.setVisibility(View.GONE);
            latReserva.setVisibility(VISIBLE);
        } else {
            latEstadisticas.setVisibility(View.GONE);
            latVacio.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            latReserva.setVisibility(View.GONE);
        }
        mRolViewModel = new RolViewModel(getApplicationContext());
        Rol rol = mRolViewModel.getByPermission(301);
        if (rol != null || !isAdminMode) {
            latInfoUser.setVisibility(VISIBLE);
        } else {
            latInfoUser.setVisibility(View.GONE);
        }
    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setText("Mi perfil");
    }

    private void loadListener() {
        edtFechaNac.setOnClickListener(this);
        fabEditar.setOnClickListener(this);
        fabEditar2.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnAltaBaja.setOnClickListener(this);
        if (tipoUsuer == 0 || mUsuario.getIdUsuario() == 40657677) {
            spinnerFacultad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    spinnerFacultad.setSelection(position);
                    switch (position) {
                        case 0:
                            //FAyA
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.style_spinner, ABC.faya);
                            carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCarrera.setAdapter(carreraAdapter);
                            break;
                        case 1:
                            //FCEyT
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.style_spinner, ABC.fceyt);
                            carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCarrera.setAdapter(carreraAdapter);
                            break;
                        case 2:
                            //FCF
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.style_spinner, ABC.fcf);
                            carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCarrera.setAdapter(carreraAdapter);
                            break;
                        case 3:
                            //FCM
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.style_spinner, ABC.fcm);
                            carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCarrera.setAdapter(carreraAdapter);
                            break;
                        case 4:
                            //FHyCS
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.style_spinner, ABC.fhcys);
                            carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCarrera.setAdapter(carreraAdapter);
                            break;
                    }
                    //Es para determinar que se cambio de carrera
                    if (facultadUser != position && mode == 1)
                        isEditMode = true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }

            });
            spinnerCarrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (mode == 0)
                        spinnerCarrera.setSelection(carreraUser);
                    if (carreraUser != position && mode == 1)
                        isEditMode = true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerFacultad.setSelection(facultadUser);
        }

    }

    private void loadViews() {
        fabEditar = findViewById(R.id.fabEditar);
        fabEditar2 = findViewById(R.id.fabEditar2);
        imgEditar = findViewById(R.id.imgEditar);
        imgEditar2 = findViewById(R.id.imgEditar2);
        latInfoUser = findViewById(R.id.latInfoUser);
        barCantidad = findViewById(R.id.barCantidad);
        latReserva = findViewById(R.id.latReserva);
        latEstadisticas = findViewById(R.id.latEstadisticas);
        latVacio = findViewById(R.id.latVacio);
        mRecyclerView = findViewById(R.id.recycler);
        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtDNI = findViewById(R.id.edtDNI);
        edtMail = findViewById(R.id.edtMail);
        edtAnioIngresoAlu = findViewById(R.id.edtAnioIngrAlu);
        btnBack = findViewById(R.id.imgFlecha);
        spinnerCarrera = findViewById(R.id.spinner2);
        spinnerFacultad = findViewById(R.id.spinner1);
        edtProvincia = findViewById(R.id.edtProvincia);
        edtTelefono = findViewById(R.id.edtCelu);
        edtLocalidad = findViewById(R.id.edtLocalidad);
        edtDomicilio = findViewById(R.id.edtDomicilio);
        edtLegajoAlu = findViewById(R.id.edtLegajo);
        edtPais = findViewById(R.id.edtPais);
        edtBarrio = findViewById(R.id.edtBarrio);
        edtFechaNac = findViewById(R.id.edtFecha);

        latGeneral = findViewById(R.id.layRango);
        latUser = findViewById(R.id.latDatos);
        latAlumno = findViewById(R.id.layAlumno);
        imgUser = findViewById(R.id.imgUserPerfil);
        latAdmin = findViewById(R.id.latAdmin);
        btnAltaBaja = findViewById(R.id.btnAltaBaja);
        edtRegistro = findViewById(R.id.edtFechaRegistro);
        edtModificacion = findViewById(R.id.edtFechaMod);
    }

    private void loadData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            isOtherViste = true;
        } else {
            isOtherViste = false;
        }
        if (isOtherViste) {
            fabEditar2.setVisibility(VISIBLE);
            fabEditar.setVisibility(View.GONE);
        } else {
            fabEditar2.setVisibility(View.GONE);
            fabEditar.setVisibility(VISIBLE);
        }
        mRolViewModel = new RolViewModel(getApplicationContext());
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mUsuarioViewModel = new UsuarioViewModel(getApplicationContext());
        if (!isAdminMode) {
            latAdmin.setVisibility(View.GONE);
            latUser.setVisibility(VISIBLE);
            latReserva.setVisibility(View.GONE);
        } else {
            latAdmin.setVisibility(VISIBLE);
            latUser.setVisibility(VISIBLE);
            edtRegistro.setEnabled(false);
            if (isOtherViste) {
                fabEditar2.setVisibility(VISIBLE);
            } else {
                fabEditar.setVisibility(View.GONE);
            }
            loadInfoReservas();
        }
        campos = new EditText[]{edtNombre, edtApellido, edtMail, edtAnioIngresoAlu, edtProvincia,
                edtPais, edtTelefono, edtLocalidad, edtDomicilio, edtLegajoAlu, edtBarrio};
        manager = getSupportFragmentManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                facultadAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.style_spinner, ABC.facultad);
                facultadAdapter.setDropDownViewResource(R.layout.style_spinner);
                spinnerFacultad.setAdapter(facultadAdapter);
                carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.style_spinner, ABC.faya);
                carreraAdapter.setDropDownViewResource(R.layout.style_spinner);
                spinnerCarrera.setAdapter(carreraAdapter);
            }
        }).start();
        Glide.with(imgUser.getContext()).load(R.drawable.ic_user).into(imgUser);
        loadInfo();
    }

    private void loadInfoReservas() {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&id=%s", ABC.URL_RESERVA_USUARIO, id, key, mUsuario.getIdUsuario());
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuestaReserva(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                latVacio.setVisibility(VISIBLE);
                latEstadisticas.setVisibility(View.GONE);
                ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                        getString(R.string.servidorOff), R.drawable.ic_error);
                dialog.dismiss();
            }
        });
        //Abro dialogo para congelar pantalla
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog_process");
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void procesarRespuestaReserva(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    latVacio.setVisibility(VISIBLE);
                    latEstadisticas.setVisibility(View.GONE);
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    loadInfoReserva(jsonObject);
                    break;
                case 2:
                    latVacio.setVisibility(VISIBLE);
                    latEstadisticas.setVisibility(View.GONE);
                    break;
                case 3:
                    latVacio.setVisibility(VISIBLE);
                    latEstadisticas.setVisibility(View.GONE);
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    latVacio.setVisibility(VISIBLE);
                    latEstadisticas.setVisibility(View.GONE);
                    //No autorizado
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            latVacio.setVisibility(VISIBLE);
            latEstadisticas.setVisibility(View.GONE);
            ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void loadInfoReserva(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mensaje")) {

                JSONArray jsonArray = jsonObject.getJSONArray("mensaje");

                mReservas = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject o = jsonArray.getJSONObject(i);

                    Reserva reserva = Reserva.mapper(o, Reserva.HISTORIAL);

                    mReservas.add(reserva);
                }

                if (mReservas.size() > 0) {
                    mReservasAdapter = new ReservasAdapter(mReservas, getApplicationContext(), ReservasAdapter.USER);
                    mRecyclerView.setAdapter(mReservasAdapter);
                    mRecyclerView.setVisibility(VISIBLE);
                    loadEstadisticas();
                } else {
                    latVacio.setVisibility(View.VISIBLE);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadEstadisticas() {
        final ArrayList<BarEntry> entries;
        final ArrayList<String> entryLabels;
        XAxis xAxis2;
        YAxis leftAxis, rightAxis;

        barCantidad.getDescription().setEnabled(false);
        barCantidad.getLegend().setEnabled(false);
        xAxis2 = barCantidad.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setTextSize(12f);
        //Habilita los labels
        xAxis2.setDrawAxisLine(true);
        xAxis2.setDrawGridLines(false);

        leftAxis = barCantidad.getAxisLeft();
        rightAxis = barCantidad.getAxisRight();

        leftAxis.setTextSize(12f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);


        entries = new ArrayList<>();
        entryLabels = new ArrayList<String>();
        int cantidadRes = 0, cantidadReti = 0, cantidadCancelado = 0, cantidadNoRetirados = 0;
        for (Reserva reserva : mReservas) {
            if (reserva.getDescripcion().equals("RESERVADO")) {
                cantidadRes++;
            } else if (reserva.getDescripcion().equals("CANCELADO")) {
                cantidadCancelado++;
            } else if (reserva.getDescripcion().equals("RETIRADO")) {

                cantidadReti++;
            } else if (reserva.getDescripcion().equals("NO RETIRADO")) {

                cantidadNoRetirados++;
            }
        }
        entries.add(new BarEntry(1, mReservas.size()));
        entryLabels.add("Total");
        entries.add(new BarEntry(2, cantidadReti));
        entryLabels.add("Retiros");
        entries.add(new BarEntry(3, cantidadRes));
        entryLabels.add("Reservas");
        entries.add(new BarEntry(4, cantidadCancelado));
        entryLabels.add("Cancelos");
        entries.add(new BarEntry(5, cantidadNoRetirados));
        entryLabels.add("No Retirados");
        BarDataSet barDataSet2 = new BarDataSet(entries, "");
        barDataSet2.setColors(new int[]{R.color.colorLightBlue, R.color.colorGreen,
                R.color.colorOrange, R.color.colorRed, R.color.colorPink}, getApplicationContext());
        barDataSet2.setValueTextSize(13);
        barDataSet2.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        barDataSet2.setValueTextColor(Color.rgb(155, 155, 155));
        BarData barData2 = new BarData(barDataSet2);
        barData2.setBarWidth(0.9f); // set custom bar width
        barCantidad.setData(barData2);
        barCantidad.setFitBars(true);
        barCantidad.invalidate();
        barCantidad.setScaleEnabled(true);
        barCantidad.setDoubleTapToZoomEnabled(false);
        barCantidad.setBackgroundColor(Color.rgb(255, 255, 255));
        xAxis2.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";
            }
        });
        latEstadisticas.setVisibility(View.VISIBLE);
    }

    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        int idLocal = manager.getValueInt(ABC.MY_ID);
        RolViewModel rolViewModel = new RolViewModel(getApplicationContext());

        if (!isAdminMode) {
            //En caso de ser perfil actual, obtengo los datos desde la BD local
            mUsuario = mUsuarioViewModel.getById(idLocal);

            // mUsuario = new UsuariosRepo(getApplicationContext()).get(idLocal);
            edtDNI.setEnabled(false);
        }
        if (mUsuario != null) {
            //Cargo la vista en base al tipo de mUsuario

            edtDNI.setText(String.valueOf(mUsuario.getIdUsuario()));
            edtNombre.setText(mUsuario.getNombre());
            edtApellido.setText(mUsuario.getApellido());
            edtFechaNac.setText(mUsuario.getFechaNac());
            edtDomicilio.setText(mUsuario.getDomicilio());
            edtBarrio.setText(mUsuario.getBarrio());
            edtLocalidad.setText(mUsuario.getLocalidad());
            edtProvincia.setText(mUsuario.getProvincia());
            edtPais.setText(mUsuario.getPais());
            edtMail.setText(mUsuario.getMail());
            edtTelefono.setText(mUsuario.getTelefono());
            edtRegistro.setText(ABC.getFechaFormat(mUsuario.getFechaRegistro()));
            edtModificacion.setText(ABC.getFechaFormat(mUsuario.getFechaModificacion()));
            //Cargo datos necesarios para operaciones

            validez = mUsuario.getValidez();
            idUser = mUsuario.getIdUsuario();
            tipoUsuer = rolViewModel.getAllByUsuario(idUser).size() > 0 ? 1 : 0;
            loadLayout(tipoUsuer);

            changeButton();

            //Alumnos
            if (tipoUsuer == 0 || mUsuario.getIdUsuario() == 40657677) {
                Alumno alumno = null;
                //Si es modo Admin saco los datos del objeto
                if (isAdminMode && mUsuario instanceof Alumno) {
                    edtLegajoAlu.setText(((Alumno) mUsuario).getLegajo());
                    edtAnioIngresoAlu.setText(((Alumno) mUsuario).getAnio());
                    loadCarrera(mUsuario);
                } else if (isAdminMode && mUsuario instanceof Usuario) {
                    edtLegajoAlu.setText("0/00");
                    edtAnioIngresoAlu.setText("0000");
                } else {
                    AlumnoViewModel alumnoViewModel = new AlumnoViewModel(getApplicationContext());
                    alumno = alumnoViewModel.getById(idLocal);
                    if (alumno != null) {
                        edtLegajoAlu.setText(alumno.getLegajo());
                        edtAnioIngresoAlu.setText(alumno.getAnio());
                        loadCarrera(alumno);
                    }
                }

            }
        }
    }

    private void loadCarrera(Usuario mUsuario) {
        Alumno alumno = (Alumno) mUsuario;
        List<String> facultad = Arrays.asList(ABC.facultad);
        List<String> faya = Arrays.asList(ABC.faya);
        List<String> fceyt = Arrays.asList(ABC.fceyt);
        List<String> fcf = Arrays.asList(ABC.fcf);
        List<String> fcm = Arrays.asList(ABC.fcm);
        List<String> fhcys = Arrays.asList(ABC.fhcys);
        int index = facultad.indexOf(alumno.getFacultad());
        facultadUser = index;
        int index2 = -1;
        spinnerFacultad.setSelection(Math.max(index, 0));
        if (index != -1)
            switch (index) {
                case 0:
                    index2 = faya.indexOf(alumno.getCarrera());
                    break;
                case 1:
                    index2 = fceyt.indexOf(alumno.getCarrera());
                    break;
                case 2:
                    index2 = fcf.indexOf(alumno.getCarrera());
                    break;
                case 3:
                    index2 = fcm.indexOf(alumno.getCarrera());
                    break;
                case 4:
                    index2 = fhcys.indexOf(alumno.getCarrera());
                    break;

            }
        carreraUser = index2;
        // spinnerCarrera.setSelection(carreraUser);
    }

    private void loadLayout(int tipoUsuario) {
        switch (tipoUsuario) {
            case 0:
                latGeneral.setVisibility(VISIBLE);
                latAlumno.setVisibility(VISIBLE);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAltaBaja:
                altaBajaUser();
                break;
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.edtFecha:
                elegirFechaNacimiento();
                break;
            case R.id.fabEditar:
            case R.id.fabEditar2:
                openModeEditor();
                break;
        }
    }


    private void openModeEditor() {
        ObjectAnimator.ofFloat(fabEditar, "rotation", 0f, 360f).setDuration(600).start();
        activateEditMode();
    }

    private void altaBajaUser() {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo(getString(R.string.advertencia))
                .setDescripcion(String.format(getString(R.string.usuarioEliminar), validez == 0 ? "alta" : "baja",
                        mUsuario.getNombre(),
                        mUsuario.getApellido()))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        if (validez == 0)
                            validez = 1;
                        else
                            validez = 0;
                        baja(validez);
                    }

                    @Override
                    public void no() {

                    }
                })
                .setIcono(R.drawable.ic_advertencia)
                .setTipo(DialogoGeneral.TIPO_SI_NO);
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.show(getSupportFragmentManager(), "dialog_ad");
    }

    private void baja(int val) {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int idLocal = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?key=%s&id=%s&idU=%s&val=%s", ABC.URL_USUARIO_ELIMINAR, key,
                idLocal, mUsuario.getIdUsuario(), val);
        StringRequest requestImage = new StringRequest(Request.Method.DELETE, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuestaEliminar(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                        getString(R.string.servidorOff), R.drawable.ic_error);
                dialog.dismiss();
            }
        });
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog_process");
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(requestImage);
    }

    private void procesarRespuestaEliminar(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    String texto = null;
                    if (validez == 0)
                        texto = getString(R.string.usuarioDeshabilitado);
                    else
                        texto = getString(R.string.usuarioHabilitado);
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            texto, R.drawable.ic_exito);
                    changeButton();
                    break;
                case 2:
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.usuarioNoExiste), R.drawable.ic_error);
                    break;
                case 4:
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_error);
                    break;
                case 3:
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private String getCarrera(int selectedItemPosition) {
        switch (selectedItemPosition) {
            case 0:
                return ABC.faya[spinnerCarrera.getSelectedItemPosition()];
            case 1:
                return ABC.fceyt[spinnerCarrera.getSelectedItemPosition()];
            case 2:
                return ABC.fcf[spinnerCarrera.getSelectedItemPosition()];
            case 3:
                return ABC.fcm[spinnerCarrera.getSelectedItemPosition()];
            case 4:
                return ABC.fhcys[spinnerCarrera.getSelectedItemPosition()];
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                    getString(R.string.cambioPrimeroGuardar), R.drawable.ic_advertencia);
        } else
            super.onBackPressed();
    }

    private void activateEditMode() {
        if (isEditMode) {
            save();
            return;
        }
        if (mode == 0)
            mode = 1;
        else
            mode = 0;
        editMode(mode);
    }

    private void editMode(int mode) {
        if (mode != 0) {
            if (isOtherViste) {
                //Drawable drawable = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_save);
                Glide.with(imgEditar2.getContext()).load(R.drawable.ic_save).into(imgEditar2);
            } else {
                Glide.with(imgEditar.getContext()).load(R.drawable.ic_save).into(imgEditar);
            }
            //fabEditar.setImageResource(R.drawable.ic_save);
            edtFechaNac.setOnClickListener(this);
            edtFechaNac.setEnabled(true);
            spinnerFacultad.setEnabled(true);
            spinnerCarrera.setEnabled(true);
        } else {
            if (isOtherViste) {
                // Drawable drawable = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_edit);
                //fabEditar2.setBackgroundDrawable(drawable);
                Glide.with(imgEditar2.getContext()).load(R.drawable.ic_edit).into(imgEditar2);
            } else {
                Glide.with(imgEditar.getContext()).load(R.drawable.ic_edit).into(imgEditar);
            }
            //fabEditar.setImageResource(R.drawable.ic_edit);
            edtFechaNac.setOnClickListener(null);
            edtFechaNac.setEnabled(false);
            spinnerFacultad.setEnabled(false);
            spinnerCarrera.setEnabled(false);
        }
        for (EditText e : campos) {
            if (mode == 0) {
                e.setEnabled(false);
                e.setBackgroundColor(getResources().getColor(R.color.transparente));
                e.removeTextChangedListener(null);
            } else {
                e.setEnabled(true);
                e.setBackground(getResources().getDrawable(R.drawable.edit_text_logreg));
                e.addTextChangedListener(this);
            }
        }

    }

    private void save() {
        Validador validador = new Validador(getApplicationContext());

        String fecha = edtFechaNac.getText().toString().trim();
        String nombre = edtNombre.getText().toString().trim();
        String apellido = edtApellido.getText().toString().trim();
        String dni = edtDNI.getText().toString().trim();
        String mail = edtMail.getText().toString().trim();
        String faculta = ABC.facultad[spinnerFacultad.getSelectedItemPosition()].trim();
        String carrera = getCarrera(spinnerFacultad.getSelectedItemPosition()).trim();
        String anioIngreso2 = edtAnioIngresoAlu.getText().toString().trim();
        String domicilio = edtDomicilio.getText().toString().trim();
        String telefono = edtTelefono.getText().toString().trim();
        String pais = edtPais.getText().toString().trim();
        String provincia = edtProvincia.getText().toString().trim();
        String localidad = edtLocalidad.getText().toString().trim();
        String legajo = edtLegajoAlu.getText().toString().trim();
        String barrio = edtBarrio.getText().toString().trim();
        //Creo un modelo para posterior almacenarlo local
        mUsuario = new Usuario(Integer.parseInt(dni), nombre, apellido, fecha, pais, provincia, localidad,
                domicilio, barrio, telefono, mail, mUsuario.getFechaRegistro(), mUsuario.getFechaModificacion(),
                mUsuario.getValidez());

        idUser = Integer.parseInt(dni);

        if (validador.validarDNI(edtDNI) && validador.validarTelefono(edtTelefono) && validador.validarMail(edtMail)
                && validador.validarFecha(edtFechaNac) && validador.validarDomicilio(edtDomicilio)
                && !validador.validarNombresEdt(edtNombre, edtApellido, edtPais,
                edtProvincia, edtLocalidad, edtBarrio)) {
            switch (tipoUsuer) {
                case 0:
                case 1:
                    if (!validador.noVacio(faculta) && !validador.noVacio(carrera) &&
                            validador.validarAnio(edtAnioIngresoAlu) && validador.validarLegajo(edtLegajoAlu)) {
                        sendServer(processString(dni, nombre, apellido, fecha, pais, provincia, localidad,
                                domicilio, barrio, telefono, mail, carrera, faculta, anioIngreso2
                                , legajo));
                        tipoUsuario = new Alumno(mUsuario.getIdUsuario(), mUsuario.getNombre(), mUsuario.getApellido(),
                                mUsuario.getFechaNac(), mUsuario.getPais(), mUsuario.getProvincia(), mUsuario.getLocalidad(),
                                mUsuario.getDomicilio(), mUsuario.getBarrio(), mUsuario.getTelefono(),
                                mUsuario.getMail(), mUsuario.getFechaRegistro(), mUsuario.getFechaModificacion(),
                                mUsuario.getValidez(), Integer.parseInt(dni), carrera, faculta,
                                anioIngreso2, legajo);
                    }
                    break;
            }

        } else ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                getString(R.string.camposInvalidos), R.drawable.ic_error);
    }

    public String processString(String dni, String nombre, String apellido, String fecha,
                                String pais, String provincia, String localidad, String domicilio,
                                String barrio, String telefono, String mail, String carrera,
                                String facultad, String anioIng, String legajo) {
        String resp = "";
        resp = String.format(ABC.dataAlumno, dni, nombre, apellido, fecha, pais, provincia,
                localidad, domicilio, carrera, facultad,
                anioIng, legajo, mail, telefono, barrio);

        return resp;
    }

    public void sendServer(String data) {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int idLocal = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s%s&key=%s&id=%s", ABC.URL_USUARIO_ACTUALIZAR, data, key, idLocal);
        //URL = URL.replaceAll(" ","%20");
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                        getString(R.string.servidorOff), R.drawable.ic_error);
                dialog.dismiss();

            }

        }
        ){
        };
        //Abro dialogo para congelar pantalla
        dialog = new DialogoProcesamiento();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog_process");
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


    private void procesarRespuesta(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.perfilActualizado), R.drawable.ic_exito);
                    isEditMode = false;
                    openModeEditor();
                    if (!isAdminMode)
                        updateInBD(mUsuario, tipoUsuario);
                    break;
                case 2:
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.actualizadoError), R.drawable.ic_error);
                    break;
                case 4:
                    //Ya existe
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.camposInvalidos), R.drawable.ic_error);
                    break;
                case 5:
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.usuarioNoExiste), R.drawable.ic_error);
                    break;
                case 3:
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 100:
                    //No autorizado
                    ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showCustomToast(PerfilActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);
        }
    }

    private void updateInBD(Usuario mUsuario, Object tipo) {
        mUsuarioViewModel.update(mUsuario);
        if (tipo instanceof Alumno) {
            AlumnoViewModel alumnoViewModel = new AlumnoViewModel(getApplicationContext());
            alumnoViewModel.update((Alumno) tipo);
        }
        loadInfo();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mode == 1)
            isEditMode = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void elegirFechaNacimiento() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String mes, dia;
                month = month + 1;
                if (month < 10) {
                    mes = "0" + month;
                } else
                    mes = String.valueOf(month);
                if (day < 10)
                    dia = "0" + day;
                else
                    dia = String.valueOf(day);
                final String selectedDate = year + "-" + mes + "-" + dia;
                if (!edtFechaNac.getText().toString().equals(selectedDate))
                    isEditMode = true;
                edtFechaNac.setText(selectedDate);
            }
        });
        newFragment.show(getFragmentManager(), "datePicker");

    }

    private void changeButton() {
        if (validez == 0)
            btnAltaBaja.setText(getString(R.string.btnAlta));
        else btnAltaBaja.setText(getString(R.string.btnBaja));
    }
}
