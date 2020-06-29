package com.example.comedor.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.comedor.Modelo.Reserva;
import com.example.comedor.R;
import com.example.comedor.Utils.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.EventosViewHolder> {
    private ArrayList<Reserva> mReservas;
    private ArrayList<Reserva> mListCopia;
    private Context context;

    public ReservasAdapter(ArrayList<Reserva> list, Context ctx) {
        this.mReservas = list;
        this.context = ctx;
        this.mListCopia = new ArrayList<>();
        this.mListCopia.addAll(mReservas);
    }

    @NonNull
    @Override
    public ReservasAdapter.EventosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva, parent, false);

        return new ReservasAdapter.EventosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservasAdapter.EventosViewHolder holder, int position) {
        Reserva reserva = mReservas.get(position);

        holder.idReserva.setText(String.valueOf(reserva.getIdReserva()));
        holder.fecha.setText(reserva.getFechaReserva());
        if (reserva.getEstado() == 0){
            holder.estado.setText("RESERVADO");
            holder.mBackg.getBackground().setColorFilter(Color.parseColor("#E64A19"), PorterDuff.Mode.SRC_OVER);
        }else if(reserva.getEstado() == 1){
            holder.estado.setText("RETIRADO");
            holder.mBackg.getBackground().setColorFilter(Color.parseColor("#32AC37"), PorterDuff.Mode.SRC_OVER);
        }else if(reserva.getEstado() == 2){
            holder.estado.setText("CANCELADO");
            holder.mBackg.getBackground().setColorFilter(Color.parseColor("#D32F2F"), PorterDuff.Mode.SRC_OVER);
        }

    }

    public void filtrar(String txt, int tipo) {
        ArrayList<Reserva> result = new ArrayList<>();
        switch (tipo) {
            case Utils.LIST_RESET:
                mReservas.clear();
                mReservas.addAll(mListCopia);
                break;
            case Utils.LIST_DNI:
                for (Reserva item : mListCopia) {
                    if (String.valueOf(item.getIdUsuario()).contains(txt)) {
                        result.add(item);
                    }

                }
                mReservas.clear();
                mReservas.addAll(result);
                break;
        }
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return mReservas.get(position).getIdUsuario();
    }

    @Override
    public int getItemCount() {
        return mReservas.size();
    }

    static class EventosViewHolder extends RecyclerView.ViewHolder {

        TextView idReserva, fecha, estado;
        LinearLayout mBackg;

        EventosViewHolder(View itemView) {
            super(itemView);

            idReserva = itemView.findViewById(R.id.txtIdReserva);
            fecha = itemView.findViewById(R.id.txtFechaRes);
            estado = itemView.findViewById(R.id.txtEstado);
            mBackg = itemView.findViewById(R.id.linlay);
        }
    }

    public void change(ArrayList<Reserva> list) {
        mReservas = list;
        this.mListCopia = new ArrayList<>();
        this.mListCopia.addAll(mReservas);
        notifyDataSetChanged();
    }
}
