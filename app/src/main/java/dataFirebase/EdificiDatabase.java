package dataFirebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.osmdroidex2.Graph;
import com.example.osmdroidex2.R;

import org.osmdroid.util.GeoPoint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import BottomNavFragment.FragmentIndoor;

@Database(entities = {Edificio.class,Aula.class}, version=2, exportSchema = false)
public abstract class EdificiDatabase extends RoomDatabase {
    //SINGLENTON
    private static EdificiDatabase instance;

    public abstract EdificioDao edificioDao();
    public abstract AulaDao AulaDao();

    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);



    //Room crea in automatico i metodi abstract tramite questo databse
    public static synchronized  EdificiDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            EdificiDatabase.class, "edifici_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private EdificioDao mEdificioDao;
        private AulaDao mAulaDao;


        private PopulateDbAsyncTask(EdificiDatabase db){
            mEdificioDao=db.edificioDao();
            mAulaDao=db.AulaDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            GeoPoint left_down = new GeoPoint(45.52391, 9.21894);
            GeoPoint left_top = new GeoPoint(45.52345, 9.22015);
            GeoPoint right_top = new GeoPoint(45.52335, 9.22009);
            GeoPoint right_down = new GeoPoint(45.52381, 9.21886);

            mEdificioDao.insertEdificio(new Edificio(left_down, left_top, right_top, right_down,
                    "u14",new GeoPoint(45.52374,9.21971),2, null, null));

            /*Graph graph_u60 = new Graph();

            graph_u60.addNode("1", (float) 0.5, (float) 0.149,  "atrium", "available", "notCrow");
            graph_u60.addNode("1.09", (float) 0.473, (float) 0.502,  "atrium", "available", "notCrow");
            graph_u60.addNode("1.10", (float) 0.473, (float) 0.802,  "atrium", "available", "notCrow");

            graph_u60.addEdge("1", "1.09", 1);
            graph_u60.addEdge("1.09", "1.10", 1);*/


            /*Graph graph_u61 = new Graph();

            graph_u61.addNode("1", (float) 0.5, (float) 0.149,  "atrium", "available", "notCrow");
            graph_u61.addNode("1.09", (float) 0.473, (float) 0.502,  "atrium", "available", "notCrow");
            graph_u61.addNode("1.10", (float) 0.473, (float) 0.802,  "atrium", "available", "notCrow");

            graph_u61.addEdge("1", "1.09", 1);
            graph_u61.addEdge("1.09", "1.10", 1);*/

            GeoPoint left_downU6= new GeoPoint(45.51773, 9.2126);
            GeoPoint left_topU6= new GeoPoint(45.51929, 9.21347);
            GeoPoint right_topU6 = new GeoPoint(45.51906, 9.21426);
            GeoPoint right_downU6 = new GeoPoint(45.51752, 9.21341);
            mEdificioDao.insertEdificio(new Edificio(left_downU6, left_topU6, right_topU6, right_downU6,
                    "u6",new GeoPoint(45.51847, 9.21297),6,
                    null,null));
            mEdificioDao.insertEdificio(new Edificio(null,null,null,null,
                    "u7", new GeoPoint(45.51731, 9.21291), 4,null,null));

            mAulaDao.insertAula(new Aula(0,new GeoPoint(45.52361, 9.21971),"u14","u14AulaFirstFloor"));
            mAulaDao.insertAula(new Aula(1,new GeoPoint(45.52352, 9.21994),"u14","u14AulaSecondFloor"));
            return null;
        }
    }

}