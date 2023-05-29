package BottomNavFragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.RoadManager;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.GroundOverlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import android.widget.GridView;
import android.widget.ImageButton;

import androidx.lifecycle.ViewModelProvider;

import Adapter.CustomAdapter;
import dataFirebase.PreDatabase;
import dataFirebase.ViewModel;

import com.example.osmdroidex2.R;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

public class FragmentNovità extends Fragment {

    //per la listView
    private GridView gridView;

    Marker scelta;
    private CustomAdapter adapter;
    private List<String> items;

    // Dichiarazione del timer
    CountDownTimer timer;


    ArrayList<GeoPoint> posizioneEdifici;
    MapView map;
    String edificioScoperto=null;
    GroundOverlay overlay;
    ArrayList<GeoPoint> waypoints;
    ArrayList<GeoPoint> waypoints2;
    Polyline roadOverlay;
    RoadManager roadManager;
    ImageButton visitaGuidata;
    ImageButton focusPoint;
    ImageButton location;

    // Dichiarazione delle variabili per la partenza e la destinazione selezionate dallo spinner
    String partenzaSelezionata = null;
    String destinazioneSelezionata = null;

    //Controller e manager per poter lavorare con i punti geospaziali.
    private IMapController mapController;

    //Viene usato per capire dove l'utente stia zommando e quindi capire se mostrare i bottoni dei layer
    BoundingBox box;
    BoundingBox boxUni;

    //Ho creato una classe intermezza tra la mia applicazione e i dati. Così l'unica classe che si dovrà
    //andare a modificare è in questo caso il PreDatabase.
    PreDatabase controller;


    //Tiene conto della destinazione selezionata, se è un aula prende quel valore se no ritorna null
    Marker aulaSelezionata;
    boolean posizioneAttuale = false;

    ImageButton logoReset;
    GpsManager gpsManager;

    //CLASSE USANDO IL REPOSITORY, COSI' VEDO SE WORKA, al posto del controller(PreDatabase)
    private ViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novita, container, false);
        mViewModel = new ViewModelProvider(this).get(ViewModel.class);
        //devo metterlo dove chiedo l'edificio, listener ogni volta che il dato cambia
        /*mViewModel.getEdificio("u14").observe(getActivity(), new Observer<Edificio>() {
            @Override
            public void onChanged(@NonNull Edificio edificio) {
                //update textView e mappe
                Toast.makeText(getActivity(), "onChanged" + edificio.getNumeroFloor()+" " +edificio.getNomeEdificio()+" "+ edificio.getLeft_up()+" "+ edificio.getPosizione(), Toast.LENGTH_SHORT).show();
            }
        });*/
        return view;
    }


}
