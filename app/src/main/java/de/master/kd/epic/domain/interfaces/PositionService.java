package de.master.kd.epic.domain.interfaces;

import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.Date;

import de.master.kd.epic.domain.position.Position;

/**
 * Created by pentax on 25.06.17.
 */

public class PositionService {
    private static final PositionRepository service = new PositionRepository();

    private PositionService() {
    }


    public static Position save(final Position currentPosition, String title, String description, LatLng latLng, String pathPicture, String pathMap) {
        Position position = currentPosition;
        if (null == currentPosition) {
            position = new Position();
            position.setId(new Date().getTime());
        }
        position.setTitle(title);
        position.setDescription(description);
        position.setLatitude(latLng.latitude);
        position.setLongitude(latLng.longitude);
        position.setPathPicture(pathPicture);
        position.setPathMap(pathMap);

        service.save(position);
        return position;
    }


    public static ArrayList<Position> getPositions() {
        return service.getAllPositions();
    }

    public static Position findPositionBy(LatLng latLng) {
        double lon = latLng.longitude;
        double lat = latLng.latitude;

        for (Position p : service.getAllPositions()) {
            if (p.getLatitude() == lat && p.getLongitude() == lon) {
                return p;
            }
        }
        return null;
    }

    public static void remove(LatLng position) {
        Position p = findPositionBy(position);
        if(null == p){
            return;
        }

        getPositions().remove(p);
    }
}
