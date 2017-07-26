package de.master.kd.epic.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

/**
 * Created by pentax on 16.07.17.
 */

public class EpicMapReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            if (!((LocationManager)context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                Toast.makeText(context, "Intent Detected. OFF",
//                        Toast.LENGTH_LONG).show();
//
            }else{
//                Toast.makeText(context, "Intent Detected. ON",
//                        Toast.LENGTH_LONG).show();
            }

        }

    }
}
