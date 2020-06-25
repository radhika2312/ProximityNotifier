package com.example.proximitynotifier;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private float DEFAULT_ZOOM=16.5f;
    private FloatingActionButton floatingActionButton,okButton;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private LatLng latLng,latLngSelected;
    private int position;
    private LocationBroadcastReceiver receiver;
    private Marker marker;
    private boolean atCur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        atCur=true;
        marker=null;
        receiver = new LocationBroadcastReceiver();
        Mapbox.getInstance(this,getString(R.string.access_token));

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //Req Location Permission
                startLocService();
            }
        } else {
            //Start the Location Service
            startLocService();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        floatingActionButton=findViewById(R.id.location_search);
        okButton=findViewById(R.id.okButton);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);


    }
    void startLocService() {
        IntentFilter filter = new IntentFilter("ACT_LOC");
        registerReceiver(receiver, filter);
        Intent intent = new Intent(MapsActivity.this, LocationService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocService();
                } else {
                    Toast.makeText(this, "Give me permissions", Toast.LENGTH_LONG).show();
                }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(26.6438, 84.9040);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        position=getIntent().getIntExtra("Position",-1);
       if(position!=-1)
        {
            String la,lo;
            la=getIntent().getStringExtra("Longitude");
            lo=getIntent().getStringExtra("Latitude");
            double lat,lon;
            lat=Double.parseDouble(la);
            lon=Double.parseDouble(lo);
            LatLng lt=new LatLng(lat,lon);
            marker=mMap.addMarker(new MarkerOptions().position(lt).title("Drag to adjust...").draggable(true));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lt,DEFAULT_ZOOM));
            atCur=false;

        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latLngSelected=latLng;
                if(marker!=null)
                {
                    marker.remove();
                }
                marker=mMap.addMarker(new MarkerOptions().position(latLng).title("Drag to adjust...").draggable(true));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));

            }
        });

        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(getString(R.string.access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(MapsActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(marker==null)
                {
                    Toast.makeText(getApplicationContext(),"Please select a location",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent=new Intent();
                    String lon=String.valueOf(latLngSelected.longitude);
                    String lat=String.valueOf(latLngSelected.latitude);
                    //Toast.makeText(getApplicationContext(),lon + "  " + lat,Toast.LENGTH_LONG).show();
                    intent.putExtra("longitude",lon);
                    intent.putExtra("latitude",lat);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }


            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CarmenFeature feature = PlaceAutocomplete.getPlace(data);
        atCur=false;
        double lat=((Point) Objects.requireNonNull(feature.geometry())).latitude();
        double lng=((Point) Objects.requireNonNull(feature.geometry())).longitude();
        latLng=new LatLng(lat,lng);
        atCur=false;
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));

    }


    public class LocationBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {



            if (Objects.equals(intent.getAction(), "ACT_LOC")) {
                double lat = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);
                String title=lat + "/" + longitude;
                //Toast.makeText(getApplicationContext(),title,Toast.LENGTH_LONG).show();
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                    if(atCur) {
                        atCur=false;
                        LatLng latLng = new LatLng(lat, longitude);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));


                    }
                }

            }
        }

    }

}
