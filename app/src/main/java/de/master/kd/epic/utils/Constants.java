package de.master.kd.epic.utils;

import android.util.EventLogTags;

/**
 * Created by pentax on 24.06.17.
 */

public interface Constants {
    public enum REQUEST {
         EDIT,
        DELETE,
        ;
    }

    public enum PARAMETER {
        POSITION,
        PICTURE,
        LOCATION,
        POSITION_ID,
        ;
    }

    public enum RESULT{
        NEW,
        UPDATE,
        DELETE,
        CAMERA,

        ;
    }
}
