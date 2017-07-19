package de.master.kd.epic.view.map.interfaces;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import de.master.kd.epic.view.map.EpicMap;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by pentax on 26.06.17.
 */

public class LocationService {
    public static final  LocationService INSTANCE = new LocationService();

    public static final int EPIC_LOCATION_PERMISSIONS_REQUEST = 99;
    private LocationManager locationManager;
    private EpicMap epicMap;

    private LocationService(){

    }

    public EpicMap getActivity(){
        return epicMap;
    }

    public LocationManager getLocationManager(EpicMap epicMap) {
        if (null == locationManager) {
            this.epicMap =epicMap;
            locationManager = (LocationManager) epicMap.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }



    public Boolean checkLocationPermission(EpicMap activity) {
        if (!hasAccessToFineLocationOnSelf(activity) && !hasAccessToCoarseLocation(activity)) {
            setRequestedPermissions(activity);
            return false;
        } else {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && !hasAccessToCoarseLocation(activity)) {
                setRequestedPermissions(activity);
                return false;
            }
            //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  activity);
        }
        return true;

    }


    public void checkLocationPermission(GoogleMap map, EpicMap epic) {
        if (ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setRequestedPermissions(epic);
        }
        map.setMyLocationEnabled(true);
    }

    public void addLocationHandler(final EpicMap epicMap, final LocationHandler locationHandler) {

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            doNetworkRequest(epicMap, locationHandler);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            doGpsRequest(epicMap);

        }
    }


    public String extractGeoCodeFromQuery(Uri uri) {
        String query = uri.getEncodedQuery();
        if (null != query && query.contains(",")) {
            String[] latLong = query.split("[^0-9 .]");
            int size = latLong.length-1;
            StringBuilder builder = new StringBuilder();
            builder.append(latLong[size-1]).append(",").append(latLong[size]);
            return builder.toString();
        }
        return null;
    }

    //----------------- HELPER -----------------------
    private void doGpsRequest(final EpicMap epicMap) {
        if (ActivityCompat.checkSelfPermission(epicMap,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epicMap,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                epicMap.doLocate(location);
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
                epicMap.resetLocationOnGpsDisabled();
            }
        });
    }


    private void doNetworkRequest(final EpicMap epicMap, final LocationHandler locationHandler) {
        if (ActivityCompat.checkSelfPermission(epicMap,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epicMap,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setRequestedPermissions(epicMap);
            return;
        }

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        locationHandler.processEvent(location);
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
    }

    private void setRequestedPermissions(EpicMap activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                EPIC_LOCATION_PERMISSIONS_REQUEST);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasAccessToFineLocationOnSelf(FragmentActivity activity) {
        return PERMISSION_GRANTED == activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasAccessToFineLocationOnAcitity(FragmentActivity activity) {
        return PERMISSION_GRANTED == activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasAccessToCoarseLocation(FragmentActivity activity) {
        return PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION);
    }


}
