package de.master.kd.epic.persistance;

import android.app.Application;
import android.content.Context;

import java.util.List;

import de.master.kd.epic.domain.EpicDatabase;
import de.master.kd.epic.domain.Position;
import de.master.kd.epic.exceptions.ElementNotFoundException;

/**
 * Created by kemal.doenmez on 28.06.17.
 */

public class PositionPersistanceService implements PositionCRUD{


    public EpicDatabase getEntityManger(Context context){
        return new EpicDatabase(context);
    }


    @Override
    public List<Position> getAllPositions() {
        return null;
    }

    @Override
    public Position find(Position position) throws ElementNotFoundException {
        return null;
    }

    @Override
    public void save(Position position) {

    }

    @Override
    public void update(Position position) {

    }

    @Override
    public void delete(Position position) {

    }
}
