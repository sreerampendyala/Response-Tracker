package com.example.util.Interfaces.DataInterfaces;

import java.util.List;

public interface SubjectList {
    void getSubjectListStatus(boolean isSuccess, List<String> subjectData);
    void onFailure(String errMessage);
}
