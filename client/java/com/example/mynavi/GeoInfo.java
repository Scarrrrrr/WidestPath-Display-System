package com.example.mynavi;

import com.amap.api.maps.model.LatLng;

public class GeoInfo {
    public double longitude;
    public double latitude;
    public GeoInfo(double longitude, double latitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
}
