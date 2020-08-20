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

  public String getUserIdInDb() {
    return UserIdInDB;
  }

  public void setUserIdInDb(String userIdInDB) {
    UserIdInDB = userIdInDB;
  }
}
