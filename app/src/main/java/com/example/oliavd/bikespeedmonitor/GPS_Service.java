package com.example.oliavd.bikespeedmonitor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by oliavd on 4/9/18.
 */


public class GPS_Service extends Service {

    public static double distance_meters;
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
        return null;
    }

    @Override
    public void onCreate(){

        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (last_loc == null){
                    last_loc = location;
                }
                distance_meters+=location.distanceTo(last_loc);
                last_loc = location;

                Intent i = new Intent("distance updates");
                i.putExtra("distance",getMiles());
                sendBroadcast(i);


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

                Intent i =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        };

        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try{

            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,locListener);

        }catch (SecurityException se){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locManager!=null){
            locManager.removeUpdates(locListener);
        }
    }

    public double getMiles(){
        return this.distance_meters/1000;

    }

    public GPS_Service(){

    }
}
