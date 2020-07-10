package com.example.util.Models;

public class SubjectDataModel {
    private String Email;
    private String Name;
    private String Age;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public SubjectDataModel(String email, String name, String age) {
        Email = email;
        Name = name;
        Age = age;
    }
}
