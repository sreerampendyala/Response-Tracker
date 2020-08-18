package com.example.util.Models;

public class SubjectDataModel {
    private String subjectEmail;
    private String subjectName;
    private String subjectAge;
    private String imageUri;

    public String getSubjectEmail() {
        return subjectEmail;
    }

    public void setSubjectEmail(String subjectEmail) {
        this.subjectEmail = subjectEmail;
    }

    public String getName() {
        return subjectName;
    }

    public void setName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectAge() {
        return subjectAge;
    }

    public void setSubjectAge(String subjectAge) {
        this.subjectAge = subjectAge;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public  SubjectDataModel() {

    }

    public SubjectDataModel(String subjectEmail, String subjectName, String subjectAge) {
        this.subjectEmail = subjectEmail;
        this.subjectName = subjectName;
        this.subjectAge = subjectAge;
    }
}
