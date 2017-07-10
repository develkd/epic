package de.master.kd.epic.map.interfaces;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import de.master.kd.epic.infomessage.AlertDialogMessageConfigurator;
import de.master.kd.epic.infomessage.InfoMessage;
import de.master.kd.epic.map.EpicMap;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by pentax on 26.06.17.
 */

public class LocationService {
    public static final int EPIC_LOCATION_PERMISSIONS_REQUEST = 99;
    private LocationManager locationManager;


//    public boolean isGpsEnabled() {
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//    }


    public Boolean checkLocationPermission(EpicMap activity) {
        if (!hasAccessToFineLocationOnSelf(activity) && !hasAccessToCoarseLocation(activity)) {
            setRequestedPermissions(activity);
            return false;
        } else {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && !hasAccessToCoarseLocation(activity)) {


                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                setRequestedPermissions(activity);
                return false;
            }
          //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  activity);
        }
        return true;

    }


    private void setRequestedPermissions(EpicMap activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                EPIC_LOCATION_PERMISSIONS_REQUEST);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasAccessToFineLocationOnSelf(FragmentActivity activity){
        return PERMISSION_GRANTED == activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasAccessToFineLocationOnAcitity(FragmentActivity activity){
        return PERMISSION_GRANTED == activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasAccessToCoarseLocation(FragmentActivity activity){
        return PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION);
    }


    public  void createLocationManager(final EpicMap epicMap, final LocationHandler locationHandler) {
        locationManager = (LocationManager) epicMap.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(epicMap,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epicMap,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationHandler.processLocationEvent(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Toast.makeText(epicMap, "onStatusChanged", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Toast.makeText(epicMap, "onProviderEnabled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(epicMap, "onProviderDisabled", Toast.LENGTH_SHORT).show();
                }
            });
        }else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationHandler.processLocationEvent(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Toast.makeText(epicMap, "onStatusChanged", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Toast.makeText(epicMap, "onProviderEnabled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(epicMap, "onProviderDisabled", Toast.LENGTH_SHORT).show();
                    epicMap.disableGPS();
                }
            });
        }
    }
}
