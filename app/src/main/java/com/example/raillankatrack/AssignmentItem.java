package com.example.raillankatrack;

import android.os.Parcel;
import android.os.Parcelable;

public class AssignmentItem implements Parcelable { //make it parcelable to pass object to new activities methods are implemented automatically
    private String trainId;
    private String trainName;
    private String date;
    private String srcStation;
    private String destStation;
    private String journeyStatus;
    private String journeyId;
    private String assignmentTime;
    private String assignmentDate;



    public AssignmentItem(String trainId, String trainName, String journeyId, String date, String srcStation, String destStation){
        this.trainId=trainId;
        this.trainName=trainName;
        this.journeyId=journeyId;
        this.date=date;
        this.srcStation=srcStation;
        this.destStation=destStation;
    }

    protected AssignmentItem(Parcel in) { //gets data from the parcel
        trainId = in.readString();
        trainName = in.readString();
        date = in.readString();
        srcStation = in.readString();
        destStation = in.readString();
        journeyStatus = in.readString();
        journeyId = in.readString();
        assignmentTime = in.readString();
        assignmentDate = in.readString();
    }

    public static final Creator<AssignmentItem> CREATOR = new Creator<AssignmentItem>() {
        @Override
        public AssignmentItem createFromParcel(Parcel in) {
            return new AssignmentItem(in); //creates a parcel
        }

        @Override
        public AssignmentItem[] newArray(int size) {
            return new AssignmentItem[size];
        }
    };

    public String getTrainId(){
        return trainId;
    }


    public String getTrainName() {
        return trainName;
    }

    public String getDate(){
        return date;
    }

    public String getSrcStation() {
        return srcStation;
    }

    public String getDestStation() {
        return destStation;
    }

    public String getAssignmentDate() {
        return assignmentDate;
    }

    public String getAssignmentTime() {
        return assignmentTime;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public String getJourneyStatus() {
        return journeyStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { //writes to the parcel
        dest.writeString(trainId);
        dest.writeString(trainName);
        dest.writeString(date);
        dest.writeString(srcStation);
        dest.writeString(destStation);
        dest.writeString(journeyStatus);
        dest.writeString(journeyId);
        dest.writeString(assignmentTime);
        dest.writeString(assignmentDate);
    }
}
