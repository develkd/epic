package de.master.kd.epic.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.master.kd.epic.R;

/**
 * Created by pentax on 26.06.17.
 */

public class CustomMapMaker extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker customMarker;
    private LatLng markerLatLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        markerLatLng = new LatLng(52.475391, 13.401893);

        setUpMapIfNeeded();
    }

    public void onMapReady(GoogleMap map) {
        this.mMap = map;
        setUpMap();
    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.epic_map));
            mapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
    }

    private void setUpMap() {

        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        final TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
        numTxt.setLongClickable(true);
        numTxt.setText("27");



        customMarker = mMap.addMarker(new MarkerOptions()
                .position(markerLatLng)
                .title("Title")
                .snippet("Description")
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker))));

        marker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = new Toast(CustomMapMaker.this);
                toast.setText("LongClick");
                toast.show();

                return true;
            }
        });
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map_1).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    LatLngBounds bounds = new LatLngBounds.Builder().include(markerLatLng).build();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                }
            });
        }
    }

    private void showContextMenu(View v) {
        Toast toast = new Toast(CustomMapMaker.this);
        toast.setText("showContextMenu");
        toast.show();

    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(final Context context, final View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}
