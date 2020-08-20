package com.example.util.Models;


public class SubjectDetailModel {
    private String subjectEmail;
    private String subjectName;
    private String subjectAge;
    private String imageUri;
    private String UserIdInDB;

    private static SubjectDetailModel instance;

    public static SubjectDetailModel getInstance() {
        if(instance == null) instance = new SubjectDetailModel();
        return instance;
    }

    public SubjectDetailModel() {

    }

    public String getSubjectEmail() {
        return subjectEmail;
    }

    public void setSubjectEmail(String subjectEmail) {
        this.subjectEmail = subjectEmail;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectAge() {
        return subjectAge;
    }

    public void setSubjectAge(String subjectAge) {
        this.subjectAge = subjectAge;
    }

    public String getSubjectImageUri() {
        return imageUri;
    }

    public void setSubjectImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUserIdInDb() {
        return UserIdInDB;
    }

    public void setUserIdInDb(String userIdInDB) {
        UserIdInDB = userIdInDB;
    }
}
