package com.example.captureapp.data.network;


public class ApiUtils {
    private ApiUtils() {}

    public static final String BASE_URL = "https://finger-photo-pad-api.herokuapp.com/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
