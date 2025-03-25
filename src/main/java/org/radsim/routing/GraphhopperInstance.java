package org.radsim.routing;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.LMProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.GHUtility;

public class GraphhopperInstance {
    public static GraphHopper createGraphHopperInstance(String ghLoc) {
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(ghLoc);
        // specify where to store graphhopper files
        hopper.setGraphHopperLocation("target/routing-graph-cache");

        // add all encoded values that are used in the custom model, these are also available as path details or for client-side custom models
        hopper.setEncodedValuesString("bike_priority, mtb_rating, hike_rating, bike_access, roundabout, bike_average_speed");
        // see docs/core/profiles.md to learn more about profiles
        hopper.setProfiles(new Profile("bike").setCustomModel(GHUtility.loadCustomModelFromJar("bike.json")));

        // Use LM preparation for custom models instead of CH
        hopper.getLMPreparationHandler().setLMProfiles(new LMProfile("bike"));

        // now this can take minutes if it imports or a few seconds for loading of course this is dependent on the area you import
        hopper.importOrLoad();
        return hopper;
    }
}
