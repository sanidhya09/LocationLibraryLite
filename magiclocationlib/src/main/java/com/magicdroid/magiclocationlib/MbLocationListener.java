package com.magicdroid.magiclocationlib;

import android.location.Location;

public interface MbLocationListener {

    void onLocationUpdate(Location location);

    void onError(MbLocationError errorCode);
}