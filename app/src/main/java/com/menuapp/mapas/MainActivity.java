package com.menuapp.mapas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private EditText inputCoordinates;
    private MapView map;
    private List<Marker> markers = new ArrayList<>();

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

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}
