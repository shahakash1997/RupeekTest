package com.rupeek.rupeektest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.rupeek.rupeektest.R;
import com.rupeek.rupeektest.databinding.ActivityPlacesBinding;
import com.rupeek.rupeektest.models.PlaceModel;
import com.rupeek.rupeektest.network.RetrofitClient;
import com.rupeek.rupeektest.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesActivity extends AppCompatActivity {
    private static final String TAG = "PlacesActivity";
    private ActivityPlacesBinding binding;
    private PlacesAdapter placesAdapter;
    private List<PlaceModel> places;
    private Map<Integer, Boolean> upvoteMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlacesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        upvoteMap = new HashMap<>();
        initViews();
        loadPlacesOfInterest();
    }

    private void initViews() {
        setUpRecyclerView();
        binding.srlRefresh.setOnRefreshListener(this::loadPlacesOfInterest);
    }

    private void loadPlacesOfInterest() {
        RetrofitClient.getRetrofitClientInstance().getApiService()
                .getPlacesOfInterest()
                .enqueue(new Callback<List<PlaceModel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<PlaceModel>> call, @NonNull Response<List<PlaceModel>> response) {
                        if (binding.srlRefresh.isRefreshing())
                            binding.srlRefresh.setRefreshing(false);
                        try {
                            if (response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                                placesAdapter.clearALl();
                                placesAdapter.updateList(response.body());

                            } else {
                                Toast.makeText(PlacesActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                            Toast.makeText(PlacesActivity.this, "Unexpected Error", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<List<PlaceModel>> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        if (binding.srlRefresh.isRefreshing())
                            binding.srlRefresh.setRefreshing(false);
                        if (t instanceof IOException) {
                            Toast.makeText(PlacesActivity.this, "Internet issues", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PlacesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void setUpRecyclerView() {
        places = new ArrayList<>();
        placesAdapter = new PlacesAdapter(places, this, (id, placeModel, pos) -> {
            switch (id) {
                case R.id.ivDirec:
                case R.id.cvMain:
                    startDirections(placeModel);
                    break;
                case R.id.ivUpvote:
                    upvotePlace(placeModel, pos);
                    break;
            }

        });
        binding.rvPlaces.setAdapter(placesAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        binding.rvPlaces.setLayoutManager(gridLayoutManager);
    }


    private void upvotePlace(PlaceModel placeModel, final int pos) {
        if (placeModel == null) return;
        Boolean isVoted = upvoteMap.get(placeModel.getId());
        if (isVoted != null) {
            upvoteMap.remove(placeModel.getId());
            if (isVoted) {
                upvoteMap.put(placeModel.getId(), false);
                placeModel.setChecked(false);
                placesAdapter.notifyItemChanged(pos);
            } else {
                upVoteItem(placeModel, pos);
            }
        } else {
            upVoteItem(placeModel, pos);

        }
    }

    private void upVoteItem(PlaceModel placeModel, int pos) {
        if (checkForVotes()) {
            Utils.showSnackBar(binding.getRoot(), "Cannot add more than 3 upvotes", "OK", v -> {
            });
        } else {
            upvoteMap.put(placeModel.getId(), true);
            placeModel.setChecked(true);
            placesAdapter.notifyItemChanged(pos);

        }

    }

    private Boolean checkForVotes() {
        int count = 0;
        for (Map.Entry<Integer, Boolean> entry : upvoteMap.entrySet()) {
            if (entry.getValue())
                count++;

        }
        return count >= 3;
    }

    private void startDirections(PlaceModel placeModel) {
        Intent intent = new Intent(this, DirectionsActivity.class);
        intent.putExtra("PLACE", placeModel);
        startActivity(intent);
    }
}