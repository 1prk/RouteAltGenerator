package org.radsim.util;

import com.graphhopper.GHResponse;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class exportGeoJSON {
    public static String exportGHResponse(GHResponse rsp) {
        JSONObject featureCollection = new JSONObject();
        try {
            featureCollection.put("type", "FeatureCollection");
            JSONArray featureList = new JSONArray();
            int alternativeIndex = 0;

            for (ResponsePath path : rsp.getAll()) {
                JSONObject feature = new JSONObject();
                feature.put("type", "Feature");

                JSONObject geometry = new JSONObject();
                geometry.put("type", "LineString");
                JSONArray coordinates = new JSONArray();

                PointList pointList = path.getPoints();
                for (int i = 0; i < pointList.size(); i++) {
                    JSONArray coord = new JSONArray();
                    coord.put(pointList.getLon(i));
                    coord.put(pointList.getLat(i));
                    coordinates.put(coord);
                }
                geometry.put("coordinates", coordinates);
                feature.put("geometry", geometry);

                JSONObject properties = new JSONObject();
                properties.put("alternative", alternativeIndex);
                properties.put("distance", path.getDistance());
                properties.put("time", path.getTime());
                feature.put("properties", properties);


                featureList.put(feature);
                alternativeIndex++;
            }
            featureCollection.put("features", featureList);
        } catch (JSONException e) {
            System.err.println("Error constructing GeoJSON: " + e.getMessage());
        }
        return featureCollection.toString();
    }

}
