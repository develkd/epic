package de.master.kd.epic.interfaces;

import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.Date;

import de.master.kd.epic.domain.position.Position;

/**
 * Created by pentax on 25.06.17.
 */

public class PositionService {
    private static final PersistanceService service = new PersistanceService();
    private PositionService(){
    }


    public static Position createPositionWith(String title, String description, LatLng latLng, String pathPicture, String pathMap){
        Position position = new Position();
        position.setId(new Date().getTime());
        position.setTitle(title);
        position.setDescription(description);
        position.setLatitude(latLng.latitude);
        position.setLongitude(latLng.longitude);
        position.setPathPicture(pathPicture);
        position.setPathMap(pathMap);

        service.save(position);
        return position;
    }

    public static ArrayList<Position> getPositions(){
        return  service.getAllPositions();
    }
}
