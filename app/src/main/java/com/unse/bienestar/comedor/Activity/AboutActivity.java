package com.unse.bienestar.comedor.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unse.bienestar.comedor.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgIcono;
    CardView cardInsta, cardWhats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadViews();

        loadListener();

        loadData();

        setToolbar();

    }

    private void loadData() {
    }

    private void loadListener() {
        imgIcono.setOnClickListener(this);
        cardWhats.setOnClickListener(this);
        cardInsta.setOnClickListener(this);
    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setText("Sobre nosotros");
    }

    private void loadViews() {
        imgIcono = findViewById(R.id.imgFlecha);
        cardInsta = findViewById(R.id.cardInsta);
        cardWhats = findViewById(R.id.cardWhats);
    }

    public static Intent newInstagramProfileIntent(PackageManager pm, String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (pm.getPackageInfo("com.instagram.android", 0) != null) {
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                final String username = url.substring(url.lastIndexOf("/") + 1);
                intent.setData(Uri.parse("http://instagram.com/_u/" + username));
                intent.setPackage("com.instagram.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        intent.setData(Uri.parse(url));
        return intent;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.cardInsta:
                String url = "https://www.instagram.com/bienestarestudiantilunse/";
                Intent openInsta = newInstagramProfileIntent(getPackageManager(), url);
                startActivity(openInsta);
                break;
            case R.id.cardWhats:
                Toast.makeText(this, "No hago nada todavía.", Toast.LENGTH_SHORT).show();
                break;

        }
    }

}
