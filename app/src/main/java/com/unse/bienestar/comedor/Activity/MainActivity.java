package com.unse.bienestar.comedor.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.unse.bienestar.comedor.Database.RolViewModel;
import com.unse.bienestar.comedor.Database.UsuarioViewModel;
import com.unse.bienestar.comedor.Dialogos.DialogoGeneral;
import com.unse.bienestar.comedor.Dialogos.DialogoProcesamiento;
import com.unse.bienestar.comedor.Fragment.EstadisticasFragment;
import com.unse.bienestar.comedor.Fragment.GestionMenuFragment;
import com.unse.bienestar.comedor.Fragment.GestionReservasFragment;
import com.unse.bienestar.comedor.Fragment.GestionUsuarioFragment;
import com.unse.bienestar.comedor.Fragment.InicioFragment;
import com.unse.bienestar.comedor.Fragment.MisReservasFragment;
import com.unse.bienestar.comedor.Modelo.Rol;
import com.unse.bienestar.comedor.Modelo.Usuario;
import com.unse.bienestar.comedor.R;
import com.unse.bienestar.comedor.Utils.PreferenciasManager;
import com.unse.bienestar.comedor.Utils.Utils;
import com.unse.bienestar.comedor.Utils.VolleySingleton;
import com.unse.bienestar.comedor.Utils.YesNoDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPreferenciasManager = new PreferenciasManager(getApplicationContext());

        if (mPreferenciasManager.getValue(Utils.IS_LOCK)) {
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
        if (mPreferenciasManager.getValue(Utils.IS_TOKEN)) {
            String fcmToken = FirebaseInstanceId.getInstance().getToken();
            if (!fcmToken.equals("")) {
                PreferenciasManager manager = new PreferenciasManager(getApplicationContext());
                final int idLocal = manager.getValueInt(Utils.MY_ID);
                String URL = String.format("%s?tok=%s&id=%s", Utils.URL_USUARIO_TEST, fcmToken, idLocal);
                StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            mPreferenciasManager.setValue(Utils.IS_TOKEN, false);
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
                Utils.showCustomToast(MainActivity.this, getApplicationContext(), "Cancelaste", R.drawable.ic_error);

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
            dniDecode[i] = String.valueOf(Utils.decode(dni.charAt(i)));
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
                Utils.showCustomToast(MainActivity.this, getApplicationContext(),
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
        String key = manager.getValueString(Utils.TOKEN);
        int id = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?idU=%s&key=%s&ir=%s&ie=%s&e=%s&d=%s", Utils.URL_RESERVA_ACTUALIZAR, id, key, idReserva, id, 3, dni);
        StringRequest request = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuestaActualizar(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Utils.showCustomToast(MainActivity.this, getApplicationContext(),
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
                    Utils.showCustomToast(MainActivity.this, getApplicationContext(),
                            getString(R.string.errorInternoAdmin), R.drawable.ic_error);
                    break;
                case 1:
                    //Exito
                    dialogo(true);
                    break;
                case 2:
                    Utils.showCustomToast(MainActivity.this, getApplicationContext(),
                            getString(R.string.errorActualizar), R.drawable.ic_error);
                    dialogo(false);
                    break;
                case 3:
                    Utils.showCustomToast(MainActivity.this, getApplicationContext(),
                            getString(R.string.tokenInvalido), R.drawable.ic_error);
                    break;
                case 4:
                    Utils.showCustomToast(MainActivity.this, getApplicationContext(),
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
                    Utils.showCustomToast(MainActivity.this, getApplicationContext(),
                            getString(R.string.tokenInexistente), R.drawable.ic_error);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showCustomToast(MainActivity.this, getApplicationContext(),
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
        String key = manager.getValueString(Utils.TOKEN);
        final int idLocal = manager.getValueInt(Utils.MY_ID);
        String URL = String.format("%s?key=%s&id=%s&idU=%s", Utils.URL_USUARIO_CHECK, key, idLocal, idLocal);
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
                        mPreferenciasManager.setValue(Utils.IS_LOCK, true);
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
        int dni = mPreferenciasManager.getValueInt(Utils.MY_ID);
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
            case R.id.item_logout:
                logout();
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

    private void updateMenu() {
        Menu menu = navigationView.getMenu();
        int[] idItem = new int[]{R.id.item_reservas, R.id.item_users, R.id.item_menu, R.id.item_estad};
        int[] idRol = new int[]{400, 300, 200, 100};
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
        Utils.resetData(getApplicationContext());
        mPreferenciasManager.setValue(Utils.IS_LOGIN, true);
        mPreferenciasManager.setValue(Utils.IS_LOCK, true);
        mPreferenciasManager.setValue(Utils.MY_ID, 0);
        mPreferenciasManager.setValue(Utils.TOKEN, "");
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

