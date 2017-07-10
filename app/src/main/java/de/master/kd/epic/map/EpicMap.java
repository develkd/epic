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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import de.master.kd.epic.R;
import de.master.kd.epic.domain.interfaces.PositionService;
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.infomessage.AlertDialogMessageConfigurator;
import de.master.kd.epic.infomessage.InfoMessage;
import de.master.kd.epic.map.interfaces.GpsService;
import de.master.kd.epic.map.interfaces.LocationHandler;
import de.master.kd.epic.map.interfaces.LocationService;
import de.master.kd.epic.map.interfaces.MenuBuilderService;
import de.master.kd.epic.map.interfaces.MenuItemHandler;
import de.master.kd.epic.map.interfaces.PictureService;
import de.master.kd.epic.position.PositionEditActivity;
import de.master.kd.epic.utils.Constants;
import de.master.kd.epic.utils.Converter;
import de.master.kd.epic.utils.LatLngHolder;
import de.master.kd.epic.utils.StringUtils;

/**
 * Created by pentax on 28.06.17.
 */

public class EpicMap extends FragmentActivity implements OnMapReadyCallback, MenuItemHandler {
    private GoogleMap googleMap;

 //   private LatLngHolder holder = new LatLngHolder();
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng location;
    private MenuBuilderService menuBuilder;
    private FloatingActionButton markerBtn;
    private Marker selectedMarker;
    private boolean isLocationEventAvailable;
    private LocationService locationService;


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

    }

    private void createLocationManager() {
        locationService = new LocationService();
        locationService.createLocationManager(this, new LocationHandler() {
            @Override
            public void processLocationEvent(Location location) {
                doLocate(location);
            }
        });

    }


    public void onMapReady(final GoogleMap map) {
        checkGpsStatus();
        this.googleMap = map;


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        googleMap.setMyLocationEnabled(true);

        //  setUpMap();
        //Initialize Google Play Services
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                buildGoogleApiClient();
//                googleMap.setMyLocationEnabled(true);
//            }
//        } else {
        buildGoogleApiClient();

/* } */
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Projection projection = googleMap.getProjection();
                selectedMarker = marker;
                menuBuilder.toggleMenuVisibilty(true);
                return false;
            }
        });
    }

    private void checkGpsStatus() {
       if( new GpsService(this).isGpsEnabled()){
           createLocationManager();
       }
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
        if(Constants.RESULT.GPS_ACTIVATED.ordinal() == requestCode){
            checkGpsStatus();
        }

    }


    public void activateGPS(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, Constants.RESULT.GPS_ACTIVATED.ordinal());
    }

    public void disableGPS() {
    }

    @Override
    public Activity getImplementer() {
        return this;
    }

    public void doLocate(Location aLocation) {
        isLocationEventAvailable = true;
        location = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.2f));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16.2f));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
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

//    private void setUpMap() {
//        final View mapView = getSupportFragmentManager().findFragmentById(R.id.epic_map).getView();
//        if (mapView.getViewTreeObserver().isAlive()) {
//            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                // We check which build version we are using.
//                @Override
//                public void onGlobalLayout() {
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    } else {
//                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    }
//                }
//            });
//        }
//    }





    private void markPosition(View view) {
        if (null == location) {
            Toast.makeText(this, "GPS ist nicht bereit", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent position = new Intent(EpicMap.this, PositionEditActivity.class);
        position.putExtra(Constants.PARAMETER.LOCATION.name(), location);
        startActivityForResult(position, Constants.RESULT.NEW.ordinal());
        //https://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/
    }



    private Position getIntendedPosition(Bundle bundle){
        Position p = (Position) bundle.get(Constants.PARAMETER.POSITION.name());
        return p;
    }

    private View getPictureLayout(Bundle bundle){
        Bitmap bitmap = (Bitmap) bundle.get(Constants.PARAMETER.PICTURE.name());
        View layout = getMarkerLayout();
        ImageView view = (ImageView) layout.findViewById(R.id.bmp_view);
        if (null != bitmap) {
            Bitmap b = PictureService.createMarkerIcon(bitmap);
            view.setImageBitmap(b);
        }
        return layout;
    }

    private View getMarkerLayout() {
        return ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
    }

    private void addNewMapMarker(Intent data) {
        if(null == data){
            return;
        }

        Bundle bundle = data.getExtras();
        Position p = getIntendedPosition(bundle);
        View layout = getPictureLayout(bundle);

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(Converter.toLatLang(p.getLatitude(), p.getLongitude()))
                .title(p.getTitle())
                .snippet(StringUtils.cut(p.getDescription(), p.getTitle().length()))
                .icon(BitmapDescriptorFactory.fromBitmap(PictureService.createBitmap(this, layout))));


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
     //   setDummyLocation(true);
    }


    private void updateMapMarker(Intent data) {
        if(null == data){
            return;
        }

        Bundle bundle = data.getExtras();
        Position p = getIntendedPosition(bundle);
        View layout = getPictureLayout(bundle);

        selectedMarker.setTitle(p.getTitle());
        selectedMarker.setSnippet(StringUtils.cut(p.getDescription(), p.getTitle().length()));
        selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(PictureService.createBitmap(this, layout)));
        location = selectedMarker.getPosition();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
       // setDummyLocation(false);
    }




//    public void setDummyLocation(boolean odd) {
//        if (!isLocationEventAvailable) {
//            location = odd ? holder.next() : holder.before();
//        }
//    }


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
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//        }

    }


    private void deleteMapMarker() {

        selectedMarker.remove();
        PositionService.remove(selectedMarker.getPosition());
        selectedMarker = null;
       // setDummyLocation(false);
    }

    private void handleEditRequest() {
        Intent intent = new Intent(EpicMap.this, PositionEditActivity.class);
        intent.putExtra(Constants.PARAMETER.LOCATION.name(), selectedMarker.getPosition());
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
            List<Address> adds =  geocoder.getFromLocation(location.latitude, location.longitude, 1 );
            pos = adds.get(0).getLocality()+" "+adds.get(0).getCountryName();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String uri = "http://maps.google.com/maps?saddr=" +location.latitude+","+location.longitude;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, selectedMarker.getTitle()+": "+pos);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,  uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));



//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"[contenttagger] " + selectedMarker.getTitle());
//        sendIntent.putExtra(Intent.EXTRA_TEXT, selectedMarker.getPosition() + "\n\n [sent from contenttagger@android]");
//        sendIntent.setType("text/plain");
//        startActivity(sendIntent);

    }



}
