package com.magicdroid.magiclocationlib.permissions;

class PermissionWrapper implements PermissionCallback {
    private PermissionCallback callback;

    PermissionWrapper(PermissionCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onPermissionGranted(int index) {
      //  MbPermissionUtil.onCallbackReady();
        callback.onPermissionGranted(index);
    }

    @Override
    public void onPermissionDenied(int index) {
     //   MbPermissionUtil.onCallbackReady();
        callback.onPermissionDenied(index);
    }
}
