package com.menuapp.mapas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private EditText inputCoordinates;
    private MapView map;
    private List<Marker> markers = new ArrayList<>();
    private LocationManager locationManager;
    private Button currentLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_main);
        inputCoordinates = findViewById(R.id.input_coordinates);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(14.0);
        GeoPoint startPoint = new GeoPoint(-19.9319695, -43.9395507);
        map.getController().setCenter(startPoint);

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = inputCoordinates.getText().toString();
                if (coordinates.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Digite as coordenadas primeiro", Toast.LENGTH_SHORT).show();
                } else {
                    String[] coordsArray = coordinates.split(",");
                    if (coordsArray.length != 2) {
                        Toast.makeText(MainActivity.this, "Coordenadas inválidas", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            double latitude = Double.parseDouble(coordsArray[0].trim());
                            double longitude = Double.parseDouble(coordsArray[1].trim());
                            GeoPoint point = new GeoPoint(latitude, longitude);
                            map.getController().setCenter(point);
                            addMarker(point);
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Coordenadas inválidas", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        currentLocationButton = findViewById(R.id.location_button);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void addMarker(GeoPoint point) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Coordenadas");
        //marker.setInfoWindow(new InfoWindow(map));
        map.getOverlays().add(marker);
        markers.add(marker);
        map.invalidate();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                GeoPoint point = new GeoPoint(latitude, longitude);
                map.getController().setCenter(point);
                addMarker(point);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        GeoPoint point = new GeoPoint(latitude, longitude);
        map.getController().setCenter(point);
        addMarker(point);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}