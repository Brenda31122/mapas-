package com.example.prueva2_mapa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlLocalizacion extends SQLiteOpenHelper {
    public String tabla=(("CREATE TABLE  ubicaciones (id integer " +
            "primary key autoincrement,calle text,latitud real,longitud real)"));


    public SqlLocalizacion(Context contexto, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, "Direcciones", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tabla);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE ubicaciones");
    }
}
