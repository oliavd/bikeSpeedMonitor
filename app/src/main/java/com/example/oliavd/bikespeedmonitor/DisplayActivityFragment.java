package com.example.oliavd.bikespeedmonitor;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.module.Led;

/**
 * A placeholder fragment containing a simple view.
 */

public class DisplayActivityFragment extends Fragment implements ServiceConnection {
    public interface FragmentSettings {
        BluetoothDevice getBtDevice();
    }

    private MetaWearBoard metawear = null;
    private FragmentSettings settings;

    public DisplayActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(
                "onCreate","TEST STRING"
        );
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

        return inflater.inflate(R.layout.fragment_display_activity, container, false);
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        Log.i(
//                "onViewCreated","View Created"
//        );
//        super.onViewCreated(view, savedInstanceState);


//        ((Switch) view.findViewById(R.id.led_ctrl)).setOnCheckedChangeListener((buttonView, isChecked) -> {
//            Led led= metawear.getModule(Led.class);
//            if (isChecked) {
//                Log.i(
//                        "onViewCreated","isChecked"
//                );
//                led.editPattern(Led.Color.BLUE, Led.PatternPreset.PULSE)
//                        .repeatCount(Led.PATTERN_REPEAT_INDEFINITELY)
//                        .commit();
//                led.play();
//            } else {
//                led.stop(true);
//            }
//        });
//    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        metawear = ((BtleService.LocalBinder) service).getMetaWearBoard(settings.getBtDevice());
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