package com.example.oliavd.bikespeedmonitor;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * GPS Service uses locationManager to get distance travelled in meter
 */



public class GPS_Service extends Service {

    private final IBinder binder = new GPS_ServiceBinder();
    public static double distance_meters = 0.0;
    public static Location last_loc = null;
    private LocationManager locManager;
    private LocationListener locListener;

    public class GPS_ServiceBinder extends Binder {
        GPS_Service getGPService(){
            return GPS_Service.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate(){

        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location newlocation) {

                if (last_loc == null){
                    last_loc = newlocation;
                }
                distance_meters += newlocation.distanceTo(last_loc);
                last_loc = newlocation;


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(GPS_Service.this,"Location Service enabled...",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(GPS_Service.this,"Enable Location Services First",Toast.LENGTH_SHORT).show();
                //open location activation setting
                Intent i =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        };

        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        try{

            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,1,locListener);

        }catch (SecurityException se){
            Log.w("error",se);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public GPS_Service(){

    }

    public void removeUpdates(){
        if (locManager!=null){
            locManager.removeUpdates(locListener);
        }
    }

    public double getDistance_meters(){
        return this.distance_meters;
    }


}
