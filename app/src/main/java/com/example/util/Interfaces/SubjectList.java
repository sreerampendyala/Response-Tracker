package com.example.util.Interfaces;

import java.util.List;

public interface SubjectList {
    void getSubjectListStatus(boolean isSuccess, List<String> subjectData);
    void onFailure(String errMessage);
}
