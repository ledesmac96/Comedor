package com.unse.bienestar.comedordos.Database;

import android.content.Context;

import com.unse.bienestar.comedordos.Modelo.Reserva;

import java.util.List;

public class ReservaRepository {

    private ReservaDAO mReservaDAO;
    private BDBienestar mBDBienestar;

    public ReservaRepository(Context context) {
        mBDBienestar = BDBienestar.getDatabase(context);
        mReservaDAO = mBDBienestar.getReservaDAO();
    }

    public void insert(final Reserva reserva) {
        mReservaDAO.insert(reserva);
    }

    public void delete(final Reserva reserva) {
        mReservaDAO.delete(reserva);
    }

    public void update(final Reserva reserva) {
        mReservaDAO.update(reserva);
    }

    public Reserva getById(final int id) {
        return mReservaDAO.get(id);

    }

    public boolean isExist(int id) {
        Reserva reserva = getById(id);
        return reserva != null;
    }

    public List<Reserva> getAll() {
        return mReservaDAO.getAll();
    }

    public void deleteAll() {
        mReservaDAO.deleteAll();
    }
}
