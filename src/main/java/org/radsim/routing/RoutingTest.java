package org.radsim.routing;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;

import java.io.FileWriter;
import java.io.IOException;
import org.radsim.util.exportGeoJSON;

public class RoutingTest {
    public static void main(String[] args) throws IOException {
        String relDir = args.length == 1 ? args[0] : "";
        Config config = ConfigReader.readJSON(relDir);
        String osm_file = config.getOsmFile();
        GraphHopper hopper = GraphhopperInstance.createGraphHopperInstance(osm_file);
        ODLatLng startLatLng = new ODLatLng(51.335861, 12.320620);
        ODLatLng endLatLng = new ODLatLng(51.344224, 12.380531);
        routing(hopper, config, startLatLng, endLatLng);
        // release resources to properly shutdown or start a new instance
        hopper.close();
    }

    public static void routing(GraphHopper hopper, Config config, ODLatLng startLatLng, ODLatLng endLatLng) {
        long startTime = System.nanoTime();
        AlternativeParams altParams = config.getAlternatives();
        GHRequest req = new GHRequest()
                .setProfile("bike")
                .addPoint(new GHPoint(startLatLng.lat, startLatLng.lng))
                .addPoint(new GHPoint(endLatLng.lat, endLatLng.lng))
                .setAlgorithm(Parameters.Algorithms.ALT_ROUTE);

        req.getHints().putObject(Parameters.Algorithms.AltRoute.MAX_PATHS, altParams.getMaxPaths());
        req.getHints().putObject(Parameters.Algorithms.AltRoute.MAX_SHARE, altParams.getMaxShare());
        req.getHints().putObject(Parameters.Algorithms.AltRoute.MAX_WEIGHT, altParams.getMaxWeight());
        req.getHints().putObject("alternative_route.min_plateau_factor", altParams.getMinPlateauFactor());
        req.getHints().putObject("alternative_route.max_exploration_factor", altParams.getMaxExplorationFactor());

        GHResponse rsp = hopper.route(req);
        long endTime = System.nanoTime();
        double routingDuration = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Alternative generation took:" + routingDuration + "secs");
        if (rsp.hasErrors())
            throw new RuntimeException(rsp.getErrors().toString());

        // Process all alternative routes
        System.out.println("Number of alternative routes: " + rsp.getAll().size());
        for (ResponsePath path : rsp.getAll()) {
            System.out.printf("Distance: %.2f m, Time: %.2f min%n",
                    path.getDistance(),
                    path.getTime() / 60000.0);
        }

        String rsp_json = exportGeoJSON.exportGHResponse(rsp);
        System.out.println(rsp_json);
        long millis = System.currentTimeMillis();
        String filename = "data/geojson/alt_routes_%d.geojson";
        String geoJSONfilename = String.format(filename, millis);
        try (FileWriter file = new FileWriter(geoJSONfilename)) {
            file.write(rsp_json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponsePath bestPath = rsp.getBest();
        System.out.println("Best path distance: " + bestPath.getDistance() + " meters");
        assert Helper.round(bestPath.getDistance(), -2) == 600;
    }
}