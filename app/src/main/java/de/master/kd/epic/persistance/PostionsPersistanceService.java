package de.master.kd.epic.persistance;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import de.master.kd.epic.domain.Position;
import de.master.kd.epic.exceptions.ElementNotFoundException;

/**
 * Created by pentax on 25.06.17.
 */

public class PostionsPersistanceService implements PositionCRUD {

//    public static final boolean ENCRYPTED = true;
//
//    private DaoSession daoSession;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,ENCRYPTED ? "notes-db-encrypted" : "notes-db");
//    Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
//    daoSession = new DaoMaster(db).newSession();
//    }
//
//    public DaoSession getDaoSession() {
//        return daoSession;
//    }

    private ArrayList<Position> positions = new ArrayList<>();
    @Override
    public ArrayList<Position> getAllPositions() {
        return positions;
    }

    @Override
    public Position find(Position position) throws ElementNotFoundException{
        for (Position p: positions) {
            if(p.getId().longValue() == position.getId().longValue()){
                return position;
            }
        }
        throw new ElementNotFoundException("Position konnte leider nicht gefunden werden");
    }

    @Override
    public void save(Position position) {
        positions.add(position);
    }

    @Override
    public void update(Position position) {
        try {
            Position oldPos = find(position);

            int idxPos =  positions.indexOf(oldPos);
            positions.add(idxPos,position);
            positions.remove(oldPos);

        } catch (ElementNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void delete(Position position) {
        positions.remove(position);
    }
}
