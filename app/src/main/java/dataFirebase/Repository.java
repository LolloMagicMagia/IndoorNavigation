package dataFirebase;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Repository {
    private EdificioDao mEdificioDao;
    private AulaDao mAulaDao;
    private LiveData<List<Edificio>> allEdifiocs;
    private LiveData<List<Aula>> allAule;
    private Edificio mEdificioLiveData;
 

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


}
