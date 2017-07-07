package de.master.kd.epic.domain.interfaces;

import java.util.ArrayList;

import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.domain.position.PositionCRUD;

/**
 * Created by pentax on 28.06.17.
 */

public class PersistanceService  implements PositionCRUD{


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
    public ArrayList<Position> getAllPositions() {
        return null;
    }
}
