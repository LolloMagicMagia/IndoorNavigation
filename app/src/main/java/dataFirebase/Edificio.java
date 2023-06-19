package dataFirebase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "edificio_universita")
@TypeConverters(GeoPointConverter.class)
public class Edificio {

    //nome dell'edificio
    @PrimaryKey
    @NonNull
    String nomeEdificio;
    //4 punti dove mettere l'immagine;
    //devo fare un converter da geopoint a string, oppure a arraylist
    GeoPoint left_below;
    GeoPoint left_up;
    GeoPoint right_up;
    GeoPoint right_down;
    //dove sta la posizione dell'edificio
    GeoPoint posizione;
    int numeroFloor;

    //Aule 1 e 2 con tutte le informazioni a riguardo
    /*@Relation(parentColumn = "nomeEdificio", entityColumn = "nomeEdificio")
    public List<Aula> aule;*/

    public Edificio(GeoPoint left_below, GeoPoint left_up,GeoPoint right_up , GeoPoint right_down, String nomeEdificio, GeoPoint posizione, int numeroFloor){
        this.nomeEdificio=nomeEdificio;
        this.left_below=left_below;
        this.left_up=left_up;
        this.right_up=right_up;
        this.right_down=right_down;
        this.posizione=posizione;
        this.numeroFloor=numeroFloor;
    }


    public String getNomeEdificio() {
        return nomeEdificio;
    }

    public void setNomeEdificio(String nomeEdificio) {
        this.nomeEdificio = nomeEdificio;
    }

    public GeoPoint getPosizione() {
        return posizione;
    }

    public void setPosizione(GeoPoint posizione) {
        this.posizione = posizione;
    }

    public int getNumeroFloor() {
        return numeroFloor;
    }

    public void setNumeroFloor(int numeroFloor) {
        this.numeroFloor = numeroFloor;
    }

    public GeoPoint getLeft_below() {
        return left_below;
    }

    public void setLeft_below(GeoPoint left_below) {
        this.left_below = left_below;
    }

    public GeoPoint getLeft_up() {
        return left_up;
    }

    public void setLeft_up(GeoPoint left_up) {
        this.left_up = left_up;
    }

    public GeoPoint getRight_up() {
        return right_up;
    }

    public void setRight_up(GeoPoint right_up) {
        this.right_up = right_up;
    }

    public GeoPoint getRight_down() {
        return right_down;
    }

    public void setRight_down(GeoPoint right_down) {
        this.right_down = right_down;
    }
}
