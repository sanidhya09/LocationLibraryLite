package com.magicdroid.magiclocation;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.magicdroid.magiclocationlib.MbLocationError;
import com.magicdroid.magiclocationlib.MbLocationListener;
import com.magicdroid.magiclocationlib.MbLocationUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocation();
    }

    private void getLocation() {
//        final MbLocationService mbLocationService = MbLocationService.mbLocationServices(this);
//
//        // Optional Params
//        mbLocationService.setFastestInterval(1000 * 5);
//        mbLocationService.setInterval(1000 * 10);
//        mbLocationService.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mbLocationService.setOneFix(true);
//        // mbLocationService.setDisplacement(15); // Default is OFF : 0
//
//        mbLocationService.executeService(new MbLocationListener() {
//            @Override
//            public void onLocationUpdate(Location location) {
//                Toast.makeText(MainActivity.this, "Latitude=" + location.getLatitude() + ", Longitude=" + location.getLongitude(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onError(MbLocationError mbLocationError) {
//                Toast.makeText(MainActivity.this, "Error=" + mbLocationError.message, Toast.LENGTH_LONG).show();
//                switch (mbLocationError.errorCode) {
//                    case MbLocationUtil.LOCATION_PLAY_SERVICE_ERROR:
//                        // Do your project specific stuff here..
//                        break;
//                    case MbLocationUtil.LOCATION_PROVIDER_ERROR:
//                        // Do your project specific stuff here..
//                        break;
//                }
//            }
//        });

        MbLocationServices mbLocationServices = MbLocationServices.with(this);

        // Optional Params
        mbLocationServices.setFastestInterval(1000 * 5);
        mbLocationServices.setInterval(1000 * 10);
        mbLocationServices.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mbLocationServices.setOneFix(true);

        mbLocationServices.init(new MbLocationListener() {
            @Override
            public void onLocationUpdate(Location location) {
                Toast.makeText(MainActivity.this, "Latitude=" + location.getLatitude() + ", Longitude=" + location.getLongitude(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(MbLocationError errorCode) {
                Toast.makeText(MainActivity.this, "Error=" + errorCode.message, Toast.LENGTH_LONG).show();
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

}
