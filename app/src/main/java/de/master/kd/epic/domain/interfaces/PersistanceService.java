package de.master.kd.epic.domain.interfaces;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.domain.position.PositionCRUD;

/**
 * Created by pentax on 28.06.17.
 */

public class PersistanceService implements PositionCRUD {


    private ArrayList<Position> postions = new ArrayList<>();

    @Override
    public Position save(Position position) {
        postions.add(position);

        return position;
    }

    @Override
    public void update(Position p) {
        updateOrDelete(false, p);
    }

    @Override
    public void delete(Position p) {
        updateOrDelete(true, p);
    }

    @Override
    public Position find(String title) {
        return findInList(null, title, null);
    }

    @Override
    public Position find(Long id) {
        return findInList(id, null, null);
    }
    @Override
    public Position find(LatLng latLng) {
        return findInList(null, null, latLng);
    }


    @Override
    public ArrayList<Position> getAllPositions() {
        return postions;
    }

    private void updateOrDelete(boolean delete, Position search) {
        int index = 0;
        for (Position p : postions) {
            if (p.getId() == search.getId()) {
                break;
            }
            index++;
        }
        if (delete) {
            removeFromList(index);
        } else {
            postions.add(index, search);
            removeFromList(index + 1);
        }
    }

    private void removeFromList(int index) {
        postions.remove(index);
    }

    private Position findInList(Long id, String text, LatLng latLng) {
        for (Position p : postions) {
            if (null != id && p.getId() == id) {
                return p;
            }

            if (null != text && text.equals(p.getTitle())) {
                return p;
            }

            if(latLng != null && p.getLongitude() == latLng.longitude && p.getLatitude() == latLng.latitude){
                return p;
            }
        }
        return null;
    }
}
