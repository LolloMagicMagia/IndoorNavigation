package dataAndRelation;

import android.content.Context;
import android.graphics.Bitmap;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    Relation mRelation;
    private static Controller controller;

    private Controller(Context context, List<Edificio> edificios, List<Aula> aulas){
        mRelation = new Relation(context, edificios, aulas);
    }

    public static Controller getInstance(Context context, List<Edificio> edificios, List<Aula> aulas){
        if(controller==null) {
            controller = new Controller(context, edificios, aulas);
        }
        return controller;
    }

    public static Controller newChange(Context context, List<Edificio> edificios, List<Aula> aulas){
        controller = new Controller(context, edificios, aulas);
        return controller;
    }

    public GeoPoint getGeoPoint(String posizione){
        return mRelation.getGeoPoint(posizione);
    }

    public String getName(GeoPoint geo){
        return mRelation.getName(geo);
    }

    public Integer getFloor(String posizione){
        return mRelation.getFloor(posizione);
    }

    public ArrayList<GeoPoint> getEdificio(){
        return mRelation.getEdifici();
    }

    public String getAppartenenza(String aula){
        return mRelation.getAppartenenza(aula);
    }

    public int getNumberOfFloor(String edificio){
        return mRelation.getNumberOfFloor(edificio);
    }

    public Bitmap getMap(String edificio, int piano){
        return mRelation.getMap(edificio,piano);
    }

    public ArrayList<GeoPoint> getPlanimetria(String edificio){
        if(edificio.equals("u14")){
            return mRelation.getPlanimetriaU14();
        }else{
            return mRelation.getPlanimetriaU6();
        }
    }

    public Edificio getEdificioU6(){
        return mRelation.getEdificioU6();
    }

}
