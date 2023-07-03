package BottomNavFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.osmdroidex2.Graph;
import com.example.osmdroidex2.IndoorNavigation;
import com.example.osmdroidex2.MapDrawer;
import com.example.osmdroidex2.Node;
import com.example.osmdroidex2.R;
import com.example.osmdroidex2.TouchTransformer;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Locale;

import dataAndRelation.Controller;
import dataAndRelation.Edificio;
import dataAndRelation.ViewModel;

public class FragmentIndoor extends Fragment implements SensorEventListener {
    private Handler handler;
    private Runnable animationRunnable;
    private int stepCount = 0;
    String edificios;
    private boolean showpath = false;
    private int steppy = 0;
    //private boolean first = true;
    // ATTRIBUTI PER BUSSOLA
    private Node nodeSphere;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private ImageView compassImageView;
    private TextView degreeTextView;
    // ATTRIBUTI PER CONTAPASSI
    private Sensor stepSensor;
    private Sensor orientationSensor;

    private TextView optTxt;

    private float[] position = new float[2];
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    // Un'istanza di PhotoView che visualizza l'immagine della planimetria
    PhotoView mapImage;
    PhotoView indicatorImage; //nuova photoView per l'indicatore utente.

    // Un'istanza di Bitmap che contiene l'immagine della planimetria
    private Bitmap mapBitmap;
    // Un'istanza di Bitmap che contiene l'immagine dell'indicatore
    private Bitmap indicatorBitmap;

    // Un'istanza di MapDrawer per disegnare percorsi sulla planimetria
    private MapDrawer mapDrawer;
    // Un'istanza di MapDrawer per disegnare indicatori utente sulla mappa
    private MapDrawer indicatorDrawer;

    private Drawable map;
    private Drawable indicator;

    private boolean[] start = new boolean[1];

    private int[] steps = new int[1];

    private double MagnitudePrevious = 0;
    private TextInputEditText startPoint;

    private TextInputEditText endPoint;

    private Switch aSwitch; //stairs

    private Switch bSwitch; //unavailable

    private Switch cSwitch; //crowded

    private String stairs = "";

    private String available = "";

    private String crowd = "";

    private Graph graph;

    private List<Node> path;

    private List<Node> path2;

    private float currentRotation = 0f;

    private IndoorNavigation indoorNav;

    private Button drawBtn;
    //private Button userBtn;

    private TouchTransformer touchTransformer;

    private Button btn_start;
    private Button stepBtn;

    private Button nextBtn;

    private Button backBtn;

    //private boolean[] user = new boolean[1];

    private TextView txt_passi;

    Controller controller;
    private SharedPreferences sharedPreferences;
    private int floorCount;

    private ViewModel viewModel;

    private Bitmap icon;

    private Edificio edificioObj;

    private Boolean navigationState;


    /**
     * Metodo onCreate per la creazione dell'activity.
     * Inizializza le variabili e carica l'immagine della planimetria.
     * Configura il grafo dei nodi e gli archi per la navigazione indoor.
     *
     * @param savedInstanceState Bundle contenente lo stato precedente dell'activity
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indoor, container, false);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        navigationState = false;

        //Vado a prendere i valori precedenti
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        initializeViews(view);

        controller = Controller.getInstance(null,null,null);

        floorCount = 0;

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.arrival_icon);

        //Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.arrival_icon);
        icon  = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);


        Log.d("IconNull", "" + icon);

        //Prendo l'edificio da cui parto e la destinazione(aula) se dal fragment di prima l'ho scelta
        String destinazione = sharedPreferences.getString("destinazione",null);
        String partenza = sharedPreferences.getString("partenza", null);
        String edificio = sharedPreferences.getString("edificio", null);

        /*
        if(destinazione == null){
            mapBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.u14);
            destinazione = "u14";
        }else{
            endPoint.setText(destinazione);  // Imposta il valore di endPoint prima di chiamare getMapFloor()
            getMapFloor(controller.getAppartenenza(destinazione),destinazione);
        }

        changeFloor(controller.getAppartenenza(destinazione));

        next_back_Btn(controller.getAppartenenza(destinazione));*/

        getMapFloor(edificio, destinazione,partenza);

       /* indicator = getResources().getDrawable(R.drawable.indicator);
        indicatorBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.indicator);

        touchTransformer = new TouchTransformer();


        txt_passi.setText("0");
        position[0] = 0;
        position[1] = 0;

        start[0] = false;

        if (mapBitmap == null || indicatorBitmap == null) {
            Log.e("MainActivity", "Failed to load map image. " +
                    "Ensure that the image is present in the res/drawable folder " +
                    "and its name matches the one in the code.");
            return view;
        }*/


       /* mapDrawer = new MapDrawer(mapBitmap);
        indicatorDrawer = new MapDrawer(indicatorBitmap);

        indoorNav = new IndoorNavigation(mapDrawer, getContext(), indicatorDrawer);

        //float[] touchPoint = new float[2];

        graph = new Graph();
        path = null;

        initializeGraphNodes();

        mapImage.setImageDrawable(map);
        mapImage.setImageBitmap(mapDrawer.getMapBitmap());
        mapImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        indicatorImage.setImageDrawable(indicator);
        indicatorImage.setImageBitmap(indicatorDrawer.getMapBitmap());
        indicatorImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        checkOptions();*/

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_start.getText() == "CANCEL") {
                    path = null;
                    indoorNav.stepNavigation(null, mapImage, steppy, indicatorImage, start, true, icon, handler, animationRunnable);
                    btn_start.setText("START");
                    showpath = false;
                }
                indoorNav.stepNavigation(path, mapImage, steppy, indicatorImage, start, false, icon, handler, animationRunnable);
                steppy ++;
                if (start[0]) {
                    start[0] = false;
                    //disegnaIndicatore(0, 0);
                    btn_start.setText("START");
                }
                else {
                    if(showpath) {
                        start[0] = true;
                        //btn_start.setVisibility(View.GONE);
                        btn_start.setText("CANCEL");
                    }
                }
            }
        });

        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    handler.removeCallbacks(animationRunnable);
                    clearPath(mapImage, indicatorImage);
                } catch (Exception e) {

                }
                clearPath(mapImage, indicatorImage);
                String start = startPoint.getText().toString();
                String end = endPoint.getText().toString();

                if( start.substring(0, 1).equals(end.substring(0, 1)) ){
                    graph = edificioObj.getGraph(start.charAt(0));
                    path = edificioObj.getGraph(start.charAt(0)).findShortestPath(startPoint.getText().toString(), endPoint.getText().toString(), stairs, available, crowd);
                    Log.d("awdad", "qui");
                }else{
                    path = edificioObj.getGraph(start.charAt(0)).findShortestPath(startPoint.getText().toString(), "stairs", stairs, available, crowd);
                    try {
                        path2 = edificioObj.getGraph(end.charAt(0)).findShortestPath(end.substring(0, 1) + path.get(path.size() - 1).getId().substring(1), endPoint.getText().toString(), stairs, available, crowd);
                    }catch (Exception e) {
                        path2 = null;
                    }
                }
                try {
                    path.get(0);
                    path.get(1);
                } catch (Exception e) {
                    //path2 = null;
                    path = null;
                }
                if(path == null) {
                    clearPath(mapImage, indicatorImage);
                }
                if (path != null) {
                    disegnaPercorso(path);
                    navigationState = true;
                    showpath = true;
                    steppy = 0;
                    position[0] = path.get(0).getX() * mapBitmap.getWidth();
                    position[1] = path.get(0).getY() * mapBitmap.getHeight();
                    disegnaIndicatore(position[0], position[1]);
                    int[] i = new int[1];
                    animazione(path.get(1).getX() * mapBitmap.getWidth(), path.get(1).getY() * mapBitmap.getHeight(), i, path);
                }
            }
        });

        //Log.d("Coordinate", "Width: "+  String.valueOf(mapBitmap.getWidth()) + "  Height: " + String.valueOf(mapBitmap.getHeight()));

        indicatorImage.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                Matrix matrix = new Matrix();
                indicatorImage.getSuppMatrix(matrix);
                mapImage.setDisplayMatrix(matrix);
            }
        });

        stepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPath(true);
                nodeSphere = indoorNav.stepNavigation(path, mapImage, steppy, indicatorImage, start, false, icon, handler, animationRunnable);
                steppy ++;
                if (nodeSphere == null) {
                    btn_start.setVisibility(View.VISIBLE);
                    showpath = false;
                    txt_passi.setText("0");
                    btn_start.setText("START");
                    path = null;
                }
            }
        });
/*
        //onCreate per bussola
       /* sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //---------- fine bussola
        //onCreate per contapassi
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        steps[0] = 0;*/

        /*
        user[0] = true;
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user[0]) {
                    userBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    user[0] = false;
                }
                else {
                    userBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    user[0] = true;
                }

            }
        });*/

        return view;
    }

    private void animazione(float xDestinazione, float yDestinazione, int[] i, List<Node> path) {
        try {
            handler = new Handler();
            animationRunnable = new Runnable() {
                @Override
                public void run() {
                    float stepSize = 60f;
                    if (Math.abs(position[0] - xDestinazione) <= stepSize &&
                            Math.abs(position[1] - yDestinazione) <= stepSize) {
                        if (xDestinazione == path.get(path.size() - 1).getX() * mapBitmap.getWidth() &&
                                yDestinazione == path.get(path.size() - 1).getY() * mapBitmap.getHeight()) {
                            // Il cerchio ha raggiunto la destinazione, interrompi l'animazione
                            i[0] = 1;
                            handler.removeCallbacks(animationRunnable);
                            position[0] = path.get(0).getX() * mapBitmap.getWidth();
                            position[1] = path.get(0).getY() * mapBitmap.getHeight();
                            animazione(path.get(i[0]).getX() * mapBitmap.getWidth(), path.get(i[0]).getY() * mapBitmap.getHeight(), i, path);
                        } else {
                            // vai al prossimo nodo
                            i[0]++;
                            handler.removeCallbacks(animationRunnable);
                            animazione(path.get(i[0]).getX() * mapBitmap.getWidth(), path.get(i[0]).getY() * mapBitmap.getHeight(), i, path);
                        }
                        return;
                    }
                    position[0] = (float) (position[0] + (calculateStepSize(position[0], xDestinazione, stepSize, true)));
                    position[1] = (float) (position[1] + (calculateStepSize(position[1], yDestinazione, stepSize, false)));

                    disegnaIndicatore(position[0], position[1]);
                    handler.postDelayed(this, 200); // 16ms corrisponde a circa 60 frame al secondo
                }
            };
            handler.post(animationRunnable);
        } catch(Exception e) {

        }
    }

    private float calculateStepSize(float v, float vDestinazione, float stepSize, boolean b) {
        if (v < vDestinazione) {
            return Math.min(stepSize, vDestinazione - v);
        } else {
            return Math.max(-stepSize, vDestinazione - v);
        }
    }


    private void initializeViews(View view) {
        //userBtn = view.findViewById(R.id.btn_user);
        drawBtn = view.findViewById(R.id.drawBtn);
        endPoint = view.findViewById(R.id.endPoint);
        optTxt = view.findViewById(R.id.btn_options);
        startPoint = view.findViewById(R.id.starPoint);
        nextBtn = view.findViewById(R.id.nextBtn);
        backBtn = view.findViewById(R.id.backBtn);
        txt_passi = view.findViewById(R.id.txt_passi);
        btn_start = view.findViewById(R.id.btn_avvia);
        mapImage = view.findViewById(R.id.map_image);
        indicatorImage = view.findViewById(R.id.indicator_image);
        stepBtn = view.findViewById(R.id.stepBtn);
        compassImageView = view.findViewById(R.id.compass_image_view);
        degreeTextView = view.findViewById(R.id.degree_text_view);
    }

    public void next_back_Btn(String edificio){
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path2 != null) {
                    handler.removeCallbacks(animationRunnable);
                    if (floorCount < controller.getNumberOfFloor(edificio)) {
                        floorCount++;
                        Log.d("Piani", "" + floorCount);

                        clearPath(mapImage, indicatorImage);
                        disegnaPercorso(path2);
                        int[] i = new int[1];
                        position[0] = path2.get(0).getX() * mapBitmap.getWidth();
                        position[1] = path2.get(0).getY() * mapBitmap.getHeight();
                        try {
                            handler.removeCallbacks(animationRunnable);
                            animazione(path2.get(1).getX() * mapBitmap.getWidth(), path2.get(1).getY() * mapBitmap.getHeight(), i, path2);
                        } catch (Exception e) {

                        }

                    /*if(navigationState && (path.get(0).getId().charAt(0) + "").equals(floorCount + "")){
                        disegnaPercorso(path);
                    }else if(navigationState && path2.get(0).getId().charAt(0) + "" == (char) floorCount + ""){
                        disegnaPercorso(path2);
                    }*/

                    } else {
                    }
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(floorCount > 0){
                    floorCount--;
                    Log.d("Piani", "" + floorCount);

                    clearPath(mapImage, indicatorImage);
                    disegnaPercorso(path);
                    int[] i = new int[1];
                    position[0] = path.get(0).getX() * mapBitmap.getWidth();
                    position[1] = path.get(0).getY() * mapBitmap.getHeight();
                    try {
                        handler.removeCallbacks(animationRunnable);
                        animazione(path.get(1).getX() * mapBitmap.getWidth(), path.get(1).getY() * mapBitmap.getHeight(), i, path);
                    } catch (Exception e) {

                    }

                }else{
                }
            }
        });
    }

    public void initializeGraphNodes(){
        graph.addNode("1", (float) 2020.6055 / 3520, (float) 1991.6936 / 4186,  "atrium", "available", "notCrow");
        graph.addNode("1.1", (float) 2278.0957 / 3520, (float) 1913.4905 / 4186,  "atrium", "available", "notCrow");
        graph.addNode("1.2", (float) 1769.668 / 3520, (float) 1773.3766 / 4186,  "atrium", "available", "notCrow");

        graph.addNode("2", (float) 1965.1758 / 3520, (float) 2835.8684 / 4186,  "atrium", "available", "notCrow");
        graph.addNode("2.1", (float) 1776.2207 / 3520, (float) 2523.056 / 4186,  "atrium", "available", "notCrow");

        graph.addNode("3", (float) 866.89453 / 3520, (float) 2128.549 / 4186, "classroom", "available", "notCrow");
        graph.addNode("3.1", (float) 1450.3027 / 3520, (float) 2089.4475 / 4186, "classroom", "available", "notCrow");

        graph.addNode("4", (float) 827.79297 / 3520, (float) 1600.678 / 4186, "bathroom", "available", "notCrow");
        graph.addNode("4.1", (float) 1029.8535 / 3520, (float) 1493.1487 / 4186, "bathroom", "available", "notCrow");

        graph.addNode("5", (float) 1342.7734 / 3520, (float) 909.651 / 4186, "classroom", "available", "notCrow");
        graph.addNode("5.1", (float) 1463.3008 / 3520, (float) 1209.4297 / 4186, "classroom", "available", "notCrow");

        graph.addNode("6", (float) 1939.1797 / 3520, (float) 883.5833 / 4186, "classroom", "available", "notCrow");
        graph.addNode("6.1", (float) 1763.1152 / 3520, (float) 1248.5312 / 4186, "classroom", "available", "notCrow");

        graph.addNode("7", (float) 2046.709 / 3520, (float) 1493.1487 / 4186, "stairs", "available", "notCrow");  ////////////////////////////////////////////////// modificare

        graph.addNode("8", (float) 1450.3027 / 3520, (float) 1519.2164 / 4186, "hallway", "available", "notCrow");
        graph.addNode("8.1", (float) 1450.3027 / 3520, (float) 1789.669 / 4186, "hallway", "available", "notCrow");
        graph.addNode("8.2", (float) 1776.2207 / 3520, (float) 1467.081 / 4186, "hallway", "available", "notCrow");

        graph.addNode("9", (float) 2591.0156 / 3520, (float) 1913.4905 / 4186,  "atrium", "available", "notCrow");

        graph.addEdge("1", "7", 1);    ////////////////////////////////////////// cancellare
        graph.addEdge("7", "6", 1);    ////////////////////////////////////////// cancellare

        graph.addEdge("1", "1.1", 1);
        graph.addEdge("1", "1.2", 1);

        graph.addEdge("2", "2.1", 1);

        graph.addEdge("3", "3.1", 1);

        graph.addEdge("4", "4.1", 1);

        graph.addEdge("5", "5.1", 1);

        graph.addEdge("6", "6.1", 1);

        graph.addEdge("8", "8.1", 1);

        graph.addEdge("8", "8.2", 1);

        graph.addEdge("1", "2.1", 1);

        graph.addEdge("1.2", "2.1", 1);
        graph.addEdge("1.2", "8.1", 1);

        graph.addEdge("3.1", "8.1", 1);

        graph.addEdge("4.1", "8", 1);

        graph.addEdge("5.1", "8", 1);

        graph.addEdge("6.1", "8.2", 1);

        graph.addEdge("7", "8.2", 1);

        graph.addEdge("9", "1.1", 1);

        //Graph.Node nodeA = graph.getNode("A");
    }

    private void disegnaIndicatore(float x, float y) {
        Matrix photoMatrix = new Matrix();
        indicatorImage.getSuppMatrix(photoMatrix);
        float[] matrixValues = new float[9];
        photoMatrix.getValues(matrixValues);
        float currentScale = matrixValues[Matrix.MSCALE_X];
        PointF currentTranslate = new PointF(matrixValues[Matrix.MTRANS_X], matrixValues[Matrix.MTRANS_Y]);
        clearPath(indicatorImage);
        TouchTransformer transformer = new TouchTransformer();
        indicatorDrawer.drawIndicator(x, y);
        indicatorImage.invalidate();
        Matrix newMatrix = new Matrix();
        newMatrix.setScale(currentScale, currentScale);
        newMatrix.postTranslate(currentTranslate.x, currentTranslate.y);
        indicatorImage.setDisplayMatrix(newMatrix);
    }

    private void checkOptions() {
        optTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog2 = new Dialog(getContext());
                //  Imposta il layout del tuo dialog personalizzato
                dialog2.setContentView(R.layout.open_dialog);

                aSwitch = dialog2.findViewById(R.id.switch1);
                bSwitch = dialog2.findViewById(R.id.switch2);
                cSwitch = dialog2.findViewById(R.id.switch3);
                ////////////////////////////

                checkProblem(aSwitch,bSwitch,cSwitch);

                ///////////////////////////
                if (stairs == "stairs") {
                    aSwitch.setChecked(true);
                }
                else aSwitch.setChecked(false);
                if (available == "unavailable") {
                    bSwitch.setChecked(true);
                }
                else bSwitch.setChecked(false);
                if (crowd == "crowded") {
                    cSwitch.setChecked(true);
                }
                else cSwitch.setChecked(false);

                aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(aSwitch.isChecked())
                            stairs = "stairs";
                        else
                            stairs = "";
                    }
                });

                bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(bSwitch.isChecked())
                            available = "unavailable";
                        else
                            available = "";
                    }
                });

                cSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(cSwitch.isChecked())
                            crowd = "crowded";
                        else
                            crowd = "";
                    }
                });
                dialog2.show();
            }
        });
    }

    /**
     * Disegna il percorso più breve tra due nodi sulla planimetria.
     * Utilizza l'istanza di MapDrawer per disegnare il percorso sulla planimetria
     * e aggiorna l'immagine visualizzata.
     *
     * @param nodes Lista di nodi che rappresentano il percorso da disegnare
     */
    private void disegnaPercorso(List<Node> nodes) {
        mapDrawer.drawPath(nodes, mapImage, true, icon, edificioObj.getOneMeter());
        mapImage.invalidate();
    }

    public void clearPath(PhotoView mapImage, PhotoView indicatorImage){
        mapDrawer.resetMap(); // Aggiungi questa riga per ripristinare la mappa nel MapDrawer
        this.mapImage.setImageBitmap(mapDrawer.getMapBitmap()); // Imposta la nuova mappa ripristinata
        this.mapImage.invalidate(); // Forza il ridisegno della PhotoView
        indicatorDrawer.resetMap();
        this.indicatorImage.setImageBitmap(indicatorDrawer.getMapBitmap());
        this.indicatorImage.invalidate();
    }

    public void clearPath(boolean b){
        mapImage.invalidate(); // Forza il ridisegno della PhotoView
    }

    public void clearPath(PhotoView image){
        indicatorDrawer.resetMap(); // Aggiungi questa riga per ripristinare la mappa nel MapDrawer
        image.setImageBitmap(indicatorDrawer.getMapBitmap()); // Imposta la nuova mappa ripristinata
        image.invalidate(); // Forza il ridisegno della PhotoView
    }

    public void checkPoint(Graph graph, TouchTransformer touchTransformer, PhotoView indicatorImage){
        indicatorImage.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {

                //indicatorImage.setScale(2.0f, x, y, true);
                //879 1091
                float pointX = touchTransformer.transformX(x, indicatorImage, indicatorBitmap) / mapBitmap.getWidth();
                float pointY = touchTransformer.transformY(y, indicatorImage, indicatorBitmap) / mapBitmap.getHeight();
                //Toast.makeText(getContext(), ""+pointX+" "+pointY, Toast.LENGTH_SHORT).show();

                /*if (!user[0]) {
                    disegnaIndicatore(pointX, pointY);
                    position[0] = pointX;
                    position[1] = pointY;
                    user[0] = true;
                    userBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }*/

                Node node = indoorNav.checkNode(graph, pointX, pointY);
                final Dialog dialog = new Dialog(getContext());

                //  Imposta il layout del tuo dialog personalizzato
                dialog.setContentView(R.layout.custom_dialog);

                TextView node_name = dialog.findViewById(R.id.node_name);
                TextView node_id = dialog.findViewById(R.id.node_id);
                TextView node_type = dialog.findViewById(R.id.node_type);

                if(node != null) {
                    node_name.setText("Node: " + node.getId());
                    node_id.setText(node.getId());
                    node_type.setText(node.getRoomType());

                    Button btn_starting = dialog.findViewById(R.id.start_btn);
                    Button btn_end = dialog.findViewById(R.id.end_btn);

                    Switch sw_crowded = dialog.findViewById(R.id.sw_crowded);
                    Switch sw_available = dialog.findViewById(R.id.sw_available);
                    if (node.getAvailability().equals("available")) {
                        sw_available.setChecked(true);
                    }
                    if (node.getAvailability().equals("unavailable")) {
                        sw_available.setChecked(false);
                    }
                    if (node.getCrowdness().equals("crowded")) {
                        sw_crowded.setChecked(true);
                    }
                    if (node.getCrowdness().equals("notCrow")) {
                        sw_crowded.setChecked(false);
                    }

                    //Toast.makeText(getContext(), ""+node.getAvailability(), Toast.LENGTH_SHORT).show();

                    btn_starting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startPoint.setText(node_id.getText().toString());
                            dialog.dismiss();
                        }
                    });

                    btn_end.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            endPoint.setText(node_id.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    sw_available.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (sw_available.isChecked()) {
                                node.setAvailability("available");
                            } else
                                node.setAvailability("unavailable");
                        }
                    });

                    sw_crowded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (sw_crowded.isChecked()) {
                                node.setCrowdness("crowded");
                            } else
                                node.setCrowdness("notCrow");
                        }
                    });

                    // Mostra il dialog
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
/*        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);*/
    }

    @Override
    public void onPause() {
        super.onPause();
       // sensorManager.unregisterListener(this);
        Log.d("cabbinculo", "pausa");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("cabbinculo", "destroy");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;
        boolean[] is_step = new boolean[1];
        is_step[0] = false;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
            mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
            mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
            mGravity[0] = mGravity[0] - event.values[0];
            mGravity[1] = mGravity[1] - event.values[1];
            mGravity[2] = mGravity[2] - event.values[2];
            //Toast.makeText(this, ""+mGravity[0]+" "+mGravity[1]+" "+mGravity[2], Toast.LENGTH_SHORT).show();
            float x_acceleration = event.values[0];
            float y_acceleration = event.values[1];
            float z_acceleration = event.values[2];

            double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
            double MagnitudeDelta = Magnitude - MagnitudePrevious;
            MagnitudePrevious = Magnitude;
            if (MagnitudeDelta > 6){
                stepCount++;
                is_step[0] = true;
            }
            txt_passi.setText(String.valueOf(stepCount));
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
            mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
            mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
        }

        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
            float degrees = (azimuthInDegrees + 360) % 360;

            degreeTextView.setText(String.format(Locale.getDefault(), "%.0f°", degrees));
            compassImageView.setRotation(-degrees);
            if (start[0]) {
                mapImage.setRotation(degrees);
                indicatorImage.setRotation(degrees);
                double radian = Math.toRadians(degrees);
                double stepLength = 30;
                if (is_step[0]) {
                    is_step[0] = false;
                    double deltaX = stepLength * Math.sin(radian);
                    double deltaY = stepLength * Math.cos(radian);
                    if (position[0] != 0) {
                        position[0] -= deltaX;
                        position[1] -= deltaY;
                        disegnaIndicatore(position[0], position[1]);
                        double dx = Math.abs(position[0]-nodeSphere.getX());
                        double dy = Math.abs(position[1]-nodeSphere.getY());
                        if (dx < 50 && dy < 50) {
                            clearPath(true);
                            nodeSphere = indoorNav.stepNavigation(path, mapImage, steppy, indicatorImage, start, false, icon, handler, animationRunnable);
                            steppy ++;
                            if (nodeSphere == null) {
                                btn_start.setVisibility(View.VISIBLE);
                                showpath = false;
                                txt_passi.setText("0");
                                return;
                            }
                        }
                    }
                }
            }
            else {
                mapImage.setRotation(0f);
                indicatorImage.setRotation(0f);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Vado a implementare la logica per cui tramite le relazioni vado a prendere la mappa corretta
    //da mostrare
    public void getMapFloor(String NameEdificioDef, String destinazione, String partenza){
        Log.d("PrendereLaMappa", ""+NameEdificioDef);
        Log.d("PrendereLaMappa", ""+destinazione);
        if(NameEdificioDef==null){
            if(destinazione!=null){
                edificios= controller.getAppartenenza(destinazione);
            }
        }else{
            edificios=NameEdificioDef;
        }
        Log.d("PrendereLaMappa", ""+edificios);
        viewModel.getEdificioObj(edificios).observe(getActivity(), new Observer<Edificio>() {
            @Override
            public void onChanged(Edificio edificio) {
                Bitmap bit = null;

                edificioObj = edificio;

                String NameEdificio = NameEdificioDef;

                if(NameEdificio == null){
                    if(destinazione!=null){
                        if (controller.getFloor(destinazione) != null) {
                            Log.d("PrendereLaMappa", "1");
                            endPoint.setText(destinazione);
                            int i = controller.getFloor(destinazione);
                            bit = controller.getMap(NameEdificio, i);
                            ///Grafo a caso per vedere se funziona

                            //se la partenza non appartiene allo stesso edificio allora l'edificio della destinazione
                            //dovrà avere come partenza l'edificio stesso che in questo caso è l'entry point
                            if(NameEdificio.equals(controller.getAppartenenza(partenza)) != NameEdificio.equals(controller.getAppartenenza(destinazione)) ){
                                Log.d("PrendereLaMappa", "2");
                                startPoint.setText(NameEdificio);
                                bit = controller.getMap(NameEdificio, 0);
                                graph = edificio.getGraph0();
                            }
                            //non è un aula
                        } else {
                            Log.d("PrendereLaMappa", ""+destinazione);
                            startPoint.setText(destinazione);
                            bit = controller.getMap(destinazione, 0);
                            Log.d("PrendereLaMappa", ""+bit);
                            graph = edificio.getGraph0();
                        }
                    }else{
                        Log.d("PrendereLaMappa", "4");
                        bit = BitmapFactory.decodeResource(getResources(),
                                R.drawable.u14);
                        String NameEdificioDef = "u14";
                        graph = new Graph();
                        initializeGraphNodes();
                    }
                //ho zoommato almeno su un edificio
                }else{
                    //se sono null vuoldire che ho zoommato solo su un edificio
                    if(NameEdificio != null && partenza==null && destinazione==null){
                        bit = controller.getMap(NameEdificio, 0);
                        graph = edificio.getGraph0();
                        startPoint.setText("");
                        endPoint.setText("");
                     //vuoldire che ho almeno cercato una partenza o una destinazione
                    }else {
                        //Se la destinazione appartiene a quell'edificio allora vedo se posso scriverla come destinazione
                        if (destinazione != null && NameEdificio.equals(controller.getAppartenenza(destinazione))) {
                            //è un aula
                            if (controller.getFloor(destinazione) != null) {
                                endPoint.setText(destinazione);
                                int i = controller.getFloor(destinazione);
                                bit = controller.getMap(NameEdificio, i);
                                ///Grafo a caso per vedere se funziona

                                //se la partenza non appartiene allo stesso edificio allora l'edificio della destinazione
                                //dovrà avere come partenza l'edificio stesso che in questo caso è l'entry point
                                if(NameEdificio.equals(controller.getAppartenenza(partenza)) != NameEdificio.equals(controller.getAppartenenza(destinazione)) ){
                                    startPoint.setText(NameEdificio);
                                    bit = controller.getMap(NameEdificio, 0);
                                    graph = edificio.getGraph0();
                                }
                                //non è un aula
                            } else {
                                startPoint.setText(NameEdificio);
                                bit = controller.getMap(NameEdificio, 0);
                                graph = edificio.getGraph0();
                            }
                        }
                        //Se la partenza appartiene a quell'edificio allora vedo se posso scriverla come partenza
                        if (partenza != null && NameEdificio.equals(controller.getAppartenenza(partenza))) {
                            //è un aula
                            if (controller.getFloor(partenza) != null) {
                                startPoint.setText(partenza);
                                int i = controller.getFloor(partenza);
                                bit = controller.getMap(NameEdificio, i);

                                //se la destinazione non appartiene allo stesso edificio allora l'edificio della partenza
                                //dovrà avere come destinazione l'edificio stesso che in questo caso è l'exit point
                                if(NameEdificio.equals(controller.getAppartenenza(partenza)) != NameEdificio.equals(controller.getAppartenenza(destinazione)) ){
                                    endPoint.setText(NameEdificio);
                                    bit = controller.getMap(NameEdificio, 0);
                                    graph = edificio.getGraph0();
                                }
                                //non è un aula
                            } else {
                                startPoint.setText(NameEdificio);
                                bit = controller.getMap(NameEdificio, 0);
                                graph = edificio.getGraph0();
                            }
                        }
                    }
                }


                /*if(NameEdificio == null){
                    bit = BitmapFactory.decodeResource(getResources(),
                            R.drawable.u14);
                    String NameEdificioDef = "u14";
                    graph = new Graph();
                    initializeGraphNodes();
                }else{
                    if(destinazione==null){
                        bit = controller.getMap(NameEdificio, 0);
                        endPoint.setText("");
                        graph = edificio.getGraph0();
                    }
                   else{
                        if(controller.getFloor(destinazione) != null){
                            endPoint.setText(destinazione);
                            endPoint.invalidate();
                            int i = controller.getFloor(destinazione);
                            String appartenenzaEdificio = controller.getAppartenenza(destinazione);
                            bit = controller.getMap(appartenenzaEdificio, i);

                            graph = edificio.getGraph0();

                        }else{
                            bit = controller.getMap(NameEdificio, 0);

                            graph = edificio.getGraph0();

                        }
                    }
                }*/

                mapBitmap = bit;

                changeFloor(NameEdificio);

                next_back_Btn(NameEdificio);

                indicator = getResources().getDrawable(R.drawable.indicator);
                indicatorBitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.indicator);

                touchTransformer = new TouchTransformer();


                txt_passi.setText("0");
                position[0] = 0;
                position[1] = 0;

                start[0] = false;

                if (mapBitmap == null || indicatorBitmap == null) {
                    Log.e("MainActivity", "Failed to load map image. " +
                            "Ensure that the image is present in the res/drawable folder " +
                            "and its name matches the one in the code.");
                }


                mapDrawer = new MapDrawer(mapBitmap);
                indicatorDrawer = new MapDrawer(indicatorBitmap);

                indoorNav = new IndoorNavigation(mapDrawer, getContext(), indicatorDrawer);

                //float[] touchPoint = new float[2];

                path = null;

                mapImage.setImageDrawable(map);
                mapImage.setImageBitmap(mapDrawer.getMapBitmap());
                mapImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                indicatorImage.setImageDrawable(indicator);
                indicatorImage.setImageBitmap(indicatorDrawer.getMapBitmap());
                indicatorImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                checkOptions();
                checkPoint(graph, touchTransformer, indicatorImage);

                indicatorImage.setMaximumScale(7.0f);
                mapImage.setMaximumScale(7.0f);

            }
        });


    }

    public void changeFloor(String edificio){
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(floorCount  < controller.getNumberOfFloor(edificio)){
                    floorCount++;
                    Log.d("countFloor", "" + floorCount);
                }else{
                    //Toast.makeText(this, "limite piani raggiunto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(floorCount > 0){
                    floorCount--;
                    Log.d("countFloor", "" + floorCount);
                }else{
                    //Toast.makeText(this, "limite piani raggiunto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkProblem(Switch a, Switch b, Switch c){
        boolean carrozzina = sharedPreferences.getBoolean("carrozzina", false);
        Log.d("proviamolo", " carrozina 4"+String. valueOf(carrozzina));
        if(carrozzina){
            stairs = "stairs";
            aSwitch.setEnabled(false);
            Log.d("proviamolo", " carrozina 5"+String. valueOf(carrozzina));
        }else{
            aSwitch.setEnabled(true);
        }
    }

}