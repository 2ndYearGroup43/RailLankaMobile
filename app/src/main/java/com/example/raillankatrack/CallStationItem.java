package com.example.raillankatrack;

public class CallStationItem {
    private String stationId;
    private String stationName;
    private String phone;

    public CallStationItem(String stationId, String stationName,  String phone){
        this.stationName=stationName;
        this.phone=phone;
        this.stationId=stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getPhone() {
        return phone;
    }

    public String getStationId() {
        return stationId;
    }
}
