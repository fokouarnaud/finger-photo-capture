package com.example.captureapp.data.network.model;

import com.example.captureapp.domain.ClassroomSubjectClass;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClassroomsubjectclassResult {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("classroom_subject_class_id")
    @Expose
    private Integer classroomSubjectClassId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getClassroomSubjectClassId() {
        return classroomSubjectClassId;
    }

    public void setClassroomSubjectClassId(Integer classroomSubjectClassId) {
        this.classroomSubjectClassId = classroomSubjectClassId;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ClassroomSubjectClass asNetworkModel(ClassroomSubjectClass classroomsubjectclass){
        ClassroomSubjectClass myModel= new ClassroomSubjectClass();
        myModel.setName(classroomsubjectclass.getName());

        return  myModel;
    }

    public ClassroomSubjectClass asDomainModel(){
        ClassroomSubjectClass myModel= new ClassroomSubjectClass();
        myModel.setName(name);
        myModel.setId(classroomSubjectClassId);
        return  myModel;
    }
}
