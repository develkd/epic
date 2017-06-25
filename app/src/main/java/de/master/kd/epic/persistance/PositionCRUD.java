package de.master.kd.epic.persistance;

import java.util.List;

import de.master.kd.epic.domain.Position;

/**
 * Created by pentax on 25.06.17.
 */

public interface PositionCRUD {

    List<Position> getAllPositions();
    Position findById(Long id);
    void save(Position position);
    void update(Position position);
    void delete(Long id);

}
