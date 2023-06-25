package dataAndRelation;
import androidx.room.TypeConverter;

import org.osmdroid.util.GeoPoint;

public class GeoPointConverter {
    @TypeConverter
    public static String fromGeoPoint(GeoPoint geoPoint) {
        // Converti il GeoPoint in una stringa nel formato desiderato
        // Ad esempio, puoi usare latitude e longitude separate da un delimitatore
        if(geoPoint!=null){
            return geoPoint.getLatitude() + "," + geoPoint.getLongitude();
        }else{
            return null;
        }
    }

    @TypeConverter
    public static GeoPoint toGeoPoint(String value) {
        // Converti la stringa nel formato desiderato in un oggetto GeoPoint
        // Ad esempio, puoi separare latitude e longitude utilizzando il delimitatore
        if (value != null) {
            String[] parts = value.split(",");
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
            return new GeoPoint(latitude, longitude);
        }else{
            return null;
        }

    }
}
