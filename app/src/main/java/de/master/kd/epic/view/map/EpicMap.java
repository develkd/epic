package de.master.kd.epic.view.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
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

import de.master.kd.epic.R;
import de.master.kd.epic.domain.interfaces.PositionService;
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.services.LocationService;
import de.master.kd.epic.services.LocationShareService;
import de.master.kd.epic.services.PictureService;
import de.master.kd.epic.utils.Constants;
import de.master.kd.epic.utils.Converter;
import de.master.kd.epic.utils.StringUtils;
import de.master.kd.epic.view.map.interfaces.LocationHandler;
import de.master.kd.epic.view.map.interfaces.MenuBuilder;
import de.master.kd.epic.view.map.interfaces.MenuItemHandler;
import de.master.kd.epic.view.position.PositionEditActivity;
import de.master.kd.epic.view.position.PositionListActivity;

/**
 * Created by pentax on 28.06.17.
 */

public class EpicMap extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, MenuItemHandler {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.navigation,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_list_marker:
                showPostionListView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPostionListView() {
        Intent intent = new Intent(EpicMap.this, PositionListActivity.class);
        startActivity(intent);
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
    public Context getContext() {
        return getApplicationContext();
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
            case ROUTE:
                doRoute();
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
        selectedMarker.setIcon(addBitmaptToMarker(layout));
        location = selectedMarker.getPosition();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }



    private void deleteMapMarker() {
        positionService.remove((Position) selectedMarker.getTag());
        selectedMarker.remove();
        //TODO: DELETE Pic and Map, too
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
        String title = selectedMarker.getTitle();
        new LocationShareService(getApplicationContext()).sharePosition(latLng, title);
    }


   private void doRoute(){
       LatLng latLng = selectedMarker.getPosition();
       new LocationShareService(getApplicationContext()).doRoute(latLng);
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

}
