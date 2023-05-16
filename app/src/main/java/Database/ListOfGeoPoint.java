package Database;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class ListOfGeoPoint {
    HashMap<String,GeoPoint> mGeoPointList;
    HashMap<GeoPoint,String> name;
    HashMap<String, Integer> auleU14;
    ArrayList<GeoPoint> edifici;
    ArrayList<GeoPoint> mappaU14;
    ArrayList<GeoPoint> mappaU6;
    HashMap<String, Integer> floor;

    //GeoPoint riguardante i punti degli edifici e delle aule. Successivamente saranno in un database
    GeoPoint u7 = new GeoPoint(45.51731, 9.21291);
    GeoPoint u6 = new GeoPoint(45.51847, 9.21297);
    GeoPoint u14 = new GeoPoint(45.52374,9.21971);
    GeoPoint u14FirstFloor = new GeoPoint(45.52361, 9.21971);
    GeoPoint u14SecondFloor = new GeoPoint(45.52352, 9.21994);


    public ListOfGeoPoint(){
        super();
        mGeoPointList=new HashMap<String, GeoPoint>();
        auleU14=new HashMap<String, Integer>();
        mappaU14=new ArrayList<>();
        mappaU6 = new ArrayList<>();
        name=new HashMap<>();
        edifici=new ArrayList<>();
        floor=new HashMap<>();
        populate();
    }

    public void addName(String nome,GeoPoint geo){
        name.put(geo,nome);
    }

    public String getName(GeoPoint posizione){
        return name.get(posizione);
    }

    public void addGeoPoint(String nome,GeoPoint geo){
        mGeoPointList.put(nome,geo);
    }

    public void addAule(String nome, int layer){
       auleU14.put(nome,layer);
    }

    public void addEdificio(GeoPoint geo){
        edifici.add(geo);
    }

    public ArrayList<GeoPoint> getEdifici(){
        return edifici;
    }

    public GeoPoint getGeoPoint(String posizione){
        return mGeoPointList.get(posizione);
    }

    public Integer getFloor(String posizione){
        return auleU14.get(posizione);
    }

    public int getNumberOfFloor(String edificio){
        return floor.get(edificio);
    }


    public ArrayList<GeoPoint> getPlanimetriaU14(){
        return mappaU14;
    }

    public ArrayList<GeoPoint> getPlanimetriaU6(){
        return mappaU6;
    }

    private void populate(){
        addGeoPoint("u7",u7);
        addGeoPoint("u6",u6);
        addGeoPoint("u14",u14);
        addGeoPoint("u14FirstFloor",u14FirstFloor);
        addGeoPoint("u14SecondFloor",u14SecondFloor);
        //popoli il mio database con le aule dicendo in che piano sono
        addAule("u14FirstFloor",1);
        addAule("u14SecondFloor",2);
        //popolo il mio database con solo gli edifici
        addEdificio(u6);
        addEdificio(u14);
        //popolo il mio database con il perimetro, Planimetria
        mappaU14.add(new GeoPoint(45.52391, 9.21894));
        mappaU14.add(new GeoPoint(45.52345, 9.22015));
        mappaU14.add(new GeoPoint(45.52335, 9.22009));
        mappaU14.add(new GeoPoint(45.52381, 9.21886));
        mappaU6.add(new GeoPoint(45.51773, 9.2126));
        mappaU6.add(new GeoPoint(45.51929, 9.21347));
        mappaU6.add(new GeoPoint(45.51906, 9.21426));
        mappaU6.add(new GeoPoint(45.51752, 9.21341));
        //popolo il mio arraylist inverso per ricevere i geopoint
        addName("u6",u6);
        addName("u14",u14);
        //popolo quanti piani ha un edificio
        floor.put("u14", 2);
        floor.put("u6",6);
    }

}
