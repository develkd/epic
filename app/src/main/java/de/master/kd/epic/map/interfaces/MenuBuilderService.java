package de.master.kd.epic.map.interfaces;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import de.master.kd.epic.R;
import de.master.kd.epic.utils.Constants;

/**
 * Created by pentax on 07.07.17.
 */

public class MenuBuilderService {

    private FloatingActionButton edit_item, route_item, snych_item, trash_item, share_item;
    private Animation showMenu, hideMenu;
    private MenuItemHandler handler;
    private Activity activity;
    // PopupMenu popupMenu;

    public MenuBuilderService(MenuItemHandler handler) {
        this.handler = handler;
        this.activity = handler.getImplementer();
        buildMenu();
    }

    private void buildMenu() {

        edit_item = getEditActionButton();
        trash_item = getDeleteActionButton();
        share_item = getShareActionButton();
        route_item = (FloatingActionButton) activity.findViewById(R.id.item_route_task);
        snych_item = (FloatingActionButton) activity.findViewById(R.id.item_synch_task);

        showMenu = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.menu_open);
        hideMenu = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.menu_close);

//            popupMenu = new PopupMenu(EpicMap.this, layout);
//            popupMenu.getMenu().add(1,R.id.item_edit_task,1,"Edit");
//            popupMenu.getMenu().add(1,R.id.item_route_task,2,"slot2");
//            popupMenu.getMenu().add(1,R.id.item_synch_task,3,"slot3");
//            popupMenu.getMenu().add(1,R.id.item_delete_task,4,"slot34");

    }

    public void toggleMenuVisibilty(boolean show) {
        animateMenuItems(show);
        enableClickEvent(show);
    }

    private void animateMenuItems(boolean show) {
        Animation anim = show ? showMenu : hideMenu;

        edit_item.startAnimation(anim);
        route_item.startAnimation(anim);
        snych_item.startAnimation(anim);
        trash_item.startAnimation(anim);
        share_item.startAnimation(anim);

    }

    private void enableClickEvent(boolean enable) {
        edit_item.setClickable(enable);
        route_item.setClickable(enable);
        snych_item.setClickable(enable);
        trash_item.setClickable(enable);
        share_item.setClickable(enable);
    }

    private FloatingActionButton getEditActionButton() {
        FloatingActionButton edit_item = (FloatingActionButton) activity.findViewById(R.id.item_edit_task);
        edit_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenuVisibilty(false);
                handler.doHandleActionEvent(Constants.REQUEST.EDIT, Constants.RESULT.UPDATED);
            }
        });
        return edit_item;
    }

    private FloatingActionButton getDeleteActionButton() {
        FloatingActionButton edit_item = (FloatingActionButton) activity.findViewById(R.id.item_delete_task);
        edit_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenuVisibilty(false);
                handler.doHandleActionEvent(Constants.REQUEST.DELETE, Constants.RESULT.NO_RESULT_CHECK);
            }
        });
        return edit_item;
    }

    private FloatingActionButton getShareActionButton() {
        FloatingActionButton edit_item = (FloatingActionButton) activity.findViewById(R.id.item_share_task);
        edit_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenuVisibilty(false);
                handler.doHandleActionEvent(Constants.REQUEST.SHARE, Constants.RESULT.NO_RESULT_CHECK);
            }
        });
        return edit_item;
    }

}
