package com.example.osmdroidex2;
import android.util.Log;

import androidx.room.TypeConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class GraphTypeConverter {

    @TypeConverter
    public static String graphToString(Graph graph) {

        if(graph == null){
            return null;
        }

        try {
            JSONObject graphJson = new JSONObject();
            JSONArray nodesJson = new JSONArray();

            // Convert nodes to JSON
            for (Node node : graph.getNodes().values()) {
                JSONObject nodeJson = new JSONObject();
                nodeJson.put("id", node.getId());
                nodeJson.put("x", node.getX());
                nodeJson.put("y", node.getY());
                nodeJson.put("roomType", node.getRoomType());
                nodeJson.put("availability", node.getAvailability());
                nodeJson.put("crowdness", node.getCrowdness());

                // Convert edges to JSON
                JSONArray edgesJson = new JSONArray();
                for (Edge edge : node.getEdges()) {
                    JSONObject edgeJson = new JSONObject();
                    edgeJson.put("destination", edge.getDestination().getId());
                    edgeJson.put("weight", edge.getWeight());
                    edgesJson.put(edgeJson);
                }
                nodeJson.put("edges", edgesJson);

                nodesJson.put(nodeJson);
            }

            graphJson.put("nodes", nodesJson);

            return graphJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }


    @TypeConverter
    public static Graph stringToGraph(String json) {

        if(json == null){
            return null;
        }
        Log.d("cazziInCulo",""+ json);
        try {
            Graph graph = new Graph();
            JSONObject graphJson = new JSONObject(json);
            JSONArray nodesJson = graphJson.getJSONArray("nodes");

            // Convert JSON to nodes
            for (int i = 0; i < nodesJson.length(); i++) {
                JSONObject nodeJson = nodesJson.getJSONObject(i);
                String id = nodeJson.getString("id");
                float x = (float) nodeJson.getDouble("x");
                float y = (float) nodeJson.getDouble("y");
                String roomType = nodeJson.getString("roomType");
                String availability = nodeJson.getString("availability");
                String crowdness = nodeJson.getString("crowdness");

                graph.addNode(id, x, y, roomType, availability, crowdness);
            }

            for (int i = 0; i < nodesJson.length(); i++) {
                JSONObject nodeJson = nodesJson.getJSONObject(i);
                String id = nodeJson.getString("id");
                JSONArray edgesJson = nodeJson.getJSONArray("edges");

                // Convert JSON to edges
                for (int j = 0; j < edgesJson.length(); j++) {
                    JSONObject edgeJson = edgesJson.getJSONObject(j);
                    String destinationId = edgeJson.getString("destination");
                    int weight = edgeJson.getInt("weight");

                    graph.addEdge(id, destinationId, weight);
                }
            }



            return graph;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
