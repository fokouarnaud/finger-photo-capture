package com.example.captureapp.domain;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class ClassroomSubjectClassCandidate {
    private  Integer candidateId;
    private  Integer classroomSubjectClassId;
    private  Boolean isPresent=false;
    private Candidate candidate;


    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public Integer getClassroomSubjectClassId() {
        return classroomSubjectClassId;
    }

    public void setClassroomSubjectClassId(Integer classroomSubjectClassId) {
        this.classroomSubjectClassId = classroomSubjectClassId;
    }

    public Boolean getPresent() {
        return isPresent;
    }

    public void setPresent(Boolean present) {
        isPresent = present;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassroomSubjectClassCandidate)) return false;
        ClassroomSubjectClassCandidate that = (ClassroomSubjectClassCandidate) o;
        return Objects.equals(candidateId, that.candidateId) &&
                Objects.equals(classroomSubjectClassId, that.classroomSubjectClassId) &&
                Objects.equals(isPresent, that.isPresent) &&
                Objects.equals(candidate, that.candidate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(candidateId, classroomSubjectClassId, isPresent, candidate);
    }

    public static DiffUtil.ItemCallback<ClassroomSubjectClassCandidate> itemCallback =
            new DiffUtil.ItemCallback<ClassroomSubjectClassCandidate>() {
                @Override
                public boolean areItemsTheSame(@NonNull ClassroomSubjectClassCandidate oldItem,
                                               @NonNull ClassroomSubjectClassCandidate newItem) {
                    return oldItem.getCandidateId()== newItem.getCandidateId() ;
                }

                @Override
                public boolean areContentsTheSame(@NonNull ClassroomSubjectClassCandidate oldItem,
                                                  @NonNull ClassroomSubjectClassCandidate newItem) {
                    return oldItem.equals(newItem);
                }
            };
}

