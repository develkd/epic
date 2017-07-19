package de.master.kd.epic.view.map.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import de.master.kd.epic.view.map.interfaces.GpsService;

/**
 * Created by pentax on 16.07.17.
 */

public class EpicMapReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       ContentResolver res =  context.getContentResolver();
        int value = Settings.Secure.getInt(res,Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            if (!((LocationManager)context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(context, "Intent Detected. OFF",
                        Toast.LENGTH_LONG).show();
                GpsService.INSTANCE.deactivateGps();

            }else{
                Toast.makeText(context, "Intent Detected. ON",
                        Toast.LENGTH_LONG).show();
                GpsService.INSTANCE.activateGPS();
            }

        }

    }
}
