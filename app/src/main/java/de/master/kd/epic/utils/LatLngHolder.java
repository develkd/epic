package de.master.kd.epic.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pentax on 26.06.17.
 */

public class LatLngHolder {

    private int index = 0;
    private List<LatLng> list = new ArrayList<>();

    public LatLngHolder() {
        init();
    }

    private void init() {
        list.add(new LatLng(52.475391, 13.401893));
        list.add(new LatLng(52.482258, 13.406754));
        list.add(new LatLng(52.480689, 13.425035));
        list.add(new LatLng(52.473423, 13.427181));
        list.add(new LatLng(52.466887, 13.431816));

        index = list.size() - 1;

    }

    public LatLng next() {
        if (index > -1) {
            return list.get(index--);
        }

        return null;
    }
}
