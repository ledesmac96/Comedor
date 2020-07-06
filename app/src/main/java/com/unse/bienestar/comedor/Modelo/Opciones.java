package com.unse.bienestar.comedor.Modelo;

public class Opciones {

    private String titulo;
    private int icon, orientation;
    private int color, colorText = 0, sizeText =0;
    private int id;
    private boolean disponibility;

    public Opciones(int orientation, int id,String titulo, int icon, int color) {
        this.titulo = titulo;
        this.icon = icon;
        this.color = color;
        this.id = id;
        this.orientation = orientation;
    }
    public Opciones(boolean dis, int orientation, int id,String titulo, int icon, int color) {
        this.titulo = titulo;
        this.icon = icon;
        this.color = color;
        this.id = id;
        this.orientation = orientation;
        this.disponibility = dis;
    }

    public Opciones(int orientation,int id,String titulo, int icon, int color, int colorText) {
        this.titulo = titulo;
        this.icon = icon;
        this.color = color;
        this.id = id;
        this.colorText = colorText;
        this.orientation = orientation;
    }

    public Opciones(boolean dis,int orientation,int id,String titulo, int icon, int color, int colorText) {
        this.titulo = titulo;
        this.icon = icon;
        this.color = color;
        this.id = id;
        this.colorText = colorText;
        this.orientation = orientation;
        this.disponibility = dis;
    }

    public Opciones(int orientation,int id,String titulo, int icon, int color, int colorText, int sizeText) {
        this.titulo = titulo;
        this.icon = icon;
        this.color = color;
        this.id = id;
        this.colorText = colorText;
        this.sizeText = sizeText;
        this.orientation = orientation;
    }

    public Opciones(int orientation, boolean disponibility, int id,String titulo, int icon, int color, int colorText, int sizeText) {
        this.titulo = titulo;
        this.icon = icon;
        this.color = color;
        this.id = id;
        this.colorText = colorText;
        this.sizeText = sizeText;
        this.orientation = orientation;
        this.disponibility = disponibility;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean getDisponibility() {
        return disponibility;
    }

    public void setDisponibility(boolean disponibility) {
        this.disponibility = disponibility;
    }

    public int getSizeText() {
        return sizeText;
    }

    public void setSizeText(int sizeText) {
        this.sizeText = sizeText;
    }

    public int getColorText() {
        return colorText;
    }

    public void setColorText(int colorText) {
        this.colorText = colorText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
