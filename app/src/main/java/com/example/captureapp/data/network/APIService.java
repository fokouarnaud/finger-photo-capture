package com.example.captureapp.data.network;

import com.example.captureapp.data.network.model.CandidatResult;
import com.example.captureapp.data.network.model.CandidateClassroomsubjectclassResult;
import com.example.captureapp.data.network.model.ClassroomsubjectclassResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @POST("fingerphoto")
    @FormUrlEncoded
    Call<CandidatResult> processingFingerphoto(@Field("action") String action,
                                               @Field("img") String img);

    @POST("candidate/{name}")
    @FormUrlEncoded
    Call<CandidatResult> saveCandidate(@Path("name") String name,
                                       @Field("descriptors") String descriptors,
                                       @Field("keypoints") String keypoints);

    @GET("processingstatus")
    Call<CandidatResult> processingStatusFingerphoto(@Query("task_id") String task_id);


    @GET("candidates")
    Call<List<CandidatResult>> getAllCandidates();

    @POST("classroomsubjectclass/{name}")
    Call<ClassroomsubjectclassResult> saveClassroomsubjectclass(@Path("name") String name);

    @GET("classroomsubjectclasses")
    Call<List<ClassroomsubjectclassResult>> getAllClassroomsubjectclasses();

    @GET("classroomsubjectclasses/{id}/candidates")
    Call<List<CandidateClassroomsubjectclassResult>> getClassroomSubjectClassWithCandidates(
            @Path("id") Integer id);

    @POST("classroomsubjectclasses/{classroom_id}/candidates/{candidat_id}")
    Call<CandidateClassroomsubjectclassResult> saveCandidateClassroomsubjectclass(
            @Path("classroom_id") long classroomSubjectClassId,
            @Path("candidat_id") long candidateId);

    @POST("candidates/{candidat_id}/authenticate")
    @FormUrlEncoded
    Call<CandidatResult> getAuthenticationMatchCandidate(
            @Path("candidat_id") long candidateId,
            @Field("descriptors") String descriptors,
            @Field("keypoints") String keypoints);

    @PUT("classroomsubjectclasses/{classroom_id}/candidates/{candidat_id}")
    @FormUrlEncoded
    Call<CandidateClassroomsubjectclassResult> updateCandidateClassroomsubjectclass(
            @Path("classroom_id") long classroomSubjectClassId,
            @Path("candidat_id") long candidateId,
            @Field("is_present") int isPresent);

}

