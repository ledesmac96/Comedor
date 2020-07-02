package com.example.comedor.Database;

import android.content.Context;

import com.example.comedor.Modelo.Reserva;

import java.util.List;

public class ReservaViewModel {

    private Context mContext;
    private ReservaRepository mRepository;

    public ReservaViewModel(Context context) {
        mContext = context;
        mRepository = new ReservaRepository(context);
    }

    public void insert(final Reserva reserva) {
        mRepository.insert(reserva);
    }

    public void delete(final Reserva reserva) {
        mRepository.delete(reserva);
    }

    public void update(final Reserva reserva) {
        mRepository.update(reserva);
    }

    public Reserva getAllByMenuID(int id) {
        return mRepository.getById(id);
    }


    public void deleteAll() {
        mRepository.deleteAll();
    }
}
