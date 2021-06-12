package com.rupeek.rupeektest.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rupeek.rupeektest.R;
import com.rupeek.rupeektest.databinding.ActivityDirectionsBinding;
import com.rupeek.rupeektest.models.PlaceModel;

import java.util.List;

public class DirectionsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "DirectionsActivity";
    private ActivityDirectionsBinding binding;

    private GoogleMap googleMap;
    private Marker currentLocationMarker, destLocMarker;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient locationProviderClient;
    public final int MY_PERMISSIONS_REQUEST_LOCATION = 990;
    private PlaceModel placeModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        if (getIntent().hasExtra("PLACE")) {
            placeModel = getIntent().getParcelableExtra("PLACE");
        }
        
        binding.setClickHandler(this);
        

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        requestLocationUpdates();

    }

    private void requestLocationUpdates() {
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(1500);
            locationRequest.setFastestInterval(1500);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            } else {
                checkLocationPermission();
            }
        } else {
            locationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    private void setPlaceMarker() {
        try {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(Double.parseDouble(placeModel.getLatitude().substring(0, placeModel.getLatitude().indexOf("°"))),
                    Double.parseDouble(placeModel.getLongitude().substring(0, placeModel.getLongitude().indexOf("°"))));
            markerOptions.position(latLng);
            markerOptions.title("Destination Location");
            destLocMarker = googleMap.addMarker(markerOptions);
            googleMap.addMarker(markerOptions);
        } catch (Exception e) {
            Log.d(TAG, "setPlaceMarker: " + e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();

    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation().getLatitude());
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                currentLocationMarker = googleMap.addMarker(markerOptions);
                setPlaceMarker();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            }
        }
    };

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", (dialogInterface, i) -> ActivityCompat.requestPermissions(DirectionsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION))
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationProviderClient != null) {
            locationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabDirec:
                if (currentLocationMarker != null && destLocMarker != null)

                {
                    Intent intent =
                            new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse(String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s",
                                            currentLocationMarker.getPosition().latitude,
                                            currentLocationMarker.getPosition().longitude,
                                            destLocMarker.getPosition().latitude,
                                            destLocMarker.getPosition().longitude
                                            )));
                    startActivity(intent);

                }
                else
                {
                    Toast.makeText(this, "Please wait!getting Locations", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}