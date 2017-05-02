package com.magicdroid.magiclocationlib;

/**
 * Created by sanidhya on 1/5/17.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MbLocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    @SuppressLint("StaticFieldLeak")
    private static volatile MbLocationService instance;
    private static long interval = MbLocationUtil.LOCATION_INTERVAL; //Default
    private static long fastestInterval = MbLocationUtil.LOCATION_FASTEST_INTERVAL; // Default
    private static int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; // Default
    private static long smallest_displacement = MbLocationUtil.LOCATION_DISPLACEMENT; // 0 meters OFF
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private MbLocationListener mbLocationListener;
    private boolean isOneFix;

    private MbLocationService(Context mContext) {
        this.mContext = mContext;

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();

    }

    public static MbLocationService with(Context context) {
        if (instance == null) {
            synchronized (MbLocationService.class) {
                instance = new MbLocationService(context.getApplicationContext());
            }
        }
        return instance;
    }

    public void setInterval(long interval) {
        MbLocationService.interval = interval;
    }

    public void setFastestInterval(long fastestInterval) {
        MbLocationService.fastestInterval = fastestInterval;
    }

    public void setPriority(int priority) {
        MbLocationService.priority = priority;
    }

    public void setDisplacement(int displacement) {
        smallest_displacement = displacement;
    }

    public void executeService(final MbLocationListener mbLocationListener) {
        this.mbLocationListener = mbLocationListener;

        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        mLocationRequest.setPriority(priority);
        if (smallest_displacement > 0)
            mLocationRequest.setSmallestDisplacement(smallest_displacement);

        final MbLocationUtil mbLocationUtil = MbLocationUtil.with(mContext);
        boolean googlePlayServicesAvailable = mbLocationUtil.isGooglePlayServicesAvailable();
        boolean anyProviderAvailable = mbLocationUtil.isAnyProviderAvailable();

        if (googlePlayServicesAvailable) {
            if (anyProviderAvailable) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mGoogleApiClient.connect();
                    if (mGoogleApiClient.isConnected()) {
                        startLocationUpdates();
                    }

                } else {
                    // Location Permission not available
                    mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_PERMISSION_ERROR, "Location Permission not available."));
                }
            } else {
                // Location Provider not available eg. GPS
                mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_PROVIDER_ERROR, "Location provider not enabled. Please check GPS."));
            }
        } else {
            // googlePlayServices not available
            mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_PLAY_SERVICE_ERROR, "Google Play Services not available."));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_CONNECTION_SUSPENDED_ERROR, "Connection Suspended Error Code =" + i));
    }


    @Override
    public void onLocationChanged(Location location) {
        mbLocationListener.onLocationUpdate(location);
        if (isOneFix) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_CONNECTION_FAILED_ERROR, "Connection Suspended Error Code =" + connectionResult.getErrorCode()));
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    // To stop the location updates.
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d("LocationRequestService", "Location update stopped .......................");
    }

    // To get the location only once. Displacement will be ignored.
    public void setOneFix(boolean isOneFix) {
        this.isOneFix = isOneFix;
    }
}