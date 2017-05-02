package com.magicdroid.magiclocation;

import android.location.Address;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.magicdroid.magiclocationlib.MbLocationError;
import com.magicdroid.magiclocationlib.MbLocationUtil;
import com.magicdroid.magiclocationlib.MbLocationListener;
import com.magicdroid.magiclocationlib.MbLocationService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocation();
    }

    private void getLocation() {
        final MbLocationService mbLocationService = MbLocationService.with(this);

        // Optional Params
        mbLocationService.setFastestInterval(1000 * 5);
        mbLocationService.setInterval(1000 * 10);
        mbLocationService.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mbLocationService.setOneFix(true);
       // mbLocationService.setDisplacement(15); // Default is OFF : 0

        mbLocationService.executeService(new MbLocationListener() {
            @Override
            public void onLocationUpdate(Location location) {
                Toast.makeText(MainActivity.this, "location.getLongitude()=" + location.getLongitude(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(MbLocationError mbLocationError) {
                Toast.makeText(MainActivity.this, "Error=" + mbLocationError.message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
