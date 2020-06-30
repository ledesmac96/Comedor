package com.example.comedor.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.comedor.Database.UsuarioViewModel;
import com.example.comedor.Fragment.GestionMenuFragment;
import com.example.comedor.Fragment.GestionReservasFragment;
import com.example.comedor.Fragment.GestionUsuarioFragment;
import com.example.comedor.Fragment.InicioFragment;
import com.example.comedor.Fragment.MisReservasFragment;
import com.example.comedor.Modelo.Usuario;
import com.example.comedor.R;
import com.example.comedor.Utils.PreferenciasManager;
import com.example.comedor.Utils.Utils;
import com.example.comedor.Utils.VolleySingleton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

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
    int itemSelecionado = -1, idUser = 0;
    ImageView imgBienestar;
    TextView txtNombre;
    UsuarioViewModel mUsuarioViewModel;
    PreferenciasManager mPreferenciasManager;

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
        }
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
        mPreferenciasManager = new PreferenciasManager(getApplicationContext());
        mUsuarioViewModel = new UsuarioViewModel(getApplicationContext());
        int dni = mPreferenciasManager.getValueInt(Utils.MY_ID);
        Usuario usuario = mUsuarioViewModel.getById(dni);
        if (usuario != null) {
            txtNombre.setText(String.format("%s %s", usuario.getNombre(), usuario.getApellido()));
        }

    }

    private void loadViews() {
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
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
                break;
            case R.id.item_reservas:
                fragmentoGenerico = new GestionReservasFragment();
                break;
            case R.id.item_users:
                fragmentoGenerico = new GestionUsuarioFragment();
                ((GestionUsuarioFragment) fragmentoGenerico).setContext(getApplicationContext());
                ((GestionUsuarioFragment) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
                break;
            case R.id.item_mis_reservas:
                fragmentoGenerico = new MisReservasFragment();
                ((MisReservasFragment) fragmentoGenerico).setContext(getApplicationContext());
                ((MisReservasFragment) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
                //
                break;
            case R.id.item_config:
                startActivity(new Intent(getApplicationContext(), PerfilActivity.class));
                break;
            case R.id.item_menu:
                fragmentoGenerico = new GestionMenuFragment();
                ((GestionMenuFragment) fragmentoGenerico).setContext(getApplicationContext());
                ((GestionMenuFragment) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
                break;
//            case R.id.item_estad:
//                fragmentoGenerico = new EstadisticasFragment();
//                ((EstadisticasFragment) fragmentoGenerico).setContext(getApplicationContext());
//                ((EstadisticasFragment) fragmentoGenerico).setFragmentManager(getSupportFragmentManager());
//                break;

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

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        findViewById(R.id.imgFlecha).setVisibility(View.GONE);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_black);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Bienestar Estudiantíl");
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

