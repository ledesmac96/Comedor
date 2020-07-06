package com.unse.bienestar.comedor.Database;

import com.unse.bienestar.comedor.Modelo.Menu;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MenuDAO {

    @Insert
    void insert(Menu alumno);

    @Update
    void update(Menu alumno);

    @Query("SELECT * FROM " + Menu.TABLE + " WHERE " + Menu.KEY_ID + " = :id ")
    Menu get(int id);

    @Delete
    void delete(Menu alumno);

    @Query("DELETE FROM "+ Menu.TABLE)
    void deleteAll();

    @Query("SELECT * FROM " + Menu.TABLE)
    List<Menu> getAll();
}

