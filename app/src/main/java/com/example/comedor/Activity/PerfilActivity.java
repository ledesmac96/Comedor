package com.example.comedor.Activity;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
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
import com.example.comedor.Database.AlumnoViewModel;
import com.example.comedor.Database.RolViewModel;
import com.example.comedor.Database.UsuarioViewModel;
import com.example.comedor.Dialogos.DatePickerFragment;
import com.example.comedor.Dialogos.DialogoGeneral;
import com.example.comedor.Dialogos.DialogoProcesamiento;
import com.example.comedor.Modelo.Alumno;
import com.example.comedor.Modelo.Usuario;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.Validador;
import com.example.comedor.Utils.VolleySingleton;
import com.example.comedor.Utils.YesNoDialogListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;
import static com.example.comedor.Utils.Utils.facultad;
import static com.example.comedor.Utils.Utils.faya;
import static com.example.comedor.Utils.Utils.fceyt;
import static com.example.comedor.Utils.Utils.fcf;
import static com.example.comedor.Utils.Utils.fcm;
import static com.example.comedor.Utils.Utils.fhcys;

public class PerfilActivity extends AppCompatActivity
        implements View.OnClickListener, TextWatcher {

    ImageView btnBack;
    CircleImageView imgUser;
    LinearLayout latGeneral, latAlumno, latAdmin, latUser;
    FloatingActionButton fabEditar;
    EditText edtNombre, edtApellido, edtDNI, edtMail, edtAnioIngresoAlu, edtLegajoAlu, edtDomicilio,
            edtProvincia, edtTelefono, edtPais, edtLocalidad, edtBarrio, edtRegistro, edtModificacion,
            edtFechaNac;
    Button btnAltaBaja;
    Spinner spinnerFacultad, spinnerCarrera;
    EditText[] campos;
    ArrayAdapter<String> carreraAdapter;
    ArrayAdapter<String> facultadAdapter;


    DialogoProcesamiento dialog;
    UsuarioViewModel mUsuarioViewModel;
    Usuario mUsuario = null;

    FragmentManager manager = null;
    Object tipoUsuario;

    boolean isEditMode = false, isReadyForLoad = false, isAdminMode = false;
    int facultadUser = 0, carreraUser = 0, mode = 0, tipoUsuer = -1, idUser = 0, validez = -1, position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        isAdmin();

        loadViews();

        setToolbar();

        loadData();

        loadListener();

        editMode(0);

    }

    private void isAdmin() {
        if (getIntent().getBooleanExtra(Utils.IS_ADMIN_MODE, false)) {
            isAdminMode = getIntent().getBooleanExtra(Utils.IS_ADMIN_MODE, false);
        }
        if (isAdminMode) {
            if (getIntent().getParcelableExtra(Utils.USER_INFO) != null) {
                mUsuario = getIntent().getParcelableExtra(Utils.USER_INFO);
            }
        }
    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setText("");
    }

    private void loadListener() {
        edtFechaNac.setOnClickListener(this);
        fabEditar.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnAltaBaja.setOnClickListener(this);
        if (tipoUsuer == 0) {
            spinnerFacultad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    spinnerFacultad.setSelection(position);
                    switch (position) {
                        case 0:
                            //FAyA
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_spinner_item, faya);
                            carreraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCarrera.setAdapter(carreraAdapter);
                            break;
                        case 1:
                            //FCEyT
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_spinner_item, fceyt);
                            carreraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCarrera.setAdapter(carreraAdapter);
                            break;
                        case 2:
                            //FCF
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_spinner_item, fcf);
                            carreraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCarrera.setAdapter(carreraAdapter);
                            break;
                        case 3:
                            //FCM
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_spinner_item, fcm);
                            carreraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCarrera.setAdapter(carreraAdapter);
                            break;
                        case 4:
                            //FHyCS
                            carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_spinner_item, fhcys);
                            carreraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        fabEditar = findViewById(R.id.fab);
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
        mUsuarioViewModel = new UsuarioViewModel(getApplicationContext());
        if (!isAdminMode) {
            latAdmin.setVisibility(View.GONE);
            latUser.setVisibility(VISIBLE);
        } else {
            latAdmin.setVisibility(VISIBLE);
            latUser.setVisibility(VISIBLE);
            edtRegistro.setEnabled(false);
        }
        campos = new EditText[]{edtNombre, edtApellido, edtMail, edtAnioIngresoAlu, edtProvincia,
                edtPais, edtTelefono, edtLocalidad, edtDomicilio, edtLegajoAlu, edtBarrio};
        manager = getSupportFragmentManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                facultadAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, facultad);
                facultadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFacultad.setAdapter(facultadAdapter);
                carreraAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, faya);
                carreraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCarrera.setAdapter(carreraAdapter);
            }
        }).start();
        loadInfo();
    }

    private void loadInfo() {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        int idLocal = manager.getValueInt(Utils.MY_ID);
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
            edtRegistro.setText(Utils.getFechaFormat(mUsuario.getFechaRegistro()));
            edtModificacion.setText(Utils.getFechaFormat(mUsuario.getFechaModificacion()));
            //Cargo datos necesarios para operaciones

            validez = mUsuario.getValidez();
            idUser = mUsuario.getIdUsuario();
            tipoUsuer = rolViewModel.getAllByUsuario(idUser).size() > 0 ? 1 : 0;
            loadLayout(tipoUsuer);

            //Alumnos
            if (tipoUsuer == 0) {
                Alumno alumno = null;
                //Si es modo Admin saco los datos del objeto
                if (isAdminMode) {
                    edtLegajoAlu.setText(((Alumno) mUsuario).getLegajo());
                    edtAnioIngresoAlu.setText(((Alumno) mUsuario).getAnio());
                    loadCarrera(mUsuario);
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
        List<String> facultad = Arrays.asList(Utils.facultad);
        List<String> faya = Arrays.asList(Utils.faya);
        List<String> fceyt = Arrays.asList(Utils.fceyt);
        List<String> fcf = Arrays.asList(Utils.fcf);
        List<String> fcm = Arrays.asList(Utils.fcm);
        List<String> fhcys = Arrays.asList(Utils.fhcys);
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
            case R.id.fab:
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
        String key = manager.getValueString(Utils.TOKEN);
        int idLocal = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?key=%s&id=%s&idU=%s&val=%s", Utils.URL_USUARIO_ELIMINAR, key,
                idLocal, mUsuario.getIdUsuario(), val);
        StringRequest requestImage = new StringRequest(Request.Method.DELETE, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuestaEliminar(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(getApplicationContext(), getString(R.string.servidorOff));
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
                    Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    String texto = null;
                    if (validez == 0)
                        texto = getString(R.string.usuarioDeshabilitado);
                    else
                        texto = getString(R.string.usuarioHabilitado);
                    Utils.showToast(getApplicationContext(), texto);
                    changeButton();
                    break;
                case 2:
                    Utils.showToast(getApplicationContext(), getString(R.string.usuarioNoExiste));
                    break;
                case 4:
                    Utils.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
                    break;
                case 3:
                    Utils.showToast(getApplicationContext(), getString(R.string.tokenInvalido));
                    break;
                case 100:
                    //No autorizado
                    Utils.showToast(getApplicationContext(), getString(R.string.tokenInexistente));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private String getCarrera(int selectedItemPosition) {
        switch (selectedItemPosition) {
            case 0:
                return faya[spinnerCarrera.getSelectedItemPosition()];
            case 1:
                return fceyt[spinnerCarrera.getSelectedItemPosition()];
            case 2:
                return fcf[spinnerCarrera.getSelectedItemPosition()];
            case 3:
                return fcm[spinnerCarrera.getSelectedItemPosition()];
            case 4:
                return fhcys[spinnerCarrera.getSelectedItemPosition()];
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            Utils.showToast(getApplicationContext(), getString(R.string.cambioPrimeroGuardar));
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
            fabEditar.setImageResource(R.drawable.ic_save);
            edtFechaNac.setOnClickListener(this);
            spinnerFacultad.setEnabled(true);
            spinnerCarrera.setEnabled(true);
        } else {
            fabEditar.setImageResource(R.drawable.ic_edit_);
            edtFechaNac.setOnClickListener(null);
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
        String faculta = facultad[spinnerFacultad.getSelectedItemPosition()].trim();
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

        } else Utils.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
    }

    public String processString(String dni, String nombre, String apellido, String
            fecha, String pais, String provincia, String localidad, String domicilio,
                                String barrio, String telefono, String mail, String carrera, String facultad, String anioIng,
                                String legajo) {
        String resp = "";
        resp = String.format(Utils.dataAlumno, dni, nombre, apellido, fecha, pais, provincia,
                localidad, domicilio, carrera, facultad,
                anioIng, legajo, mail, telefono, barrio);


        return resp;
    }

    public void sendServer(String data) {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(Utils.TOKEN);
        int idLocal = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s%s&key=%s&id=%s", Utils.URL_USUARIO_ACTUALIZAR, data, key, idLocal);
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Utils.showToast(getApplicationContext(), getString(R.string.servidorOff));
                dialog.dismiss();

            }
        });
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
                    Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    Utils.showToast(getApplicationContext(), getString(R.string.perfilActualizado));
                    isEditMode = false;
                    openModeEditor();
                    if (!isAdminMode)
                        updateInBD(mUsuario, tipoUsuario);
                    break;
                case 2:
                    Utils.showToast(getApplicationContext(), getString(R.string.actualizadoError));
                    break;
                case 4:
                    //Ya existe
                    Utils.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
                    break;
                case 5:
                    Utils.showToast(getApplicationContext(), getString(R.string.usuarioNoExiste));
                    break;
                case 3:
                    Utils.showToast(getApplicationContext(), getString(R.string.tokenInvalido));
                    break;
                case 100:
                    //No autorizado
                    Utils.showToast(getApplicationContext(), getString(R.string.tokenInexistente));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
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
