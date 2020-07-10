package com.example.util.Interfaces;

public interface CredValidationInterface {

    //This interface is to handle all the callbacks.

    void onSuccessValidatingCredentials(boolean isSuccess);
    void onFailure(String errMessage);
}
