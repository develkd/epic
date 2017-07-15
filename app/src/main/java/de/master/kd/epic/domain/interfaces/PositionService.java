package de.master.kd.epic.domain.interfaces;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.master.kd.epic.domain.EpicDatabase;
import de.master.kd.epic.domain.position.Position;

/**
 * Created by pentax on 25.06.17.
 */

public class PositionService {
    private  final PositionRepository repro;

    public PositionService(Context context) {
        repro = new PositionRepository(context);
    }



    public  Position save(final Position position) {
        return repro.save(position);
    }



    public  List<Position> getPositions() {
        return repro.getAllPositions();
    }

    public  Position findPositionBy(LatLng latLng) {
        return new Position();
    }

    public  void remove(LatLng latLng) {

    }
}
