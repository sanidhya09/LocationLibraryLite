package com.magicdroid.magiclocation;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.magicdroid.magiclocationlib.location.MbLocationError;
import com.magicdroid.magiclocationlib.location.MbLocationListener;
import com.magicdroid.magiclocationlib.location.MbLocationServices;
import com.magicdroid.magiclocationlib.location.MbLocationUtil;

public class MainActivity extends AppCompatActivity {

    private MbLocationServices mbLocationServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void getLocation(boolean oneFix, int displacement) {

        mbLocationServices = MbLocationServices.with(this);

        // Optional Params
        mbLocationServices.setFastestInterval(1000 * 5);
        mbLocationServices.setInterval(1000 * 10);
        mbLocationServices.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mbLocationServices.setOneFix(oneFix);
        if (displacement > 0)
            mbLocationServices.setDisplacement(displacement);

        mbLocationServices.init(new MbLocationListener() {
            @Override
            public void onLocationUpdate(Location location) {
                Toast.makeText(MainActivity.this, "Latitude=" + location.getLatitude() + ", Longitude=" + location.getLongitude(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(MbLocationError errorCode) {
                Toast.makeText(MainActivity.this, "ErrorCode:" + errorCode.errorCode + " ErrorMsg: " + errorCode.message, Toast.LENGTH_LONG).show();
                switch (errorCode.errorCode) {
                    case MbLocationUtil.LOCATION_PLAY_SERVICE_ERROR:
                        // Do your project specific stuff here..
                        break;
                    case MbLocationUtil.LOCATION_PROVIDER_ERROR:
                        // Do your project specific stuff here..
                        break;
                }
            }
        });
    }

    public void requestLocation(View view) {
        if (mbLocationServices != null)
            mbLocationServices.stopLocationUpdates();
        getLocation(false, 0);
    }

    public void requestLocationOneFix(View view) {
        if (mbLocationServices != null)
            mbLocationServices.stopLocationUpdates();
        getLocation(true, 0);
    }

    public void requestLocationDisplacement(View view) {
        if (mbLocationServices != null)
            mbLocationServices.stopLocationUpdates();
        getLocation(false, 25);
    }

    public void stopLocationUpdate(View view) {
        if (mbLocationServices != null)
            mbLocationServices.stopLocationUpdates();
    }
}
