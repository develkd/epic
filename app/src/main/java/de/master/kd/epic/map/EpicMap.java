package de.master.kd.epic.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.position.PositionEditActivity;
import de.master.kd.epic.utils.Constants;
import de.master.kd.epic.utils.Converter;
import de.master.kd.epic.utils.LatLngHolder;
import de.master.kd.epic.utils.StringUtils;

/**
 * Created by pentax on 28.06.17.
 */

public class EpicMap extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private Marker customMarker;
    private LatLng markerLatLng;
    private LatLngHolder holder = new LatLngHolder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        markerLatLng = holder.next();

        setUpMapIfNeeded();
    }

    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        setUpMap();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        // Try to obtain the map from the SupportMapFragment.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.epic_map);
        mapFragment.getMapAsync(this);
        // Check if we were successful in obtaining the map.

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.select_point);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markPosition(view);
            }
        });
    }

    private void setUpMap() {


        final View mapView = getSupportFragmentManager().findFragmentById(R.id.epic_map).getView();
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
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
                }
            });
        }
    }

    private void showContextMenu(View v) {
        Toast toast = new Toast(this);
        toast.setText("showContextMenu");
        toast.show();

    }

    // Convert a view to bitmap
    public Bitmap createDrawableFromView(final Context context, final View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void markPosition(View view) {

        Intent position = new Intent(EpicMap.this, PositionEditActivity.class);


        position.putExtra(Constants.MAP.LOCATION.name(), markerLatLng);
        startActivityForResult(position, Constants.RESULT.MAP.ordinal());
        //https://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();

        Position p = (Position) bundle.get(Constants.MAP.POSITION.name());

        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        final TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
        numTxt.setLongClickable(true);


        customMarker = googleMap.addMarker(new MarkerOptions()
                .position(Converter.toLatLang(p.getLatitude(), p.getLongitude()))
                .title(p.getTitle())
                .snippet(StringUtils.cut(p.getDescription(), 1))
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker))));


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng));
        markerLatLng = holder.next();

    }

}
