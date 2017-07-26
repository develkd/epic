package de.master.kd.epic.services;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import de.master.kd.epic.utils.StringUtils;

/**
 * Created by pentax on 26.07.17.
 */

public  class LocationShareService  {
    private Context context;

    public LocationShareService(Context context){
        this.context = context;
    }

    public void sharePosition(LatLng latLng, String title){

        String uri = "https://maps.google.com/?q=" + latLng.latitude + "," + latLng.longitude;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getGeoCodedAdressInfo(title, latLng));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
       context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    public void doRoute(LatLng latLng){
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ latLng.latitude + "," + latLng.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
       context.startActivity(mapIntent);
    }


    private String getGeoCodedAdressInfo(String title, LatLng latLng) {
        StringBuilder builder = new StringBuilder();
        builder.append(title).append(": \n");
        Address address = getAdress(latLng);
        if (null == address) {
            return "Keine Adresse vorhanden";
        }

        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            builder.append(address.getAddressLine(i)).append("\n");
        }
        String area = address.getAdminArea();
        if (!StringUtils.isEmpty(area)) {
            builder.append(area).append("\n");
        }
        return builder.toString();

    }


    private Address getAdress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> adds = null;
        try {
            adds = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == adds || adds.isEmpty()) {
            return null;
        }
        return adds.get(0);
    }

}
