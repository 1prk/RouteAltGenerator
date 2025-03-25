package org.radsim.routing;

public class AlternativeParams {
    private int maxPaths;
    private double maxShare;
    private double maxWeight;
    private double minPlateauFactor;
    private double maxExplorationFactor;

    public int getMaxPaths() {
        return maxPaths;
    }

    public double getMaxShare() {
        return maxShare;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public double getMinPlateauFactor() {
        return minPlateauFactor;
    }

    public double getMaxExplorationFactor() {
        return maxExplorationFactor;
    }

    public void setMaxPaths(int maxPaths) {
        this.maxPaths = maxPaths;
    }

    public void setMaxShare(double maxShare) {
        this.maxShare = maxShare;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public void setMinPlateauFactor(double minPlateauFactor) {
        this.minPlateauFactor = minPlateauFactor;
    }

    public void setMaxExplorationFactor(double maxExplorationFactor) {
        this.maxExplorationFactor = maxExplorationFactor;
    }
}
