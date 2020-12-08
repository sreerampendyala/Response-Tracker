package com.example.util.Models;

public class TapModel {
    private int correctTapsCount;
    private double avgTimeBetweenTaps;
    private double sdTimebwTaps;
    private int wrongTapsCount;
    private double avgSpeed;
//    private boolean showReport;
//    private boolean showDoubleTapTest;
//    private boolean showSingleTapTest;


    public TapModel() {

    }

    public TapModel(int correctCount, int avgTimeBetweenTaps, int sdTimebwTaps, int wrongCounts) {
        this.correctTapsCount = correctCount;
        this.avgTimeBetweenTaps = avgTimeBetweenTaps;
        this.sdTimebwTaps = sdTimebwTaps;
        this.wrongTapsCount = wrongCounts;
    }

    public int getCorrectTapsCount() {
        return correctTapsCount;
    }

    public void setCorrectTapsCount(int correctTapsCount) {
        this.correctTapsCount = correctTapsCount;
    }

    public double getAvgTimeBetweenTaps() {
        return avgTimeBetweenTaps;
    }

    public void setAvgTimeBetweenTaps(double avgTimeBetweenTaps) {
        this.avgTimeBetweenTaps = avgTimeBetweenTaps;
    }

    public double getSdTimebwTaps() {
        return sdTimebwTaps;
    }

    public void setSdTimebwTaps(double sdTimebwTaps) {
        this.sdTimebwTaps = sdTimebwTaps;
    }

    public int getWrongTapsCount() {
        return wrongTapsCount;
    }

    public void setWrongTapsCount(int wrongTapsCount) {
        this.wrongTapsCount = wrongTapsCount;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}
