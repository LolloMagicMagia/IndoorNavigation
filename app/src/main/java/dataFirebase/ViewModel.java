package dataFirebase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<Edificio> mEdificioLiveData;

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

    public LiveData<Edificio> getEdificio(String s){
        mEdificioLiveData = mRepository.getEdificio(s);
        return mEdificioLiveData;
    }

}
