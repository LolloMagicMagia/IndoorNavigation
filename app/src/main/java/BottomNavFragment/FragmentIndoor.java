package BottomNavFragment;

import static android.content.Context.SENSOR_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
    private int stepCount = 0;
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

        //Vado a prendere i valori precedenti
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        initializeViews(view);

        controller = Controller.getInstance(null,null,null);

        floorCount = 0;

        //Prendo l'edificio da cui parto e la destinazione(aula) se dal fragment di prima l'ho scelta
        String destinazione = sharedPreferences.getString("destinazione",null);
        String partenza = sharedPreferences.getString("partenza", null);
        if(partenza != null){
            startPoint.setText(partenza);
        }
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

        Log.d("nomeEdifico",""+destinazione);
        Log.d("nomeEdifico",""+controller.getAppartenenza(destinazione));

        getMapFloor(controller.getAppartenenza(destinazione), destinazione);

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
                    indoorNav.stepNavigation(null, mapImage, steppy, indicatorImage, start, true);
                    btn_start.setText("START");
                    showpath = false;
                }
                indoorNav.stepNavigation(path, mapImage, steppy, indicatorImage, start, false);
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
                clearPath();
                path = graph.findShortestPath(startPoint.getText().toString(), endPoint.getText().toString(), stairs, available, crowd);
                try {
                    path.get(1);
                    path.get(2);
                } catch (Exception e) {
                    path = null;
                }
                if (path != null) {
                    disegnaPercorso(path);
                    showpath = true;
                    steppy = 0;
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
                nodeSphere = indoorNav.stepNavigation(path, mapImage, steppy, indicatorImage, start, false);
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
                if(floorCount  < controller.getNumberOfFloor(edificio)){
                    floorCount++;
                    Log.d("Piani", "" + floorCount);
                }else{
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(floorCount > 0){
                    floorCount--;
                    Log.d("Piani", "" + floorCount);
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
        mapDrawer.drawPath(nodes, mapImage, true);
        mapImage.invalidate();
    }

    public void clearPath(){
        mapDrawer.resetMap(); // Aggiungi questa riga per ripristinare la mappa nel MapDrawer
        mapImage.setImageBitmap(mapDrawer.getMapBitmap()); // Imposta la nuova mappa ripristinata
        mapImage.invalidate(); // Forza il ridisegno della PhotoView
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
                //879 1091
                float pointX = touchTransformer.transformX(x, indicatorImage, indicatorBitmap) / mapBitmap.getWidth();
                float pointY = touchTransformer.transformY(y, indicatorImage, indicatorBitmap) / mapBitmap.getHeight();

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
                    if (node.getAvailability() == "available") {
                        sw_available.setChecked(true);
                    }
                    if (node.getAvailability() == "unavailable") {
                        sw_available.setChecked(false);
                    }
                    if (node.getCrowdness() == "crowded") {
                        sw_crowded.setChecked(true);
                    }
                    if (node.getCrowdness() == "notCrow") {
                        sw_crowded.setChecked(false);
                    }
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
                            nodeSphere = indoorNav.stepNavigation(path, mapImage, steppy, indicatorImage, start, false);
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
    public void getMapFloor(String NameEdificioDef, String destinazione){
        viewModel.getEdificioObj(NameEdificioDef).observe(getActivity(), new Observer<Edificio>() {
            @Override
            public void onChanged(Edificio edificio) {
                Bitmap bit;

                String NameEdificio = NameEdificioDef;

                if(NameEdificio == null){
                    bit = BitmapFactory.decodeResource(getResources(),
                            R.drawable.u14);
                    String NameEdificioDef = "u14";
                    graph = new Graph();
                    initializeGraphNodes();
                }else{
                    if(destinazione==null){
                        bit = controller.getMap(NameEdificio, 0);

                        graph = new Graph();

                        graph.addNode("1", (float) 0.5, (float) 0.149,  "atrium", "available", "notCrow");
                        graph.addNode("1.09", (float) 0.473, (float) 0.502,  "atrium", "available", "notCrow");
                        graph.addNode("1.10", (float) 0.473, (float) 0.802,  "atrium", "available", "notCrow");

                        graph.addEdge("1", "1.09", 1);
                        graph.addEdge("1.09", "1.10", 1);

                    }
                    else{
                        if(controller.getFloor(destinazione) != null){
                            endPoint.setText(destinazione);
                            endPoint.invalidate();
                            int i = controller.getFloor(destinazione);
                            String appartenenzaEdificio = controller.getAppartenenza(destinazione);
                            bit = controller.getMap(appartenenzaEdificio, i);

                            graph = new Graph();

                            graph.addNode("1", (float) 0.5, (float) 0.149,  "atrium", "available", "notCrow");
                            graph.addNode("1.09", (float) 0.473, (float) 0.502,  "atrium", "available", "notCrow");
                            graph.addNode("1.10", (float) 0.473, (float) 0.802,  "atrium", "available", "notCrow");

                            graph.addEdge("1", "1.09", 1);
                            graph.addEdge("1.09", "1.10", 1);

                        }else{
                            bit = controller.getMap(NameEdificio, 0);

                            graph = new Graph();

                            graph.addNode("1", (float) 0.5, (float) 0.149,  "atrium", "available", "notCrow");
                            graph.addNode("1.09", (float) 0.473, (float) 0.502,  "atrium", "available", "notCrow");
                            graph.addNode("1.10", (float) 0.473, (float) 0.802,  "atrium", "available", "notCrow");

                            graph.addEdge("1", "1.09", 1);
                            graph.addEdge("1.09", "1.10", 1);

                        }
                    }
                }

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