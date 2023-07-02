package com.example.osmdroidex2;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.LinkedList;
import java.util.List;

public class IndoorNavigation {
    private MapDrawer mapDrawer;
    private MapDrawer indicatorDrawer;

    private Context context;

    public IndoorNavigation(MapDrawer mapDrawer, Context context, MapDrawer indicatorDrawer) {
        this.mapDrawer = mapDrawer;
        this.context = context;
        this.indicatorDrawer = indicatorDrawer;
    }

    /*public Graph.Node stepNavigation(List<Graph.Node> path, PhotoView mapView, int count, PhotoView indicatorImage, boolean[] start, boolean cancel){

        if (cancel) {
            Toast.makeText(context  , "Canceled Trip", Toast.LENGTH_SHORT).show();
            start[0] = false;
            clearPath(mapView, indicatorImage);
            return null;
        }

        if(path == null || path.size() == 0){

            Toast.makeText(context  , "Warning: Select a possible path", Toast.LENGTH_SHORT).show();
            return null;

        } else if(count + 1   >= path.size()){

            Toast.makeText(context  , "you arrived!!", Toast.LENGTH_SHORT).show();
            start[0] = false;
            clearPath(mapView, indicatorImage);
            return null;

        }else {

            Graph.Node node2 = path.get(count + 1);

            Graph.Node node = path.get(count);

            List<Graph.Node> edge = new LinkedList<>();
            edge.add(node);
            edge.add(node2);

            mapDrawer.drawStep(path, mapView, edge);

            return node2;
        }

    }*/

    public Node stepNavigation(List<Node> path, PhotoView mapView, int count, PhotoView indicatorImage, boolean[] start, boolean stop, Bitmap icon, Handler handler, Runnable animationRunnable){

        if(stop) {
            Toast.makeText(context  , "Trip Canceled", Toast.LENGTH_SHORT).show();
            start[0] = true;
            clearPath(mapView, indicatorImage);
            return null;
        }

        if(path == null || path.size() == 0){

            Toast.makeText(context  , "Warning: Select a possible path", Toast.LENGTH_SHORT).show();
            return null;

        } else if(count + 1   >= path.size()){
            handler.removeCallbacks(animationRunnable);
            Toast.makeText(context  , "you arrived!!", Toast.LENGTH_SHORT).show();
            start[0] = false;
            clearPath(mapView, indicatorImage);
            return null;

        }else {

            Node node2 = path.get(count + 1);

            Node node = path.get(count);

            List<Node> edge = new LinkedList<>();
            edge.add(node);
            edge.add(node2);

            mapDrawer.drawStep(path, mapView, edge, icon);

            indicatorImage.setMaximumScale(7.0f);

            indicatorImage.setScale(1.0f, node.getX() * indicatorImage.getWidth(), node.getY() * indicatorImage.getHeight(), true);

            indicatorImage.setScale(6.0f, (node.getX() * indicatorImage.getWidth() + node2.getX() * indicatorImage.getWidth()) / 2, (node.getY() * indicatorImage.getHeight() + node2.getY() * indicatorImage.getHeight()) / 2, true);

            return node2;
        }

    }

    /*public Graph.Node checkNode(Graph graph, float pointX, float pointY) {
        String id = "1";
        while (graph.getNode(id) != null) {
            Graph.Node node = graph.getNode(id);
            if (Math.abs(pointX - node.getX()) <= 200) {
                if (Math.abs(pointY - node.getY()) <= 200) {
                    return node;
                }
            }
            int a = Integer.parseInt(id);
            a++;
            id = String.valueOf(a);
        }
        return null;
    }

    public void clearPath(PhotoView mapView, PhotoView indicatorImage){
        mapDrawer.resetMap(); // Aggiungi questa riga per ripristinare la mappa nel MapDrawer
        mapView.setImageBitmap(mapDrawer.getMapBitmap()); // Imposta la nuova mappa ripristinata
        mapView.invalidate(); // Forza il ridisegno della PhotoView
        indicatorDrawer.resetMap();
        indicatorImage.setImageBitmap(indicatorDrawer.getMapBitmap());
        indicatorImage.invalidate();
    }*/

    public Node checkNode(Graph graph, float pointX, float pointY) {
        String id = "1";
        int counter = 0;
        while ((graph.getNode("T"+id) != null || Double.parseDouble(id) >= 1 || Double.parseDouble(id) <= 10) && counter < 1000) {
            if (graph.getNode("T"+id) != null) {
                Node node = graph.getNode("T"+id);
                if (Math.abs(pointX - node.getX()) <= 0.05) {
                    if (Math.abs(pointY - node.getY()) <= 0.05) {
                        return node;
                    }
                }
            }
            double a = Double.parseDouble(id);
            a = a + 0.01;
            id = String.format("%.2f", a);
            id = id.replace(",", ".");
            counter ++;
            //Toast.makeText(context, ""+id, Toast.LENGTH_SHORT).show();
        }
        id = "1";
        while ((graph.getNode("1"+id) != null || Double.parseDouble(id) >= 1 || Double.parseDouble(id) <= 10) && counter < 1000) {
            if (graph.getNode("1"+id) != null) {
                Node node = graph.getNode("1"+id);
                if (Math.abs(pointX - node.getX()) <= 0.05) {
                    if (Math.abs(pointY - node.getY()) <= 0.05) {
                        return node;
                    }
                }
            }
            double a = Double.parseDouble(id);
            a = a + 0.01;
            id = String.format("%.2f", a);
            id = id.replace(",", ".");
            counter ++;
            Toast.makeText(context, ""+id, Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public void clearPath(PhotoView mapView, PhotoView indicatorImage){
        mapDrawer.resetMap(); // Aggiungi questa riga per ripristinare la mappa nel MapDrawer
        mapView.setImageBitmap(mapDrawer.getMapBitmap()); // Imposta la nuova mappa ripristinata
        mapView.invalidate(); // Forza il ridisegno della PhotoView
        indicatorDrawer.resetMap();
        indicatorImage.setImageBitmap(indicatorDrawer.getMapBitmap());
        indicatorImage.invalidate();
    }

}