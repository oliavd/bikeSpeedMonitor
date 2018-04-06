package com.example.oliavd.bikespeedmonitor;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Vibrator;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.mbientlab.metawear.Data;
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
    private String totalTime,
    avgSpeed, totalDistance;
    private Timer timerModule;
    private Timer.ScheduledTask scheduledTask;



    public DisplayActivityFragment1() {
    }

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


        TextView speedTarget = (TextView) view.findViewById(R.id.speedTargetValueTextView);

        speedTarget.setOnClickListener(v->{

              //getting prompt from prompt.xml view
              LayoutInflater li = LayoutInflater.from(getContext());
              View promptView = li.inflate(R.layout.prompt,null);
              //Build alert dialog
              AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

              //setting prompt.xml to alertDialg.Builder
              alertDialogBuilder.setView(promptView);
               //get UserInput
              final EditText userInput = (EditText) promptView.findViewById(R.id.editTextDialogUserInput);

              //set dialg message

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

          });


//        ((Switch) view.findViewById(R.id.led_ctrl)).setOnCheckedChangeListener((buttonView, isChecked) -> {
//           Led led= metawear.getModule(Led.class);
//            if (isChecked) {
//               Log.i(
//                       "onViewCreated","isChecked"
//                );
//              led.editPattern(Led.Color.BLUE, Led.PatternPreset.PULSE)
//                       .repeatCount(Led.PATTERN_REPEAT_INDEFINITELY)
//                      .commit();
//               led.play();
//           } else {
//               led.stop(true);
//           }
//        });

        view.findViewById(R.id.button_start).setOnClickListener(v -> {


            view.findViewById(R.id.stopbutton).setVisibility(View.VISIBLE);

            /*stream temperature with board
            *
             */

            //TODO stream temperature from temperature sensor



            /*
            * Setup time chronos*/

            Chronometer chronos = (Chronometer) view.findViewById(R.id.elapsedTimeValue);
            chronos.setBase(SystemClock.elapsedRealtime());
            chronos.start();

            Log.i("onClickStart", "Clicked");

//                accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
//
//                    @Override
//                    public void configure(RouteComponent source) {
//
//                        source.highpass((byte) 4).lowpass((byte) 4).multicast()
//
//                                .to().stream(new Subscriber() {
//                            @Override
//                            public void apply(Data data, Object... env) {
//                                Log.i(TAG_ACCEL+" {x,y,z} ",data.value(Acceleration.class).toString());
//                            }
//                        }).to().split().index(0).stream(new Subscriber() {
//
//                                    @Override
//                                    public void apply(Data data, Object... env) {
//
//                                        Log.i( TAG_ACCEL+" x: ", data.value(Float.class).toString());
//
////                                        getActivity().runOnUiThread(new Runnable() {
////                                            @Override
////                                            public void run() {
////
////                                                //accel_data.setText(data.value(Acceleration.class).toString());
////                                                accel_data.setText(String.format("%2f", data.value(Float.class)));
////                                            }
////                                        });
//
//                                    }
//                        }).to().split().index(1).stream(new Subscriber() {
//                            @Override
//                            public void apply(Data data, Object... env) {
//
//                                Log.i(TAG_ACCEL + " y: ", data.value(Float.class).toString());
//
//                            }
//
//                        }).to().split().index(2).stream(new Subscriber() {
//                            @Override
//                            public void apply(Data data, Object... env) {
//                                Log.i(TAG_ACCEL+" z: ",data.value(Float.class).toString());
//                            }
//                        });
//                    }
//
//                }).continueWith(new Continuation<Route, Void>() {
//                    @Override
//                    public Void then(Task<Route> task) throws Exception {
//                        accelerometer.acceleration().start();
//                        accelerometer.start();
//                        return null;
//                    }
//                });

            sensorfusion.linearAcceleration().addRouteAsync(source -> {

                source.multicast()

                        .to().stream((Subscriber) (data, env) -> {

                            final Acceleration value = data.value(Acceleration.class);

                            Log.i("{x,y,z}:",
                                    value.toString());
                            Log.i("{x}", String.format("%2f", value.x()));
                            Log.i("{y}", String.format("%2f", value.y()));
                            Log.i("{z}", String.format("%2f", value.z()));

                            //TODO implement algorithms to find velocity (data generated every 10 ms
                    //TODO use haptic feedback to vibrate board when velocity less than speedTarget

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


            view.findViewById(R.id.stopbutton).setOnClickListener(v1 -> {

                view.findViewById(R.id.stopbutton).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.button_start).setVisibility(View.VISIBLE);
                //get Totaltime
                totalTime = chronos.getText().toString();
                //stop chronos
                chronos.stop();
                chronos.setBase(SystemClock.elapsedRealtime());

                Log.i("total time:",totalTime);
                metawear.tearDown();
                Led led = metawear.getModule(Led.class);

                led.editPattern(Led.Color.GREEN, Led.PatternPreset.PULSE)
                        .riseTime((short) 0)
                        .pulseDuration((short) 1000)
                        .repeatCount((byte) 5)
                        .highTime((byte) 16)
                        .lowIntensity((byte) 16)
                        .commit();
                led.play();


                //getting prompt from prompt.xml view
                LayoutInflater li = LayoutInflater.from(getContext());
                View summaryView = li.inflate(R.layout.summary_dialog,null);
                //Build alert dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                //setting summary_dialog.xml to alertDialog.Builder
                alertDialogBuilder.setView(summaryView);
                //get totalTime TextView
                final TextView totalTimeView = (TextView) summaryView.findViewById(R.id.totalTimeValue),
                targetSpeedTextView = (TextView) summaryView.findViewById(R.id.speedTargetValue);
                totalTimeView.setText(totalTime);
                targetSpeedTextView.setText(speedTarget.getText());
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



        sensorfusion = metawear.getModule(SensorFusionBosch.class);
        sensorfusion.configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_500DPS)
                .commit();
    }



    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    /**
     * Called when the app has reconnected to the board
     */
    public void reconnected() {

    }
}
