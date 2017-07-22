package de.master.kd.epic.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pentax on 22.07.17.
 */

public class MapThumbnailService extends AsyncTask<LatLng, Void, Bitmap>{

    public MapThumbnailService(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    public Bitmap getThumbnailFor(LatLng latLng){
        return doInBackground(latLng);
    }
    @Override
    protected Bitmap doInBackground(LatLng... params) {
        return getGoogleMapThumbnail(params[0]);
    }


   private  Bitmap getGoogleMapThumbnail(LatLng latLng) {

        String location = "http://maps.google.com/maps/api/staticmap?center=" + latLng.latitude + "," + latLng.longitude + "&zoom=16&size=200x200&sensor=false";
        Bitmap bmp = null;
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        try {
            URL url = new URL(location);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            bmp = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            //NOP

        } finally {
            if (null != urlConnection) {
                urlConnection.disconnect();
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOP
                }
            }
        }
        return bmp;
    }
}
