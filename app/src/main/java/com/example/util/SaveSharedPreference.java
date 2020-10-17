package com.example.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.util.Models.PhysicianDetailModel;
import com.example.util.Models.SubjectDetailModel;

public class SaveSharedPreference {
  static final String PREF_USER_NAME= "username";
  static final String physician_Email_for_Subject = "physicianEmail";
  static final String NAME = "name";
  static final String AGE = "age";
  static final String is_Subject = "isSubject";
  static final String userIdInDB = "userIdInDb";

  static SharedPreferences getSharedPreferences(Context ctx) {
    return PreferenceManager.getDefaultSharedPreferences(ctx);
  }

  public static void setUserNameAndType(Context ctx) {
    SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
    if(EntityClass.getInstance().isSubject()) {
      editor.putString(PREF_USER_NAME, SubjectDetailModel.getInstance().getSubjectEmail());
      editor.putString(physician_Email_for_Subject, PhysicianDetailModel.getInstance().getPhysicianEmail());
      editor.putString(NAME, SubjectDetailModel.getInstance().getSubjectName());
      editor.putString(AGE, SubjectDetailModel.getInstance().getSubjectAge());
      editor.putString(userIdInDB, SubjectDetailModel.getInstance().getUserIdInDb());
      editor.putBoolean(is_Subject, true);
    } else {
      editor.putString(PREF_USER_NAME, PhysicianDetailModel.getInstance().getPhysicianEmail());
      editor.putString(NAME, PhysicianDetailModel.getInstance().getPhysicianName());
      editor.putString(userIdInDB, PhysicianDetailModel.getInstance().getUserIdInDb());
      editor.putBoolean(is_Subject, false);
    }
    editor.commit();
  }

  public static String getUserName(Context ctx) {
    return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
  }

  public static String getPhysicainEmailForSubject(Context ctx) {
    return getSharedPreferences(ctx).getString(physician_Email_for_Subject, "");
  }

  public static String getName(Context ctx) {
    return getSharedPreferences(ctx).getString(NAME, "");
  }
  public static String getAge(Context ctx) {
    return getSharedPreferences(ctx).getString(AGE, "");
  }

  public static String getUserId(Context ctx) {
    return getSharedPreferences(ctx).getString(userIdInDB, "");
  }

  public static boolean isSubjectLogin(Context ctx) {
    return  getSharedPreferences(ctx).getBoolean(is_Subject, false);
  }
  public static void clearUserData(Context ctx) {
    SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
    editor.clear(); //clear all stored data
    editor.commit();
  }
}
