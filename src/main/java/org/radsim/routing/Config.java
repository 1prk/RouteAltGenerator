package org.radsim.routing;

import java.util.List;

public class Config {
    private String profile;
    private String customModel;
    private List<String> encodedValues;
    private String osmFile;
    private String odPairs;
    private AlternativeParams alternatives;

    public String getProfile() {
        return profile;
    }

    public String getCustomModel() {
        return customModel;
    }

    public List<String> getEncodedValues() {
        return encodedValues;
    }
    public String getOsmFile() {
        return osmFile;
    }

    public String getOdPairs() {
        return odPairs;
    }

    public AlternativeParams getAlternatives() {
        return alternatives;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setCustomModel(String customModel) {
        this.customModel = customModel;
    }

    public void setEncodedValues(List<String> encodedValues) {
        this.encodedValues = encodedValues;
    }

    public void setOsmFile(String osmFile) {
        this.osmFile = osmFile;
    }

    public void setOdPairs(String odPairs) {
        this.odPairs = odPairs;
    }

    public void setAlternatives(AlternativeParams alternatives) {
        this.alternatives = alternatives;
    }
}
