package de.master.kd.epic.view.map.interfaces;

import android.app.Activity;
import android.content.Context;

import de.master.kd.epic.utils.Constants;

/**
 * Created by pentax on 07.07.17.
 */

public interface MenuItemHandler {

    Context getContext();
    void doHandleActionEvent(Constants.REQUEST map, Constants.RESULT result);

}
