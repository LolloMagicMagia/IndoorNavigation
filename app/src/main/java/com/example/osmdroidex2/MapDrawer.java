package com.example.osmdroidex2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;


/**
 * La classe MapDrawer gestisce il disegno dei percorsi sulla planimetria.
 * Fornisce metodi per disegnare e cancellare percorsi sulla mappa, utilizzando un oggetto Canvas
 * e un oggetto Paint per personalizzare lo stile delle linee tracciate.
 */
public class MapDrawer {

    private Bitmap originalBitmap;
    // Un'istanza di Bitmap che contiene l'immagine della planimetria
    private Bitmap mapBitmap;

    // Un'istanza di Canvas per disegnare sulla planimetria
    private Canvas mapCanvas;

    // Un'istanza di Paint per configurare lo stile delle linee tracciate
    private Paint linePaint;

    /**
     * Costruttore della classe MapDrawer.
     * Inizializza le variabili e configura lo stile delle linee tracciate.
     *
     * @param mapBitmap Bitmap dell'immagine della planimetria
     */
    public MapDrawer(Bitmap mapBitmap) {

        this.originalBitmap = mapBitmap;
        this.mapBitmap = mapBitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.mapCanvas = new Canvas(this.mapBitmap);

        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(25);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
    }


    /**
     * Restituisce l'immagine della planimetria come un oggetto Bitmap.
     *
     * @return Un oggetto Bitmap che rappresenta l'immagine della planimetria
     */
    public Bitmap getMapBitmap() {
        return mapBitmap;
    }

    /**
     * Disegna un percorso sulla planimetria utilizzando una lista di nodi.
     * Ogni nodo nella lista contiene le coordinate x e y del punto sulla planimetria.
     *
     * @param nodes Lista di nodi che rappresentano il percorso da disegnare
     */
    public void drawPath(List<Graph.Node> nodes) {
        if (nodes == null || nodes.size() < 2) {
            return;
        }

        Path path = new Path();
        Graph.Node firstNode = nodes.get(0);
        path.moveTo(firstNode.getX(), firstNode.getY());

        for (int i = 1; i < nodes.size(); i++) {
            Graph.Node currentNode = nodes.get(i);
            path.lineTo(currentNode.getX(), currentNode.getY());
        }

        mapCanvas.drawPath(path, linePaint);
    }

    public void resetMap() {
        // Crea una copia dell'immagine originale
        mapBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        mapCanvas = new Canvas(mapBitmap);
    }

    /**
     * ANCORA NON FUNZIONANTE
     * Cancella il percorso disegnato sulla planimetria.
     * Ripulisce la Canvas e ridisegna l'immagine di sfondo sulla Canvas.
     */


    /*public void drowLine(PhotoView mapImage){

        mapImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float startX = -1;
                float startY = -1;
                float endX = -1;
                float endY = -1;
                float imgX = imageX(event.getX(),  mapImage);
                float imgY = imageY(event.getY(), mapImage);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (startX == -1 && startY == -1) {
                        // Imposta il punto di partenza
                        startX = imgX;
                        startY = imgY;

                    } else if (endX == -1 && endY == -1) {
                        // Imposta il punto di arrivo
                        endX = imgX;
                        endY = imgY;

                        // Disegna la linea tra i due punti
                        mapCanvas.drawLine(startX, startY, endX, endY, linePaint);
                        mapImage.invalidate();

                        Log.d("Coordinate", String.valueOf(startX) + "   " + String.valueOf(startY));
                        Log.d("Coordinate", String.valueOf(endX) + "   " + String.valueOf(endY));

                        // Reimposta i valori di partenza e arrivo per il prossimo tracciamento
                        startX = -1;
                        startY = -1;
                        endX = -1;
                        endY = -1;
                    }
                }
                return true;
            }
        });
    }

    private float imageX(float touchX, PhotoView mapImage) {
        float viewWidth = mapImage.getWidth();
        float bitmapWidth = mapBitmap.getWidth();
        float scaleFactor = Math.min((float) viewWidth / bitmapWidth, (float) mapImage.getHeight() / mapBitmap.getHeight());
        return (touchX - (viewWidth - bitmapWidth * scaleFactor) / 2) / scaleFactor;
    }

    private float imageY(float touchY, PhotoView mapImage) {
        float viewHeight = mapImage.getHeight();
        float bitmapHeight = mapBitmap.getHeight();
        float scaleFactor = Math.min((float) mapImage.getWidth() / mapBitmap.getWidth(), (float) viewHeight / bitmapHeight);
        return (touchY - (viewHeight - bitmapHeight * scaleFactor) / 2) / scaleFactor;
    }*/

}


