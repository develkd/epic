package de.master.kd.epic.infomessage;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by pentax on 07.07.17.
 */

public class InfoMessage {
    private InfoMessage(){

    }



    public static void showAllertDialog(Activity activity, final AlertDialogMessageConfigurator configurator){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(configurator.getTitle());
        dialog.setMessage(configurator.getMessage());

        dialog.setPositiveButton(configurator.getPositiveButtonName(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                configurator.doPostiveOnClickHandling();
            }
        });

        dialog.setNegativeButton(configurator.getNegativeButtonName(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                configurator.doNegativeOnClickHandling();
            }
        });

        dialog.show();
    }
}
