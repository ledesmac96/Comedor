package com.unse.bienestar.comedordos.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.unse.bienestar.comedordos.Database.RolViewModel;
import com.unse.bienestar.comedordos.Database.UsuarioViewModel;
import com.unse.bienestar.comedordos.Dialogos.DialogoGeneral;
import com.unse.bienestar.comedordos.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedordos.Fragment.EstadisticasFragment;
import com.unse.bienestar.comedordos.Fragment.GestionMenuFragment;
import com.unse.bienestar.comedordos.Fragment.GestionOpiniones;
import com.unse.bienestar.comedordos.Fragment.GestionReservasFragment;
import com.unse.bienestar.comedordos.Fragment.GestionUsuarioFragment;
import com.unse.bienestar.comedordos.Fragment.InicioFragment;
import com.unse.bienestar.comedordos.Fragment.MisReservasFragment;
import com.unse.bienestar.comedordos.Modelo.Rol;
import com.unse.bienestar.comedordos.Modelo.Usuario;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.Utils.PreferenciasManager;
import com.unse.bienestar.comedordos.Utils.ABC;
import com.unse.bienestar.comedordos.Utils.Validador;
import com.unse.bienestar.comedordos.Utils.VolleySingleton;
import com.unse.bienestar.comedordos.Utils.YesNoDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar mToolbar;
    View headerView;
    Fragment mFragment;
    int itemSelecionado = -1, idRes = 0, dniUser = 0;
    ImageView imgBienestar;
    TextView txtNombre, txtTitulo;
    UsuarioViewModel mUsuarioViewModel;
    PreferenciasManager mPreferenciasManager;
    DialogoProcesamiento dialog;
    boolean reservar = false;
    RolViewModel mRolViewModel;

    EditText edtDescripcion;
    Button btnEnviar;
    Dialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPreferenciasManager = new PreferenciasManager(getApplicationContext());

        if (mPreferenciasManager.getValue(ABC.IS_LOCK)) {
            startActivity(new Intent(getApplicationContext(), LockedActivity.class));
            finishAffinity();
        } else {

            loadViews();

            comprobarNavigationView();

            setToolbar();

            loadData();

            checkInfo();

            sendTokenToServer();

            //decodeQR("COMEDOR UNIVERSITARIO - BIENESTAR ESTUDIANTIL\n¡MUCHAS GRACIAS POR RESERVAR!\n#TMW57W77-10#");

        }
    }

    private void sendTokenToServer() {
        if (mPreferenciasManager.getValue(ABC.IS_TOKEN)) {
            String fcmToken = FirebaseInstanceId.getInstance().getToken();
            if (fcmToken != null && !fcmToken.equals("")) {
                PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
                final int idLocal = manager.getValueInt(ABC.MY_ID);
                String URL = String.format("%s?tok=%s&id=%s", ABC.URL_USUARIO_TEST, fcmToken, idLocal);
                StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mPreferenciasManager.setValue(ABC.IS_TOKEN, false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });
                //Abro dialogo para congelar pantalla
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentIntegrator = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentIntegrator != null) {
            if (intentIntegrator.getContents() == null) {
                ABC.showCustomToast(MainActivity.this, getApplicationContext(), "Cancelaste", R.drawable.ic_error);

            } else {
                String contenido = intentIntegrator.getContents();
                decodeQR(contenido);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void decodeQR(String contenido) {
        Pattern pattern = Pattern.compile("#[0-9A-Z]+-");
        Matcher matcher = pattern.matcher(contenido);
        String dni = "";
        if (matcher.find()) {
            dni = matcher.group().replace("#", "").replace("-", "");
        }
        pattern = Pattern.compile("-[0-9]+#");
        matcher = pattern.matcher(contenido);
        String idReserva = "";
        if (matcher.find()) {
            idReserva = matcher.group().replace("#", "").replace("-", "");
        }
        String[] dniDecode = new String[dni.length()];
        for (int i = 0; i < dniDecode.length; i++) {
            dniDecode[i] = String.valueOf(ABC.decode(dni.charAt(i)));
        }
        StringBuilder dniModif = new StringBuilder();
        for (int i = 0; i < dniDecode.length; i++) {
            dniModif.append(dniDecode[i]);
        }
        dni = dniModif.toString();
        try {
            if (!dni.equals("") && !idReserva.equals("")) {
                idRes = Integer.parseInt(idReserva);
                dniUser = Integer.parseInt(dni);
                reservar = true;
            } else {
                ABC.showCustomToast(MainActivity.this, getApplicationContext(),
                        getString(R.string.qrNoData), R.drawable.ic_error);
            }
        } catch (NumberFormatException e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (reservar) {
            if (dniUser != 0 && idRes != 0) {
                changeEstado(String.valueOf(idRes), String.valueOf(dniUser));
                reservar = false;
            }
        }

    }

    private void changeEstado(String idReserva, String dni) {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&ir=%s&ie=%s&e=%s&d=%s", ABC.URL_RESERVA_ACTUALIZAR, id, key, idReserva, id, 3, dni);
        StringRequest request = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuestaActualizar(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ABC.showCustomToast(MainActivity.this, getApplicationContext(),
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

    private void procesarRespuestaActualizar(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case -1:
                    ABC.showCustomToast(MainActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    dialogo(true);
                    break;
                case 2:
                    ABC.showCustomToast(MainActivity.this, getApplicationContext(),
                            getString(R.string.errorActualizar), R.drawable.ic_error);
                    dialogo(false);
                    break;
                case 3:
                    ABC.showCustomToast(MainActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 4:
                    ABC.showCustomToast(MainActivity.this, getApplicationContext(),
                            getString(R.string.camposIncompletos), R.drawable.ic_error);
                    break;
                case 5:
                    dialogoCustom(5);
                    break;
                case 6:
                    dialogoCustom(6);
                    break;
                case 7:
                    dialogoCustom(7);
                    break;
                case 8:
                    dialogoCustom(8);
                    break;
                case 9:
                    dialogoCustom(9);
                    break;
                case 10:
                    dialogoCustom(10);
                    break;
                case 11:
                    dialogoCustom(11);
                    break;
                case 12:
                    dialogoCustom(12);
                    break;
                case 13:
                    dialogoCustom(13);
                    break;
                case 14:
                    dialogoCustom(14);
                    break;
                case 15:
                    dialogoCustom(15);
                    break;
                case 100:
                    //No autorizado
                    ABC.showCustomToast(MainActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showCustomToast(MainActivity.this, getApplicationContext(),
                    getString(R.string.errorInternoAdmin), R.drawable.ic_error);

        }
    }

    private void dialogoCustom(int valor) {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo(getString(
                        valor == 5 ? R.string.yaRetiro :
                                valor == 6 ? R.string.reservaNoExiste :
                                        (valor == 7 || valor == 8 || valor == 9 || valor == 10 || valor == 11) ? R.string.noMenuActual :
                                                valor == 12 ? R.string.cambioUsuario :
                                                        (valor == 13 || valor == 14) ? R.string.reservaYaComprobada :
                                                                valor == 15 ? R.string.error :
                                                                        R.string.error))
                .setDescripcion(getString(
                        valor == 5 ? R.string.reservaRetirada :
                                valor == 6 ? R.string.reservaNoExisteDesc :
                                        valor == 7 ? R.string.reservaAnteriorNoRetirada :
                                                valor == 8 ? R.string.reservaAnteriorCancelada :
                                                        valor == 9 ? R.string.reservaAnteriorRetirada :
                                                                valor == 10 ? R.string.reservaAnteriorYaNoRetirada :
                                                                        valor == 11 ? R.string.reservaNoExisteDesc :
                                                                                valor == 12 ? R.string.reservaNoDNI :
                                                                                        valor == 13 ? R.string.reservaHoyCancelada :
                                                                                                valor == 14 ? R.string.reservaHoyNoRetirada :
                                                                                                        valor == 15 ? R.string.menuCerrado :
                                                                                                                R.string.menuCerrado))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {

                    }

                    @Override
                    public void no() {

                    }
                })
                .setIcono((valor == 5 || valor == 6 || valor == 12 || valor == 13 || valor == 14 || valor == 15) ? R.drawable.ic_error :
                        (valor == 7 || valor == 8 || valor == 9 || valor == 10 | valor == 11) ? R.drawable.ic_advertencia :
                                R.drawable.ic_error)
                .setTipo(DialogoGeneral.TIPO_ACEPTAR);
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.show(getSupportFragmentManager(), "dialog_ad");
    }

    private void dialogo(boolean estado) {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo(getString(estado ? R.string.confirmado : R.string.noConfirmado))
                .setDescripcion(getString(estado ? R.string.confirmadoReserva : R.string.confirmadoReservaError))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {

                    }

                    @Override
                    public void no() {

                    }
                })
                .setIcono(estado ? R.drawable.ic_exito : R.drawable.ic_advertencia)
                .setTipo(DialogoGeneral.TIPO_ACEPTAR);
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.show(getSupportFragmentManager(), "dialog_ad");
    }

    private void checkInfo() {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        String key = manager.getValueString(ABC.TOKEN);
        final int idLocal = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s?key=%s&id=%s&idU=%s", ABC.URL_USUARIO_CHECK, key, idLocal, idLocal);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuesta(response, idLocal);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        //Abro dialogo para congelar pantalla
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void procesarRespuesta(String response, int idUser) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int estado = jsonObject.getInt("estado");
            switch (estado) {
                case 1:
                    UsuarioViewModel usuarioViewModel = new UsuarioViewModel(getApplicationContext());
                    Usuario usuario = usuarioViewModel.getById(idUser);
                    int validez = jsonObject.getInt("mensaje");
                    usuario.setValidez(validez);
                    usuarioViewModel.update(usuario);
                    if (validez == 0) {
                        mPreferenciasManager.setValue(ABC.IS_LOCK, true);
                        startActivity(new Intent(getApplicationContext(), LockedActivity.class));
                        finishAffinity();
                    }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        mRolViewModel = new RolViewModel(getApplicationContext());
        mPreferenciasManager = new PreferenciasManager(getApplicationContext());
        mUsuarioViewModel = new UsuarioViewModel(getApplicationContext());
        int dni = mPreferenciasManager.getValueInt(ABC.MY_ID);
        Usuario usuario = mUsuarioViewModel.getById(dni);
        if (usuario != null) {
            txtNombre.setText(String.format("%s %s", usuario.getNombre(), usuario.getApellido()));
        }
        updateMenu();

    }

    private void loadViews() {
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
        txtTitulo = findViewById(R.id.txtTitulo);
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        headerView = navigationView.inflateHeaderView(R.layout.cabecera_drawer);
        imgBienestar = headerView.findViewById(R.id.logoBienestar);
        txtNombre = headerView.findViewById(R.id.txtNombreUser);
    }

    private void comprobarNavigationView() {
        if (navigationView != null) {
            prepararDrawer(navigationView);
            seleccionarItem(navigationView.getMenu().getItem(0));
        }
    }

    private void prepararDrawer(NavigationView navigationView) {
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        seleccionarItem(menuItem);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
        navigationView.setCheckedItem(R.id.item_inicio);
        changeMenuIcon();
    }

    private void changeMenuIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.item_inicio).setIcon(R.drawable.ic_home);
            menu.findItem(R.id.item_reservas).setIcon(R.drawable.ic_reserva);
            menu.findItem(R.id.item_mis_reservas).setIcon(R.drawable.ic_reserva);
            menu.findItem(R.id.item_users).setIcon(R.drawable.ic_users);
            menu.findItem(R.id.item_menu).setIcon(R.drawable.ic_menu_2);
            menu.findItem(R.id.item_sugerencias_gestion).setIcon(R.drawable.ic_corazon);
            menu.findItem(R.id.item_estad).setIcon(R.drawable.ic_estadisticas);
            menu.findItem(R.id.item_perfil).setIcon(R.drawable.ic_perfil);
            menu.findItem(R.id.item_about).setIcon(R.drawable.ic_b_bienestar);
            menu.findItem(R.id.item_faqs).setIcon(R.drawable.ic_faqs);
            menu.findItem(R.id.item_sugerencias).setIcon(R.drawable.ic_corazon);
            menu.findItem(R.id.item_logout).setIcon(R.drawable.ic_salida);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void seleccionarItem(MenuItem itemDrawer) {
        Fragment fragmentoGenerico = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (itemDrawer.getItemId()) {
            case R.id.item_inicio:
                fragmentoGenerico = new InicioFragment();
                ((InicioFragment) fragmentoGenerico).setActivity(MainActivity.this);
                ((InicioFragment) fragmentoGenerico).setContext(getApplicationContext());
                ((InicioFragment) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
                break;
            case R.id.item_reservas:
                fragmentoGenerico = new GestionReservasFragment();
                break;
            case R.id.item_users:
                fragmentoGenerico = new GestionUsuarioFragment();
                ((GestionUsuarioFragment) fragmentoGenerico).setContext(getApplicationContext());
                ((GestionUsuarioFragment) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
                ((GestionUsuarioFragment) fragmentoGenerico).setActivity(MainActivity.this);
                break;
            case R.id.item_mis_reservas:
                fragmentoGenerico = new MisReservasFragment();
                ((MisReservasFragment) fragmentoGenerico).setContext(getApplicationContext());
                ((MisReservasFragment) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
                //
                break;
            case R.id.item_perfil:
                startActivity(new Intent(getApplicationContext(), PerfilActivity.class));
                break;
            case R.id.item_menu:
                fragmentoGenerico = new GestionMenuFragment();
                ((GestionMenuFragment) fragmentoGenerico).setContext(getApplicationContext());
                ((GestionMenuFragment) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
                break;
            case R.id.item_estad:
                fragmentoGenerico = new EstadisticasFragment();
                ((EstadisticasFragment) fragmentoGenerico).setContext(getApplicationContext());
                ((EstadisticasFragment) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
                break;
            case R.id.item_about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                break;
            case R.id.item_faqs:
                startActivity(new Intent(getApplicationContext(), FaqsActivity.class));
                break;
            case R.id.item_sugerencias_gestion:
                fragmentoGenerico = new GestionOpiniones();
                ((GestionOpiniones) fragmentoGenerico).setContext(getApplicationContext());
                ((GestionOpiniones) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
                break;
            case R.id.item_logout:
                logout();
                break;
            case R.id.item_sugerencias:
                openDialog();
                break;

        }

        if (fragmentoGenerico != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.contenedor_principal, fragmentoGenerico)
                    .commit();
        }

        mFragment = fragmentoGenerico;

        itemSelecionado = itemDrawer.getItemId();

    }

    private void openDialog() {
        dialogo = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_sugerencia, null);
        edtDescripcion = view.findViewById(R.id.edtDescripcion);
        btnEnviar = view.findViewById(R.id.btnAceptar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOpinion();
            }
        });
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialogo.getWindow() != null)
            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogo.setContentView(view);

        dialogo.show();
    }

    private void checkOpinion() {
        String descripcion = edtDescripcion.getText().toString();
        Validador validador = new Validador(getApplicationContext());
        if (validador.validarNombres(edtDescripcion)) {
            sendOpinion(descripcion);
        } else {
            ABC.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
        }
    }

    private void sendOpinion(final String descripcion) {
        PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
        final String key = manager.getValueString(ABC.TOKEN);
        final int id = manager.getValueInt(ABC.MY_ID);
        String URL = String.format("%s", ABC.URL_SUGERENCIA_INSERTAR);
        //?idU=%s&key=%s&iu=%s&d=%s
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                procesarRespuesta(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ABC.showToast(getApplicationContext(), getString(R.string.servidorOff));
                dialog.dismiss();

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("key", key);
                params.put("iu", String.valueOf(id));
                params.put("d", descripcion);
                params.put("idU", String.valueOf(id));
                return params;
            }
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
                    ABC.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
                    break;
                case 1:
                    //Exito
                    ABC.showToast(getApplicationContext(), getString(R.string.enviado));
                    dialogo.dismiss();
                    break;
                case 2:
                    ABC.showToast(getApplicationContext(), getString(R.string.errorSugerencia));
                    break;
                case 3:
                    ABC.showToast(getApplicationContext(), getString(R.string.tokenInvalido));
                    break;
                case 4:
                    ABC.showToast(getApplicationContext(), getString(R.string.camposInvalidos));
                    break;
                case 100:
                    //No autorizado
                    ABC.showToast(getApplicationContext(), getString(R.string.tokenInexistente));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ABC.showToast(getApplicationContext(), getString(R.string.errorInternoAdmin));
        }
    }

    private void updateMenu() {
        Menu menu = navigationView.getMenu();
        int[] idItem = new int[]{R.id.item_reservas, R.id.item_users, R.id.item_menu, R.id.item_estad, R.id.item_sugerencias_gestion};
        int[] idRol = new int[]{400, 300, 200, 100, 600};
        int i = 0;
        for (Integer integer : idItem) {
            MenuItem item = menu.findItem(integer);
            Rol rol = mRolViewModel.getByPermission(idRol[i]);
            if (rol == null) {
                item.setVisible(false);
            }
            i++;

        }
        MenuItem item = menu.findItem(R.id.item_mis_reservas);
        Rol rol = mRolViewModel.getByPermission(403);
        if (rol != null) {
            item.setVisible(false);
        }
        /*int position = 0, i = 0;
        for (ItemDrawer itemDrawer : mItemDrawers) {
            if (itemDrawer.getId() == R.id.item_sistema) {
                position = i;
                break;
            }
            i++;
        }
        if (rol == null) {
            mItemDrawers.remove(position);
            mAdapter.notifyDataSetChanged();
        }*/
    }

    private void logout() {
        ABC.resetData(getApplicationContext());
        mPreferenciasManager.setValue(ABC.IS_LOGIN, true);
        mPreferenciasManager.setValue(ABC.IS_LOCK, true);
        mPreferenciasManager.setValue(ABC.MY_ID, 0);
        mPreferenciasManager.setValue(ABC.TOKEN, "");
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finishAffinity();
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        findViewById(R.id.imgFlecha).setVisibility(View.GONE);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
            txtTitulo.setText("Comedor Universitario");
            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(Gravity.LEFT);
        else if (!(mFragment instanceof InicioFragment)) {
            seleccionarItem(navigationView.getMenu().getItem(0));
            navigationView.setCheckedItem(R.id.item_inicio);
        } else
            super.onBackPressed();
    }

}

