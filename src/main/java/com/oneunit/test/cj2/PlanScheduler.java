package com.oneunit.test.cj2;

/**
 * Created by Elena on 02/11/2015.
 */
import android.app.*;
import android.content.*;
import android.os.*;

public class PlanScheduler extends BroadcastReceiver{
    @Override
    public void onReceive (Context context, Intent intent){
        // on broadcast receive start the traffic readout service
        Intent intentP = new Intent(context, TrafficReadoutService.class);
        context.startService(intentP); // starts the automatic data collection
    }
}