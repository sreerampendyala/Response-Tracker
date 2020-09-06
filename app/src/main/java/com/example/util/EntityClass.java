package com.example.util;

import com.example.util.Models.PhysicianChoiceModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EntityClass {
    private List<PhysicianChoiceModel> physicianChoiceList;
    private boolean isSubject;

    private static EntityClass instance;

    public static EntityClass getInstance() {

        if(instance == null)  instance = new EntityClass();

        return  instance;
    }

    public boolean isSubject() {
        return isSubject;
    }

    public void setSubject(boolean subject) {
        isSubject = subject;
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

    public Map<String, String> generateMap(ArrayList<String> Keys, ArrayList<String> Values) {
        Map<String, String> map = new HashMap<>();
        if(Keys.size() == Values.size()) {
            for(int index = 0; index<Keys.size(); index++) {
                map.put(Keys.get(index), Values.get(index));
            }
        }
        return map;
    }

}
