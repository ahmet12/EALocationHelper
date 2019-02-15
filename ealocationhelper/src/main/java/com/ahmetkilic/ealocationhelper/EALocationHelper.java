package com.ahmetkilic.ealocationhelper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

/**
 * Created by Ahmet Kılıç on 15.02.2019.
 * Copyright © 2019. All rights reserved.
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */
public class EALocationHelper {
    private long updateInterval = 10 * 1000;  /* 10 secs */
    private long fastestInterval = 1000; /* 1 sec */

    private LocationListener locationListener;

    private Context context;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private boolean permissionDenied;

    private boolean sendBroadcast;

    /**
     * Broadcast intent key.
     */
    public static final String LOCATION_BROADCAST_KEY = "ea_location_helper_broadcast";
    public static final String LOCATION_INTENT_EXTRA_KEY = "ea_location";

    public EALocationHelper(Context context) {
        initialize(context, null, true);
    }

    public EALocationHelper(Context context, LocationListener locationListener) {
        initialize(context, locationListener, false);
    }

    public EALocationHelper(Context context, LocationListener locationListener, boolean sendBroadcast) {
        initialize(context, locationListener, sendBroadcast);
    }

    private void initialize(Context context, LocationListener locationListener, boolean sendBroadcast) {
        this.context = context;
        this.locationListener = locationListener;
        this.sendBroadcast = sendBroadcast;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(updateInterval);
        mLocationRequest.setFastestInterval(fastestInterval);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    /**
     * Set listener
     *
     * @param locationListener listener
     */
    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    /**
     * Set the update interval. Default is 10 sec
     *
     * @param updateInterval interval in millis
     */
    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Set the fastest interval. Default is 1 sec
     *
     * @param fastestInterval fastest interval in millis
     */
    public void setFastestInterval(long fastestInterval) {
        this.fastestInterval = fastestInterval;
    }

    /**
     * Set to true if you want to receive broadcast for location updates
     *
     * @param sendBroadcast send broadcast
     */
    public void setSendBroadcast(boolean sendBroadcast) {
        this.sendBroadcast = sendBroadcast;
    }

    /**
     * Set permission denied true if you want to re-ask permission
     *
     * @param permissionDenied permission denied status
     */
    public void setPermissionDenied(boolean permissionDenied) {
        this.permissionDenied = permissionDenied;
    }

    /**
     * Check if permission is denied before
     *
     * @return permission denied
     */
    public boolean isPermissionDenied() {
        return permissionDenied;
    }

    /**
     * Start location updates
     */
    public void startLocationUpdates() {
        if (permissionDenied)
            return;
        if (
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(FunctionType.LOCATION_UPDATES);
        } else
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    /**
     * Get last known location from FusedLocationClient with listener or broadcast.
     */
    public void getLastLocation() {
        if (permissionDenied)
            return;
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermission(FunctionType.LAST_LOCATION);
        else
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            onLocationChanged(location);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
    }

    /**
     * Get last known location from location updates. Can return null
     *
     * @return last known location
     */
    public @Nullable
    Location getLastLocationFromUpdates() {
        return mLastLocation;
    }

    /**
     * Stop location updates
     */
    public void stopLocationUpdates() {
        if (mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void requestPermission(@FunctionType final int forFunction) {
        TedPermission
                .with(context)
                .setRationaleMessage(R.string.rationale_location_permission)
                .setRationaleConfirmText(R.string.rationale_comfirm_button)
                .setDeniedCloseButtonText(R.string.denied_close_button_text)
                .setGotoSettingButtonText(R.string.settings_button_text)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        permissionDenied = false;
                        if (forFunction == FunctionType.LOCATION_UPDATES)
                            startLocationUpdates();
                        else
                            getLastLocation();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        permissionDenied = true;
                    }
                })
                .setDeniedMessage(context.getString(R.string.error_permission_denied_location))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    private void onLocationChanged(Location location) {
        mLastLocation = location;
        if (locationListener != null)
            locationListener.onLocationChanged(mLastLocation);
        if (sendBroadcast) {
            Intent intent = new Intent(EALocationHelper.LOCATION_BROADCAST_KEY);
            intent.putExtra(EALocationHelper.LOCATION_INTENT_EXTRA_KEY, location);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }
}

