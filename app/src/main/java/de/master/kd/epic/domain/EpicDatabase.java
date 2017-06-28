package de.master.kd.epic.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.master.kd.epic.domain.position.PositionTabel;

/**
 * Created by kemal.doenmez on 28.06.17.
 */

public class EpicDatabase extends SQLiteOpenHelper{


    public static final String DB_NAME="de.master.kd.epic.db";
    public static final int DB_VERSION=1;
    public static String TABLE_POSITION = PositionTabel.TABLE;



    public EpicDatabase(Context context){
        super(context,DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(PositionTabel.getTableDescription());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_POSITION);
        onCreate(db);
    }



}
