package com.example.osmdroidex2;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.GroundOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;


public class MainActivity extends AppCompatActivity implements LocationListener {

    MapView map;
    ArrayList<GeoPoint> waypoints;
    ArrayList<GeoPoint> waypoints2;
    Polyline roadOverlay;
    RoadManager roadManager;
    Button button;
    Button button1;
    Button visitaGuidata;
    Button zoomButton;
    Button deZoomButton;
    Road road1;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    // Dichiarazione delle variabili per la partenza e la destinazione selezionate
    String partenzaSelezionata = null;
    String destinazioneSelezionata = null;

    GeoPoint u7 = new GeoPoint(45.51731, 9.21291);
    GeoPoint u6 = new GeoPoint(45.51847, 9.21297);
    GeoPoint u14 = new GeoPoint(45.52388, 9.21877);

    private IMapController mapController;
    private LocationManager locationManager;
    private MyLocationNewOverlay myLocationNewOverlay;

    //viene usato per capire dove l'utente stia zommando e quindi capire se mostrare i bottoni dei layer
    BoundingBox box;
    BoundingBox boxUni;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinnerPartenza = findViewById(R.id.spinner_partenza);
        Spinner spinnerDestinazione = findViewById(R.id.spinner_destinazione);
        button = (Button) findViewById(R.id.image);
        button1 = (Button) findViewById(R.id.image1);
        visitaGuidata=(Button)findViewById(R.id.appGhero);
        zoomButton = (Button) findViewById(R.id.buttonZoom);
        deZoomButton = (Button) findViewById(R.id.buttonZoom2);
        List<String> opzioniPartenza = Arrays.asList("U14");
        List<String> opzioniDestinazione = Arrays.asList("U6", "U7");
        // Crea un adapter per le opzioni di selezione della partenza
        ArrayAdapter<String> adapterPartenza = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opzioniPartenza);
        adapterPartenza.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPartenza.setAdapter(adapterPartenza);

        // Crea un adapter per le opzioni di selezione della destinazione
        ArrayAdapter<String> adapterDestinazione = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opzioniDestinazione);
        adapterDestinazione.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestinazione.setAdapter(adapterDestinazione);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        roadManager = new OSRMRoadManager(this, "Prova indoor");

        //per capire se esiste il gps è attivo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, this);

        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);

        //per avere solo una porzione di mappa dell'uni
       /* map.setScrollableAreaLimitLatitude(45.5329, 45.5075, 0);
        map.setScrollableAreaLimitLongitude(9.1841, 9.2241, 0);*/

        //casa mia
        /*map.setScrollableAreaLimitLatitude(45.61506, 45.61118, 0);
        map.setScrollableAreaLimitLongitude(9.15603, 9.16372, 0);*/

        //Then we add default zoom buttons, and ability to zoom with 2 fingers (multi-touch)
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        //We can move the map on a default view point. For this, we need access to the map controller:
        mapController = map.getController();
        mapController.setZoom(15);
        //per regolare il max/min zoom
        map.setMaxZoomLevel(22.0);
        //map.setMinZoomLevel(15.0);
        //map.setMinZoomLevel(10.0);




        GeoPoint endPoint = new GeoPoint(45.52378, 9.2198);
        GeoPoint middlePoint = new GeoPoint(45.52376, 9.21968);

        /* Resources res = getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.baseline_heart_broken_24, null);*/

        GeoPoint startPoint = new GeoPoint(45.52379, 9.21958);
        Marker startMarker = new Marker(map);
        startMarker.setId("start");
        startMarker.setIcon(getDrawable(R.drawable.baseline_heart_broken_24));
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        addRemoveMarker(true, startMarker);

        Marker endMarker = new Marker(map);
        endMarker.setPosition(endPoint);
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        addRemoveMarker(true, endMarker);

        //per cancellare gli overlay con quell'id
                /* for(int i=0;i<map.getOverlays().size();i++){
                Overlay overlay2=map.getOverlays().get(i);
                if(overlay2 instanceof Marker&&((Marker) overlay2).getId().equals("start")){
                map.getOverlays().remove(overlay);
                }
            }*/

        waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        waypoints.add(middlePoint);
        waypoints.add(endPoint);


        List<GeoPoint> points = new ArrayList<>();
        points.add(startPoint);
        points.add(middlePoint);
        points.add(endPoint);
        // Create the Polyline with the GeoPoint List and set the line color and width
        Polyline line = new Polyline();
        line.setPoints(points);
        line.setColor(Color.RED);
        line.setWidth(5f);

        // Add the Polyline to the map view
        addRemoveLayerLine(true,line);

        /*serve per rimuovere lo zoom automatico
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);*/

        ArrayList<GeoPoint>pointsU14 = addU14Layer();
        /**
         * Aggiungere l'animazione grazie alla classe Animation al path creato dai diversi geopoint
         * dentro a waypoints
         */
        Animation animation=new Animation(map, waypoints,ctx);
        animation.addOverlays();

        boxUni= map.getBoundingBox();

        //Mostra la bussola
        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        //invalidare la mappa per aggiornarla
        map.invalidate();


        // Imposta il listener sul menu a tendina della partenza
        spinnerPartenza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posizione, long id) {
                // Quando viene selezionata un'opzione dalla lista della partenza, si salva il valore in una variabile
                partenzaSelezionata = (String) adapterView.getItemAtPosition(posizione);
                // Eseguire l'operazione desiderata con il valore selezionato
                if (partenzaSelezionata != null && destinazioneSelezionata != null&&map!=null) {
                    // Entrambe le opzioni sono state selezionate, quindi è possibile eseguire il calcolo del percorso
                    Log.d("routing", partenzaSelezionata);
                    waypoints2 = new ArrayList<GeoPoint>();
                    waypoints2.add(u14);
                    ExecuteTaskInBackGround ex = new ExecuteTaskInBackGround();
                    ex.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Non viene selezionata alcuna opzione
            }
        });

        // Imposta il listener sul menu a tendina della destinazione
        spinnerDestinazione.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posizione, long id) {
                // Quando viene selezionata un'opzione dalla lista della destinazione, si salva il valore in una variabile
                destinazioneSelezionata = (String) adapterView.getItemAtPosition(posizione);
                // Eseguire l'operazione desiderata con il valore selezionato
                if (partenzaSelezionata != null && destinazioneSelezionata != null &&map!=null) {
                    // Entrambe le opzioni sono state selezionate, quindi è possibile eseguire il calcolo del percorso
                    waypoints2 = new ArrayList<GeoPoint>();
                    waypoints2.add(u14);
                    ExecuteTaskInBackGround ex = new ExecuteTaskInBackGround();
                    ex.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Non viene selezionata alcuna opzione
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*usato per cercare i valori sul dispositivo

                Log.d("Immagine", "entra nell'onclick");
                Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"pickAnImage"),1);
                */

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.piantina1);
                GroundOverlay overlay = new GroundOverlay();
                overlay.setTransparency(0.0f);
                overlay.setImage(bitmap);
                if (map.getOverlays().get(1) != null) {
                    map.getOverlays().remove(1);
                }
                overlay.setPosition(new GeoPoint(45.52391, 9.21894), new GeoPoint(45.52345, 9.22015), new GeoPoint(45.52335, 9.22009), new GeoPoint(45.52381, 9.21886));
                map.getOverlayManager().add(1, overlay);
                map.invalidate();
                Log.d("layer", "0");
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.piantina2);
                GroundOverlay overlay = new GroundOverlay();
                overlay.setTransparency(0.0f);
                overlay.setImage(bitmap);
                if (map.getOverlays().get(1) != null) {
                    map.getOverlays().remove(1);
                }
                overlay.setPosition(new GeoPoint(45.52391, 9.21894), new GeoPoint(45.52345, 9.22015), new GeoPoint(45.52335, 9.22009), new GeoPoint(45.52381, 9.21886));
                map.getOverlayManager().add(1, overlay);
                map.invalidate();
                Log.d("layer", "1");
            }
        });

        //serve per capire quando si zoomma dall'evento dell'ingrandimento con le due dita
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                box =map.getBoundingBox();
                boolean ciao=false;
                if (map.getZoomLevelDouble() >= 20) {
                    for(int i=0; i<4;i++){
                        if(box.contains(pointsU14.get(i))){
                            ciao=true;
                        }
                    }
                    if (ciao == true) {
                        button.setVisibility(View.VISIBLE);
                        button1.setVisibility(View.VISIBLE);
                    }else{
                        button.setVisibility(View.GONE);
                        button1.setVisibility(View.GONE);
                    }
                }else {
                    button.setVisibility(View.GONE);
                    button1.setVisibility(View.GONE);
                }
                return false;
            }
        });

        //Serve per capire quando lo zoom della mappa arriva a un max e quindi bloccarlo
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.getController().zoomIn();
                box =map.getBoundingBox();
                boolean ciao=false;
                if (map.getZoomLevelDouble() >= 20) {
                    for(int i=0; i<4;i++){
                        if(box.contains(pointsU14.get(i))){
                            ciao=true;
                        }
                    }
                    if (ciao == true) {
                        button.setVisibility(View.VISIBLE);
                        button1.setVisibility(View.VISIBLE);
                    }
                    else {
                        button.setVisibility(View.GONE);
                        button1.setVisibility(View.GONE);
                    }
                } else {
                    button.setVisibility(View.GONE);
                    button1.setVisibility(View.GONE);
                }
            }
        });

        deZoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.getController().zoomOut();
                box =map.getBoundingBox();
                boolean ciao=false;
                if (map.getZoomLevelDouble() >= 20) {
                    for(int i=0; i<4;i++){
                        if(box.contains(pointsU14.get(i))){
                            ciao=true;
                        }
                    }
                    if (ciao == true) {
                        button.setVisibility(View.VISIBLE);
                        button1.setVisibility(View.VISIBLE);
                    }
                    else {
                        button.setVisibility(View.GONE);
                        button1.setVisibility(View.GONE);
                    }
                } else {
                    button.setVisibility(View.GONE);
                    button1.setVisibility(View.GONE);
                }
            }
        });

        visitaGuidata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IndoorNavActivity.class);
                startActivity(intent);
            }
        });


    }

    private void addRemoveMarker(boolean add, Marker marker) {
        if(add == true) {
            map.getOverlayManager().add(0, marker);
        }else{
            map.getOverlays().remove(0);
        }
    }

    public void addRemoveLayerLine(boolean add, Polyline line){
        if(add == true) {
            map.getOverlayManager().add(2, line);
        }else{
            map.getOverlays().remove(2);
        }
    }

    public ArrayList<GeoPoint> addU14Layer(){
        ArrayList<GeoPoint> layerU14=new ArrayList<GeoPoint>();
        layerU14.add(new GeoPoint(45.52391, 9.21894));
        layerU14.add(new GeoPoint(45.52345, 9.22015));
        layerU14.add(new GeoPoint(45.52335, 9.22009));
        layerU14.add(new GeoPoint(45.52381, 9.21886));
        return layerU14;
    }

    public class ExecuteTaskInBackGround extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            GeoPoint destinazione;
            if (destinazioneSelezionata.equals("U6")) {
                destinazione = u6;
                Log.d("routing", "U6");
            } else {
                destinazione = u7;
                Log.d("routing", "u7");
            }
            if (map.getOverlays().get(2) != null) {
                addRemoveLayerLine(false,roadOverlay);
            }
            waypoints2.add(destinazione);
            Road road = roadManager.getRoad(waypoints2);
            roadOverlay = RoadManager.buildRoadOverlay(road);
            addRemoveLayerLine(true,roadOverlay);

            map.invalidate();
            return null;
        }
    }

    /**
     * Il metodo andrà a fare le sue operazioni quando il gps
     * è attivo, se spento non lo andrà ad eseguire in automatico.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("ChangeLocation","entrato");
        //serve per capire se sono fuori dall'uni
        GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        BoundingBox boxUni2= map.getBoundingBox();
        Log.d("CONTIENE", "dentro? "+ boxUni2.contains(currentLocation));
        if(boxUni2.contains(currentLocation)){
            // initialize the location overlay, attiva il gps e mostra dove sono, e la cam segue la persona
            //mylocover abilita la posizione e il focus su di essa e quindi senza enableFollow non potrei andarmene
            myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
            myLocationNewOverlay.enableMyLocation();
            myLocationNewOverlay.enableFollowLocation();
            map.getOverlays().add(4, myLocationNewOverlay);
            Log.d("CONTIENE", "esiste roadOverlay? "+ roadOverlay);
            if (waypoints2 != null && roadOverlay!=null ) {
                    Log.d("ChangeLocation", "dove " + location.getLatitude() + " " + location.getLongitude());
                    // Verifica se la distanza supera la soglia massima
                    ArrayList<GeoPoint> strada = (ArrayList<GeoPoint>) roadOverlay.getActualPoints();
                    double minDistance = 300;
                    for (GeoPoint roadPoint : strada) {
                        Location roadLocation = new Location("");
                        roadLocation.setLatitude(roadPoint.getLatitude());
                        roadLocation.setLongitude(roadPoint.getLongitude());
                        double distance = location.distanceTo(roadLocation);
                        if (distance < minDistance) {
                            minDistance = distance;
                        }
                    }
                    if (minDistance >= 150) {
                        waypoints2 = new ArrayList<GeoPoint>();
                        waypoints2.add(currentLocation);
                        ExecuteTaskInBackGround ex = new ExecuteTaskInBackGround();
                        ex.execute();
                    }else{
                        ExecuteTaskInBackGround ex = new ExecuteTaskInBackGround();
                        ex.execute();
                    }
                }
        }
        else{
            if (myLocationNewOverlay != null) {
                Log.d("CONTIENE", "dentro? "+ "cancellare");
                map.getOverlays().remove(myLocationNewOverlay);
                if(roadOverlay!=null) {
                    addRemoveLayerLine(false, roadOverlay);
                }
                map.invalidate();
            }
        }
    }

    /*Usata per andare a cercare l'immagine nel drive, invece che sul dispositivo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK && requestCode==1){

            ImageView imageView=findViewById(R.id.imageView);

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                GroundOverlay overlay = new GroundOverlay();
                overlay.setTransparency(0.0f);
                overlay.setImage(bitmap);
                if(map.getOverlays().get(0)!=null){
                    map.getOverlays().remove(0);
                }
                overlay.setPosition(new GeoPoint(45.52391, 9.21894),new GeoPoint(45.52345, 9.22015) ,new GeoPoint( 45.52335, 9.22009),new GeoPoint(45.52381, 9.21886));
                map.getOverlayManager().add(0,overlay);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        if (myLocationNewOverlay != null) {
            myLocationNewOverlay.enableMyLocation();
            myLocationNewOverlay.enableFollowLocation();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myLocationNewOverlay != null) {
            myLocationNewOverlay.enableFollowLocation();
        }
        locationManager.removeUpdates(this);
        map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myLocationNewOverlay != null) {
            myLocationNewOverlay.enableFollowLocation();
        }
        map.onDetach();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myLocationNewOverlay != null) {
            myLocationNewOverlay.enableFollowLocation();
        }
    }


}


