package BottomNavFragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.GroundOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.osmdroidex2.Animation;
import com.example.osmdroidex2.IndoorNavActivity;

import Adapter.CustomAdapter;
import Database.PreDatabase;
import com.example.osmdroidex2.R;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

public class FragmentOSM extends Fragment {

    //per la listView
    private GridView gridView;
    private CustomAdapter adapter;
    private List<String> items;

    // Dichiarazione del timer
    CountDownTimer timer;


    ArrayList<GeoPoint> posizioneEdificio;
    MapView map;
    String edificioScoperto=null;
    GroundOverlay overlay;
    ArrayList<GeoPoint> waypoints;
    ArrayList<GeoPoint> waypoints2;
    Polyline roadOverlay;
    RoadManager roadManager;
    Button button;
    Button button1;
    ImageButton visitaGuidata;
    Button zoomButton;
    Button deZoomButton;
    ImageButton focusPoint;
    ImageButton location;

    // Dichiarazione delle variabili per la partenza e la destinazione selezionate dallo spinner
    String partenzaSelezionata = null;
    String destinazioneSelezionata = null;

    //Controller e manager per poter lavorare con i punti geospaziali.
    private IMapController mapController;
    private LocationManager locationManager;
    private MyLocationNewOverlay myLocationNewOverlay;

    //Viene usato per capire dove l'utente stia zommando e quindi capire se mostrare i bottoni dei layer
    BoundingBox box;
    BoundingBox boxUni;

    //Ho creato una classe intermezza tra la mia applicazione e i dati. Così l'unica classe che si dovrà
    //andare a modificare è in questo caso il PreDatabase.
    PreDatabase controller = new PreDatabase();


    //Tiene conto della destinazione selezionata, se è un aula prende quel valore se no ritorna null
    Marker aulaSelezionata;
    boolean posizioneAttuale = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posizioneEdificio=controller.getEdificio();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_osm,container,false);

        Spinner spinnerPartenza = view.findViewById(R.id.spinner_partenza);
        Spinner spinnerDestinazione = view.findViewById(R.id.spinner_destinazione);

        //i button chiamati in questo modo orribile sono per i 2 layer aggiunti
        focusPoint=(ImageButton) view.findViewById(R.id.focus);
        location = (ImageButton) view.findViewById(R.id.location);

        visitaGuidata = (ImageButton) view.findViewById(R.id.appGhero);
        visitaGuidata.setVisibility(View.GONE);
        /*zoomButton = (Button) view.findViewById(R.id.buttonZoom);
        deZoomButton = (Button) view.findViewById(R.id.buttonZoom2);*/

        //per la listView
        gridView = view.findViewById(R.id.grid_view);
        gridView.setVisibility(View.GONE);

        items = new ArrayList<>();
        adapter = new CustomAdapter(getContext(), items);
        gridView.setAdapter(adapter);

        //popolo le mie scelte, successivamente si andranno a prendere dal database
        List<String> opzioniPartenza = Arrays.asList("u14","u6","Posizione Attuale","u7", "u14FirstFloor","u14SecondFloor");
        List<String> opzioniDestinazione = Arrays.asList("u6","u14", "u7", "u14FirstFloor","u14SecondFloor");
        // Crea un adapter per le opzioni di selezione della partenza
        ArrayAdapter<String> adapterPartenza = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, opzioniPartenza);
        adapterPartenza.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPartenza.setAdapter(adapterPartenza);

        // Crea un adapter per le opzioni di selezione della destinazione
        ArrayAdapter<String> adapterDestinazione = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, opzioniDestinazione);
        adapterDestinazione.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestinazione.setAdapter(adapterDestinazione);

        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //Manager per calcolare in automatico il routing
        roadManager = new OSRMRoadManager(ctx, "Prova indoor");

        //Vado a creare la mappa
        map = (MapView)  view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);

        //per avere solo una porzione di mappa dell'uni
       /*map.setScrollableAreaLimitLatitude(45.5329, 45.5075, 0);
        map.setScrollableAreaLimitLongitude(9.1841, 9.2241, 0);*/

        //per avere solo una porzione di mappa di casa mia
       /* map.setScrollableAreaLimitLatitude(45.61506, 45.61118, 0);
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

        map.setBuiltInZoomControls(true);


        //Vado a creare due marker che mi vanno a mostrare il percorso, e il waypoint che poi servirà
        //per andare a creare il percorso tra questi 2 punti
        GeoPoint startPoint = new GeoPoint(45.52379, 9.21958);
        GeoPoint endPoint = new GeoPoint(45.52378, 9.2198);
        GeoPoint middlePoint = new GeoPoint(45.52376, 9.21968);
        Marker startMarker = new Marker(map);
        startMarker.setId("start");
        startMarker.setIcon(getResources().getDrawable(R.drawable.baseline_heart_broken_24));
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

        List<GeoPoint> points = new ArrayList<>();
        points.add(startPoint);
        points.add(middlePoint);
        points.add(endPoint);
        Polyline line = new Polyline();
        line.setPoints(points);
        line.setColor(Color.RED);
        line.setWidth(5f);

        // Add the Polyline to the map view
        addRemoveLayerLine(true, line);

        //serve per rimuovere lo zoom automatico
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);


        boxUni = map.getBoundingBox();

        //serve per fare una polilinea animata
        waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        waypoints.add(middlePoint);
        waypoints.add(endPoint);
        Animation animation = new Animation(map, waypoints, ctx);
        animation.addOverlays();

        //Mostra la bussola
        /*CompassOverlay compassOverlay = new CompassOverlay(ctx, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);*/

        //invalidare la mappa per aggiornarla, al cambio di qualcosa della mappa è consigliato
        //aggiornarla.
        map.invalidate();

        //GPS//
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Se il GPS è attivo, aggiungi l'icona sulla mappa, poichè il listener del gps non funziona
        // da subito, quindi c'è bisogno di un controllo iniziale
        if (isGPSEnabled) {
            myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), map);
            map.getOverlayManager().add(myLocationNewOverlay);
            myLocationNewOverlay.enableFollowLocation();
        }

        // Imposta il listener sul menu a tendina della partenza
        spinnerPartenza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posizione, long id) {
                // Quando viene selezionata un'opzione dalla lista della partenza, si salva il valore in una variabile
                partenzaSelezionata = (String) adapterView.getItemAtPosition(posizione);
                // Se entrambe le scelte sono state selezionate allora vado ad eseguire il codice
                if (partenzaSelezionata != null && destinazioneSelezionata != null && map != null) {
                    // Visto che sono state selezionate vado a creare una lista di punti che poi verrà trasformata
                    // in una strada da dare in pasto al RoadManager
                    Log.d("routing", partenzaSelezionata);

                    waypoints2 = new ArrayList<GeoPoint>();

                    //In questo caso devo controllare che lo spinner abbia selezionato la posizioneAttuale così
                    //da poter capire da dove parte, in questo caso dal gps
                    if(partenzaSelezionata == "Posizione Attuale"){
                        posizioneAttuale=true;
                        Log.d("routing", ""+myLocationNewOverlay);
                        waypoints2.add(myLocationNewOverlay.getMyLocation());
                    }else{
                        posizioneAttuale=false;
                        waypoints2.add(controller.getGeoPoint(partenzaSelezionata));
                    }

                    // In questo caso vado ad aggiungere un controllo poichè così facendo riconosco
                    //quale destinazioni sono delle aule, e quindi se lo sono mi va a mostrare l'aula
                    // scelta tramite il marker
                    if(controller.getFloor(destinazioneSelezionata) != null){
                        //prendo il punto dove devo mettere il marker
                        addRemoveMarker(false,aulaSelezionata);
                        aulaSelezionata=null;
                        GeoPoint point = controller.getGeoPoint(destinazioneSelezionata);
                        //creo il marker per poter segnalare il posto
                        aulaSelezionata=new Marker(map);
                        aulaSelezionata.setPosition(point);
                        aulaSelezionata.setTitle("Nome del luogo");
                        aulaSelezionata.setSubDescription("Piano 1");
                        aulaSelezionata.setIcon(getResources().getDrawable(R.drawable.baseline_heart_broken_24));
                        getFloorDestinazione(controller.getFloor(destinazioneSelezionata));
                    }else{
                        if (overlay != null) {
                            map.getOverlayManager().remove(overlay);
                        }
                        if(aulaSelezionata != null){
                            map.getOverlays().remove(aulaSelezionata);
                        }
                        map.invalidate();
                    }
                    FragmentOSM.ExecuteTaskInBackGround ex = new FragmentOSM.ExecuteTaskInBackGround();
                    ex.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Non viene selezionata alcuna opzione
            }
        });

        // Imposta il listener sul menu a tendina della destinazione, stesse considerazioni del listener
        // della partenza.
        spinnerDestinazione.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posizione, long id) {
                // Quando viene selezionata un'opzione dalla lista della destinazione, si salva il valore in una variabile
                destinazioneSelezionata = (String) adapterView.getItemAtPosition(posizione);
                // Eseguire l'operazione desiderata con il valore selezionato
                if (partenzaSelezionata != null && destinazioneSelezionata != null && map != null) {
                    // Entrambe le opzioni sono state selezionate, quindi è possibile eseguire il calcolo del percorso
                    waypoints2 = new ArrayList<GeoPoint>();
                    waypoints2.add(controller.getGeoPoint(partenzaSelezionata));

                    //In questo caso devo controllare che lo spinner abbia selezionato la posizioneAttuale così
                    //da poter capire da dove parte, in questo caso dal gps
                    if(partenzaSelezionata == "Posizione Attuale"){
                        posizioneAttuale=true;
                        waypoints2.add(myLocationNewOverlay.getMyLocation());
                    }else{
                        posizioneAttuale=false;
                        waypoints2.add(controller.getGeoPoint(partenzaSelezionata));
                    }

                    if(controller.getFloor(destinazioneSelezionata) != null){
                        addRemoveMarker(false,aulaSelezionata);
                        aulaSelezionata=null;
                        //prendo il punto dove devo metterlo
                        GeoPoint point=controller.getGeoPoint(destinazioneSelezionata);
                        //creo il marker per poter segnalare il posto
                        aulaSelezionata=new Marker(map);
                        //tutti questi parametri verranno presi dal database se ne avremo bisogno
                        //per ora ho scelto delle descrizioni fisse per non complicare troppo il codice
                        aulaSelezionata.setPosition(point);
                        aulaSelezionata.setTitle("Nome del luogo");
                        aulaSelezionata.setSubDescription("Piano 1");
                        aulaSelezionata.setIcon(getResources().getDrawable(R.drawable.baseline_heart_broken_24));
                        getFloorDestinazione(controller.getFloor(destinazioneSelezionata));
                    }else{
                        if (overlay != null) {
                            map.getOverlayManager().remove(overlay);
                        }
                        if(aulaSelezionata != null){
                            map.getOverlays().remove(aulaSelezionata);
                        }
                        map.invalidate();
                    }

                    FragmentOSM.ExecuteTaskInBackGround ex = new FragmentOSM.ExecuteTaskInBackGround();
                    ex.execute();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Non viene selezionata alcuna opzione
            }
        });

        //Per selezionare il piano corretto
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addRemoveMarker(false,aulaSelezionata);
                getFloorDestinazione(position);
            }
        });

        //Serve per andare al punto di destinazione
        focusPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(destinazioneSelezionata!=null){
                    mapController = map.getController();
                    mapController.animateTo(controller.getGeoPoint(destinazioneSelezionata));
                }
            }
        });

        //serve per capire quando si zoomma dall'evento dell'ingrandimento con le due dita
        // Imposta il timer con una durata di 1 secondo (1000 millisecondi)
        timer = new CountDownTimer(150, 250) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Questo metodo viene chiamato ad ogni tick del timer (non necessario in questo caso)
            }

            @Override
            public void onFinish() {
                box = map.getBoundingBox();
                boolean ciao = false;
                edificioScoperto=null;
                if (map.getZoomLevelDouble() >= 19) {
                    for (GeoPoint geo : posizioneEdificio) {
                        if (box.contains(geo)) {
                            edificioScoperto = controller.getName(geo);
                            Log.d("edificio",edificioScoperto);
                            ciao = true;
                        }
                    }
                    if (ciao == true) {
                        gridView.setVisibility(View.VISIBLE);
                        updateItems(controller.getNumberOfFloor(edificioScoperto));
                        visitaGuidata.setVisibility(View.VISIBLE);
                    } else {
                        gridView.setVisibility(View.GONE);
                        visitaGuidata.setVisibility(View.GONE);
                        map.getOverlays().remove(overlay);
                        overlay=null;
                    }
                } else {
                    gridView.setVisibility(View.GONE);
                    visitaGuidata.setVisibility(View.GONE);
                    map.getOverlays().remove(overlay);
                    overlay=null;
                }
            }
        };

        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                timer.cancel(); // Annulla il timer precedente se presente
                timer.start();  // Avvia il nuovo timer
                return false;
            }
        });


        //Serve per capire quando lo zoom della mappa arriva a un max e quindi bloccarlo
        /*zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.getController().zoomIn();
                box = map.getBoundingBox();
                boolean contenente = false;
                //Voglio far comparire i bottoni dei layer solo se si ha un certo livello di zoom
                //potevo usare direttamente un for each.
                if (map.getZoomLevelDouble() >= 20) {
                    for (int i = 0; i < 4; i++) {
                        if (box.contains(pointsU14.get(i))) {
                            contenente = true;
                        }
                    }
                    if (contenente == true) {
                        button.setVisibility(View.VISIBLE);
                        button1.setVisibility(View.VISIBLE);
                    } else {
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
                box = map.getBoundingBox();
                boolean contenente = false;
                //Voglio far comparire i bottoni dei layer solo se si ha un certo livello di zoom
                //potevo usare direttamente un for each.
                if (map.getZoomLevelDouble() >= 20) {
                    for (int i = 0; i < 4; i++) {
                        if (box.contains(pointsU14.get(i))) {
                            contenente = true;
                        }
                    }
                    if (contenente == true) {
                        button.setVisibility(View.VISIBLE);
                        button1.setVisibility(View.VISIBLE);
                    } else {
                        button.setVisibility(View.GONE);
                        button1.setVisibility(View.GONE);
                    }
                } else {
                    button.setVisibility(View.GONE);
                    button1.setVisibility(View.GONE);
                }
            }
        });*/

        //Andare nella parte della Navigazione Indoor, dovrà usare edificioTrovato come variabile,
        //per capire a che edificio si riferisce
        visitaGuidata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IndoorNavActivity.class);
                startActivity(intent);
            }
        });

        //Questo bottone serve a chiedere i permessi del gps se non li si avesse, con tutti i vari
        //controlli. Nel caso si avesse i permessi allora funziona come il bottone del gps normale
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
                    Log.d("getLocation","prova il get");
                    getLocation();
                } else {
                    showAlertMessageLocationDisabled();
                }
            }
        });

        //Prima del locationManager bisogna vedere questi permessi, devo vedere se riesco a toglierli
        //però per ora funziona, quindi lascio così.

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //PARTE DEL RICALCOLO DEL PERCORSO SE SI SBAGLIA STRADA
                Log.d("ChangeLocation", "entrato");
                if(posizioneAttuale == true) {

                    //serve per capire se sono fuori dall'uni
                    GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    BoundingBox boxUni2 = map.getBoundingBox();
                    Log.d("CONTIENE", "dentro? " + boxUni2.contains(currentLocation));

                    //Fatta per non calcolare il percorso fuori dalla mappa dell'uni, poichè pensavo che
                    //se sono fuori dall'area dell'uni, non vado manco a calcolare il percorso. Così
                    //da avere solo un pezzo di mappa

                    if (boxUni2.contains(currentLocation)) {

                        //mylocover abilita la posizione e il focus su di essa e quindi senza enableFollow non potrei andarmene
                        if (waypoints2 != null && roadOverlay != null) {

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

                            if (minDistance >= 50) {
                                Log.d("cambioPercorso", "effettuato");
                                waypoints2 = new ArrayList<GeoPoint>();
                                waypoints2.add(currentLocation);
                                FragmentOSM.ExecuteTaskInBackGround ex = new FragmentOSM.ExecuteTaskInBackGround();
                                ex.execute();
                            }

                        }

                    } else {
                        if (myLocationNewOverlay != null) {

                            Log.d("CONTIENE", "dentro? " + "cancellare");
                            map.getOverlays().remove(myLocationNewOverlay);

                            if (roadOverlay != null) {
                                addRemoveLayerLine(false, roadOverlay);
                            }

                            map.invalidate();
                        }

                    }
                }
            }

            //Cosa fare se tolgo il gps
            @Override
            public void onProviderDisabled(String provider) {
                map.getOverlays().remove(myLocationNewOverlay);
                map.invalidate();
            }

            //Cosa fare se abilito il gps
            @Override
            public void onProviderEnabled(String provider) {
                //In questo caso aggiorno la posizione

                map.getOverlayManager().remove(myLocationNewOverlay);
                if(myLocationNewOverlay != null){
                    myLocationNewOverlay.disableFollowLocation();
                }
                myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), map);
                Log.d("getLocation","val1"+myLocationNewOverlay);
                map.getOverlayManager().add(myLocationNewOverlay);
                myLocationNewOverlay.enableMyLocation();
                myLocationNewOverlay.enableFollowLocation();
                map.invalidate();
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        });

        return view;
    }

    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

            map.getOverlayManager().remove(myLocationNewOverlay);
            myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), map);
            map.getOverlayManager().add(myLocationNewOverlay);
            myLocationNewOverlay.enableMyLocation();
            myLocationNewOverlay.enableFollowLocation();

        } else {
            Log.d("getLocation","permessi mancanti");
            requestPermission();
        }

    }

    private void showAlertMessageLocationDisabled(){
        AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
        builder.setMessage("Device location is turned off, Turn on The device location, Do you want to turn on location?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length ==1  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                // Autorizzazione negata, gestire di conseguenza
            }
        }
    }
    /////////////////////////////////////////////////

    private void addRemoveMarker(boolean add, Marker marker) {
        if(add == true) {
            map.getOverlayManager().add(marker);
        } else {
            map.getOverlays().remove(marker);
        }
    }

    public void addRemoveLayerLine(boolean add, Polyline line){
        if(add == true) {
            map.getOverlayManager().add(line);
        }else{
            map.getOverlays().remove(line);
        }
    }

    //Con questo metodo vado a mostrare la bitMap relative al piano indicato
    public void getFloorDestinazione(int i){
        ArrayList<GeoPoint> pointGroundOverley = controller.getPlanimetria(edificioScoperto);
        if(edificioScoperto=="u14") {
            if (i == 0) {
                //Guardo se era già presente e se lo era la tolgo per rimetterla
                if (overlay != null) {
                    map.getOverlays().remove(overlay);
                }

                //Vado a prendere la mappa salvata come bitmap
                //DEVO METTERLO NEL DATABASE DI PROVA E PASSARLA COSì NON DEVO ESEGUIRE STA OPERAZIONE QUA
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.piantina1);
                overlay = new GroundOverlay();
                overlay.setTransparency(0.0f);
                overlay.setImage(bitmap);

                //Vado a scegliere i punti dove andare a mettere la mappa
                overlay.setPosition(pointGroundOverley.get(0),pointGroundOverley.get(1),pointGroundOverley.get(2),pointGroundOverley.get(3));
                //Scelgo l'overlay su dove metterla
                map.getOverlayManager().add(overlay);

                if (aulaSelezionata != null && controller.getFloor(destinazioneSelezionata) == 1) {
                    // Aggiunta del marker alla mappa
                    addRemoveMarker(false, aulaSelezionata);
                    addRemoveMarker(true, aulaSelezionata);
                }

                map.invalidate();
                Log.d("layer", "0");

            } else if (i == 1) {

                if (overlay!= null) {
                    map.getOverlays().remove(overlay);
                }

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.piantina2);
                overlay = new GroundOverlay();
                overlay.setTransparency(0.0f);
                overlay.setImage(bitmap);


                overlay.setPosition(pointGroundOverley.get(0),pointGroundOverley.get(1),pointGroundOverley.get(2),pointGroundOverley.get(3));
                map.getOverlayManager().add(overlay);

                if (aulaSelezionata != null && controller.getFloor(destinazioneSelezionata) == 2) {
                    // Aggiunta del marker alla mappa
                    addRemoveMarker(false, aulaSelezionata);
                    addRemoveMarker(true, aulaSelezionata);
                }

                map.invalidate();
                Log.d("layer", "1");
            } else {

            }
        }else{

            //Guardo se era già presente e se lo era la tolgo per rimetterla
            if (map.getOverlays().get(1) != null) {
                map.getOverlays().remove(1);
            }

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.u6);
            overlay = new GroundOverlay();
            overlay.setTransparency(0.0f);
            overlay.setImage(bitmap);


            //Vado a scegliere i punti dove andare a mettere la mappa
            overlay.setPosition(pointGroundOverley.get(0),pointGroundOverley.get(1),pointGroundOverley.get(2),pointGroundOverley.get(3));
            //Scelgo l'overlay su dove metterla
            map.getOverlayManager().add(1, overlay);

            map.invalidate();
            Log.d("layer", "0");
        }
    }

    //Va a calcolarti il percorso data la partenza e destinazione dagli spinner
    public class ExecuteTaskInBackGround extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            GeoPoint destinazione = controller.getGeoPoint(destinazioneSelezionata);
            addRemoveLayerLine(false,roadOverlay);
            waypoints2.add(destinazione);
            Road road = roadManager.getRoad(waypoints2);
            roadOverlay = RoadManager.buildRoadOverlay(road);
            addRemoveLayerLine(true,roadOverlay);

            map.invalidate();
            return null;
        }
    }

    private void updateItems(int nfloor) {
        // Aggiorna la lista degli elementi
        items.clear();

        for (int i = 0; i < nfloor; i++) {
            items.add("" + (i + 1));
        }
        // Aggiorna l'adapter con i nuovi dati
        adapter.updateItems(items);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (myLocationNewOverlay != null) {
            myLocationNewOverlay.enableMyLocation();
            myLocationNewOverlay.enableFollowLocation();
        }

        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (myLocationNewOverlay != null) {
            myLocationNewOverlay.enableFollowLocation();
        }

        myLocationNewOverlay.disableFollowLocation();
        map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myLocationNewOverlay != null) {
            myLocationNewOverlay.enableFollowLocation();
        }

        map.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (myLocationNewOverlay != null) {
            myLocationNewOverlay.enableFollowLocation();
        }

    }

}
