package com.example.oliavd.bikespeedmonitor;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mbientlab.bletoolbox.scanner.BleScannerFragment;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;

import java.util.UUID;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends AppCompatActivity implements ServiceConnection,BleScannerFragment.ScannerCommunicationBus{


    public static final int REQUEST_START_APP= 1;
    //Define Ble Binder
    private BtleService.LocalBinder myServiceBinder;

    private MetaWearBoard metawear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Bind activity to BleService
        getApplicationContext().bindService(new Intent(this,BtleService.class),this,BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ///< Unbind the service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Handle Ble device scan
        switch(requestCode) {
            case REQUEST_START_APP:
                ((BleScannerFragment) getFragmentManager().findFragmentById(R.id.scanner_fragment)).startBleScan();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    /*
    Make sure only metawear devices are found. Can be used to only return desired metawear device
     */

    @Override
    public UUID[] getFilterServiceUuids() {
        return new UUID[] {MetaWearBoard.METAWEAR_GATT_SERVICE};
    }

    /*
    Specify scan duration
     */
    @Override
    public long getScanDuration() {
        return 10000L;
    }
    /*
    Connect to the selected device and start intent for DisplayActivity
     */

    @Override
    public void onDeviceSelected(final BluetoothDevice device) {
        //Log.i("device value",device.toString());
        metawear = myServiceBinder.getMetaWearBoard(device);

        final ProgressDialog connectDialog = new ProgressDialog(this);
        connectDialog.setTitle(getString(R.string.title_connecting));
        connectDialog.setMessage(getString(R.string.message_wait));
        connectDialog.setCancelable(false);
        connectDialog.setCanceledOnTouchOutside(false);
        connectDialog.setIndeterminate(true);
        connectDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), (dialogInterface, i) -> metawear.disconnectAsync());
        connectDialog.show();

        metawear.connectAsync().continueWithTask(task -> task.isCancelled() || !task.isFaulted() ? task : reconnect(metawear))
                .continueWith(task -> {
                    if (!task.isCancelled()) {
                        runOnUiThread(connectDialog::dismiss);
                        Intent navActivityIntent = new Intent(MainActivity.this, DisplayActivity.class);
                        navActivityIntent.putExtra(DisplayActivity.EXTRA_BT_DEVICE, device);
                        startActivityForResult(navActivityIntent, REQUEST_START_APP);
                    }

                    return null;
                });
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        myServiceBinder = (BtleService.LocalBinder)service;
        Log.i("onServiceConnected","Service is connected");
    }



    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public static Task<Void> reconnect(final MetaWearBoard board) {
        return board.connectAsync().continueWithTask(task -> task.isFaulted() ? reconnect(board) : task);
    }

}
