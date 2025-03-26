package org.radsim.routing;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.radsim.util.exportGeoJSON;

public class RoutingTest {

    private static final ExecutorService ROUTING_EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ExecutorService IO_EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws Exception {
        String relDir = args.length == 1 ? args[0] : "";
        Config config = ConfigReader.readJSON(relDir);
        String osmFile = config.getOsmFile();

        ODCSVReader reader = new ODCSVReader();
        List<ODCsv> odData = reader.readCSV(config.getOdPairs());
        System.out.println("Loaded routing configuration");

        GraphHopper hopper = GraphhopperInstance.createGraphHopperInstance(osmFile, config);
        System.out.println("Started Graphhopper Instance");
        long startTime = System.nanoTime();

        // Parallel processing with completion tracking
        List<CompletableFuture<RouteResult>> futures = odData.stream()
                .map(od -> CompletableFuture.supplyAsync(() ->
                        processRoute(hopper, config, od), ROUTING_EXECUTOR))
                .collect(Collectors.toList());

        // Collect all results
        List<RouteResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        double duration = (System.nanoTime() - startTime) / 1_000_000_000.0;
        System.out.printf("Alternative generation processed in: %.4f secs%n", duration);

        // Generate single GeoJSON
        List<GHResponse> allResponses = results.stream()
                .map(r -> r.response)
                .collect(Collectors.toList());

        List<String> trajectoryIds = results.stream()
                .map(r -> r.trajectoryId)
                .collect(Collectors.toList());

        String json = exportGeoJSON.exportAllResponses(allResponses, trajectoryIds);
        saveGeoJSONAsync(json, "data/geojson/all_routes_" + System.currentTimeMillis() + ".geojson");

        // Cleanup
        hopper.close();
        shutdownExecutors();
    }

    private static RouteResult processRoute(GraphHopper hopper, Config config, ODCsv od) {
        try {
            ODLatLng start = new ODLatLng(od.getStartLat(), od.getStartLng());
            ODLatLng end = new ODLatLng(od.getEndLat(), od.getEndLng());

            GHResponse response = routing(hopper, config, start, end, od);
            return new RouteResult(od.getTrajectoryId(), response);

        } catch (Exception e) {
            throw new CompletionException("Failed processing trajectory " + od.getTrajectoryId(), e);
        }
    }

    private static class RouteResult {
        final String trajectoryId;
        final GHResponse response;

        RouteResult(String trajectoryId, GHResponse response) {
            this.trajectoryId = trajectoryId;
            this.response = response;
        }
    }

    private static GHResponse routing(GraphHopper hopper, Config config,
                                      ODLatLng start, ODLatLng end, ODCsv od) {
        long startTime = System.nanoTime();

        AlternativeParams altParams = config.getAlternatives();
        GHRequest req = new GHRequest()
                .setProfile("bike")
                .addPoint(new GHPoint(start.lat, start.lng))
                .addPoint(new GHPoint(end.lat, end.lng))
                .setAlgorithm(Parameters.Algorithms.ALT_ROUTE);

        req.getHints()
                .putObject(Parameters.Algorithms.AltRoute.MAX_PATHS, altParams.getMaxPaths())
                .putObject(Parameters.Algorithms.AltRoute.MAX_SHARE, altParams.getMaxShare())
                .putObject(Parameters.Algorithms.AltRoute.MAX_WEIGHT, altParams.getMaxWeight())
                .putObject("alternative_route.min_plateau_factor", altParams.getMinPlateauFactor())
                .putObject("alternative_route.max_exploration_factor", altParams.getMaxExplorationFactor());

        GHResponse rsp = hopper.route(req);

//        logProcessingTime(od.getTrajectoryId(), startTime);
        validateResponse(rsp);

        return rsp;
    }

    private static void logProcessingTime(String trajectoryId, long startTime) {
        double duration = (System.nanoTime() - startTime) / 1_000_000_000.0;
        System.out.printf("Trajectory %s processed in: %.4f secs%n", trajectoryId, duration);
    }

    private static void validateResponse(GHResponse rsp) {
        if (rsp.hasErrors()) {
            throw new RuntimeException("Routing failed: " + rsp.getErrors());
        }
    }

    private static void saveGeoJSONAsync(String json, String filename) {
        IO_EXECUTOR.submit(() -> {
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(json);
                System.out.println("GeoJSON saved: " + filename);
            } catch (IOException e) {
                System.err.println("Error writing GeoJSON: " + e.getMessage());
            }
        });
    }

    private static void shutdownExecutors() {
        ROUTING_EXECUTOR.shutdown();
        IO_EXECUTOR.shutdown();
        try {
            if (!ROUTING_EXECUTOR.awaitTermination(1, TimeUnit.MINUTES)) {
                ROUTING_EXECUTOR.shutdownNow();
            }
            if (!IO_EXECUTOR.awaitTermination(1, TimeUnit.MINUTES)) {
                IO_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}