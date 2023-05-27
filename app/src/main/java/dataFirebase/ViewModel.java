package dataFirebase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<Edificio> mEdificioLiveData;
    private LiveData<Aula> mAulaLiveData;

    public ViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
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



}
