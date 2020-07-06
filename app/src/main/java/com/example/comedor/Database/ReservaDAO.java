package com.example.comedor.Database;

import com.example.comedor.Modelo.Reserva;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ReservaDAO {

    @Insert
    void insert(Reserva alumno);

    @Update
    void update(Reserva alumno);

    @Query("SELECT * FROM " + Reserva.TABLE + " WHERE " + Reserva.KEY_ID_ALU + " = :id ")
    Reserva get(int id);

    @Delete
    void delete(Reserva alumno);

    @Query("DELETE FROM " + Reserva.TABLE)
    void deleteAll();

    @Query("SELECT * FROM " + Reserva.TABLE)
    List<Reserva> getAll();
}
