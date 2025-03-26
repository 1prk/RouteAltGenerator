package org.radsim.routing;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.LMProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.GHUtility;

public class GraphhopperInstance {
    public static GraphHopper createGraphHopperInstance(String ghLoc, Config config) {
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(ghLoc);
        // specify where to store graphhopper files
        hopper.setGraphHopperLocation("target/routing-graph-cache");

        // add all encoded values that are used in the custom model, these are also available as path details or for client-side custom models
        hopper.setEncodedValuesString(config.getEncodedValues().get(0));
        // see docs/core/profiles.md to learn more about profiles
        hopper.setProfiles(new Profile(config.getProfile()).setCustomModel(GHUtility.loadCustomModelFromJar(config.getCustomModel())));

        // Use LM preparation for custom models instead of CH
        hopper.getLMPreparationHandler().setLMProfiles(new LMProfile(config.getProfile()));

        // now this can take minutes if it imports or a few seconds for loading of course this is dependent on the area you import
        hopper.importOrLoad();
        return hopper;
    }
}
