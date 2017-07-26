package de.master.kd.epic.view.position;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import de.master.kd.epic.R;
import de.master.kd.epic.domain.interfaces.PositionService;
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.services.BroadcastReceiverHandler;
import de.master.kd.epic.services.LocationShareService;
import de.master.kd.epic.utils.Constants;

public class PositionListActivity extends AppCompatActivity  {

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
        if(null == selectedPosition){
            Toast.makeText(this, "Kein Item vorhanden oder ausgewählt", Toast.LENGTH_LONG).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.item_edit_task:
                doEdit();
                return true;
            case R.id.item_route_task:
                doRoute();
                return true;
            case R.id.item_share_task:
                doShare();
                return true;
            case R.id.item_synch_task:
                doSynch();
                return true;
            case R.id.item_delete_task:
                doDelete();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constants.RESULT.UPDATED.ordinal() == requestCode) {
           doUpdate(data);
        }
    }


    private void doEdit() {
        Intent intent = new Intent(PositionListActivity.this, PositionEditActivity.class);
        intent.putExtra(Constants.PARAMETER.POSITION.name(), selectedPosition);
        intent.putExtra(Constants.PARAMETER.POSITION_ID.name(), Constants.RESULT.UPDATED);
        startActivityForResult(intent, Constants.RESULT.UPDATED.ordinal());
    }

    private void doUpdate(Intent data) {
        doBroadcast(Constants.PARAMETER.POSITION_ID.name(), Constants.REQUEST.EDIT);
    }


    private void doRoute(){
        LatLng latLng = new LatLng(selectedPosition.getLatitude(), selectedPosition.getLongitude());
        new LocationShareService(getApplicationContext()).doRoute(latLng);
        finish();
    }


    private void doShare() {
        LatLng latLng = new LatLng(selectedPosition.getLatitude(), selectedPosition.getLongitude());
        String title = selectedPosition.getTitle();
        new LocationShareService(getApplicationContext()).sharePosition(latLng, title);
        finish();
    }

    private void doSynch(){
        Toast.makeText(this, "Funktion ist nicht freigeschalten", Toast.LENGTH_LONG).show();
    }

    private void doDelete() {
        doBroadcast(Constants.PARAMETER.POSITION_ID.name(), Constants.REQUEST.DELETE);
        selectedPosition = null;
    }

    private void doBroadcast(String key, Constants.REQUEST request) {
        Intent intent = new Intent(BroadcastReceiverHandler.class.getName());
        intent.putExtra(Constants.PARAMETER.POSITION.name(), selectedPosition);
        intent.putExtra(key,request);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }
}
