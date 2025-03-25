package org.radsim.routing;


public class ODLatLng extends LatLng {
    private LatLng odLatLng;

    public ODLatLng(double lat, double lng) {
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("invalid latitude");
        }
        if (lng < - 180 || lng > 180) {
            throw new IllegalArgumentException("invalid longitude");
        }

        setLat(lat);
        setLng(lng);
    }

    public LatLng getODLatLng() {
        return this;
    }

    public LatLng getOdLatLng() {
        return odLatLng;
    }


    public void setODLatLng(LatLng odLatLng) {
        this.odLatLng = odLatLng;
    }
}
