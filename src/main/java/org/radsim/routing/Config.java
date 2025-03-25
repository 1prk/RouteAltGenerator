package org.radsim.routing;

import java.util.List;

public class Config {
    private String profile;
    private String customModel;
    private List<String> encodedValues;
    private String osmFile;
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

    public AlternativeParams getAlternatives() {
        return alternatives;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setCustom_model(String customModel) {
        this.customModel = customModel;
    }

    public void setEncoded_values(List<String> encodedValues) {
        this.encodedValues = encodedValues;
    }

    public void setOsm_file(String osmFile) {
        this.osmFile = osmFile;
    }

    public void setAlternatives(AlternativeParams alternatives) {
        this.alternatives = alternatives;
    }
}
