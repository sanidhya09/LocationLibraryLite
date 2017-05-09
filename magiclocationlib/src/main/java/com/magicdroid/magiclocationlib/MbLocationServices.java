package com.magicdroid.magiclocationlib;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by sanidhya on 5/5/17.
 */

public class MbLocationServices {
    private static long INTERVAL = MbLocationUtil.LOCATION_INTERVAL; //Default
    private static long FASTESTINTERVAL = MbLocationUtil.LOCATION_FASTEST_INTERVAL; // Default
    private static int PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY; // Default
    private static int DISPLACEMENT = MbLocationUtil.LOCATION_DISPLACEMENT; // 0 meters OFF
    private static long EXPIRATIONTIME = 0; // 0 minutes , No Expiration
    private static boolean isOneFix;

    private final LocationResolverFragment mLocationResolverFragment;
    private Context mContext;

    private MbLocationServices(FragmentActivity fragmentActivity) {
        mLocationResolverFragment = LocationResolverFragment.from(fragmentActivity.getSupportFragmentManager());
    }

    private MbLocationServices(Context mContext) {
        this.mContext = mContext;
        mLocationResolverFragment = null;

    }

    public void init(final MbLocationListener mbLocationListener) {
        if (mLocationResolverFragment == null) {
            MbLocationService mbLocationService = MbLocationService.with(mContext);

            mbLocationService.setFastestInterval(FASTESTINTERVAL);
            mbLocationService.setInterval(INTERVAL);
            mbLocationService.setPriority(PRIORITY);
            mbLocationService.setOneFix(isOneFix);
            mbLocationService.setDisplacement(DISPLACEMENT);

            mbLocationService.executeService(mbLocationListener);
        } else
            mLocationResolverFragment.executeService(mbLocationListener);
    }

    public static MbLocationServices with(FragmentActivity fragmentActivity) {
        return new MbLocationServices(fragmentActivity);
    }

    public static MbLocationServices with(Context fragmentActivity) {
        return new MbLocationServices(fragmentActivity);
    }

    public void setInterval(long interval) {
        MbLocationServices.INTERVAL = interval;
    }

    public void setFastestInterval(long fastestInterval) {
        MbLocationServices.FASTESTINTERVAL = fastestInterval;
    }

    public void setPriority(int priority) {
        MbLocationServices.PRIORITY = priority;
    }

    public void setDisplacement(int displacement) {
        MbLocationServices.DISPLACEMENT = displacement;
    }

    public void setExpiration(long expiration) {
        MbLocationServices.EXPIRATIONTIME = expiration;
    }

    // To stop the location updates.
    public void stopLocationUpdates() {
        mLocationResolverFragment.stopLocationUpdates();
    }

    // To get the location only once. Displacement will be ignored.
    public void setOneFix(boolean isOneFix) {
        MbLocationServices.isOneFix = isOneFix;
    }

    public static class LocationResolverFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
        private static final String TAG = "LocationSettings";
        private static final int LOCATION_SETTINGS_REQUEST = 21;
        private static final int REQUEST_PERMISSION_LOCATION = 22;
        private GoogleApiClient mGoogleApiClient;
        private LocationRequest mLocationRequest;
        private MbLocationListener mbLocationListener;

        public LocationResolverFragment() {
        }

        @NonNull
        static LocationResolverFragment from(@NonNull FragmentManager fragmentManager) {
            LocationResolverFragment resolutionFragment = (LocationResolverFragment) fragmentManager.findFragmentByTag(TAG);
            if (resolutionFragment == null) {
                resolutionFragment = new LocationResolverFragment();
                fragmentManager.beginTransaction()
                        .add(resolutionFragment, TAG)
                        .commit();
                fragmentManager.executePendingTransactions();
            }
            return resolutionFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == LOCATION_SETTINGS_REQUEST) {
                boolean isLocationSettingsEnabled = (resultCode == Activity.RESULT_OK);
                if (isLocationSettingsEnabled) {
                    startLocationUpdates();
                } else {
                    mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_GPS_DISABLED_ERROR, "Location cannot function without GPS."));
                }
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            checkLocationPermission();
        }

        @Override
        public void onConnectionSuspended(int i) {
            mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_CONNECTION_SUSPENDED_ERROR, "Connection Suspended Error Code =" + i));
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_CONNECTION_FAILED_ERROR, "Connection Suspended Error Code =" + connectionResult.getErrorCode()));
        }

        @Override
        public void onLocationChanged(Location location) {
            mbLocationListener.onLocationUpdate(location);
            if (isOneFix) {
                stopLocationUpdates();
            }
        }

        public void executeService(final MbLocationListener mbLocationListener) {
            this.mbLocationListener = mbLocationListener;

            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setFastestInterval(FASTESTINTERVAL);
            mLocationRequest.setPriority(PRIORITY);

            if (DISPLACEMENT > 0)
                mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

            if (EXPIRATIONTIME > 0)
                mLocationRequest.setExpirationDuration(EXPIRATIONTIME);


            if (mGoogleApiClient.isConnected()) {
                checkLocationPermission();
            } else {
                mGoogleApiClient.connect();
            }

        }

        private void checkLocationPermission() {
            if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), ACCESS_FINE_LOCATION)) {
                // We've been denied once before. Explain why we need the permission, then ask again.
                mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_PERMISSION_ERROR, "Location Permission not available."));
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            } else {
                // We've never asked. Just do it.
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            }
        }

        private void startLocationUpdates() {
            final MbLocationUtil mbLocationUtil = MbLocationUtil.with(getActivity());
            final boolean anyProviderAvailable = mbLocationUtil.isAnyProviderAvailable();
            if (anyProviderAvailable) {
                if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_PERMISSION_ERROR, "Location Permission not available."));
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                // Location Provider not available eg. GPS
                // mbLocationListener.onError(new MbLocationError(MbLocationUtil.LOCATION_PROVIDER_ERROR, "Location provider not enabled. Please check GPS."));
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(mLocationRequest);

                builder.setAlwaysShow(true);

                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(@NonNull LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied. The client can initialize location
                                // requests here.
                                startLocationUpdates();
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the user
                                // a dialog.
//                                try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                try {
                                    startIntentSenderForResult(status.getResolution().getIntentSender(), LOCATION_SETTINGS_REQUEST, null, 0, 0, 0, null);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }

                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                });
            }


        }

        public void stopLocationUpdates() {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == REQUEST_PERMISSION_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                // We were not granted permission this time, so don't try to show the contact picker
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

}
