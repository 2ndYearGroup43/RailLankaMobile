package com.example.raillankatrack;

public class PastAssignmentItem {
    private String trainId;
    private String date;
    private String srcStation;
    private String destStation;
    private String moderatorId;
    private String finishedDate;
    private String finishedTime;

    public PastAssignmentItem(String trainId, String date,  String srcStation, String destStation, String moderatorId,
                              String finishedDate, String finishedTime){
        this.trainId=trainId;
        this.date=date;
        this.srcStation=srcStation;
        this.destStation=destStation;
        this.moderatorId=moderatorId;
        this.finishedDate=finishedDate;
        this.finishedTime=finishedTime;
    }

    public String getTrainId() {
        return trainId;
    }

    public String getSrcStation() {
        return srcStation;
    }

    public String getDestStation() {
        return destStation;
    }

    public String getModeratorId() {
        return moderatorId;
    }

    public String getDate() {
        return date;
    }

    public String getFinishedDate() {
        return finishedDate;
    }

    public String getFinishedTime() {
        return finishedTime;
    }
}
