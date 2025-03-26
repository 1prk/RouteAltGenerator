package org.radsim.routing;

import com.opencsv.bean.CsvBindByName;

public class ODCsv {

    @CsvBindByName(column = "trajectoryId")
    private String trajectoryId;

    @CsvBindByName(column = "startLat")
    private double startLat;

    @CsvBindByName(column = "startLng")
    private double startLng;

    @CsvBindByName(column = "endLat")
    private double endLat;

    @CsvBindByName(column = "endLng")
    private double endLng;


    public String getTrajectoryId() {
        return trajectoryId;
    }

    public void setTrajectoryId(String trajectoryId) {
        this.trajectoryId = trajectoryId;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }
}