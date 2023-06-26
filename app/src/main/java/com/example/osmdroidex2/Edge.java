package com.example.osmdroidex2;

public class Edge {
    private Node destination;
    private int weight;

    public Edge(Node destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public Node getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
