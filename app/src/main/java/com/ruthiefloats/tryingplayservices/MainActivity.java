package com.ruthiefloats.tryingplayservices;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private final String PLAY_LOG_TAG = "PlaySvcsDemo";

    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(PLAY_LOG_TAG, "onCreate: Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable(Wearable.API)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(PLAY_LOG_TAG, "onStart: Connecting to Play Svcs");

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            googleApiAvailability.getErrorDialog(this, resultCode, 1).show();
        } else {
            mGoogleApiClient.connect();
        }
    }

    /*Two methods for the ConnectionCallbacks interface : */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i(PLAY_LOG_TAG, "called onConnected");
        if (mGoogleApiClient.hasConnectedApi(Wearable.API)) {
            Log.i(PLAY_LOG_TAG, "wearable api present");
        } else {
            Log.i(PLAY_LOG_TAG, "no wearable present");
        }

        if (mGoogleApiClient.hasConnectedApi(LocationServices.API)) {
            Log.i(PLAY_LOG_TAG, "location api present");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.i(PLAY_LOG_TAG, "onConnected location " + getLocation());

        } else {
            Log.i(PLAY_LOG_TAG, "no location present");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(PLAY_LOG_TAG, "onConnectionSuspended due to cause: " + i);
        mGoogleApiClient.connect();
    }

    /*For the OnConnectionFailedListener interface */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(PLAY_LOG_TAG, "onConnectionFailed due to cause: " + connectionResult.getErrorMessage());
        Log.i(PLAY_LOG_TAG, "onConnectionFailed due to cause: " + connectionResult.getErrorCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(PLAY_LOG_TAG, "on result location " + getLocation());
            }
        }
    }

    private Location getLocation() {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            return location;
        } catch (SecurityException e) {
            return null;
        }
    }
}
