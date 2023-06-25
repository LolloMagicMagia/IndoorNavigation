package dataAndRelation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<Edificio> mEdificioLiveData;
    private LiveData<Aula> mAulaLiveData;

    public ViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

     public LiveData<Edificio> getEdificioObj(String edificio){
        return mRepository.getEdificioObj(edificio);
     }
    public void insert(Edificio edificio){
        mRepository.insertEdificio(edificio);
    }

    public void update(Edificio edificio){
        mRepository.updateEdificio(edificio);
    }
    public void delete(Edificio edificio){
        mRepository.deleteEdificio(edificio);
    }

    public LiveData<List<Edificio>> getAllEdificios(){
        return mRepository.getAllEdificios();
    }

    public LiveData<List<Aula>> getAllAule(){
        return mRepository.getAllAule();
    }

    public Polyline routeCalculation(RoadManager roadManager, ArrayList<GeoPoint> waypoints2, Polyline roadOverlay) throws ExecutionException, InterruptedException {
        Polyline roadRet = mRepository.routeCalculation(roadManager,waypoints2,roadOverlay);
        return roadRet;
    }



}
