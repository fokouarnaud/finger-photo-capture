package com.example.captureapp.data.network.model;


import com.example.captureapp.domain.Candidate;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CandidatResult {
    @SerializedName("id")
    @Expose
    private Integer candidateId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("img")
    @Expose
    private String img;

    @SerializedName("keypoints")
    @Expose
    private  String keypoints;

    @SerializedName("descriptors")
    @Expose
    private  String descriptors;

    @SerializedName("task_id")
    @Expose
    private  String taskid;

    @SerializedName("current")
    @Expose
    private  Integer current;


    @SerializedName("ratio_match")
    @Expose
    private  Integer ratioMatch;


    public Integer getRatioMatch() {
        return ratioMatch;
    }

    public void setRatioMatch(Integer ratioMatch) {
        this.ratioMatch = ratioMatch;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }



    @Override
    public String toString() {
        return name;
    }

    public static CandidatResult asNetworkModel(Candidate candidate){
        CandidatResult myModel= new CandidatResult();
        myModel.setImg(candidate.getImg());

        return  myModel;
    }

    public  Candidate asDomainModel(){
        Candidate myModel= new Candidate();
        myModel.setName(name);
        myModel.setId(candidateId);
        myModel.setDescriptors(descriptors);
        return  myModel;
    }
}
