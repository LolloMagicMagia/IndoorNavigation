package com.example.osmdroidex2;

import android.app.Dialog;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Locale;

import dataFirebase.PreDatabase;


/**
 * MainActivity è la classe principale dell'applicazione IndoorNavigationSolution.
 * Questa classe si occupa di caricare l'immagine della planimetria e di gestire il grafo
 * per la navigazione indoor, disegnando il percorso più breve tra due punti, sfruttando
 * le classi MapDrawer e Graph per la logica implementatiiva vera a propria.
 */

public class IndoorNavActivity extends AppCompatActivity implements SensorEventListener {
    private int stepCount = 0;
    private boolean showpath = false;
    private int steppy = 0;
    private boolean first = true;
    // ATTRIBUTI PER BUSSOLA
    private Graph.Node nodeSphere;
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

    private List<Graph.Node> path;

    private float currentRotation = 0f;

    private IndoorNavigation indoorNav;

    private Button drawBtn;
    private Button userBtn;

    private TouchTransformer touchTransformer;

    private Button btn_start;

    private boolean[] user = new boolean[1];

    private TextView txt_passi;

    PreDatabase controller;

    /**
     * Metodo onCreate per la creazione dell'activity.
     * Inizializza le variabili e carica l'immagine della planimetria.
     * Configura il grafo dei nodi e gli archi per la navigazione indoor.
     *
     * @param savedInstanceState Bundle contenente lo stato precedente dell'activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indoornavactivity);
        drawBtn = findViewById(R.id.drawBtn);
        controller = PreDatabase.getInstance(null,null,null);

        //Prendo i dati dall'activity precedente per capire che piantina prendere e l'aula da scegliere
        Intent intent=getIntent();
        String edificio = intent.getStringExtra("edificio");
        String destinazione = null;

        if(intent.getStringExtra("destinazione")!=null){
            destinazione = intent.getStringExtra("destinazione");
        }

        getMapFloor(edificio,destinazione);
        /////////////////////////////

        txt_passi = findViewById(R.id.txt_passi);
        txt_passi.setText("0");
        position[0] = 0;
        position[1] = 0;

        startPoint = findViewById(R.id.starPoint);
        endPoint = findViewById(R.id.endPoint);

        btn_start = findViewById(R.id.btn_avvia);
        start[0] = false;

        indicator = getResources().getDrawable(R.drawable.indicator);
        indicatorBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.indicator);

        touchTransformer = new TouchTransformer();

        if (mapBitmap == null || indicatorBitmap == null) {
            Log.e("MainActivity", "Failed to load map image. " +
                    "Ensure that the image is present in the res/drawable folder " +
                    "and its name matches the one in the code.");
            return;
        }

        mapDrawer = new MapDrawer(mapBitmap);
        indicatorDrawer = new MapDrawer(indicatorBitmap);

        indoorNav = new IndoorNavigation(mapDrawer, getApplicationContext(), indicatorDrawer);

        //float[] touchPoint = new float[2];

        graph = new Graph(mapBitmap);
        path = null;

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

        Graph.Node nodeA = graph.getNode("A");

        mapImage = findViewById(R.id.map_image);
        mapImage.setImageDrawable(map);
        mapImage.setImageBitmap(mapDrawer.getMapBitmap());
        mapImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        indicatorImage = findViewById(R.id.indicator_image);
        indicatorImage.setImageDrawable(indicator);
        indicatorImage.setImageBitmap(indicatorDrawer.getMapBitmap());
        indicatorImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        optTxt = findViewById(R.id.btn_options);
        checkOptions();
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeSphere = indoorNav.stepNavigation(path, mapImage, steppy, indicatorImage, start);
                steppy ++;
                if (start[0]) {
                    start[0] = false;
                    disegnaIndicatore(0, 0);
                }
                else {
                    if(showpath) {
                        start[0] = true;
                        btn_start.setVisibility(View.GONE);
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

        Log.d("Coordinate", "Width: "+  String.valueOf(mapBitmap.getWidth()) + "  Height: " + String.valueOf(mapBitmap.getHeight()));

        indicatorImage.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                Matrix matrix = new Matrix();
                indicatorImage.getSuppMatrix(matrix);
                mapImage.setDisplayMatrix(matrix);
            }
        });

        Button stepBtn = findViewById(R.id.stepBtn);

        stepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPath(true);
                nodeSphere = indoorNav.stepNavigation(path, mapImage, steppy, indicatorImage, start);
                steppy ++;
                if (nodeSphere == null) {
                    btn_start.setVisibility(View.VISIBLE);
                    showpath = false;
                    txt_passi.setText("0");
                }
            }
        });

        //onCreate per bussola
        compassImageView = findViewById(R.id.compass_image_view);
        degreeTextView = findViewById(R.id.degree_text_view);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //---------- fine bussola
        //onCreate per contapassi
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        steps[0] = 0;

        userBtn = findViewById(R.id.btn_user);
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
        });
        checkPoint(indicatorImage, graph, mapBitmap, touchTransformer, indicatorImage);
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
                final Dialog dialog2 = new Dialog(IndoorNavActivity.this);
                //  Imposta il layout del tuo dialog personalizzato
                dialog2.setContentView(R.layout.open_dialog);

                aSwitch = dialog2.findViewById(R.id.switch1);
                bSwitch = dialog2.findViewById(R.id.switch2);
                cSwitch = dialog2.findViewById(R.id.switch3);

                if (stairs == "stairs") {
                    aSwitch.setChecked(true);
                }
                else aSwitch.setChecked(false);
                if (available == "available") {
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
    private void disegnaPercorso(List<Graph.Node> nodes) {
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

    public void checkPoint(PhotoView mapImage, Graph graph, Bitmap mapBitmap, TouchTransformer touchTransformer, PhotoView indicatorImage){
        indicatorImage.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                //879 1091
                float pointX = touchTransformer.transformX(x, indicatorImage, indicatorBitmap);
                float pointY = touchTransformer.transformY(y, indicatorImage, indicatorBitmap);

                if (!user[0]) {
                    disegnaIndicatore(pointX, pointY);
                    position[0] = pointX;
                    position[1] = pointY;
                    user[0] = true;
                    userBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }

                Graph.Node node = indoorNav.checkNode(graph, pointX, pointY);
                final Dialog dialog = new Dialog(IndoorNavActivity.this);

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
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
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
            /*mGravity[0] = 0.09f;
            mGravity[1] = 0.59f;
            mGravity[2] = 2.28f;*/
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
        boolean success = getRotationMatrix(R, I, mGravity, mGeomagnetic);

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
                            nodeSphere = indoorNav.stepNavigation(path, mapImage, steppy, indicatorImage, start);
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

    // metodo di SensorManager, trasferito in native per efficienza:
    public static boolean getRotationMatrix(float[] R, float[] I,
                                            float[] gravity, float[] geomagnetic) {
        // TODO: move this to native code for efficiency
        float Ax = gravity[0];
        float Ay = gravity[1];
        float Az = gravity[2];

        final float normsqA = (Ax * Ax + Ay * Ay + Az * Az);
        final float g = 9.81f;
        final float freeFallGravitySquared = 0.01f * g * g;
        if (normsqA < freeFallGravitySquared) {
            // gravity less than 10% of normal value
            return false;
        }

        final float Ex = geomagnetic[0];
        final float Ey = geomagnetic[1];
        final float Ez = geomagnetic[2];
        float Hx = Ey * Az - Ez * Ay;
        float Hy = Ez * Ax - Ex * Az;
        float Hz = Ex * Ay - Ey * Ax;
        final float normH = (float) Math.sqrt(Hx * Hx + Hy * Hy + Hz * Hz);

        if (normH < 0.1f) {
            // device is close to free fall (or in space?), or close to
            // magnetic north pole. Typical values are  > 100.
            return false;
        }
        final float invH = 1.0f / normH;
        Hx *= invH;
        Hy *= invH;
        Hz *= invH;
        final float invA = 1.0f / (float) Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
        Ax *= invA;
        Ay *= invA;
        Az *= invA;
        final float Mx = Ay * Hz - Az * Hy;
        final float My = Az * Hx - Ax * Hz;
        final float Mz = Ax * Hy - Ay * Hx;
        if (R != null) {
            if (R.length == 9) {
                R[0] = Hx;     R[1] = Hy;     R[2] = Hz;
                R[3] = Mx;     R[4] = My;     R[5] = Mz;
                R[6] = Ax;     R[7] = Ay;     R[8] = Az;
            } else if (R.length == 16) {
                R[0]  = Hx;    R[1]  = Hy;    R[2]  = Hz;   R[3]  = 0;
                R[4]  = Mx;    R[5]  = My;    R[6]  = Mz;   R[7]  = 0;
                R[8]  = Ax;    R[9]  = Ay;    R[10] = Az;   R[11] = 0;
                R[12] = 0;     R[13] = 0;     R[14] = 0;    R[15] = 1;
            }
        }
        if (I != null) {
            // compute the inclination matrix by projecting the geomagnetic
            // vector onto the Z (gravity) and X (horizontal component
            // of geomagnetic vector) axes.
            final float invE = 1.0f / (float) Math.sqrt(Ex * Ex + Ey * Ey + Ez * Ez);
            final float c = (Ex * Mx + Ey * My + Ez * Mz) * invE;
            final float s = (Ex * Ax + Ey * Ay + Ez * Az) * invE;
            if (I.length == 9) {
                I[0] = 1;     I[1] = 0;     I[2] = 0;
                I[3] = 0;     I[4] = c;     I[5] = s;
                I[6] = 0;     I[7] = -s;     I[8] = c;
            } else if (I.length == 16) {
                I[0] = 1;     I[1] = 0;     I[2] = 0;
                I[4] = 0;     I[5] = c;     I[6] = s;
                I[8] = 0;     I[9] = -s;     I[10] = c;
                I[3] = I[7] = I[11] = I[12] = I[13] = I[14] = 0;
                I[15] = 1;
            }
        }
        return true;
    }

    public void getMapFloor(String edificio, String destinazione){
        Bitmap bit;
        if(destinazione==null){
            bit = controller.getMap(edificio, 0);
        }
        else{
            if(controller.getFloor(destinazione)!=null){
                int i = controller.getFloor(destinazione);
                String appartenenzaEdificio = controller.getAppartenenza(destinazione);
                bit = controller.getMap(appartenenzaEdificio, i);
            }else{
                bit = controller.getMap(edificio, 0);
            }
        }

        mapBitmap = bit;
    }

}
