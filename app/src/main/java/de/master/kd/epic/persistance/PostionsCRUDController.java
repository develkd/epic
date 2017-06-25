package de.master.kd.epic.persistance;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import de.master.kd.epic.domain.Position;

/**
 * Created by pentax on 25.06.17.
 */

public class PostionsCRUDController implements PositionCRUD {

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


    @Override
    public List<Position> getAllPositions() {
        return null;
    }

    @Override
    public Position findById(Long id) {
        return null;
    }

    @Override
    public void save(Position position) {

    }

    @Override
    public void update(Position position) {

    }

    @Override
    public void delete(Long id) {

    }
}
