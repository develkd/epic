package de.master.kd.epic.domain;

import android.provider.BaseColumns;

/**
 * Created by kemal.doenmez on 28.06.17.
 */

 class PositionTabel implements BaseColumns {

    public static final String TABLE = "position";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String MAP_PATH = "mapPath";
    public static final String PICTURE_PATH = "picturePath";
    public static final String CREATE_DATE = "crateDate";
    public static final String UPDATE_DATE = "updateDate";


    static String getTableDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ");
        builder.append(TABLE).append(" ( ");
        builder.append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        builder.append(TITLE).append(" TEXT NOT NULL );");
        builder.append(DESCRIPTION).append(" TEXT NOT NULL );");
        builder.append(LATITUDE).append(" TEXT NOT NULL );");
        builder.append(LONGITUDE).append(" TEXT NOT NULL );");
        builder.append(MAP_PATH).append(" TEXT NOT NULL );");
        builder.append(PICTURE_PATH).append(" TEXT NOT NULL );");
        builder.append(CREATE_DATE).append(" TEXT NOT NULL );");
        builder.append(UPDATE_DATE).append(" TEXT NOT NULL );");

        return builder.toString();
    }
}
