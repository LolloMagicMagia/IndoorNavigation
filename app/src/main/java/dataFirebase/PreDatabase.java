package dataFirebase;

import android.content.Context;
import android.graphics.Bitmap;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class PreDatabase {
    ListOfGeoPoint listOfGeoPoint;
    private static PreDatabase controller;

    private PreDatabase(Context context, List<Edificio> edificios, List<Aula> aulas){
        listOfGeoPoint = new ListOfGeoPoint(context, edificios, aulas);
    }

    public static PreDatabase getInstance(Context context, List<Edificio> edificios, List<Aula> aulas){
        if(controller==null) {
            controller = new PreDatabase(context, edificios, aulas);
        }
        return controller;
    }

    public static PreDatabase newChange(Context context, List<Edificio> edificios, List<Aula> aulas){
        controller = new PreDatabase(context, edificios, aulas);
        return controller;
    }

    public GeoPoint getGeoPoint(String posizione){
        return listOfGeoPoint.getGeoPoint(posizione);
    }

    public String getName(GeoPoint geo){
        return listOfGeoPoint.getName(geo);
    }

    public Integer getFloor(String posizione){
        return listOfGeoPoint.getFloor(posizione);
    }

    public ArrayList<GeoPoint> getEdificio(){
        return listOfGeoPoint.getEdifici();
    }

    public String getAppartenenza(String aula){
        return listOfGeoPoint.getAppartenenza(aula);
    }

    public int getNumberOfFloor(String edificio){
        return listOfGeoPoint.getNumberOfFloor(edificio);
    }

    public Bitmap getMap(String edificio, int piano){
        return listOfGeoPoint.getMap(edificio,piano);
    }

    public ArrayList<GeoPoint> getPlanimetria(String edificio){
        if(edificio.equals("u14")){
            return listOfGeoPoint.getPlanimetriaU14();
        }else{
            return listOfGeoPoint.getPlanimetriaU6();
        }
    }

}
