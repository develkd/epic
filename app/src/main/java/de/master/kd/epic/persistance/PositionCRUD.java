package de.master.kd.epic.persistance;

import java.util.List;

import de.master.kd.epic.domain.Position;
import de.master.kd.epic.exceptions.ElementNotFoundException;

/**
 * Created by pentax on 25.06.17.
 */

public interface PositionCRUD {

    List<Position> getAllPositions();
    Position find(Position position) throws ElementNotFoundException;
    void save(Position position);
    void update(Position position);
    void delete(Position position);

}
