package com.example.comedor.Database;

import android.content.Context;

import com.example.comedor.Modelo.Rol;

import java.util.List;

public class RolViewModel {
    private Context mContext;
    private RolRepository mRepository;

    public RolViewModel(Context context) {
        mContext = context;
        mRepository = new RolRepository(context);
    }

    public void insert(final Rol rol) {
        mRepository.insert(rol);
    }

    public void delete(final Rol rol) {
        mRepository.delete(rol);
    }

    public void update(final Rol rol) {
        mRepository.update(rol);
    }

    public List<Rol> getAllByUsuario(int id) {
        return mRepository.getAllByUsuario(id);
    }

    public Rol getByPermission(int id) {
        return mRepository.get(id);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }
}
