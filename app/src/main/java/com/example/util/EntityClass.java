package com.example.util;

import com.example.util.Models.PhysicianChoiceModel;


import java.util.List;


public class EntityClass {
    private String userName;
    private String userIdInDb;
    private String subjectName;
    private String subjectEmail;
    private String physicianEmail;
    private String subjectAge;
    private String subjectImageUri;
    private List<PhysicianChoiceModel> physicianChoiceList;
    private boolean isSubject;

    private static EntityClass instance;

    public static EntityClass getInstance() {

        if(instance == null)  instance = new EntityClass();

        return  instance;
    }

    EntityClass() {

    }

    public List<PhysicianChoiceModel> getPhysicianChoiceList() {
        return physicianChoiceList;
    }

    public void setPhysicianChoiceList(List<PhysicianChoiceModel> physicianChoiceList) {
        this.physicianChoiceList = physicianChoiceList;
    }

    public void changeValueAtPhysicianChoiseList(int position, boolean isTrue) {
        PhysicianChoiceModel model = new PhysicianChoiceModel();
        model.setLable(this.physicianChoiceList.get(position).getLable());
        model.setValue(isTrue);
        this.physicianChoiceList.set(position, model);
    }

    public String getLbl(SetupOptions setup) {
        String returnLbl = null;
        switch (setup) {
            case ReportLbl:
                returnLbl =  "Reports";
                break;
            case DoubleButtonLbl:
                returnLbl = "Double Button Tap Test";
                break;
            case SingleButtonLbl:
                returnLbl = "Single Button Tap Test";
                break;
        }
        return returnLbl;
    }

    public String getPhysicianEmail() {
        return physicianEmail;
    }

    public void setPhysicianEmail(String physicianEmail) {
        this.physicianEmail = physicianEmail;
    }

    public boolean isSubject() {
        return isSubject;
    }

    public void setSubject(boolean subject) {
        isSubject = subject;
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

    public String getSubjectAge() {
        return subjectAge;
    }

    public void setSubjectAge(String subjectAge) {
        this.subjectAge = subjectAge;
    }

    public String getSubjectImageUri() {
        return subjectImageUri;
    }

    public void setSubjectImageUri(String subjectImageUri) {
        this.subjectImageUri = subjectImageUri;
    }

}
