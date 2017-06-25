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
import de.master.kd.epic.domain.Position;
import de.master.kd.epic.interfaces.PositionService;
import de.master.kd.epic.map.MapActivity;
import de.master.kd.epic.utils.Constants;

public class PositionEditActivity extends AppCompatActivity {


    private ImageView imageView;
    private FloatingActionButton postionSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postion_edit);
        imageView = (ImageView)findViewById(R.id.imageViewCamera);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSnapShot(v);
            }
        });

        postionSave = (FloatingActionButton)findViewById(R.id.postionSave);
        postionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave(v);
            }
        });


    }

    private void doSave(View v) {
        Position p = callPersistPositionService();
        Intent intent = new Intent(PositionEditActivity.this, MapActivity.class);

       intent.putExtra(Constants.MAP.POSITION.name(),p);
        setResult(Constants.RESULT.MAP.ordinal(), intent);
        finish();
    }

    public void doSnapShot(View view){
        Intent intent_picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent_picture,Constants.RESULT.CAMERA.ordinal());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
        Bitmap bitmap = (Bitmap)extras.get("data");
        assert  null != bitmap;
        imageView.setImageBitmap(bitmap);
    }


    private Position callPersistPositionService(){
        EditText text = (EditText)findViewById(R.id.titleField);
        EditText describe = (EditText)findViewById(R.id.describeField);
        Bundle bundle = getIntent().getExtras();
        LatLng latLng = (LatLng)bundle.get(Constants.MAP.LOCATION.name());

        return PositionService.buildPositionWith(String.valueOf(text.getText()), String.valueOf(describe.getText()), latLng,Long.valueOf(imageView.getId()),null);

    }
}
