package org.radsim.util;

import com.graphhopper.GHResponse;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class exportGeoJSON {

    public static String exportAllResponses(List<GHResponse> responses, List<String> trajectoryIds) {
        JSONObject featureCollection = new JSONObject();
        JSONArray features = new JSONArray();

        try {
            featureCollection.put("type", "FeatureCollection");

            for (int i = 0; i < responses.size(); i++) {
                GHResponse response = responses.get(i);
                String trajectoryId = trajectoryIds.get(i);

                int alternativeIndex = 0;
                for (ResponsePath path : response.getAll()) {
                    JSONObject feature = createFeature(path, trajectoryId, alternativeIndex);
                    features.put(feature);
                    alternativeIndex++;
                }
            }

            featureCollection.put("features", features);
        } catch (Exception e) {
            System.err.println("Error creating GeoJSON: " + e.getMessage());
        }

        return featureCollection.toString(2); // Pretty-print with indentation
    }

    private static JSONObject createFeature(ResponsePath path, String trajectoryId, int alternativeIndex) {
        JSONObject feature = new JSONObject();
        JSONObject geometry = new JSONObject();
        JSONObject properties = new JSONObject();
        JSONArray coordinates = new JSONArray();

        try {
            // Geometry
            PointList points = path.getPoints();
            for (int i = 0; i < points.size(); i++) {
                JSONArray coord = new JSONArray();
                coord.put(points.getLon(i)); // GeoJSON expects lon-first!
                coord.put(points.getLat(i));
                coordinates.put(coord);
            }

            geometry.put("type", "LineString");
            geometry.put("coordinates", coordinates);

            // Properties
            properties.put("trajectoryId", trajectoryId);
            properties.put("alternative", alternativeIndex);
            properties.put("distance", path.getDistance());
            properties.put("time", path.getTime());

            // Feature
            feature.put("type", "Feature");
            feature.put("geometry", geometry);
            feature.put("properties", properties);

        } catch (Exception e) {
            System.err.println("Error creating feature: " + e.getMessage());
        }

        return feature;
    }
}