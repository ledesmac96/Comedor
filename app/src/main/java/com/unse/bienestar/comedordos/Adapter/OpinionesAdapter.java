package com.unse.bienestar.comedordos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unse.bienestar.comedordos.Modelo.Sugerencia;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.Utils.ABC;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OpinionesAdapter extends RecyclerView.Adapter<OpinionesAdapter.OpinionViewHolder> {

    private ArrayList<Sugerencia> mSugerencias;
    private Context mContext;

    public OpinionesAdapter(ArrayList<Sugerencia> sugerencias, Context context) {
        mSugerencias = sugerencias;
        mContext = context;
    }

    @NonNull
    @Override
    public OpinionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sugerencias, parent, false);
        return new OpinionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpinionViewHolder holder, int position) {
        Sugerencia sugerencia = mSugerencias.get(position);

        holder.txtFecha.setText(ABC.getFechaOrder(
                ABC.getFechaDateWithHour(sugerencia.getFechaRegistro())
        ));
        holder.txtDescripcion.setText(sugerencia.getDescripcion());

    }

    @Override
    public int getItemCount() {
        return mSugerencias.size();
    }

    public void change(ArrayList<Sugerencia> list) {
        mSugerencias = list;
        notifyDataSetChanged();
    }

    public static class OpinionViewHolder extends RecyclerView.ViewHolder {

        TextView txtFecha, txtDescripcion;

        public OpinionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtDescripcion = itemView.findViewById(R.id.txtDesc);
        }
    }
}
