package com.example.proximitynotifier;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Intent intent = new Intent("ACT_LOC");
                intent.putExtra("latitude", locationResult.getLastLocation().getLatitude());
                intent.putExtra("longitude", locationResult.getLastLocation().getLongitude());
                sendBroadcast(intent);
            }
        };
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void requestLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
}
