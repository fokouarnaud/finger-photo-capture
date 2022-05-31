package com.example.captureapp.domain;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class Candidate {

    private String img;

    private String keypoints;

    private String descriptors;

    private String name;

    private Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Candidate)) return false;
        Candidate candidate = (Candidate) o;
        return Objects.equals(img, candidate.img) &&
                Objects.equals(keypoints, candidate.keypoints) &&
                Objects.equals(descriptors, candidate.descriptors) &&
                Objects.equals(name, candidate.name) &&
                Objects.equals(id, candidate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(img, keypoints, descriptors, name, id);
    }

    public static DiffUtil.ItemCallback<Candidate> itemCallback =
            new DiffUtil.ItemCallback<Candidate>() {
                @Override
                public boolean areItemsTheSame(@NonNull Candidate oldItem,
                                               @NonNull Candidate newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Candidate oldItem,
                                                  @NonNull Candidate newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
