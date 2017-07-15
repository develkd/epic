package de.master.kd.epic.domain.interfaces;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.master.kd.epic.domain.EpicDatabase;
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.domain.position.PositionCRUD;
import de.master.kd.epic.domain.position.PositionTabel;
import de.master.kd.epic.utils.Converter;

/**
 * Created by pentax on 28.06.17.
 */

public class PositionRepository  implements PositionCRUD{
    private EpicDatabase em;

    public PositionRepository(Context context){
        em = new EpicDatabase(context);
    }


    @Override
    public Position save(Position position) {

        SQLiteDatabase db = em.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PositionTabel.TITLE, position.getTitle());
        values.put(PositionTabel.DESCRIPTION, position.getDescription());
        values.put(PositionTabel.LATITUDE,  Converter.toString(position.getLatitude()));
        values.put(PositionTabel.LONGITUDE,  Converter.toString(position.getLongitude()));
        values.put(PositionTabel.MAP_PATH, position.getPathMap());
        values.put(PositionTabel.PICTURE_PATH, position.getPathPicture());
        values.put(PositionTabel.CREATE_DATE, Converter.toString(new Date()));

        long id = db.insertWithOnConflict(PositionTabel.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        position.setId(Long.valueOf(id));
        return position;
    }

    @Override
    public void update(Position p) {
        updateOrDelete(false, p);
    }

    @Override
    public void delete(Position p) {
        SQLiteDatabase db = em.getWritableDatabase();
        long id = p.getId();
        System.out.println("Position deleted with id: " + id);
        db.delete(PositionTabel.TABLE, PositionTabel._ID + " = " + id, null);
    }

    @Override
    public Position find(String title) {
        return findInList(null, title, null);
    }

    @Override
    public Position find(Long id) {
        return findInList(id, null, null);
    }

    @Override
    public Position find(LatLng latLng) {
        return findInList(null, null, latLng);
    }


    @Override
    public List<Position> getAllPositions() {
        SQLiteDatabase db = em.getReadableDatabase();
        List<Position> positions = new ArrayList<>();

        Cursor cursor = db.query(PositionTabel.TABLE,
                PositionTabel.ALL_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Position position = createPosition(cursor);
            positions.add(position);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return positions;
    }


    private void updateOrDelete(boolean delete, Position search) {

    }

    private void removeFromList(int index) {

    }

    private Position findInList(Long id, String text, LatLng latLng) {

        return null;
    }


    private Position createPosition(Cursor cursor){
        Position p = new Position();

        p.setId(cursor.getLong(cursor.getColumnIndex(PositionTabel._ID)));
        p.setTitle(cursor.getString(cursor.getColumnIndex(PositionTabel.TITLE)));
        p.setDescription(cursor.getString(cursor.getColumnIndex(PositionTabel.DESCRIPTION)));
        p.setLatitude(Converter.toDouble(cursor.getString(cursor.getColumnIndex(PositionTabel.LATITUDE))));
        p.setLongitude(Converter.toDouble(cursor.getString(cursor.getColumnIndex(PositionTabel.LONGITUDE))));
        p.setPathMap(cursor.getString(cursor.getColumnIndex(PositionTabel.MAP_PATH)));
        p.setPathPicture(cursor.getString(cursor.getColumnIndex(PositionTabel.PICTURE_PATH)));
        p.setCreateDate(Converter.toDate(cursor.getString(cursor.getColumnIndex(PositionTabel.CREATE_DATE))));
        p.setCreateDate(Converter.toDate(cursor.getString(cursor.getColumnIndex(PositionTabel.UPDATE_DATE))));


        return p;
    }

}
