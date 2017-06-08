package com.magicdroid.magiclocationlib.permissions;

import android.content.Context;

public class MbPermissionUtil {

    private static MbPermissionUtil instance;
    private PermissionCallback callback;

    private static MbPermissionUtil getInstance() {
        if (instance == null)
            instance = new MbPermissionUtil();
        return instance;
    }

    public static void checkPermissions(Context context, PermissionCallback callback, String[] group) {
        getInstance().setWrappedCallback(callback);
        PermissionActivity.checkGroup(context, group);
    }

    static PermissionCallback getCallback() {
        return getInstance().callback;
    }

    private void setWrappedCallback(PermissionCallback callback) {
        setCallback(wrap(callback));
    }

    private PermissionCallback wrap(PermissionCallback callback) {
        return new PermissionWrapper(callback);
    }

    private void setCallback(PermissionCallback callback) {
        this.callback = callback;
    }
}
