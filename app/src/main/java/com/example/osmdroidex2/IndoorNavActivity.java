package com.example.osmdroidex2;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;


/**
 * MainActivity è la classe principale dell'applicazione IndoorNavigationSolution.
 * Questa classe si occupa di caricare l'immagine della planimetria e di gestire il grafo
 * per la navigazione indoor, disegnando il percorso più breve tra due punti, sfruttando
 * le classi MapDrawer e Graph per la logica implementatiiva vera a propria.
 */


public class IndoorNavActivity extends AppCompatActivity {
    // Un'istanza di PhotoView che visualizza l'immagine della planimetria
    private PhotoView mapImage;

    // Un'istanza di Bitmap che contiene l'immagine della planimetria
    private Bitmap mapBitmap;

    // Un'istanza di MapDrawer per disegnare percorsi sulla planimetria
    private MapDrawer mapDrawer;

    private Drawable map;

    private TextInputEditText startPoint;

    private TextInputEditText endPoint;

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

        startPoint = findViewById(R.id.starPoint);
        endPoint = findViewById(R.id.endPoint);

        map = getResources().getDrawable(R.drawable.planimetria);
        mapImage = findViewById(R.id.map_image);
        mapImage.setImageDrawable(map);
        mapBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.planimetria);

        if (mapBitmap == null) {
            Log.e("MainActivity", "Failed to load map image. " +
                    "Ensure that the image is present in the res/drawable folder " +
                    "and its name matches the one in the code.");
            return;
        }

        mapDrawer = new MapDrawer(mapBitmap);
        mapImage.setImageBitmap(mapDrawer.getMapBitmap());
        mapImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        Graph graph = new Graph(mapBitmap);

        graph.addNode("1", (float) 2020.6055 / 3520, (float) 1991.6936 / 4186);
        graph.addNode("1.1", (float) 2278.0957 / 3520, (float) 1913.4905 / 4186);
        graph.addNode("1.2", (float) 1769.668 / 3520, (float) 1773.3766 / 4186);

        graph.addNode("2", (float) 1965.1758 / 3520, (float) 2835.8684 / 4186);
        graph.addNode("2.1", (float) 1776.2207 / 3520, (float) 2523.056 / 4186);

        graph.addNode("3", (float) 866.89453 / 3520, (float) 2128.549 / 4186);
        graph.addNode("3.1", (float) 1450.3027 / 3520, (float) 2089.4475 / 4186);

        graph.addNode("4", (float) 827.79297 / 3520, (float) 1600.678 / 4186);
        graph.addNode("4.1", (float) 1029.8535 / 3520, (float) 1493.1487 / 4186);

        graph.addNode("5", (float) 1342.7734 / 3520, (float) 909.651 / 4186);
        graph.addNode("5.1", (float) 1463.3008 / 3520, (float) 1209.4297 / 4186);

        graph.addNode("6", (float) 1939.1797 / 3520, (float) 883.5833 / 4186);
        graph.addNode("6.1", (float) 1763.1152 / 3520, (float) 1248.5312 / 4186);

        graph.addNode("7", (float) 2046.709 / 3520, (float) 1493.1487 / 4186);

        graph.addNode("8", (float) 1450.3027 / 3520, (float) 1519.2164 / 4186);
        graph.addNode("8.1", (float) 1450.3027 / 3520, (float) 1789.669 / 4186);
        graph.addNode("8.2", (float) 1776.2207 / 3520, (float) 1467.081 / 4186);

        graph.addNode("9", (float) 2591.0156 / 3520, (float) 1913.4905 / 4186);

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



        Button drawBtn;

        drawBtn = findViewById(R.id.drawBtn);

        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPath();
                disegnaPercorso(graph.findShortestPath(startPoint.getText().toString(), endPoint.getText().toString()));
            }
        });

        Log.d("Coordinate", "Width: "+  String.valueOf(mapBitmap.getWidth()) + "  Height: " + String.valueOf(mapBitmap.getHeight()));


    }

    /**
     * Disegna il percorso più breve tra due nodi sulla planimetria.
     * Utilizza l'istanza di MapDrawer per disegnare il percorso sulla planimetria
     * e aggiorna l'immagine visualizzata.
     *
     * @param nodes Lista di nodi che rappresentano il percorso da disegnare
     */
    private void disegnaPercorso(List<Graph.Node> nodes) {
        mapDrawer.drawPath(nodes);
        mapImage.invalidate();
    }

    public void clearPath() {
        mapDrawer.resetMap(); // Aggiungi questa riga per ripristinare la mappa nel MapDrawer
        mapImage.setImageBitmap(mapDrawer.getMapBitmap()); // Imposta la nuova mappa ripristinata
        mapImage.invalidate(); // Forza il ridisegno della PhotoView
    }
}
