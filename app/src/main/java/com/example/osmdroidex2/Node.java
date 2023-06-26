package com.example.osmdroidex2;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String id;
    private float x;
    private float y;
    private String roomType;
    private String availability;
    private String crowdness;
    private List<Edge> edges;

    public Node(String id, float x, float y, String roomType, String availability, String crowdness) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.availability = availability;
        this.crowdness = crowdness;
        if(roomType.equals("classroom") || roomType.equals("elevator") || roomType.equals("stairs") || roomType.equals("atrium") || roomType.equals("bathroom") || roomType.equals("hallway")){
            this.roomType = roomType;
        }else{
            throw new IllegalArgumentException("Invalid room type");
        }
        edges = new ArrayList<>();
    }

    // Aggiungi i metodi getter per x e y

    /**
     * Restituisce la coordinata x del nodo.
     *
     * @return La coordinata x del nodo
     */
    public float getX() {
        return x;
    }

    /**
     * Restituisce la coordinata y del nodo.
     *
     * @return La coordinata y del nodo
     */
    public float getY() {
        return y;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public String getId() {
        return id;
    }

    public String getRoomType(){
        return roomType;
    }

    /**
     * Restituisce la lista di archi adiacenti al nodo.
     *
     * @return La lista di archi adiacenti al nodo
     */
    public List<Edge> getEdges() {
        return edges;
    }

    public void addRoomTypes(String type) {
        //
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getCrowdness() {
        return crowdness;
    }

    public void setCrowdness(String crowdness) {
        this.crowdness = crowdness;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}