package com.unse.bienestar.comedordos.Modelo;

import com.unse.bienestar.comedordos.Utils.ABC;

public class ItemDato extends ItemBase {

    public static final int TIPO_MENU = 1;
    private Menu mMenu;
    private int tipo;

    public ItemDato() {
    }

    public String getTextValue() {
        switch (getTipoDato()) {
            case TIPO_MENU:
                return ABC.getInfoDate(mMenu.getDia(), mMenu.getMes(), mMenu.getAnio());
        }
        return null;
    }


    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu menu) {
        mMenu = menu;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getTipoDato() {
        return tipo;
    }

    @Override
    public int getTipo() {
        return TIPO_DATO;
    }
}
