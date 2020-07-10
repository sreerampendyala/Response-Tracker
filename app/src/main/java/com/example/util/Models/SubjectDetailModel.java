package com.example.util.Models;

public class SubjectDetailModel extends SubjectDataModel {
    private boolean accessReports;
    private boolean accessDuelTapTest;
    private boolean accessSingleTapTest;

    public boolean isAccessReports() {
        return accessReports;
    }

    public void setAccessReports(boolean accessReports) {
        this.accessReports = accessReports;
    }

    public boolean isAccessDuelTapTest() {
        return accessDuelTapTest;
    }

    public void setAccessDuelTapTest(boolean accessDuelTapTest) {
        this.accessDuelTapTest = accessDuelTapTest;
    }

    public boolean isAccessSingleTapTest() {
        return accessSingleTapTest;
    }

    public void setAccessSingleTapTest(boolean accessSingleTapTest) {
        this.accessSingleTapTest = accessSingleTapTest;
    }

    public SubjectDetailModel(String email, String name, String age, boolean accessReports, boolean accessDuelTapTest, boolean accessSingleTapTest) {
        super(email, name, age);
        this.accessReports = accessReports;
        this.accessDuelTapTest = accessDuelTapTest;
        this.accessSingleTapTest = accessSingleTapTest;
    }
}
