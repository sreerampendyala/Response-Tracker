package com.example.util.Interfaces;

public interface MyStatListener {
  void status(boolean isSuccess, Object obj);
  void onFailure(String errMessage);
}
