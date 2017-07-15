package de.master.kd.epic.domain.position;

import android.provider.BaseColumns;

import java.util.List;

/**
 * Created by kemal.doenmez on 28.06.17.
 */

public final class PositionTabel implements BaseColumns {

    public static final String TABLE = "position";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String MAP_PATH = "map_path";
    public static final String PICTURE_PATH = "picture_path";
    public static final String CREATE_DATE = "create_date";
    public static final String UPDATE_DATE = "update_date";

    public static String[] ALL_COLUMNS = {_ID, TITLE, DESCRIPTION,
            LATITUDE, LONGITUDE, MAP_PATH, PICTURE_PATH, CREATE_DATE, UPDATE_DATE};

    private PositionTabel() {

    }


    public static String getCreateTableDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ");
        builder.append(TABLE).append(" ( ");
        builder.append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        builder.append(TITLE).append(" TEXT NOT NULL, ");
        builder.append(DESCRIPTION).append(" TEXT NULL, ");
        builder.append(LATITUDE).append(" TEXT NOT NULL, ");
        builder.append(LONGITUDE).append(" TEXT NOT NULL, ");
        builder.append(MAP_PATH).append(" TEXT NULL, ");
        builder.append(PICTURE_PATH).append(" TEXT NULL, ");
        builder.append(CREATE_DATE).append(" TEXT NOT NULL, ");
        builder.append(UPDATE_DATE).append(" TEXT NULL );");

        return builder.toString();
    }

    public static String getDropTableDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ").append(TABLE);

        return builder.toString();
    }
}
