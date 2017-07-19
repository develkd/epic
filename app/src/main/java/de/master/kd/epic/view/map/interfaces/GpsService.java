package de.master.kd.epic.view.map.interfaces;

import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;

import de.master.kd.epic.infomessage.AlertDialogMessageConfigurator;
import de.master.kd.epic.infomessage.InfoMessage;
import de.master.kd.epic.view.map.EpicMap;

/**
 * Created by pentax on 09.07.17.
 */

public class GpsService {

    public static final GpsService INSTANCE = new GpsService();
    private EpicMap epicMap;

    private  GpsService(){
    }


    public boolean isGpsEnabled(EpicMap epicMap) {
        if(null == this.epicMap){
            this.epicMap = epicMap;
        }
        return checkGpsStatus(LocationService.INSTANCE.getLocationManager(epicMap)
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
            if (!LocationService.INSTANCE.checkLocationPermission(epicMap)) {
                Toast.makeText(epicMap, "No permission for GPS found", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
        return enabaled;
    }


    public void activateGPS() {
        epicMap.addLocationService();
    }

    public void deactivateGps() {
        epicMap.resetLocationOnGpsDisabled();
    }
}
