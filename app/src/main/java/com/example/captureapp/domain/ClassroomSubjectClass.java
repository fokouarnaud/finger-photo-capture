package com.example.captureapp.domain;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class ClassroomSubjectClass {
    private String name;
    private  Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassroomSubjectClass)) return false;
        ClassroomSubjectClass that = (ClassroomSubjectClass) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public String toString() {
        return name;
    }

    public static DiffUtil.ItemCallback<ClassroomSubjectClass> itemCallback =
            new DiffUtil.ItemCallback<ClassroomSubjectClass>() {
                @Override
                public boolean areItemsTheSame(@NonNull ClassroomSubjectClass oldItem,
                                               @NonNull ClassroomSubjectClass newItem) {
                    return oldItem.getId()== newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ClassroomSubjectClass oldItem,
                                                  @NonNull ClassroomSubjectClass newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
