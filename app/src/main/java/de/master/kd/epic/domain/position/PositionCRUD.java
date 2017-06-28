package de.master.kd.epic.domain.position;

import java.util.List;

/**
 * Created by pentax on 28.06.17.
 */

public interface PositionCRUD {

    Position save(Position position);

    void update(Position p);

    void delete(Position p);

    Position read(String title);

    List<Position> getAllPositions();

}
