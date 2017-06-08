package com.magicdroid.magiclocationlib.permissions;

public interface PermissionCallback {

    void onPermissionGranted(int index);

    void onPermissionDenied(int index);
}
