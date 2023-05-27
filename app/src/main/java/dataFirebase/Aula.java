package dataFirebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.osmdroidex2.R;

import org.osmdroid.util.GeoPoint;

@Entity(tableName = "aula_universita", primaryKeys = {"nomeEdificio", "nomeAula"},
        foreignKeys = @ForeignKey(entity = Edificio.class,
                parentColumns = "nomeEdificio",
                childColumns = "nomeEdificio",
                onDelete = ForeignKey.CASCADE))
/*@TypeConverters({GeoPointConverter.class,BitMapConverter.class})*/
@TypeConverters(GeoPointConverter.class)
public class Aula {

    @NonNull
    String nomeEdificio;
    @NonNull
    String nomeAula;
    /*Bitmap bitmap;*/
    int piano;
    GeoPoint posizione;

    public Aula(int piano, GeoPoint posizione, /*Bitmap bitmap,*/ String nomeEdificio, String nomeAula){
        /*this.bitmap=bitmap;*/
        this.piano=piano;
        this.posizione=posizione;
        this.nomeAula=nomeAula;
        this.nomeEdificio=nomeEdificio;
    }

    /*public Bitmap getBitmap() {
        return bitmap;
    }*/

    public int getPiano() {
        return piano;
    }

    public GeoPoint getPosizione() {
        return posizione;
    }

    /*public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }*/

    public void setPiano(int piano) {
        this.piano = piano;
    }

    public void setPosizione(GeoPoint posizione) {
        this.posizione = posizione;
    }

    public String getNomeEdificio() {
        return nomeEdificio;
    }

    public void setNomeEdificio(String nomeEdificio) {
        this.nomeEdificio = nomeEdificio;
    }

    public String getNomeAula() {
        return nomeAula;
    }

    public void setNomeAula(String nomeAula) {
        this.nomeAula = nomeAula;
    }
}
