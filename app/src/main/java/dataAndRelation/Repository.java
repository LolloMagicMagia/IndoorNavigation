package dataAndRelation;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository {
    private EdificioDao mEdificioDao;
    private AulaDao mAulaDao;
    private LiveData<List<Edificio>> allEdifiocs;
    private LiveData<List<Aula>> allAule;
    private Edificio mEdificioLiveData;

    public LiveData<Edificio> getEdificioObj(String edificio){
        return mEdificioDao.getEdificioObj(edificio);
    }


    public Repository(Application application){
        EdificiDatabase database = EdificiDatabase.getInstance(application);
        mEdificioDao = database.edificioDao();
        mAulaDao = database.AulaDao();
        allEdifiocs=mEdificioDao.getAllEdificio();
        allAule=mAulaDao.getAllAule();

    }

    public void insertEdificio(Edificio edificio){
        new InsertEdificiAsyncTask(mEdificioDao).execute(edificio);
    }
    public void updateEdificio(Edificio edificio){
        new UpdateEdificiAsyncTask(mEdificioDao).execute(edificio);
    }
    public void deleteEdificio(Edificio edificio){
        new DeleteEdificiAsyncTask(mEdificioDao).execute(edificio);
    }

    public Polyline routeCalculation(RoadManager roadManager, ArrayList<GeoPoint> waypoints2, Polyline roadOverlay) throws ExecutionException, InterruptedException {
        Repository.RouteCalculationAsyncTask ex = new Repository.RouteCalculationAsyncTask(roadManager, waypoints2,roadOverlay);
        Polyline roadRet = ex.execute().get();
        return roadRet;
    }

    public LiveData<List<Edificio>> getAllEdificios(){
        return allEdifiocs;
    }

    public LiveData<List<Aula>> getAllAule(){
        return allAule;
    }


    private static class InsertEdificiAsyncTask extends AsyncTask<Edificio, Void, Void>{
        private EdificioDao edificioDao;

        private InsertEdificiAsyncTask(EdificioDao edificioDao){
            this.edificioDao=edificioDao;
        }

        @Override
        protected Void doInBackground(Edificio... edificios){
            edificioDao.insertEdificio(edificios[0]);
            return null;
        }
    }

    private static class UpdateEdificiAsyncTask extends AsyncTask<Edificio, Void, Void>{
        private EdificioDao edificioDao;

        private UpdateEdificiAsyncTask(EdificioDao edificioDao){
            this.edificioDao=edificioDao;
        }

        @Override
        protected Void doInBackground(Edificio... edificios){
            edificioDao.updateEdificio(edificios[0]);
            return null;
        }
    }

    private static class DeleteEdificiAsyncTask extends AsyncTask<Edificio, Void, Void>{
        private EdificioDao edificioDao;

        private DeleteEdificiAsyncTask(EdificioDao edificioDao){
            this.edificioDao=edificioDao;
        }

        @Override
        protected Void doInBackground(Edificio... edificios){
            edificioDao.delete(edificios[0]);
            return null;
        }
    }

    private static class RouteCalculationAsyncTask extends AsyncTask<Void, Void, Polyline>{
        private RoadManager roadManager;
        private ArrayList<GeoPoint> waypoints2;
        Polyline roadOverlay;

        public RouteCalculationAsyncTask(RoadManager roadManager, ArrayList<GeoPoint> waypoints2, Polyline roadOverlay) {
            this.roadManager=roadManager;
            this.waypoints2 = waypoints2;
            this.roadOverlay = roadOverlay;
        }

        @Override
        protected Polyline doInBackground(Void... voids){
            Road road =  roadManager.getRoad(waypoints2);
            roadOverlay = RoadManager.buildRoadOverlay(road);
            return roadOverlay;
        }
    }


}
