package com.example.osmdroidex2;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.List;

public class ListOfGeoPoint {
    HashMap<String,GeoPoint> mGeoPointList;
    HashMap<String, Integer> aule;

    public ListOfGeoPoint(){
        super();
        mGeoPointList=new HashMap<String, GeoPoint>();
        aule=new HashMap<String, Integer>();
    }


    public void addGeoPoint(String nome,GeoPoint geo){
        mGeoPointList.put(nome,geo);
    }

    public void addAule(String nome, int layer){
       aule.put(nome,layer);
    }

    public GeoPoint getGeoPoint(String posizione){
        return mGeoPointList.get(posizione);
    }

    public Integer getFloor(String posizione){
        return aule.get(posizione);
    }
}
