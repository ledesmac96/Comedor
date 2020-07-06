package com.unse.bienestar.comedor.Database;

import android.content.Context;

import com.unse.bienestar.comedor.Modelo.Alumno;
import com.unse.bienestar.comedor.Modelo.Menu;
import com.unse.bienestar.comedor.Modelo.Reserva;
import com.unse.bienestar.comedor.Modelo.Rol;
import com.unse.bienestar.comedor.Modelo.Usuario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Usuario.class, Alumno.class, Rol.class, Reserva.class, Menu.class}, version = 1, exportSchema = false)
abstract class BDBienestar extends RoomDatabase {

    public abstract UsuarioDAO getUserDao();

    public abstract AlumnoDAO getAlumnoDao();

    public abstract RolDAO getRolDAO();

    public abstract ReservaDAO getReservaDAO();

    public abstract MenuDAO getMenuDAO();

    private static volatile BDBienestar INSTANCE;
    private static String DATABASE_NAME = "bienestar";
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static BDBienestar getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BDBienestar.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BDBienestar.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
