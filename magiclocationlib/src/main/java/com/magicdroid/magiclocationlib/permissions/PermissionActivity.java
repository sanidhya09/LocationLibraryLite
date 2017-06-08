package com.magicdroid.magiclocationlib.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.view.WindowManager;

public class PermissionActivity extends Activity {


    private static final String PERMISSION = "PERMISSION";

    static void checkGroup(Context context, String[] permissions) {
        if (permissions != null && permissions.length != 0) {
            Intent intent = new Intent(context, PermissionActivity.class);
            intent.putExtra(PERMISSION, permissions);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        String[] requestedPermissions = getRequestedPermissions();

        if (requestedPermissions != null)
            for (String permission : requestedPermissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                    requestPermissionsIfNeeded(PackageManager.PERMISSION_DENIED, permission);
                }
            }

    }

    private String[] getRequestedPermissions() {
        return getIntent().getExtras() != null ? getIntent().getExtras().getStringArray(PERMISSION) : null;
    }

    private void requestPermissionsIfNeeded(int result, String requestedPermissions) {
        if (result == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{requestedPermissions}, 1001);
        } else {
            onPermissionGranted(result);
        }
    }

    private void onPermissionDenied(int resultCode) {
        if (MbPermissionUtil.getCallback() != null) {
            MbPermissionUtil.getCallback().onPermissionDenied(resultCode);
        }
        finish();
    }

    private void onPermissionGranted(int resultCode) {
        if (MbPermissionUtil.getCallback() != null) {
            MbPermissionUtil.getCallback().onPermissionGranted(resultCode);
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handleGrantedPermissionResult(checkPermissionResults(grantResults));
    }

    private void handleGrantedPermissionResult(int grantResult) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted(grantResult);
        } else {
            onPermissionDenied(grantResult);
        }
    }

    private int checkPermissionResults(int[] grantResults) {
        int resultedPermission = 0;
        for (int grantPermissionCode : grantResults) {
            if (grantPermissionCode == PackageManager.PERMISSION_DENIED) {
                resultedPermission = grantPermissionCode;
                break;
            }
        }
        return resultedPermission;
    }

}
