package com.example.util.Models;


public class PhysicianDetailModel {
  private String physicianName;
  private String physicianEmail;
  private String UserIdInDB;

  public static PhysicianDetailModel instance;

  public static PhysicianDetailModel getInstance() {
    if(instance == null) instance = new PhysicianDetailModel();
    return instance;
  }

  public PhysicianDetailModel() {
  }

  public String getPhysicianName() {
    return physicianName;
  }

  public void setPhysicianName(String physicianName) {
    this.physicianName = physicianName;
  }

  public String getPhysicianEmail() {
    return physicianEmail;
  }

  public void setPhysicianEmail(String physicianEmail) {
    this.physicianEmail = physicianEmail;
  }

  public String getUserIdInDB() {
    return UserIdInDB;
  }

  public void setUserIdInDB(String userIdInDB) {
    UserIdInDB = userIdInDB;
  }
}
