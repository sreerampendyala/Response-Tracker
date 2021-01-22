package com.example.util.Models;

import java.util.ArrayList;

public class PosturalStabilityDataModel {
  public ArrayList<Float> accelerometer_xValue = new ArrayList<>();
  public ArrayList<Float> accelerometer_yValue = new ArrayList<>();
  public ArrayList<Float> accelerometer_zValue = new ArrayList<>();

//
  public ArrayList<Float> accelerometer_xValue_linear = new ArrayList<>();
  public ArrayList<Float> accelerometer_yValue_linear = new ArrayList<>();
  public ArrayList<Float> accelerometer_zValue_linear = new ArrayList<>();

  public ArrayList<Float> accelerometer_xValue_uncalibrated = new ArrayList<>();
  public ArrayList<Float> accelerometer_yValue_uncalibrated = new ArrayList<>();
  public ArrayList<Float> accelerometer_zValue_uncalibrated = new ArrayList<>();
//

  public ArrayList<Float> gyroscope_xValue = new ArrayList<>();
  public ArrayList<Float> gyroscope_yValue = new ArrayList<>();
  public ArrayList<Float> gyroscope_zValue = new ArrayList<>();

  public PosturalStabilityDataModel(ArrayList<Float> accelerometer_xValue,
                                    ArrayList<Float> accelerometer_yValue,
                                    ArrayList<Float> accelerometer_zValue,
                                    ArrayList<Float> accelerometer_xValue_uncalibrated,
                                    ArrayList<Float> accelerometer_yValue_uncalibrated,
                                    ArrayList<Float> accelerometer_zValue_uncalibrated,
                                    ArrayList<Float> accelerometer_xValue_linear,
                                    ArrayList<Float> accelerometer_yValue_linear,
                                    ArrayList<Float> accelerometer_zValue_linear,
                                    ArrayList<Float> gyroscope_xValue,
                                    ArrayList<Float> gyroscope_yValue,
                                    ArrayList<Float> gyroscope_zValue) {
    this.accelerometer_xValue = accelerometer_xValue;
    this.accelerometer_yValue = accelerometer_yValue;
    this.accelerometer_zValue = accelerometer_zValue;

    this.accelerometer_xValue_uncalibrated = accelerometer_xValue_uncalibrated;
    this.accelerometer_yValue_uncalibrated = accelerometer_yValue_uncalibrated;
    this.accelerometer_zValue_uncalibrated = accelerometer_zValue_uncalibrated;

    this.accelerometer_xValue_linear = accelerometer_xValue_linear;
    this.accelerometer_yValue_linear = accelerometer_yValue_linear;
    this.accelerometer_zValue_linear = accelerometer_zValue_linear;

    this.gyroscope_xValue = gyroscope_xValue;
    this.gyroscope_yValue = gyroscope_yValue;
    this.gyroscope_zValue = gyroscope_zValue;
  }

  public void setAccelerometer_xValue(ArrayList<Float> accelerometer_xValue) {
    this.accelerometer_xValue = accelerometer_xValue;
  }

  public void setAccelerometer_yValue(ArrayList<Float> accelerometer_yValue) {
    this.accelerometer_yValue = accelerometer_yValue;
  }

  public void setAccelerometer_zValue(ArrayList<Float> accelerometer_zValue) {
    this.accelerometer_zValue = accelerometer_zValue;
  }

  public void setGyroscope_xValue(ArrayList<Float> gyroscope_xValue) {
    this.gyroscope_xValue = gyroscope_xValue;
  }

  public void setGyroscope_yValue(ArrayList<Float> gyroscope_yValue) {
    this.gyroscope_yValue = gyroscope_yValue;
  }

  public void setGyroscope_zValue(ArrayList<Float> gyroscope_zValue) {
    this.gyroscope_zValue = gyroscope_zValue;
  }
}
