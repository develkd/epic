package de.master.kd.epic.position;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import de.master.kd.epic.R;
import de.master.kd.epic.utils.Constants;

public class PositionEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postion_edit);
        Bundle bundle = getIntent().getExtras();
        LatLng position = (LatLng)bundle.get(Constants.MAP.CURRENT_LOCATION.name());


    }
}
