package de.master.kd.epic.infomessage;

/**
 * Created by pentax on 07.07.17.
 */

public interface AlertDialogMessageConfigurator {

    String getTitle();
    String getMessage();
    String getPositiveButtonName();
    String getNegativeButtonName();
    void doPostiveOnClickHandling();
    void doNegativeOnClickHandling();
}
