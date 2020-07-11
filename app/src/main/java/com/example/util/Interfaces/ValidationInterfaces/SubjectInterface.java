package com.example.util.Interfaces.ValidationInterfaces;

public interface SubjectInterface {
    void subjectExistOrCreated(boolean isSuccess);
    void onFailure(String errMessage);
}
