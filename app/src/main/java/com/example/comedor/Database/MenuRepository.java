package com.example.comedor.Database;

import android.content.Context;

import com.example.comedor.Modelo.Menu;

import java.util.List;

public class MenuRepository {

    private MenuDAO mMenuDAO;
    private BDBienestar mBDBienestar;

    public MenuRepository(Context context) {
        mBDBienestar = BDBienestar.getDatabase(context);
        mMenuDAO = mBDBienestar.getMenuDAO();
    }

    public void insert(final Menu alumno) {
        mMenuDAO.insert(alumno);
    }

    public void delete(final Menu alumno) {
        mMenuDAO.delete(alumno);
    }

    public void update(final Menu alumno) {
        mMenuDAO.update(alumno);
    }

    public Menu getById(final int id) {
        return mMenuDAO.get(id);

    }

    public boolean isExist(int id) {
        Menu alumno = getById(id);
        return alumno != null;
    }

    public List<Menu> getAll() {
        return mMenuDAO.getAll();
    }

    public void deleteAll() {
        mMenuDAO.deleteAll();
    }

}
