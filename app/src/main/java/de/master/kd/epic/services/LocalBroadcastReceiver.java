package de.master.kd.epic.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pentax on 26.07.17.
 */

public class LocalBroadcastReceiver extends BroadcastReceiver {
    private BroadcastReceiverHandler handler;
    public LocalBroadcastReceiver(BroadcastReceiverHandler handler){
        this.handler = handler;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        handler.handleIntent(intent);
    }
}
