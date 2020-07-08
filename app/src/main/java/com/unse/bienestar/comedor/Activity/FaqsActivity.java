package com.unse.bienestar.comedor.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.unse.bienestar.comedor.Adapter.ExpandablePreguntasAdapter;
import com.unse.bienestar.comedor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class FaqsActivity extends AppCompatActivity {

    ImageView btnBack;

    private ExpandableListView expandableListView;
    private ExpandablePreguntasAdapter mExpandablePreguntasAdapter;
    private List<String> listDataGroup;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadViews();

        loadData();

        loadListener();

        setToolbar();

    }

    private void setToolbar() {
        ((TextView) findViewById(R.id.txtTitulo)).setText("Preguntas frecuentes");
    }

    private void loadListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadViews() {
        expandableListView = findViewById(R.id.expandableListView);
        btnBack = findViewById(R.id.imgFlecha);
    }

    private void initListeners() {
        // ExpandableListView on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
              //Hago algo con los listener
                return false;
            }
        });
    }

    private void loadData() {
        listDataGroup = new ArrayList<>();
        listDataChild = new HashMap<>();

        mExpandablePreguntasAdapter = new ExpandablePreguntasAdapter(this, listDataGroup, listDataChild);
        expandableListView.setAdapter(mExpandablePreguntasAdapter);

        listDataGroup.add(getString(R.string.text_q1));
        listDataGroup.add(getString(R.string.text_q2));
        listDataGroup.add(getString(R.string.text_q3));
        listDataGroup.add(getString(R.string.text_q4));
        listDataGroup.add(getString(R.string.text_q5));

        String[] array;
        List<String> q1 = new ArrayList<>();
        array = getResources().getStringArray(R.array.text_q1);
        for (String item : array) {
            q1.add(item);
        }
        List<String> q2 = new ArrayList<>();
        array = getResources().getStringArray(R.array.text_q2);
        for (String item : array) {
            q2.add(item);
        }
        List<String> q3 = new ArrayList<>();
        array = getResources().getStringArray(R.array.text_q3);
        for (String item : array) {
            q3.add(item);
        }

        List<String> q4 = new ArrayList<>();
        array = getResources().getStringArray(R.array.text_q4);
        for (String item : array) {
            q4.add(item);
        }

        List<String> q5 = new ArrayList<>();
        array = getResources().getStringArray(R.array.text_q5);
        for (String item : array) {
            q5.add(item);
        }

        listDataChild.put(listDataGroup.get(0), q1);
        listDataChild.put(listDataGroup.get(1), q2);
        listDataChild.put(listDataGroup.get(2), q3);
        listDataChild.put(listDataGroup.get(3), q4);
        listDataChild.put(listDataGroup.get(4), q5);

        mExpandablePreguntasAdapter.notifyDataSetChanged();
    }

}
