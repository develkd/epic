package de.master.kd.epic.view.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import de.master.kd.epic.view.map.interfaces.LocationHandler;
import de.master.kd.epic.services.LocationService;
import de.master.kd.epic.view.map.interfaces.MenuBuilder;
import de.master.kd.epic.view.map.interfaces.MenuItemHandler;
import de.master.kd.epic.services.PictureService;
import de.master.kd.epic.view.position.PositionEditActivity;
import de.master.kd.epic.utils.Constants;
import de.master.kd.epic.utils.Converter;
import de.master.kd.epic.utils.StringUtils;

/**
 * Created by pentax on 28.06.17.
 */

public class EpicMap extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, MenuItemHandler {
    private GoogleMap googleMap;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng location;
    private MenuBuilder menuBuilder;
    private FloatingActionButton markerBtn;
    private Marker selectedMarker;
    private boolean firstEntry = true;
    private float actuallZoomLevel = 16.2f;
    private PositionService positionService;
    private LocationService locationService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        markerBtn = (FloatingActionButton) findViewById(R.id.select_point);
        markerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markPosition(view);
            }
        });
        menuBuilder = new MenuBuilder(this);

        positionService = new PositionService(this);
        activateMap();
    }

    public void activateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.epic_map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(final GoogleMap map) {
        this.googleMap = map;

        this.locationService = new LocationService(this);

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

        buildGoogleApiClient();

        if (locationService.checkPermissions()) {
            configLocationHandling();
        }
        setExistingMarker();

        Intent intent = getIntent();
        String action = intent.getAction();
        if ("android.intent.action.VIEW".equals(action)) {
            createMarkerForIncomingGeoData(intent);
        }
    }

    private void configLocationHandling() {
        locationService.isGpsEnabled();
        locationService.addGpsRequestHandler(googleMap);

        addLocationService();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean requestsAreGranded =true;
        for (int result :grantResults) {
            if(0 != result && requestsAreGranded) {
                requestsAreGranded = false;
            }
        }
        if(requestsAreGranded) {
            configLocationHandling();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Constants.RESULT.NEW.ordinal() == requestCode) {
            addNewMapMarker(data);
        }

        if (Constants.RESULT.UPDATED.ordinal() == requestCode) {
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

    public void resetLocationOnGpsDisabled() {
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

    public void addLocationService() {
        locationService.addLocationHandler(this, new LocationHandler() {
            @Override
            public void processEvent(Location location) {
                doLocate(location);
            }
        });
    }

    //-------------------------- HELPERS -------------------------------------------------


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
            Bitmap b = PictureService.loadImage(getApplicationContext(), path);
            if (null != b) {
                view.setImageBitmap(b);
                view.setRotation(90);
            }
        }
        return layout;
    }

    private View getMarkerLayout() {
        return ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
    }

    private void setExistingMarker() {
        for (Position position : positionService.getPositions()) {
            MarkerOptions mops = createMarkerWith(position, getPictureLayout(position.getPicturePath()));
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
        MarkerOptions mops = createMarkerWith(position, layout);

        Marker marker = googleMap.addMarker(mops);
        marker.setTag(position);
        LatLng loc = new LatLng(position.getLatitude(), position.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

    }


    private MarkerOptions createMarkerWith(Position p, View layout) {
        MarkerOptions mops = new MarkerOptions()
                .position(Converter.toLatLang(p.getLatitude(), p.getLongitude()))
                .title(p.getTitle())
                .snippet(StringUtils.cut(p.getDescription(), p.getTitle().length()))
                .icon(addBitmaptToMarker(layout));

        return mops;
    }

    private BitmapDescriptor addBitmaptToMarker(View layout) {
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
        positionService.remove((Position) selectedMarker.getTag());
        selectedMarker.remove();
        selectedMarker = null;
    }

    private void handleEditRequest() {
        Intent intent = new Intent(EpicMap.this, PositionEditActivity.class);
        intent.putExtra(Constants.PARAMETER.POSITION.name(), (Position) selectedMarker.getTag());
        intent.putExtra(Constants.PARAMETER.POSITION_ID.name(), Constants.RESULT.UPDATED);
        startActivityForResult(intent, Constants.RESULT.UPDATED.ordinal());
    }


    private void shareMapMarker() {
        LatLng latLng = selectedMarker.getPosition();

        String uri = "https://maps.google.com/?q=" + latLng.latitude + "," + latLng.longitude;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getGeoCodedAdressInfo(selectedMarker.getTitle(), latLng));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
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
        Geocoder geocoder = new Geocoder(getApplicationContext());
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


    private void createMarkerForIncomingGeoData(Intent intent) {
        String geoCode = locationService.extractGeoCodeFromQuery(intent.getData());
        if (null == geoCode) {
            return;
        }

        Position position = positionService.findPositionBy(geoCode);
        createIntentWith(position);
    }


    private void createIntentWith(Position position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.PARAMETER.POSITION.name(), position);
        addNewMapMarker(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }
}
