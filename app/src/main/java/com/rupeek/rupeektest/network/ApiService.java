package com.rupeek.rupeektest.network;


import com.rupeek.rupeektest.models.PlaceModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("he-public-data/placesofinterest39c1c48.json")
    Call<List<PlaceModel>> getPlacesOfInterest();


}

