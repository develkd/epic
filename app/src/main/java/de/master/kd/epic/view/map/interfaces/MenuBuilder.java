package de.master.kd.epic.view.map.interfaces;

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

public class MenuBuilder {

    private FloatingActionButton edit_item, route_item, snych_item, trash_item, share_item;
    private Animation showMenu, hideMenu;
    private MenuItemHandler handler;

    public MenuBuilder(MenuItemHandler handler) {
        this.handler = handler;
        buildMenu();
    }

    private void buildMenu() {

        edit_item = getEditActionButton();
        trash_item = getDeleteActionButton();
        share_item = getShareActionButton();
        route_item = getRouteActionButton();
        snych_item = (FloatingActionButton) ((Activity)handler).findViewById(R.id.item_synch_task);

        showMenu = AnimationUtils.loadAnimation(handler.getContext(), R.anim.menu_open);
        hideMenu = AnimationUtils.loadAnimation(handler.getContext(), R.anim.menu_close);
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
        FloatingActionButton item = (FloatingActionButton)  ((Activity)handler).findViewById(R.id.item_edit_task);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenuVisibilty(false);
                handler.doHandleActionEvent(Constants.REQUEST.EDIT, Constants.RESULT.UPDATED);
            }
        });
        return item;
    }

    private FloatingActionButton getDeleteActionButton() {
        FloatingActionButton item = (FloatingActionButton)  ((Activity)handler).findViewById(R.id.item_delete_task);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenuVisibilty(false);
                handler.doHandleActionEvent(Constants.REQUEST.DELETE, Constants.RESULT.NO_RESULT_CHECK);
            }
        });
        return item;
    }

    private FloatingActionButton getShareActionButton() {
        FloatingActionButton item = (FloatingActionButton)  ((Activity)handler).findViewById(R.id.item_share_task);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenuVisibilty(false);
                handler.doHandleActionEvent(Constants.REQUEST.SHARE, Constants.RESULT.NO_RESULT_CHECK);
            }
        });
        return item;
    }

    private FloatingActionButton getRouteActionButton() {
        FloatingActionButton item = (FloatingActionButton)  ((Activity)handler).findViewById(R.id.item_route_task);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenuVisibilty(false);
                handler.doHandleActionEvent(Constants.REQUEST.ROUTE, Constants.RESULT.NO_RESULT_CHECK);
            }
        });
        return item;
    }
}
