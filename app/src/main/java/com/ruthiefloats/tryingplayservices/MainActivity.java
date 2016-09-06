package com.ruthiefloats.tryingplayservices;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
}
