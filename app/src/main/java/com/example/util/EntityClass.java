package com.example.util;

import android.content.Context;
import android.content.Intent;

import com.example.util.Models.PhysicianChoiceModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EntityClass {
    private List<PhysicianChoiceModel> physicianChoiceList;
    private boolean isSubject;
    private boolean isPractice;

    private static List<Integer> existingNotifications = new ArrayList<Integer>();

    private static EntityClass instance;

    public static EntityClass getInstance() {

        if(instance == null)  instance = new EntityClass();

        return  instance;
    }

    public boolean isPractice() {
        return isPractice;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
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
            case posturalStabilityLbl:
                returnLbl = "Postural Stability Test";
                break;
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

    public void startMyService(Context ctx) {
        ctx.startService(new Intent(ctx, CreateChannel.class));
    }

    public void stopMyService(Context ctx) {
        ctx.stopService(new Intent(ctx, CreateChannel.class));
    }

    public static List<Integer> getExistingNotifications() {
        return existingNotifications;
    }

    public static void removeAllNotifications() {
        EntityClass.existingNotifications.removeAll(EntityClass.getExistingNotifications());
    }

    public static void addToExistingNotifications(int code) {
        EntityClass.existingNotifications.add(code);
    }

    public static void removeFromExistingNotifications(int code) {
        EntityClass.existingNotifications.remove(new Integer(code));
    }
}
