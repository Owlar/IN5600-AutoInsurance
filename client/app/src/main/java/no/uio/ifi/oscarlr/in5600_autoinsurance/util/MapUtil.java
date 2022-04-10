package no.uio.ifi.oscarlr.in5600_autoinsurance.util;

import com.google.android.gms.maps.model.LatLng;

public class MapUtil {

    public static LatLng stringLocationToLatLng(String location) {
        String[] latLngStrings = location.split(",");
        return new LatLng(Double.parseDouble(latLngStrings[0]), Double.parseDouble(latLngStrings[1]));
    }
}
