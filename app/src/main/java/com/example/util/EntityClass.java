package com.example.util;

import com.google.firebase.firestore.DocumentReference;


public class EntityClass {
    private String userName;
    private String userIdInDb;
    private String subjectName;
    private String subjectEmail;

    private static EntityClass instance;

    public static EntityClass getInstance() {

        if(instance == null)  instance = new EntityClass();

        return  instance;
    }

    EntityClass() {

    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectEmail() {
        return subjectEmail;
    }

    public void setSubjectEmail(String subjectEmail) {
        this.subjectEmail = subjectEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIdInDb() {
        return userIdInDb;
    }

    public void setUserIdInDb(String userIdInDb) {
        this.userIdInDb = userIdInDb;
    }
}
