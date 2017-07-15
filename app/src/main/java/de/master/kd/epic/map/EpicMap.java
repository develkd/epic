package de.master.kd.epic.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import de.master.kd.epic.R;
import de.master.kd.epic.domain.interfaces.PositionService;
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.map.interfaces.GpsService;
import de.master.kd.epic.map.interfaces.LocationHandler;
import de.master.kd.epic.map.interfaces.LocationService;
import de.master.kd.epic.map.interfaces.MenuBuilderService;
import de.master.kd.epic.map.interfaces.MenuItemHandler;
import de.master.kd.epic.map.interfaces.PictureService;
import de.master.kd.epic.position.PositionEditActivity;
import de.master.kd.epic.utils.Constants;
import de.master.kd.epic.utils.Converter;
import de.master.kd.epic.utils.StringUtils;

/**
 * Created by pentax on 28.06.17.
 */

public class EpicMap extends FragmentActivity implements OnMapReadyCallback, MenuItemHandler {
    private GoogleMap googleMap;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng location;
    private MenuBuilderService menuBuilder;
    private FloatingActionButton markerBtn;
    private Marker selectedMarker;
    private LocationService locationService;
    private boolean firstEntry = true;
    private float actuallZoomLevel = 16.2f;
    private GpsService gpsService;
    private PositionService service;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.epic_map);

        mapFragment.getMapAsync(this);

        markerBtn = (FloatingActionButton) findViewById(R.id.select_point);
        markerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markPosition(view);
            }
        });
        menuBuilder = new MenuBuilderService(this);
        locationService = new LocationService();

        buildGoogleApiClient();
        gpsService = new GpsService(this);
        service = new PositionService(this);
    }


    public void onMapReady(final GoogleMap map) {
        this.googleMap = map;

        locationService.checkLocationPermission(googleMap,this);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Projection projection = googleMap.getProjection();
                selectedMarker = marker;
                menuBuilder.toggleMenuVisibilty(true);
                return false;
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (firstEntry) {
                    return;
                }
                actuallZoomLevel = googleMap.getCameraPosition().zoom;
            }
        });


        gpsService.isGpsEnabled();
        setExistingMarker();
        addLocationService();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Constants.RESULT.NEW.ordinal() == resultCode) {
            addNewMapMarker(data);
        }

        if (Constants.RESULT.UPDATED.ordinal() == resultCode) {
            updateMapMarker(data);
        }
        if (Constants.RESULT.GPS_ACTIVATED.ordinal() == requestCode) {
            addLocationService();
        }

    }


    public void activateGPS() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, Constants.RESULT.GPS_ACTIVATED.ordinal());
    }

    public void disableGPS() {
        location = null;
        firstEntry = true;
    }

    @Override
    public Activity getImplementer() {
        return this;
    }

    public void doLocate(Location aLocation) {

        location = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, actuallZoomLevel));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(actuallZoomLevel));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        firstEntry = false;
    }


    @Override
    public void doHandleActionEvent(Constants.REQUEST request, Constants.RESULT result) {
        if (null == selectedMarker) {
            Toast.makeText(this, "Kein Marker vorhanden oder ausgewählt", Toast.LENGTH_LONG).show();
            return;
        }
        switch (request) {
            case EDIT:
                handleEditRequest();
                break;

            case DELETE:
                deleteMapMarker();
                break;

            case SHARE:
                shareMapMarker();
                break;

            default:
                throw new IllegalArgumentException("Requesttype " + request + " not supported yet");
        }

    }

    //-------------------------- HELPERS -------------------------------------------------


    private void addLocationService(){
        locationService.processLocationEvent(this, new LocationHandler() {
            @Override
            public void processEvent(Location location) {
                doLocate(location);
            }
        });
    }

    private void markPosition(View view) {
        if (null == location) {
            Toast.makeText(this, "GPS ist nicht bereit", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(EpicMap.this, PositionEditActivity.class);
        intent.putExtra(Constants.PARAMETER.LOCATION.name(), location);
        intent.putExtra(Constants.PARAMETER.POSITION_ID.name(), Constants.RESULT.NEW);
        startActivityForResult(intent, Constants.RESULT.NEW.ordinal());
        //https://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/
    }


    private Position getIntendedPosition(Bundle bundle) {
        Position p = (Position) bundle.get(Constants.PARAMETER.POSITION.name());
        return p;
    }

    private View getPictureLayout(Bitmap bitmap) {

        View layout = getMarkerLayout();
        ImageView view = (ImageView) layout.findViewById(R.id.bmp_view);
        if (null != bitmap) {
            Bitmap b = PictureService.createMarkerIcon(bitmap);
            view.setImageBitmap(b);
        }
        return layout;
    }

    private View getPictureLayout(String path) {

        View layout = getMarkerLayout();
        ImageView view = (ImageView) layout.findViewById(R.id.bmp_view);
        if (null != path) {
//            Bitmap b = PictureService.createMarkerIcon(bitmap);
//            view.setImageBitmap(b);
        }
        return layout;
    }

    private View getMarkerLayout() {
        return ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
    }

    private void setExistingMarker() {
        for (Position position:service.getPositions()) {
            MarkerOptions mops = createMarkerWith(position, getPictureLayout(position.getPathPicture()));
            Marker marker = googleMap.addMarker(mops);
            marker.setTag(position);
        }
    }

    private void addNewMapMarker(Intent data) {
        if (null == data) {
            return;
        }

        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get(Constants.PARAMETER.PICTURE.name());
        Position position = getIntendedPosition(bundle);
        View layout = getPictureLayout(bitmap);
        MarkerOptions mops = createMarkerWith(position,layout);

        Marker marker = googleMap.addMarker(mops);
        marker.setTag(position);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

    }


    private  MarkerOptions createMarkerWith(Position p, View layout){
      MarkerOptions mops = new MarkerOptions()
                .position(Converter.toLatLang(p.getLatitude(), p.getLongitude()))
                .title(p.getTitle())
                .snippet(StringUtils.cut(p.getDescription(), p.getTitle().length()))
                .icon(addBitmaptToMarker(layout));

        return mops;
    }

    private BitmapDescriptor addBitmaptToMarker(View layout){
        return BitmapDescriptorFactory.fromBitmap(PictureService.createBitmap(this, layout));
    }
    private void updateMapMarker(Intent data) {
        if (null == data) {
            return;
        }

        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get(Constants.PARAMETER.PICTURE.name());
        Position p = getIntendedPosition(bundle);
        View layout = getPictureLayout(bitmap);

        selectedMarker.setTitle(p.getTitle());
        selectedMarker.setSnippet(StringUtils.cut(p.getDescription(), p.getTitle().length()));
        selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(PictureService.createBitmap(this, layout)));
        location = selectedMarker.getPosition();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
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
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }


    private void deleteMapMarker() {
        service.remove((Position) selectedMarker.getTag());
        selectedMarker.remove();
        selectedMarker = null;
    }

    private void handleEditRequest() {
        Intent intent = new Intent(EpicMap.this, PositionEditActivity.class);
        intent.putExtra(Constants.PARAMETER.POSITION.name(), (Position)selectedMarker.getTag());
        intent.putExtra(Constants.PARAMETER.POSITION_ID.name(), Constants.RESULT.UPDATED);
        startActivityForResult(intent, Constants.RESULT.UPDATED.ordinal());
    }


    private void shareMapMarker() {
        LatLng location = selectedMarker.getPosition();
        Double latitude = location.latitude;
        Double longitude = location.longitude;

//        String uri = "geo:" + latitude + ","
//                +longitude + "?q=" + latitude
//                + "," + longitude;
//        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
//                Uri.parse(uri)));
        Geocoder geocoder = new Geocoder(getApplicationContext());
        String pos = "";
        try {
            List<Address> adds = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            pos = adds.get(0).getLocality() + " " + adds.get(0).getCountryName();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String uri = "http://maps.google.com/maps?saddr=" + location.latitude + "," + location.longitude;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, selectedMarker.getTitle() + ": " + pos);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));


//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"[contenttagger] " + selectedMarker.getTitle());
//        sendIntent.putExtra(Intent.EXTRA_TEXT, selectedMarker.getPosition() + "\n\n [sent from contenttagger@android]");
//        sendIntent.setType("text/plain");
//        startActivity(sendIntent);

    }


}
