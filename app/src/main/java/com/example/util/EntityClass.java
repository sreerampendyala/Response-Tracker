package com.example.util;

import com.example.util.Models.PhysicianChoiceModel;


import java.util.List;


public class EntityClass {
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

    public boolean isSubject() {
        return isSubject;
    }

    public void setSubject(boolean subject) {
        isSubject = subject;
    }

}
