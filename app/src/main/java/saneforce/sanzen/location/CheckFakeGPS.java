package saneforce.sanzen.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import saneforce.sanzen.R;


public class CheckFakeGPS {

    public  static  void CheckLocationStatus(Activity context){

        LocationManager  locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {boolean isMock = isMockLocation(context.getApplicationContext(),location);
                    if (isMock) {
                        locationManager.removeUpdates(this);
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setCancelable(false);
                        LayoutInflater inflater = context.getLayoutInflater();
                        View alertLayout = inflater.inflate(R.layout.fake_gps_alert_box, null);
                        Button btnOk = alertLayout.findViewById(R.id.BtnClose);
                        alert.setView(alertLayout);
                        AlertDialog dialog = alert.create();
                        dialog.show();
                        btnOk.setOnClickListener(v -> {
                            context.finishAffinity();
                            System.exit(0);
                            dialog.dismiss();
                        });

                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }


    }

    private static boolean isMockLocation(Context context, Location location) {
        if (location == null) {
            return false;
        }
        if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0) {
            return true;
        }
        if (location.isFromMockProvider()) {
            return true;
        }

        return false;
    }

}
