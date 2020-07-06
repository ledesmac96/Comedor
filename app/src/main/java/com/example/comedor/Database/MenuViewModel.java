package com.example.comedor.Database;

import android.content.Context;

import com.example.comedor.Modelo.Menu;

public class MenuViewModel {

    private Context mContext;
    private MenuRepository mRepository;

    public MenuViewModel(Context context) {
        mContext = context;
        mRepository = new MenuRepository(context);
    }

    public void insert(final Menu reserva) {
        mRepository.insert(reserva);
    }

    public void delete(final Menu reserva) {
        mRepository.delete(reserva);
    }

    public void update(final Menu reserva) {
        mRepository.update(reserva);
    }

    public Menu getByMenuID(int id) {
        return mRepository.getById(id);
    }


    public void deleteAll() {
        mRepository.deleteAll();
    }
}

