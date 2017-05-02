package com.magicdroid.magiclocationlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sanidhya on 1/5/17.
 */
public class MbLocationUtil {

    public static final int LOCATION_PERMISSION_ERROR = 300;
    public static final int LOCATION_PROVIDER_ERROR = 301;
    public static final int LOCATION_PLAY_SERVICE_ERROR = 302;
    public static final int LOCATION_CONNECTION_SUSPENDED_ERROR = 303;
    public static final int LOCATION_CONNECTION_FAILED_ERROR = 304;

    public static final int LOCATION_INTERVAL = 6000 * 10;
    public static final int LOCATION_FASTEST_INTERVAL = 1000 * 10;
    public static final int LOCATION_DISPLACEMENT = 0;

    // Safe to suppress because this is always an application context
    @SuppressLint("StaticFieldLeak")
    private static volatile MbLocationUtil instance;
    private Context context;
    private LocationManager locationManager;

    private MbLocationUtil(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static MbLocationUtil with(Context context) {
        if (instance == null) {
            synchronized (MbLocationUtil.class) {
                instance = new MbLocationUtil(context.getApplicationContext());
            }
        }
        return instance;
    }

    /**
     * Indicates if location services are enabled for the device.
     *
     * @return <code>true</code> if the user has turned on location services.
     */
    public boolean locationServicesEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int locationMode = Settings.Secure.LOCATION_MODE_OFF;

            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException ignored) {
                // This is ignored
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * Indicates if any <em>active</em> location provider is enabled.
     *
     * @return <code>true</code> if an active location provider (network, GPS) is enabled.
     */
    public boolean isAnyProviderAvailable() {
        return isGpsAvailable() || isNetworkAvailable();
    }

    /**
     * Indicates if GPS location updates are enabled.
     *
     * @return <code>true</code> if GPS location updates are enabled.
     */
    public boolean isGpsAvailable() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Indicates if location updates from mobile network signals are enabled.
     *
     * @return <code>true</code> if location can be determined from mobile network signals.
     */
    public boolean isNetworkAvailable() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Indicates if the "passive" location provider is enabled.
     *
     * @return <code>true</code> if location updates from other applications are enabled.
     */
    public boolean isPassiveAvailable() {
        return locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
    }

    /**
     * Indicates if the device allows mock locations.
     *
     * @return <code>true</code> if mock locations are enabled for the entire device.
     * @deprecated use {@link android.location.Location#isFromMockProvider()} instead for Android
     * KitKat devices and higher.
     */
    @Deprecated
    public boolean isMockSettingEnabled() {
        return !("0".equals(Settings.Secure.getString(context.getContentResolver(), Settings
                .Secure.ALLOW_MOCK_LOCATION)));
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    /**
     * Gets the human readable address.
     *
     * @return human readable address.
     * requires network
     */
    public Address getAddressFromLocation(final double latitude, final double longitude) {
        Address address = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                address = addressList.get(0);
            }
        } catch (IOException e) {
            Toast.makeText(context, "GeoCoder Exception = " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Log.e("MbLocationUtil", "Unable connect to Geocoder", e);
        }
        return address;


    }

}