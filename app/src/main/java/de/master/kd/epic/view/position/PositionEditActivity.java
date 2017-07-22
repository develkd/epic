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
import de.master.kd.epic.utils.StringUtils;
import de.master.kd.epic.view.map.EpicMap;
import de.master.kd.epic.services.PictureService;
import de.master.kd.epic.utils.Constants;

public class PositionEditActivity extends AppCompatActivity {


    private ImageView imageView;
    private FloatingActionButton postionSave;
    private Bitmap bitmap;
    private PositionService service;
    private Position actualPosition;
    private String imagePath;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_postion_edit);
        service = new PositionService(this);

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

    private void addPostionData() {
        actualPosition = getPositionFromIntent();

        EditText text = (EditText) findViewById(R.id.titleField);
        EditText describe = (EditText) findViewById(R.id.describeField);
        text.setText(actualPosition.getTitle());
        describe.setText(actualPosition.getDescription());
        if(!StringUtils.isEmpty(actualPosition.getPicturePath())) {
            imageView.setImageBitmap(FileHandlingService.getImageBitmap(getApplicationContext(), actualPosition.getPicturePath()));
            imageView.setRotation(90);
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
        intent.putExtra(Constants.PARAMETER.PICTURE.name(), bitmap);
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
        bitmap = (Bitmap) extras.get("data");
       if(null == bitmap) {
           return;
       }
        imagePath = FileHandlingService.getSavedImagePath(getApplicationContext(),bitmap);
        imageView.setImageBitmap(PictureService.fitBitmapIn(bitmap));
    }


    private Position callPersistPositionService() {

        LatLng pos = getLocationFromIntent();
        EditText title = (EditText) findViewById(R.id.titleField);
        EditText describe = (EditText) findViewById(R.id.describeField);
        actualPosition.setTitle(title.getText().toString());
        actualPosition.setDescription(describe.getText().toString());
        actualPosition.setLatitude(pos.latitude);
        actualPosition.setLongitude(pos.longitude);
        actualPosition.setPicturePath(imagePath);
        return service.save(actualPosition);

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
