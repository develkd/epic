package de.master.kd.epic.map.interfaces;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import de.master.kd.epic.infomessage.AlertDialogMessageConfigurator;
import de.master.kd.epic.infomessage.InfoMessage;
import de.master.kd.epic.map.EpicMap;

/**
 * Created by pentax on 09.07.17.
 */

public class GpsService {
    private EpicMap epicMap;
    private LocationService locationService;

    public GpsService(EpicMap epicMap) {
        this.epicMap = epicMap;
        locationService = new LocationService();
    }


    public boolean isGpsEnabled() {
        return checkGpsStatus(locationService.getLocationManager(epicMap)
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
    }


    private boolean checkGpsStatus(boolean enabaled) {

        if (!enabaled) {
            InfoMessage.showAllertDialog(epicMap, new AlertDialogMessageConfigurator() {
                @Override
                public String getTitle() {
                    return "GPS-Einstellungen";
                }

                @Override
                public String getMessage() {
                    return "GPS ist nicht aktive. Möchten Sie das GPS aktivieren?";
                }

                @Override
                public String getPositiveButtonName() {
                    return "Aktivieren";
                }

                @Override
                public String getNegativeButtonName() {
                    return "Abbrechen";
                }

                @Override
                public void doPostiveOnClickHandling() {
                    epicMap.activateGPS();
                }

                @Override
                public void doNegativeOnClickHandling() {
                }
            });

        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!locationService.checkLocationPermission(epicMap)) {
                Toast.makeText(epicMap, "No permission for GPS found", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
        return enabaled;
    }


}
