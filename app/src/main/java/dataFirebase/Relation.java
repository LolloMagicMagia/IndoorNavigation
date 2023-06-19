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

public class Relation {
    //Quello che vado a fare è andare a prendere i dati dal database e poi metterli in ArrayList e HashMap
    //per andare a definire le relazioni esistenti che mi serviranno nell'applicazione. Tutti i dati utili
    //per andare a definirle vengono presi direttamente nel database, e al cambio dei dati nel database
    //la classe verrà ridefinita. Questo viene fatto poichè il fragmentOSM richiede dei dati molti velocemente
    // e quindi sembrava sbagliato andare ogni volta a prendere i dati direttamente dal db

    private ViewModel mViewModel;

    //Dati del database
    List<Edificio> mEdificios;
    Edificio u14D;
    Edificio u6D;
    Edificio u7D;
    List<Aula> mAulas;
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

    public Relation(Context context, List<Edificio> edificios, List<Aula> aulas){
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

    private void addName(String nome,GeoPoint geo){
        name.put(geo,nome);
    }

    public String getName(GeoPoint posizione){
        return name.get(posizione);
    }

    private void addGeoPoint(String nome,GeoPoint geo){
        mGeoPointList.put(nome,geo);
    }

    public GeoPoint getGeoPoint(String posizione){
        return mGeoPointList.get(posizione);
    }

    private void addAule(String nome, int layer){
       auleU14.put(nome,layer);
    }

    public Bitmap getMap(String edificio, int piano){
        Bitmap bitmap;
        bitmap = showFloor.get(edificio).get(piano);
        return bitmap;
    }

    private void addEdificio(GeoPoint geo){
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

    public Edificio getEdificioU6(){
        Edificio u6F = null;
        for(Edificio ed: mEdificios){
            if(ed.getNomeEdificio().equals("u6")) {
                u6F = ed;
            }
        }
        return u6F;
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
                floor.put("u14", 3);
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
                contenuto.put("u6","u6");
                addEdificio(u6D.getPosizione());
                addName("u6",u6D.getPosizione());
            }else if(ed.getNomeEdificio().equals("u7")){
                u7D=ed;
                addGeoPoint("u7",u7D.getPosizione());
                contenuto.put("u7","u7");
            }
        }
        for(Aula a:mAulas){
            Log.d("database",""+a.getNomeAula()+" "+  a.getNomeEdificio());
            contenuto.put(a.getNomeAula(),a.getNomeEdificio());
            contenuto.put(a.getNomeAula(),a.getNomeEdificio());
            addGeoPoint(a.getNomeAula(),a.getPosizione());
            addAule(a.getNomeAula(),a.getPiano());
        }
        showFloor.put("u14",new HashMap<Integer, Bitmap>());
        showFloor.get("u14").put(0, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.u14));
        showFloor.get("u14").put(1, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.piantina1));
        showFloor.get("u14").put(2, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.piantina2));
        showFloor.put("u6",new HashMap<>());
        showFloor.get("u6").put(0, BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.u6));
    }

}
