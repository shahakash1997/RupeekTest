package com.rupeek.rupeektest.network;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rupeek.rupeektest.Config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private Retrofit retrofit;
    private ApiService apiService;

    private static volatile RetrofitClient _instance = null;

    private RetrofitClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiService = retrofit.create(ApiService.class);

    }

    public ApiService getApiService() {
        return apiService;
    }


    public static RetrofitClient getRetrofitClientInstance() {
        if (_instance == null) {
            synchronized (RetrofitClient.class) {
                if (_instance == null) {
                    _instance = new RetrofitClient();
                }
            }

        }
        return _instance;

    }


}
