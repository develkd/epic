package de.master.kd.epic.interfaces;

import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.Date;

import de.master.kd.epic.domain.Position;
import de.master.kd.epic.persistance.PostionsPersistanceService;

/**
 * Created by pentax on 25.06.17.
 */

public class PositionService {
    private static final PostionsPersistanceService service = new PostionsPersistanceService();
    private PositionService(){
    }


    public static Position buildPositionWith(String title, String description, LatLng latLng, Long picId, Long mapId){
        Position position = new Position();
        position.setId(new Date().getTime());
        position.setTitle(title);
        position.setDescription(description);
        position.setLatitude(latLng.latitude);
        position.setLongitude(latLng.longitude);
        position.setRefIdPicture(picId);
        position.setRefIdMap(mapId);

        service.save(position);
        return position;
    }

    public static ArrayList<Position> getPositions(){
        return  service.getAllPositions();
    }
}
