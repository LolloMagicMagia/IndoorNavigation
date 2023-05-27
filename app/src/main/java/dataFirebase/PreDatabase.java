package dataFirebase;

import android.content.Context;
import android.graphics.Bitmap;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class PreDatabase {
    ListOfGeoPoint listOfGeoPoint;

    public PreDatabase(Context context, List<Edificio> edificios, List<Aula> aulas){
        super();
        listOfGeoPoint= new ListOfGeoPoint(context, edificios, aulas);
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
