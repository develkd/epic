package de.master.kd.epic.map.interfaces;

import android.app.Activity;

import de.master.kd.epic.utils.Constants;

/**
 * Created by pentax on 07.07.17.
 */

public interface  MenuItemHandler {

    Activity getImplementer();
    void doHandleActionEvent(Constants.REQUEST map, Constants.RESULT result);

}
