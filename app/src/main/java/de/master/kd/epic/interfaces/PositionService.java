package de.master.kd.epic.interfaces;

import com.google.android.gms.maps.model.LatLng;


import java.util.Date;

import de.master.kd.epic.domain.Position;
import de.master.kd.epic.persistance.PostionsCRUDController;

/**
 * Created by pentax on 25.06.17.
 */

public class PositionService {
    private PositionService(){
    }


    public static Position buildPositionWith(String title, String description, LatLng latLng,Long picId, Long mapId){
        Position position = new Position();
        position.setId(new Date().getTime());
        position.setTitle(title);
        position.setDescription(description);
        position.setLatitude(latLng.latitude);
        position.setLongitude(latLng.longitude);
        position.setRefIdPicture(picId);
        position.setRefIdMap(mapId);
        new PostionsCRUDController().save(position);
        return position;
    }
}
