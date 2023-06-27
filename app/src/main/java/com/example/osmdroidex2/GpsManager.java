package com.example.osmdroidex2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class GpsManager {
    private MyLocationNewOverlay myLocationNewOverlay;
    MapView mapView;
    private LocationManager locationManager;

    public GpsManager(MapView map) {
        mapView=map;
        enableMyLocationOverlay();
    }

    //Serve per abilitare il gps nella posizione iniziale, se fosse attivo il gps già all'inizio
    public void gpsStart(){
        locationManager = (LocationManager) mapView.getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Se il GPS è attivo, aggiungi l'icona sulla mappa, poichè il listener del gps non funziona
        // da subito, quindi c'è bisogno di un controllo iniziale
        if (isGPSEnabled) {
            mapView.getOverlayManager().add(myLocationNewOverlay);
            myLocationNewOverlay.enableFollowLocation();
        }
    }

    public LocationManager getLocationManager(){
        return locationManager;
    }

    public void enableMyLocationOverlay(){
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(mapView.getContext()), mapView);
    }

    public MyLocationNewOverlay getLocationNewOverlay(){
        return myLocationNewOverlay;
    }

    public GeoPoint getMyLocationNewOverlay(){
        return myLocationNewOverlay.getMyLocation();
    }

    public void disableMyLocation(){
        if(myLocationNewOverlay!=null){
            myLocationNewOverlay.disableMyLocation();
            myLocationNewOverlay.disableFollowLocation();
            mapView.getOverlays().remove(myLocationNewOverlay);
            myLocationNewOverlay=null;
        }
        mapView.invalidate();
    }

    public void enableMyLocation(){
        if(myLocationNewOverlay==null){
            enableMyLocationOverlay();
        }
        mapView.getOverlayManager().remove(myLocationNewOverlay);
        if(myLocationNewOverlay != null){
            myLocationNewOverlay.disableFollowLocation();
        }
        mapView.getOverlayManager().add(myLocationNewOverlay);
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.enableFollowLocation();
        mapView.invalidate();
    }

    public void followMe(){
        if(myLocationNewOverlay==null){
            enableMyLocationOverlay();
        }
        mapView.getOverlayManager().remove(myLocationNewOverlay);
        if(myLocationNewOverlay != null){
            myLocationNewOverlay.disableFollowLocation();
        }
        mapView.getOverlayManager().add(myLocationNewOverlay);
        mapView.invalidate();
    }

    public void disableFollowLocation(){
        if(myLocationNewOverlay==null){
            enableMyLocationOverlay();
        }
        myLocationNewOverlay.disableMyLocation();
        myLocationNewOverlay.disableFollowLocation();
    }

    public boolean gpsEnabled(){
        return locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
    }



}
