package dataFirebase;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

public class Repository {
    private EdificioDao mEdificioDao;
    /*private AulaDao mAulaDao;*/
    private LiveData<Edificio> mEdificioLiveData;
    /*private LiveData<Aula> mAulaLiveData;*/


    public Repository(Application application){
        EdificiDatabase database = EdificiDatabase.getInstance(application);
        mEdificioDao = database.edificioDao();
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


    //il live Data va a farlo automaticamente su un thread a parte ma gli altri metodi no
    public LiveData<Edificio> getEdificio(String edificio){
        mEdificioLiveData = mEdificioDao.getEdificio(edificio);
        return mEdificioLiveData;
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
