package dataFirebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.osmdroidex2.R;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListOfGeoPoint {

    private ViewModel mViewModel;
    Context mcontext;
    //Relazione tra edificio/aula e punto sulla mappa
    HashMap<String,GeoPoint> mGeoPointList;
    //Il contrario della relazione di prima, in questo caso dal punto risalvo all'edificio, serve
    // per capire a quale edificio sto zoommando
    HashMap<GeoPoint,String> name;
    //Dico le aule a che piano sono
    HashMap<String, Integer> auleU14;
    //Vado ad indicare gli edifici possibili(u14/u6)
    ArrayList<GeoPoint> edifici;
    //Vado ad indicare i punti dove andrò a mettere le bitmap
    ArrayList<GeoPoint> mappaU14;
    ArrayList<GeoPoint> mappaU6;
    //Numero di piani che ha un edificio
    HashMap<String, Integer> floor;
    //A che edificio corrispondono le aule
    HashMap<String,String> contenuto;
    //In base all'edificio scelto e al piano gli passo la BitMap
    HashMap<String, HashMap<Integer, Bitmap>> showFloor;
    //GeoPoint riguardante i punti degli edifici e delle aule. Successivamente saranno in un database
    GeoPoint u7 = new GeoPoint(45.51731, 9.21291);
    GeoPoint u6 = new GeoPoint(45.51847, 9.21297);
    GeoPoint u14 = new GeoPoint(45.52374,9.21971);
    GeoPoint u14FirstFloor = new GeoPoint(45.52361, 9.21971);
    GeoPoint u14SecondFloor = new GeoPoint(45.52352, 9.21994);


    List<Edificio> mEdificios;
    Edificio u14D;
    Edificio u6D;
    Edificio u7D;
    List<Aula> mAulas;

    public ListOfGeoPoint(Context context, List<Edificio> edificios, List<Aula> aulas){
        super();
        mcontext=context;
        mEdificios=edificios;
        mAulas=aulas;
        mGeoPointList=new HashMap<String, GeoPoint>();
        auleU14=new HashMap<String, Integer>();
        mappaU14=new ArrayList<>();
        mappaU6 = new ArrayList<>();
        name=new HashMap<>();
        contenuto=new HashMap<>();
        edifici=new ArrayList<>();
        floor=new HashMap<>();
        showFloor=new HashMap<String, HashMap<Integer, Bitmap>>();
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

    public GeoPoint getGeoPoint(String posizione){
        return mGeoPointList.get(posizione);
    }

    public void addAule(String nome, int layer){
       auleU14.put(nome,layer);
    }

    public Bitmap getMap(String edificio, int piano){
        Bitmap bitmap;
        bitmap = showFloor.get(edificio).get(piano);
        return bitmap;
    }

    public void addEdificio(GeoPoint geo){
        edifici.add(geo);
    }

    public ArrayList<GeoPoint> getEdifici(){
        return edifici;
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

    public String getAppartenenza(String aula){
        //ritorna l'edificio
        return contenuto.get(aula);
    }

    private void populate(){
        for(Edificio ed: mEdificios){
            if(ed.getNomeEdificio().equals("u14")){
                u14D=ed;
                mappaU14.add(u14D.getLeft_below());
                mappaU14.add(u14D.getLeft_up());
                mappaU14.add(u14D.getRight_up());
                mappaU14.add(u14D.getRight_down());
                addGeoPoint("u14", u14D.getPosizione());
                floor.put("u14", 2);
                contenuto.put("u14","u14");
                addEdificio(u14D.getPosizione());
                addName("u14",u14D.getPosizione());
            }else if(ed.getNomeEdificio().equals("u6")){
                u6D=ed;
                mappaU6.add(u6D.getLeft_below());
                mappaU6.add(u6D.getLeft_up());
                mappaU6.add(u6D.getRight_up());
                mappaU6.add(u6D.getRight_down());
                addGeoPoint("u6", u6D.getPosizione());
                floor.put("u6",6);
                contenuto.put("u14","u14");
                addEdificio(u6D.getPosizione());
                addName("u6",u6D.getPosizione());
            }else if(ed.getNomeEdificio().equals("u7")){
                u7D=ed;
                addGeoPoint("u7",u7D.getPosizione());
                contenuto.put("u14","u14");
            }
        }
        for(Aula a:mAulas){
            contenuto.put(a.getNomeAula(),a.getNomeEdificio());
            contenuto.put(a.getNomeAula(),a.getNomeEdificio());
            addGeoPoint(a.getNomeAula(),a.getPosizione());
            addAule(a.getNomeAula(),a.getPiano());
        }
        showFloor.put("u14",new HashMap<Integer, Bitmap>());
        showFloor.get("u14").put(0, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.piantina1));
        showFloor.get("u14").put(1, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.piantina2));
        showFloor.put("u6",new HashMap<>());
        showFloor.get("u6").put(0, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.u6));
        //popolo il mio database con tutte le posizioni dei punti
        /*addGeoPoint("u7",u7);
        addGeoPoint("u6",u6);
        addGeoPoint("u14",u14);*/
        /*addGeoPoint("u14AulaFirstFloor",u14FirstFloor);
        addGeoPoint("u14AulaSecondFloor",u14SecondFloor);*/
        //popoli il mio database con le aule dicendo in che piano sono
        /*addAule("u14AulaFirstFloor",0);
        addAule("u14AulaSecondFloor",1);*/
        //popolo il mio database con solo gli edifici
        /*addEdificio(u6);
        addEdificio(u14);*/

        //popolo il mio database con il perimetro, Planimetria
        /*mappaU14.add(new GeoPoint(45.52391, 9.21894));
        mappaU14.add(new GeoPoint(45.52345, 9.22015));
        mappaU14.add(new GeoPoint(45.52335, 9.22009));
        mappaU14.add(new GeoPoint(45.52381, 9.21886));
        mappaU6.add(new GeoPoint(45.51773, 9.2126));
        mappaU6.add(new GeoPoint(45.51929, 9.21347));
        mappaU6.add(new GeoPoint(45.51906, 9.21426));
        mappaU6.add(new GeoPoint(45.51752, 9.21341));*/

        //popolo il mio arraylist inverso per ricevere i geopoint
        /*addName("u6",u6);
        addName("u14",u14);*/
        //popolo quanti piani ha un edificio
        /*floor.put("u14", 2);
        floor.put("u6",6);*/
        //popolo i piani per ogni edificio(non riguardano più i Marker)
        /*showFloor.put("u14",new HashMap<Integer, Bitmap>());
        showFloor.get("u14").put(0, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.piantina1));
        showFloor.get("u14").put(1, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.piantina2));
        showFloor.put("u6",new HashMap<>());
        showFloor.get("u6").put(0, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.u6));*/
        //relazione tra edificio e aule
        /*contenuto.put("u14","u14");
        contenuto.put("u14AulaFirstFloor","u14");
        contenuto.put("u14AulaSecondFloor","u14");
        contenuto.put("u6","u6");*/
    }

}
