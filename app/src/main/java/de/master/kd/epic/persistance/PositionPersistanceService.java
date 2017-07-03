package de.master.kd.epic.persistance;

import android.content.Context;

import java.util.List;

import de.master.kd.epic.domain.EpicDatabase;
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.domain.position.PositionCRUD;
import de.master.kd.epic.exceptions.ElementNotFoundException;

/**
 * Created by kemal.doenmez on 28.06.17.
 */

public class PositionPersistanceService implements PositionCRUD {


    @Override
    public Position save(Position position) {
        return null;
    }

    @Override
    public void update(Position p) {

    }

    @Override
    public void delete(Position p) {

    }

    @Override
    public Position read(String title) {
        return null;
    }

    @Override
    public List<Position> getAllPositions() {
        return null;
    }
}
