package saneforce.sanzen.utility.location;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.List;


public class LocationFinder {
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    static LocationEvents mlocEvents;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    public static final String TAG = "Location Finder";
    Runnable runnable = null;
    Handler handler = null;
    int running = 0;
    int timeout = 30;
    Activity mContext;

    public LocationFinder(Activity context, LocationEvents locationEvents) {
        mContext = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mlocEvents = locationEvents;
        this.running = 0;
        createLocationRequest();
        ShowLocationWarn();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    Location location = null;

    public Location getLocation() {
        location = null;
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            handler = new Handler(Looper.getMainLooper());
            runnable = new Runnable() {
                @Override
                public void run() {
                    running++;
                    if (running > timeout) {
                        mlocEvents.OnLocationRecived(null);
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                            mFusedLocationClient = null;
                        }
                        handler.removeCallbacks(runnable);
                        return;
                    }
                    handler.postDelayed(this, 1000);
                }
            };
            handler.postDelayed(runnable, 1000);
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost Location Permission." + unlikely);
        }
        return location;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                try {
                    Location location = locationList.get(locationList.size() - 1);
                    if (running < timeout) mlocEvents.OnLocationRecived(location);
                    Log.i("LC_finder", "Location: " + location.getLatitude() + " " + location.getLongitude());
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    mFusedLocationClient = null;
                    handler.removeCallbacks(runnable);
                } catch (Exception ignored) {

                }
            }
        }
    };

    public void ShowLocationWarn() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext.getApplicationContext());
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(mContext, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        getLocation();
                    }
                })
                .addOnFailureListener(mContext, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = (( ApiException ) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    Log.i(TAG, "PendingIntent INSAP.");

                                    Log.v("LOACTION_SUCCESS", "ONFAILURE");
                                    ResolvableApiException rae = ( ResolvableApiException ) e;
                                    rae.startResolutionForResult(mContext, 1000);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                //Log.e(TAG, errorMessage);
                                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
