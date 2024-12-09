package com.example.prueva2_mapa;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleMap mMap;
    double lat = 0.0, lng = 0.0;
    LatLng coordenadas;
    private Marker marcador;
    String calle2 = "";
    TextView calle;
    Button lugares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lugares = findViewById(R.id.bLocalizacion);
        calle = findViewById(R.id.tvLocalizacion);
        calle.setText(calle2);
        lugares.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(true);
        settings.setRotateGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);

        mMap.setOnMapLongClickListener(latLng -> mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marca)) // Cambia mipmap por drawable
                .anchor(0.0f, 1.0f)
                .position(latLng)).setTitle("Clic aquí " + latLng));

        mMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(getApplicationContext(), "Click " + marker.getPosition(), Toast.LENGTH_SHORT).show();
            return true;
        });

        direccionAct();
    }

    private void direccionAct() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PETICION_PERMISO_LOCALIZACION);
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateLocalizacion(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, locationListener);
    }

    private void Marcador(double lat, double lng) {
        coordenadas = new LatLng(lat, lng);
        CameraUpdate ubicacionCam = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas)
                .title("Dirección: " + calle2 + " (" + coordenadas + ")")
                .icon(BitmapDescriptorFactory.defaultMarker()));
        mMap.animateCamera(ubicacionCam);
    }

    private void updateLocalizacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Marcador(lat, lng);
            guardar();
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocalizacion(location);
            setLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS ACTIVADO", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS DESACTIVADO", Toast.LENGTH_SHORT).show();
            locationStart();
        }
    };

    public void guardar() {
        SqlLocalizacion lugar = new SqlLocalizacion(this, "ubicaciones", null, 1);
        SQLiteDatabase db = lugar.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("calle", calle2);
        valores.put("latitud", coordenadas.latitude);
        valores.put("longitud", coordenadas.longitude);
        db.insert("ubicaciones", null, valores);
        db.close();
    }

    public void setLocation(Location location) {
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    calle2 = DirCalle.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void locationStart() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bLocalizacion) {
            Intent verUbicacion = new Intent(MapsActivity.this, Ubicacion.class);
            startActivity(verUbicacion);
        }
    }}
