package com.example.prueva2_mapa;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Ubicacion extends AppCompatActivity {
    TextView vRegistros;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion2);

        vRegistros = findViewById(R.id.tvUbicacion);

        SqlLocalizacion lugar = new SqlLocalizacion(this, "ubicaciones", null, 1);
        SQLiteDatabase db = lugar.getReadableDatabase();
        c = db.rawQuery("SELECT * FROM ubicaciones", null);

        if (c.moveToFirst()) {
            do {
                Integer id = c.getInt(0);
                String calle = c.getString(1);
                Double latitud = c.getDouble(2);
                Double longitud = c.getDouble(3);
                vRegistros.append("ID: " + id + "\n" +
                        "Dirección: " + calle + "\n" +
                        "Latitud: " + latitud + "\n" +
                        "Longitud: " + longitud + "\n\n");
            } while (c.moveToNext());
        }
        db.close();
    }
}
