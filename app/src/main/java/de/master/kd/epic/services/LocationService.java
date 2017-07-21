package de.master.kd.epic.services;

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

import de.master.kd.epic.infomessage.AlertDialogMessageConfigurator;
import de.master.kd.epic.infomessage.InfoMessage;
import de.master.kd.epic.view.map.EpicMap;
import de.master.kd.epic.view.map.interfaces.LocationHandler;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by pentax on 26.06.17.
 */

public class LocationService {

    public static final int EPIC_LOCATION_PERMISSIONS_REQUEST = 99;
    private static final int interval_between_location_updates = 10000;
    private static final int distance_between_location_updates = 10;

    private LocationManager locationManager;
    private EpicMap epic;

    public LocationService(EpicMap epic) {
        this.epic = epic;
        locationManager = (LocationManager) epic.getSystemService(Context.LOCATION_SERVICE);
    }


    public boolean checkPermissions() {
        boolean hasPermission = ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED;

        if (!hasPermission) {
            setRequestedPermissions(epic);
        }
        return hasPermission;
    }

    public boolean isGpsEnabled() {
        return isLocationRequestAcitvated(locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
    }


    public void addLocationHandler(EpicMap epicMap, final LocationHandler locationHandler) {
        doNetworkRequest(locationHandler);
    }


    public String extractGeoCodeFromQuery(Uri uri) {
        String query = uri.getEncodedQuery();
        if (null != query && query.contains(",")) {
            String[] latLong = query.split("[^0-9 .]");
            int size = latLong.length - 1;
            StringBuilder builder = new StringBuilder();
            builder.append(latLong[size - 1]).append(",").append(latLong[size]);
            return builder.toString();
        }
        return null;
    }




    public boolean addGpsRequestHandler(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            setRequestedPermissions(epic);
            return false;
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval_between_location_updates, distance_between_location_updates, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                epic.doLocate(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(epic, "onStatusChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(epic, "onProviderEnabled", Toast.LENGTH_SHORT).show();
                epic.addLocationService();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(epic, "onProviderDisabled", Toast.LENGTH_SHORT).show();
                epic.resetLocationOnGpsDisabled();
            }
        });

        googleMap.setMyLocationEnabled(true);
        return true;
    }


    //----------------- HELPER -----------------------

    private boolean isLocationRequestAcitvated(boolean providerEnabled) {

        if (!providerEnabled) {
            InfoMessage.showAllertDialog(epic, new AlertDialogMessageConfigurator() {
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
                    epic.activateGPS();
                }

                @Override
                public void doNegativeOnClickHandling() {
                }
            });
        }
        return providerEnabled;
    }


    private void doNetworkRequest(final LocationHandler locationHandler) {
        if (ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(epic,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            setRequestedPermissions(epic);
            return;
        }


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                interval_between_location_updates, distance_between_location_updates,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        locationHandler.processEvent(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Toast.makeText(epic, "onStatusChanged", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                        Toast.makeText(epic, "onProviderEnabled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        epic.resetLocationOnGpsDisabled();
                        Toast.makeText(epic, "onProviderDisabled", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setRequestedPermissions(EpicMap activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE},
                EPIC_LOCATION_PERMISSIONS_REQUEST);
    }


}
