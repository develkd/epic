package de.master.kd.epic.view.position;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import de.master.kd.epic.R;
import de.master.kd.epic.domain.interfaces.PositionService;
import de.master.kd.epic.domain.position.Position;

public class PositionListActivity extends AppCompatActivity {

    private PositionService positionService;
    private ListView customList;
    private Position selectedPosition;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_list);
        positionService = new PositionService(this);
        customList = (ListView) findViewById(R.id.custom_list);


        PositionItemAdatper itemAdatper = new PositionItemAdatper(this, positionService.getPositions());

        customList.setAdapter(itemAdatper);
        registerForContextMenu(customList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.custom_list) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            selectedPosition = (Position) lv.getItemAtPosition(info.position);
        }

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_edit_task:

                return true;
            case R.id.item_route_task:

                return true;
            case R.id.item_share_task:

                return true;
            case R.id.item_synch_task:

                return true;
            case R.id.item_delete_task:

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
