package de.master.kd.epic.domain.interfaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
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

public class PositionRepository implements PositionCRUD {
    private EpicDatabase em;

    public PositionRepository(Context context) {
        em = new EpicDatabase(context);
    }


    @Override
    public Position save(Position position) {
        SQLiteDatabase db = em.getWritableDatabase();
        long id = db.insertWithOnConflict(PositionTabel.TABLE, null, createContentValues(position, true),
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        position.setId(Long.valueOf(id));
        return position;
    }

    @Override
    public Position update(Position position) {
        SQLiteDatabase db = em.getWritableDatabase();
        long id = db.updateWithOnConflict(PositionTabel.TABLE, createContentValues(position, false),
                PositionTabel._ID + " = ? ", new String[]{Converter.toString(position.getId())},
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        return findById(position.getId());

    }



    @Override
    public void delete(LatLng latLng) {
        StringBuilder builder = new StringBuilder();
        builder.append(PositionTabel.LATITUDE).append(" = ? ");
        builder.append(" AND ");
        builder.append(PositionTabel.LONGITUDE).append(" = ? ");
        SQLiteDatabase db = em.getWritableDatabase();

        System.out.println("Position deleted with location: " + latLng);
        db.delete(PositionTabel.TABLE, builder.toString(),
                new String[]{Converter.toString(latLng.latitude),
                Converter.toString(latLng.longitude)});
    }

    @Override
    public void delete(Position p) {
        SQLiteDatabase db = em.getWritableDatabase();
        System.out.println("Position deleted with id: " + p.getId());
        db.delete(PositionTabel.TABLE, PositionTabel._ID + " = ? ", new String[]{Converter.toString(p.getId())});
    }

    @Override
    public Position findByTitle(String title) {
        return null;
    }

    @Override
    public Position findById(Long id) {
        SQLiteDatabase db = em.getReadableDatabase();
        Cursor cursor = db.query(PositionTabel.TABLE,
                PositionTabel.ALL_COLUMNS, " _id = ?",
                new String[]{Converter.toString(id),
                        }, null, null, null);

        List<Position> positions = exceuteQuery(cursor);
        return positions.isEmpty() ? null : positions.get(0);
    }

    @Override
    public List<Position> findByLocation(LatLng latLng) {
        return findByLocation(Converter.toString(latLng.latitude),Converter.toString(latLng.longitude) );
    }

    public List<Position> findByLocation(String latitude, String longitude) {
        SQLiteDatabase db = em.getReadableDatabase();

        Cursor cursor = db.query(PositionTabel.TABLE,
                PositionTabel.ALL_COLUMNS, " latitude = ? AND longitude = ? ",
                new String[]{latitude,
                        longitude}, null, null, null);

        return exceuteQuery(cursor);
    }

    @Override
    public List<Position> getAllPositions() {
        SQLiteDatabase db = em.getReadableDatabase();


        Cursor cursor = db.query(PositionTabel.TABLE,
                PositionTabel.ALL_COLUMNS, null, null, null, null, null);


        return exceuteQuery(cursor);
    }


    private List<Position> exceuteQuery(Cursor cursor){
        List<Position> positions = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Position position = createPosition(cursor);
            positions.add(position);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return  positions;
    }

    private Position createPosition(Cursor cursor) {
        Position p = new Position();

        p.setId(cursor.getLong(cursor.getColumnIndex(PositionTabel._ID)));
        p.setTitle(cursor.getString(cursor.getColumnIndex(PositionTabel.TITLE)));
        p.setDescription(cursor.getString(cursor.getColumnIndex(PositionTabel.DESCRIPTION)));
        p.setLatitude(Converter.toDouble(cursor.getString(cursor.getColumnIndex(PositionTabel.LATITUDE))));
        p.setLongitude(Converter.toDouble(cursor.getString(cursor.getColumnIndex(PositionTabel.LONGITUDE))));
        p.setMapPath(cursor.getString(cursor.getColumnIndex(PositionTabel.MAP_PATH)));
        p.setPicturePath(cursor.getString(cursor.getColumnIndex(PositionTabel.PICTURE_PATH)));
        p.setCreateDate(Converter.toDate(cursor.getString(cursor.getColumnIndex(PositionTabel.CREATE_DATE))));
        p.setUpdateDate(Converter.toDate(cursor.getString(cursor.getColumnIndex(PositionTabel.UPDATE_DATE))));


        return p;
    }

    private ContentValues createContentValues(Position position, boolean create){
        ContentValues values = new ContentValues();
        values.put(PositionTabel.TITLE, position.getTitle());
        values.put(PositionTabel.DESCRIPTION, position.getDescription());
        values.put(PositionTabel.LATITUDE, Converter.toString(position.getLatitude()));
        values.put(PositionTabel.LONGITUDE, Converter.toString(position.getLongitude()));
        values.put(PositionTabel.MAP_PATH, position.getMapPath());
        values.put(PositionTabel.PICTURE_PATH, position.getPicturePath());

        if(create){
            values.put(PositionTabel.CREATE_DATE, Converter.toString(new Date()));
        }else{
            values.put(PositionTabel.UPDATE_DATE, Converter.toString(new Date()));
        }

        return  values;
    }
}
