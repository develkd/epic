package de.master.kd.epic.domain.position;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by pentax on 28.06.17.
 */

public interface PositionCRUD {

    Position save(Position position);

    Position update(Position p);

    void delete(Position p);
    void delete(LatLng latLng);
    Position findByTitle(String title);
    Position findById(Long id);
    List<Position> findByLocation(LatLng latLng);

    List<Position> getAllPositions();

}
