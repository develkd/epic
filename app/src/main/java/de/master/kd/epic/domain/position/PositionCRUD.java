package de.master.kd.epic.domain.position;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by pentax on 28.06.17.
 */

public interface PositionCRUD {

    Position save(Position position);

    void update(Position p);

    void delete(Position p);

    Position find(String title);
    Position find(Long id);
    Position find(LatLng latLng);

    List<Position> getAllPositions();

}
