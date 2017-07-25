package de.master.kd.epic.view.position;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import de.master.kd.epic.R;
import de.master.kd.epic.domain.interfaces.PositionService;

public class PositionListActivity extends AppCompatActivity {

    private PositionService positionService;
    private ListView customList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_list);
        positionService = new PositionService(this);
        customList = (ListView) findViewById(R.id.custom_list);
        PositionItemAdatper itemAdatper = new PositionItemAdatper(this,positionService.getPositions());
        customList.setAdapter(itemAdatper);
    }
}
