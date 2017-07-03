package de.master.kd.epic.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import de.master.kd.epic.map.interfaces.LocationService;
import de.master.kd.epic.map.interfaces.PictureService;
import de.master.kd.epic.position.PositionEditActivity;
import de.master.kd.epic.utils.Constants;
import de.master.kd.epic.utils.Converter;
import de.master.kd.epic.utils.LatLngHolder;
import de.master.kd.epic.utils.StringUtils;

/**
 * Created by pentax on 28.06.17.
 */

public class EpicMap extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap googleMap;
    private Marker customMarker;
    private LatLng markerLatLng;
    private LatLngHolder holder = new LatLngHolder();
    private LatLngHolder dummyHolder;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng location;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        markerLatLng = holder.next();

        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.epic_map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.select_point);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markPosition(view);
            }
        });
    }


    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        setUpMap();
        checkGpsStatus();
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
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

    private void checkGpsStatus() {
        LocationService locationService = new LocationService();
        if (locationService.isGpsEnabled(this)) {
            Toast.makeText(this, "GPS not present. Use dummy locations", Toast.LENGTH_LONG).show();
            dummyHolder = new LatLngHolder();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (locationService.checkLocationPermission(this)) {
                Toast.makeText(this, "No permission for GPS found", Toast.LENGTH_LONG).show();
            }
        }

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
        Bitmap bitmap = (Bitmap) bundle.get(Constants.MAP.PICTURE.name());

        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        final ImageView view = (ImageView) marker.findViewById(R.id.bmp_view);



        if (null != bitmap) {
          Bitmap  b = PictureService.resizeBitmap(34,31,bitmap);
            view.setImageBitmap(b);
        }

        customMarker = googleMap.addMarker(new MarkerOptions()
                .position(Converter.toLatLang(p.getLatitude(), p.getLongitude()))
                .title(p.getTitle())
                .snippet(StringUtils.cut(p.getDescription(), 1))
                .icon(BitmapDescriptorFactory.fromBitmap(PictureService.createBitmap(this, marker))));


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng));
        markerLatLng = holder.next();

    }

    @Override
    public void onLocationChanged(Location location) {
        //Place current location marker
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(this.location));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }

    public void share() {
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"[contenttagger] " + this.note.getTitle());
//        sendIntent.putExtra(Intent.EXTRA_TEXT, this.note.getContent() + "\n\n [sent from contenttagger@android]");
//        sendIntent.setType("text/plain");
//        startActivity(sendIntent);
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        createConnection(bundle);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        System.out.print("AUTSCH");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        System.out.print("AUTSCH");
                    }
                })
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    private void createConnection(Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

}
