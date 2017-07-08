package de.master.kd.epic.position;

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
import de.master.kd.epic.map.EpicMap;
import de.master.kd.epic.map.interfaces.PictureService;
import de.master.kd.epic.utils.Constants;

public class PositionEditActivity extends AppCompatActivity {


    private ImageView imageView;
    private FloatingActionButton postionSave;
    private Bitmap bitmap;
    private Position actualPosition;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_postion_edit);

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
        if(null == actualPosition){
            return;
        }

        EditText text = (EditText) findViewById(R.id.titleField);
        EditText describe = (EditText) findViewById(R.id.describeField);
        text.setText(actualPosition.getTitle());
        describe.setText(actualPosition.getDescription());
    }

    private Position getPositionFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            LatLng latLng = (LatLng) bundle.get(Constants.PARAMETER.LOCATION.name());
            if (null != latLng) {
                return PositionService.findPositionBy(latLng);
            }
        }
        return null;
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
        return null == actualPosition ? Constants.RESULT.NEW : Constants.RESULT.UPDATED;
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
        assert null != bitmap;
        imageView.setImageBitmap(PictureService.resizeBitmap(bitmap, 100, 100));
    }


    private Position callPersistPositionService() {
        EditText text = (EditText) findViewById(R.id.titleField);
        EditText describe = (EditText) findViewById(R.id.describeField);
        Bundle bundle = getIntent().getExtras();
        LatLng latLng = (LatLng) bundle.get(Constants.PARAMETER.LOCATION.name());
        String posId = (String) bundle.get(Constants.PARAMETER.POSITION_ID.name());
        // Position actualPosition = PositionService.findPositionBy(latLng);
        return PositionService.save(actualPosition,String.valueOf(text.getText()), String.valueOf(describe.getText()), latLng, null, null);

    }
}
