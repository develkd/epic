package de.master.kd.epic.domain.interfaces;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;


import java.util.Date;
import java.util.List;

import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.utils.Converter;

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

    public void remove(Position position) {
        repro.delete(position);
    }

    public Position findPositionBy(LatLng latLng) {
        List<Position> ps =  repro.findByLocation(latLng);
       return ps.isEmpty() ? new Position() : ps.get(0);
    }

    public Position findPositionBy(String geoCode) {
        String [] geo = geoCode.split(",");
        String lat = geo[0];
        String log = geo[1];
        List<Position> ps =  repro.findByLocation(lat,log);

        return ps.isEmpty() ? getCreatedPositionWith(lat, log) : ps.get(0);
    }

    private Position getCreatedPositionWith(String latidue, String longitude){
        Position p = new Position();
        p.setTitle("Incoming");
        p.setLatitude(Converter.toDouble(latidue));
        p.setLongitude(Converter.toDouble(longitude));
        p.setCreateDate(new Date());
        return  save(p);
    }
}
