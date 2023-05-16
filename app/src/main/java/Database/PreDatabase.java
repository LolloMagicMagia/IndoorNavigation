package Database;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class PreDatabase {
    ListOfGeoPoint listOfGeoPoint;

    public PreDatabase(){
        super();
        listOfGeoPoint= new ListOfGeoPoint();
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

    public int getNumberOfFloor(String edificio){
        return listOfGeoPoint.getNumberOfFloor(edificio);
    }

    public ArrayList<GeoPoint> getPlanimetria(String edificio){
        if(edificio == "u14"){
            return listOfGeoPoint.getPlanimetriaU14();
        }else{
            return listOfGeoPoint.getPlanimetriaU6();
        }
    }

}
