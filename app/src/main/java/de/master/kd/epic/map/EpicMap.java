package de.master.kd.epic.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
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

import de.master.kd.epic.R;
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.infomessage.AlertDialogMessageConfigurator;
import de.master.kd.epic.infomessage.InfoMessage;
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

    private LatLngHolder holder = new LatLngHolder();
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng location;
    private MenuBuilder menuBuilder;
    private FloatingActionButton markerBtn;
    private  LatLng selectedMarkerLongLat;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        setUpMapIfNeeded();
        menuBuilder = new MenuBuilder();
        menuBuilder.buildMenu();
    }


    private void setUpMapIfNeeded() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.epic_map);
        mapFragment.getMapAsync(this);

        markerBtn = (FloatingActionButton) findViewById(R.id.select_point);
        markerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markPosition(view);
            }
        });

    }


    public void onMapReady(final GoogleMap map) {
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
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Projection projection = googleMap.getProjection();
                selectedMarkerLongLat = marker.getPosition();
               // Point screenPosition = projection.toScreenLocation(selectedMarkerLongLat);
                //  Toast.makeText(EpicMap.this, "screenPosition =  " + screenPosition, Toast.LENGTH_LONG).show();
                menuBuilder.showMenuItems();
                return false;
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
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    private void checkGpsStatus() {
        LocationService locationService = new LocationService(this);

        if (!locationService.isGpsEnabled()) {
            InfoMessage.showAllertDialog(this, new AlertDialogMessageConfigurator() {
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
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    EpicMap.this.startActivity(intent);
                }

                @Override
                public void doNegativeOnClickHandling() {
                    location = holder.next();
                }
            });

        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             if (!locationService.checkLocationPermission(EpicMap.this)) {
                Toast.makeText(this, "No permission for GPS found", Toast.LENGTH_LONG).show();
            }
        }

    }


    private void markPosition(View view) {
        if(null == location){
            Toast.makeText(this, "Nutze Dummywerte", Toast.LENGTH_SHORT).show();


            location = holder.next();
            Location l = new Location("noGPSLocation");
           l.setLongitude(location.longitude);
            l.setLongitude(location.longitude);
            onLocationChanged(l);
        }
        Intent position = new Intent(EpicMap.this, PositionEditActivity.class);
        position.putExtra(Constants.MAP.LOCATION.name(), location);
        startActivityForResult(position, Constants.RESULT.MAP_NEW.ordinal());
        //https://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) {
            return;
        }

      if(Constants.RESULT.MAP_NEW.ordinal() == resultCode){
          addNewMapMarker(data);
      }

        if(Constants.RESULT.MAP_UPDATE.ordinal() == resultCode){
            addNewMapMarker(data);
        }
    }


    private void addNewMapMarker( Intent data) {
        Bundle bundle = data.getExtras();

        Position p = (Position) bundle.get(Constants.MAP.POSITION.name());
        Bitmap bitmap = (Bitmap) bundle.get(Constants.MAP.PICTURE.name());

        final View layout = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        ImageView view = (ImageView) layout.findViewById(R.id.bmp_view);

        if (null != bitmap) {
            Bitmap b = PictureService.createMarkerIcon(bitmap);
            view.setImageBitmap(b);
        }


        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(Converter.toLatLang(p.getLatitude(), p.getLongitude()))
                .title(p.getTitle())
                .snippet(StringUtils.cut(p.getDescription(), 1))
                .icon(BitmapDescriptorFactory.fromBitmap(PictureService.createBitmap(this, layout))));


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        location = holder.next();
    }

    @Override
    public void onLocationChanged(Location aLocation) {
             //Place current location marker
        this.location = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
        //move map camera
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(this.location));
        LatLngBounds bounds = new LatLngBounds.Builder().include(location).build();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

//        //stop location updates
//        if (googleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "GPS ist aktiviert", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "GPS ist deaktiviert", Toast.LENGTH_LONG).show();
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
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//        }

    }


    private class MenuBuilder {
        private FloatingActionButton edit_item, route_item, snych_item, trash_item;
        private Animation showMenu, hideMenu;
        private boolean isOpen = false;

       // PopupMenu popupMenu;

        public void buildMenu() {

            edit_item = getEditActionButton();
            route_item = (FloatingActionButton) findViewById(R.id.item_route_task);
            snych_item = (FloatingActionButton) findViewById(R.id.item_synch_task);
            trash_item = (FloatingActionButton) findViewById(R.id.item_delete_task);

            showMenu = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menu_open);
            hideMenu = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menu_close);


//            popupMenu = new PopupMenu(EpicMap.this, layout);
//            popupMenu.getMenu().add(1,R.id.item_edit_task,1,"Edit");
//            popupMenu.getMenu().add(1,R.id.item_route_task,2,"slot2");
//            popupMenu.getMenu().add(1,R.id.item_synch_task,3,"slot3");
//            popupMenu.getMenu().add(1,R.id.item_delete_task,4,"slot34");


        }


        public void showMenuItems() {

            if (isOpen) {
                isOpen = !isOpen;
                edit_item.startAnimation(hideMenu);
                route_item.startAnimation(hideMenu);
                snych_item.startAnimation(hideMenu);
                trash_item.startAnimation(hideMenu);
//                markerBtn.startAnimation(hideMenu);
                //item_menu.startAnimation(rotateToClose);
            } else {
                 isOpen = !isOpen;
                edit_item.startAnimation(showMenu);
                route_item.startAnimation(showMenu);
                snych_item.startAnimation(showMenu);
                trash_item.startAnimation(showMenu);
                //                markerBtn.startAnimation(showMenu);
                //item_menu.startAnimation(rotadeToOpen);
            }

            //  markerBtn.setClickable(isOpen);
            edit_item.setClickable(isOpen);
            route_item.setClickable(isOpen);
            snych_item.setClickable(isOpen);
            trash_item.setClickable(isOpen);

        }

        private FloatingActionButton getEditActionButton() {
            FloatingActionButton edit_item = (FloatingActionButton) findViewById(R.id.item_edit_task);
            edit_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent position = new Intent(EpicMap.this, PositionEditActivity.class);
                    position.putExtra(Constants.MAP.LOCATION.name(), location);
                    startActivityForResult(position, Constants.RESULT.MAP_UPDATE.ordinal());
                    showMenuItems();
                    //https://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/

                }
            });
            return edit_item;
        }
    }
}
