package com.example.captureapp.data.network.model;

import com.example.captureapp.domain.Candidate;
import com.example.captureapp.domain.ClassroomSubjectClassCandidate;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CandidateClassroomsubjectclassResult {
    @SerializedName("classroom_subject_class_id")
    @Expose
    private Integer classroomSubjectClassId;

    @SerializedName("candidate_id")
    @Expose
    private Integer candidatId;

    @SerializedName("is_present")
    @Expose
    private Integer isPresent;

    @SerializedName("name_candidate")
    @Expose
    private String nameCandidate;

    @SerializedName("keypoints")
    @Expose
    private String keypoints;

    @SerializedName("descriptors")
    @Expose
    private String descriptors;

    public Integer getClassroomSubjectClassId() {
        return classroomSubjectClassId;
    }

    public void setClassroomSubjectClassId(Integer classroomSubjectClassId) {
        this.classroomSubjectClassId = classroomSubjectClassId;
    }

    public Integer getCandidatId() {
        return candidatId;
    }

    public void setCandidatId(Integer candidatId) {
        this.candidatId = candidatId;
    }

    public Integer getPresent() {
        return isPresent;
    }

    public void setPresent(Integer present) {
        isPresent = present;
    }

    public String getNameCandidate() {
        return nameCandidate;
    }

    public void setNameCandidate(String nameCandidate) {
        this.nameCandidate = nameCandidate;
    }

    public String getKeypoints() {
        return keypoints;
    }

    public void setKeypoints(String keypoints) {
        this.keypoints = keypoints;
    }

    public String getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(String descriptors) {
        this.descriptors = descriptors;
    }

    public ClassroomSubjectClassCandidate asDomainModel(){
        ClassroomSubjectClassCandidate myModel= new ClassroomSubjectClassCandidate();
        myModel.setCandidateId(candidatId);
        myModel.setClassroomSubjectClassId(classroomSubjectClassId);
        myModel.setPresent(isPresent == 1);

        Candidate myCandidate= new Candidate();
        myCandidate.setName(nameCandidate);
        myCandidate.setKeypoints(keypoints);
        myCandidate.setDescriptors(descriptors);
        myCandidate.setId(candidatId);
        myModel.setCandidate(myCandidate);

        return  myModel;
    }
}

