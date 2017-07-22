package de.master.kd.epic.view.position;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import de.master.kd.epic.R;
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.domain.interfaces.PositionService;
import de.master.kd.epic.services.FileHandlingService;
import de.master.kd.epic.services.MapThumbnailService;
import de.master.kd.epic.utils.StringUtils;
import de.master.kd.epic.view.map.EpicMap;
import de.master.kd.epic.services.PictureService;
import de.master.kd.epic.utils.Constants;

public class PositionEditActivity extends AppCompatActivity {


    private ImageView imageView;
    private ImageView mapView;
    private FloatingActionButton postionSave;
    private Bitmap pictureBitmap;
    private Bitmap mapBitmap;
    private PositionService service;
    private Position actualPosition;
    private String mapPath;
    private String picutePath;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_postion_edit);
        service = new PositionService(this);
        mapView = (ImageView)findViewById(R.id.imageViewMap);

        imageView = (ImageView) findViewById(R.id.imageViewCamera);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSnapShot(v);
            }
        });

        postionSave = (FloatingActionButton) findViewById(R.id.postionSave);
        postionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave(v);
            }
        });
        addPostionData();

    }

    private void addMapTumbnail() {
      mapBitmap =  new MapThumbnailService().getThumbnailFor(getLocationFromIntent());
        if(null !=mapBitmap){
            mapView.setImageBitmap(mapBitmap);
        }
    }

    private void addPostionData() {
        actualPosition = getPositionFromIntent();

        EditText text = (EditText) findViewById(R.id.titleField);
        EditText describe = (EditText) findViewById(R.id.describeField);
        text.setText(actualPosition.getTitle());
        describe.setText(actualPosition.getDescription());
        picutePath = actualPosition.getPicturePath();
        mapPath = actualPosition.getMapPath();
        if(!StringUtils.isEmpty(picutePath)) {
            pictureBitmap = getBitmapFor(picutePath);
            imageView.setImageBitmap(pictureBitmap);
            imageView.setRotation(90);
        }

        if(!StringUtils.isEmpty(mapPath)) {
            mapBitmap = getBitmapFor(mapPath);
            mapView.setImageBitmap(mapBitmap);
        }else{
            addMapTumbnail();
        }
    }

    private Position getPositionFromIntent() {
        Position position = new Position();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
           Constants.RESULT result = (Constants.RESULT) bundle.get(Constants.PARAMETER.POSITION_ID.name());
            if(Constants.RESULT.UPDATED == result) {

                Position pos = (Position) bundle.get(Constants.PARAMETER.POSITION.name());
                if (null != pos) {
                    position = pos;
                }
            }
        }
        return position;
    }

    private void doSave(View v) {
        Position p = callPersistPositionService();
        Intent intent = new Intent(PositionEditActivity.this, EpicMap.class);

        intent.putExtra(Constants.PARAMETER.POSITION.name(), p);
        intent.putExtra(Constants.PARAMETER.PICTURE.name(), pictureBitmap);
        setResult(getResultType().ordinal(), intent);
        finish();
    }

    private Constants.RESULT getResultType(){
        Bundle bundle = getIntent().getExtras();
        return (Constants.RESULT) bundle.get(Constants.PARAMETER.POSITION_ID.name());
    }
    public void doSnapShot(View view) {
        Intent intent_picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent_picture, Constants.RESULT.CAMERA.ordinal());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
        pictureBitmap = (Bitmap) extras.get("data");
       if(null != pictureBitmap) {
           imageView.setImageBitmap(PictureService.fitBitmapIn(pictureBitmap));
       }
    }


    private Position callPersistPositionService() {

        LatLng pos = getLocationFromIntent();
        EditText title = (EditText) findViewById(R.id.titleField);
        EditText describe = (EditText) findViewById(R.id.describeField);
        actualPosition.setTitle(title.getText().toString());
        actualPosition.setDescription(describe.getText().toString());
        actualPosition.setLatitude(pos.latitude);
        actualPosition.setLongitude(pos.longitude);
        actualPosition.setPicturePath(getPathFor("PIC_",picutePath,pictureBitmap));
        actualPosition.setMapPath(getPathFor("MAP_",mapPath,mapBitmap));
        return service.save(actualPosition);

    }

    private String getPathFor(String prefix, String path, Bitmap bitmap){
        String newPath = null;
        if(null != bitmap) {
            newPath = FileHandlingService.getSavedImagePath(getApplicationContext(), prefix, path,bitmap);
        }
        return newPath;
    }

    private Bitmap getBitmapFor(String path){
       return FileHandlingService.getImageBitmap(getApplicationContext(), path);
    }
    private LatLng getLocationFromIntent(){
        LatLng latLng = null;
                Bundle bundle = getIntent().getExtras();
        if(Constants.RESULT.NEW == (Constants.RESULT)bundle.get(Constants.PARAMETER.POSITION_ID.name())){
            latLng = (LatLng)bundle.get(Constants.PARAMETER.LOCATION.name());
        }else{
            Position pos = (Position) bundle.get(Constants.PARAMETER.POSITION.name());
            latLng = new LatLng(pos.getLatitude(), pos.getLongitude());
        }

        return latLng;
    }
}
