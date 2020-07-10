package com.example.util.Models;

import android.app.Application;

public class TapModel {
    private int count;
    private int timerDuration;
    private int avgSpeed;
//    private boolean showReport;
//    private boolean showDoubleTapTest;
//    private boolean showSingleTapTest;


    public TapModel() {

    }

    public TapModel(int count, int timerDuration, int avgSpeed) {
        this.count = count;
        this.timerDuration = timerDuration;
        this.avgSpeed = avgSpeed;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTimerDuration() {
        return timerDuration;
    }

    public void setTimerDuration(int timerDuration) {
        this.timerDuration = timerDuration;
    }

    public int getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(int avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

}
