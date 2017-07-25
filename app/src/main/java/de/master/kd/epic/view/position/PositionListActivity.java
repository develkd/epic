package de.master.kd.epic.view.position;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.master.kd.epic.R;
import de.master.kd.epic.domain.interfaces.PositionService;
import de.master.kd.epic.domain.position.Position;

public class PositionListActivity extends AppCompatActivity {

    private PositionService positionService;
    private ListView customList;
    private Position selectedPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_list);
        positionService = new PositionService(this);
        customList = (ListView) findViewById(R.id.custom_list);
        PositionItemAdatper itemAdatper = new PositionItemAdatper(this,positionService.getPositions());
        customList.setAdapter(itemAdatper);
        customList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customList.getAdapter().getItem(position);
            }
        });

    }
}
