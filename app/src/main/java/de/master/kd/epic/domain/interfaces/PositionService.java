package de.master.kd.epic.domain.interfaces;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;


import java.util.List;

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
        if(null == position.getId()){
            repro.save(position);
        }
        return repro.update(position);
    }



    public  List<Position> getPositions() {
        return repro.getAllPositions();
    }

    public  Position find(Position position) {
        return new Position();
    }

    public void remove(LatLng latLng) {

        repro.delete(latLng);
    }
    public void remove(Position position) {
        repro.delete(position);
    }

    public Position findPositionBy(LatLng latLng) {
        List<Position> ps =  repro.findByLocation(latLng);
       return ps.isEmpty() ? new Position() : ps.get(0);
    }
}
