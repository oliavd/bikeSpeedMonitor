package com.example.oliavd.bikespeedmonitor;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Vibrator;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mbientlab.metawear.CodeBlock;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.ForcedDataProducer;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.builder.filter.Comparison;
import com.mbientlab.metawear.builder.filter.ThresholdOutput;
import com.mbientlab.metawear.builder.function.Function1;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.Debug;
import com.mbientlab.metawear.module.Led;
import com.mbientlab.metawear.module.Logging;
import com.mbientlab.metawear.module.AccelerometerBosch;
import com.mbientlab.metawear.module.Accelerometer;

import bolts.Continuation;
import bolts.Task;
import com.mbientlab.metawear.module.GyroBmi160;
import com.mbientlab.metawear.module.SensorFusionBosch;
import com.mbientlab.metawear.data.*;
import com.mbientlab.metawear.module.Temperature;
import com.mbientlab.metawear.module.Timer;
import com.mbientlab.metawear.module.Haptic;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */

public class DisplayActivityFragment1 extends Fragment implements ServiceConnection {
    public interface FragmentSettings {
        BluetoothDevice getBtDevice();
    }

    private MetaWearBoard metawear = null;
    private FragmentSettings settings;
    private static final String TAG_ACCEL = "accelerometer";
    private SensorFusionBosch sensorfusion;
    private Temperature.Sensor tempSensor;
    private Temperature tempModule;
    private String totalTime,
    avgSpeed;
    private Timer timerModule;
    private double vx, vy, vz, vel = 0,targetSpeed=0;
    //used for movement_end_check
    private int countx, county, countz;
    private GPS_Service odometer;
    private boolean bound = false;
    private TextView distanceValue;




    public DisplayActivityFragment1() {
    }

    /*
    Create a Service Connection to bing to the GPS_Service
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GPS_Service.GPS_ServiceBinder gpsBinder = (GPS_Service.GPS_ServiceBinder) service;
            odometer = gpsBinder.getGPService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate","FragmentOnCreate");


        Activity owner= getActivity();
        if (!(owner instanceof FragmentSettings)) {
            throw new ClassCastException("Owning activity must implement the FragmentSettings interface");
        }

        settings= (FragmentSettings) owner;
        owner.getApplicationContext().bindService(new Intent(owner, BtleService.class), this, Context.BIND_AUTO_CREATE);

         /*
        Check permissions for Location Service
         */


        if(Build.VERSION.SDK_INT >=23 && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        ///< Unbind the service when the activity is destroyed
        getActivity().getApplicationContext().unbindService(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        return inflater.inflate(R.layout.fragment_display_activity1, container, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.i(
                "onViewCreated","View Created"
        );

        super.onViewCreated(view, savedInstanceState);

        /*
        TextView holding the distance value form the GPS_Service
         */
        distanceValue = (TextView) view.findViewById(R.id.distanceValueTextView);

        //create handler for .gps_service thread
        final Handler handler = new Handler();

        /*
        Handle User Input of Threshold speed value
         */

        TextView speedTarget = (TextView) view.findViewById(R.id.speedTargetValueTextView);

        speedTarget.setOnClickListener(v->{

              //getting prompt from prompt.xml view
              LayoutInflater li = LayoutInflater.from(getContext());
              View promptView = li.inflate(R.layout.prompt,null);
              //Build alert dialog
              AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

              //setting prompt.xml to alertDialog.Builder
              alertDialogBuilder.setView(promptView);
               //get UserInput
              final EditText userInput = (EditText) promptView.findViewById(R.id.editTextDialogUserInput);

              //set dialog message

              alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {


                      speedTarget.setText(userInput.getText());

                  }
              }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      dialogInterface.cancel();
                  }
              });

              //create alert dialog
              AlertDialog alertDialog = alertDialogBuilder.create();
              //show dialog
              alertDialog.show();

              targetSpeed = Double.parseDouble(speedTarget.getText().toString());



          });

        /*
        Handles when the user click the start button
         */

        view.findViewById(R.id.button_start).setOnClickListener((View v) -> {

            Led led = metawear.getModule(Led.class);

            led.editPattern(Led.Color.GREEN, Led.PatternPreset.PULSE)
                    .riseTime((short) 0)
                    .pulseDuration((short) 1000)
                    .repeatCount((byte) 5)
                    .highTime((byte) 16)
                    .lowIntensity((byte) 16)
                    .commit();
            led.play();

            //make stop button visible
            view.findViewById(R.id.stopbutton).setVisibility(View.VISIBLE);
             /*
             Bind to .GPS_Service
         */
            Intent intent = new Intent(getActivity(),GPS_Service.class);
            getActivity().bindService(intent,connection,Context.BIND_AUTO_CREATE);

            Log.i("onClickStart", "Clicked");

            //handle schedule reading from GPS_Service and update UI every 3000 ms
            handler.post(new Runnable() {
                @Override
                public void run() {
                    double distance = 0.0;
                    if (odometer!=null){
                        distance = odometer.getDistance_meters();
                    }
                    distanceValue.setText(String.format("%1$,.2f m",distance));
                    Log.i("distance",String.format("%1$,.2f m",distance));
                    handler.postDelayed(this,3000);
                }
            });

            //initialize vx,vy,vz, countx,county,countz.
            vx=0;
            vy=0;
            vz=0;
            vel=0;
            countx=0;
            county=0;
            countz=0;

            /*
            *   stream temperature with board
             */

            TextView tempVal = view.findViewById(R.id.tempValueTextView);

            //Schedule temp reading task for every x ms

           tempSensor.addRouteAsync(source -> {
               source.stream(((data, env) -> {
                   final Float celsius = data.value(Float.class);
                   Log.i("temp",celsius.toString());
                   getActivity().runOnUiThread(() -> {
                       tempVal.setText(getString(R.string.celsius,celsius.toString()));
                   });
               }));
           })

                   .onSuccessTask(ignored-> timerModule.scheduleAsync(5000,false,tempSensor::read))
                   .continueWithTask((Task<Timer.ScheduledTask> task) -> {
                       if (task.isFaulted()){
                           Log.w("tempRead","Task Failed",task.getError());
                       }else{
                           Log.i("tempRead","Ready");
                           task.getResult().start();
                       }
                       return null;
                   });

            /*
            * Setup time chronometer
            */

            Chronometer chronos = (Chronometer) view.findViewById(R.id.elapsedTimeValue);
            chronos.setBase(SystemClock.elapsedRealtime());
            chronos.start();



            TextView velTextView = view.findViewById(R.id.currentSpeedValueTextView);
            /*
            Add a route for streaming linear acceleration from board
             */

            sensorfusion.linearAcceleration().addRouteAsync(source -> {

                source.multicast()

                        .to().stream((Subscriber) (data, env) -> {

                            final Acceleration value = data.value(Acceleration.class);

                            Log.i("{x,y,z}:",
                                    value.toString());
                            Log.i("{x}", String.format("%.1f", value.x()));
                            Log.i("{y}", String.format("%.1f", value.y()));
                            Log.i("{z}", String.format("%.1f", value.z()));

                            //TODO implement algorithms to find velocity (data generated every 10 ms
                            vx+=roundAvoid(value.x(),1) * 0.01;
                            vy+=roundAvoid(value.y(),1) * 0.01;
                            vz+=roundAvoid(value.z(),1) * 0.01;

                        Log.i("{Vx}", String.format("%.1f" +
                                "", vx));
                        Log.i("{Vy}", String.format("%.1f", vy));
                        Log.i("{Vz}", String.format("%.1f", vz));

                        /*Combine speed*/
                        vel = Math.sqrt(Math.pow(vx,2)+Math.pow(vy,2)+Math.pow(vz,2));

                        Log.i("V",String.format("%.1f",vel));



                        /*
                        Movement end check: if next 25 samples are 0.0 then movement_end detected
                         */

                        if (roundAvoid(value.x(),1) == 0.0){
                            countx++;
                        }else{
                            countx=0;
                        }

                        if (countx>=25){
                            vx = 0;
                        }

                        if (roundAvoid(value.y(),1) == 0.0){
                            county++;
                        }else{
                            county=0;
                        }

                        if (countx>=25){
                            vy = 0;
                        }

                        if (roundAvoid(value.z(),1) == 0.0){
                            countz++;
                        }else{
                            countz=0;
                        }

                        if (countz>=25){
                            vz = 0;
                        }

                    //vel = vel / 0.01;

                    getActivity().runOnUiThread(() -> {
                        velTextView.setText(getString(R.string.current_speed_value,String.format("%.1f",vel)));
                    });

                    //get target speed from Speed target TextView and convert to double
                    targetSpeed = Double.parseDouble(speedTarget.getText().toString());

                    //Compare to current vel and initiate haptic motor if lower
                   if (vel < targetSpeed){

                       Log.i("test","under target speed");
                       metawear.getModule(Haptic.class).startBuzzer((short) 500);
                   }

                        });

//                        .to().stream((Subscriber) (data,env)-> {
//
//
//                                Log.i("{x}", data.value(Float.class).toString());

//
//                        }).to().split().index(1).stream(new Subscriber() {
//                            @Override
//                            public void apply(Data data, Object... env) {
//                                Log.i("{y}:", data.value(Float.class).toString());
//                            }
//                        }).to().split().index(2).stream(new Subscriber() {
//                            @Override
//                            public void apply(Data data, Object... env) {
//                                Log.i("{z}:", data.value(Float.class).toString());
//                            }
//                        });
            }).continueWith((Continuation<Route, Void>) task -> {
                sensorfusion.linearAcceleration().start();
                sensorfusion.start();
                return null;
            });



            view.findViewById(R.id.pausebutton).setOnClickListener(v2->{
                view.findViewById(R.id.stopbutton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.pausebutton).setVisibility(View.INVISIBLE);
                //TODO Set all view element appropriately if i decide to implement a pause button
            });

            /*
            Stop button clicked
             */
            view.findViewById(R.id.stopbutton).setOnClickListener(v1 -> {

                view.findViewById(R.id.stopbutton).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.button_start).setVisibility(View.VISIBLE);

                //trying to clean up
                //remove all callbacks and message from handler
                handler.removeCallbacksAndMessages(null);
                odometer.removeUpdates();
                if (bound){
                getActivity().unbindService(connection);
                bound = false;
                }


                //get Total time
                totalTime = chronos.getText().toString();

                //stop chronometer
                chronos.stop();
                chronos.setBase(SystemClock.elapsedRealtime());

                Log.i("total time:",totalTime);
                metawear.tearDown();
                /*
                Led showing that workout stopped
                 */

                led.editPattern(Led.Color.RED, Led.PatternPreset.PULSE)
                        .riseTime((short) 0)
                        .pulseDuration((short) 1000)
                        .repeatCount((byte) 5)
                        .highTime((byte) 16)
                        .lowIntensity((byte) 16)
                        .commit();
                led.play();
                /*
                Display a summary dialog box with some metrics
                 */

                //getting prompt from prompt.xml view
                LayoutInflater li = LayoutInflater.from(getContext());
                View summaryView = li.inflate(R.layout.summary_dialog,null);
                //Build alert dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                //setting summary_dialog.xml to alertDialog.Builder
                alertDialogBuilder.setView(summaryView);
                //get totalTime TextView
                final TextView totalTimeView = (TextView) summaryView.findViewById(R.id.totalTimeValue),
                targetSpeedTextView = (TextView) summaryView.findViewById(R.id.speedTargetValue), totalDistanceTextView = (TextView) summaryView.findViewById(R.id.totalDistanceValue);
                totalTimeView.setText(totalTime);
                targetSpeedTextView.setText(speedTarget.getText());
                totalDistanceTextView.setText(distanceValue.getText());
                //set dialog message

                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });

                //create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                //show dialog
                alertDialog.show();

            });

        });
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        metawear = ((BtleService.LocalBinder) service).getMetaWearBoard(settings.getBtDevice());
        /*
        Get required module from board
         */
        sensorfusion = metawear.getModule(SensorFusionBosch.class);
        sensorfusion.configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_500DPS)
                .commit();

        metawear.getModule(BarometerBosch.class).start();
        tempModule = metawear.getModule(Temperature.class);
        tempSensor = tempModule.findSensors(Temperature.SensorType.BOSCH_ENV)[0];
        timerModule = metawear.getModule(Timer.class);


    }

    /*
    Callback from requestPermissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //Toast location service enabled

                Toast.makeText(getContext(),"Location Permission Granted",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onServiceDisconnected(ComponentName name) {


    }

    /**
     * Called when the app has reconnected to the board
     */
    public void reconnected() {

    }
    /*
    Offset linear Acceleration values because no calibration present on the sensor fusion algorithm of board
     */

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

//    private void getDistance(){
//
//
//
//    }

}
