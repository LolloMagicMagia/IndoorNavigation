package Adapter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import dataAndRelation.ViewModel;

public class NetworkViewModel extends ViewModel {

    public NetworkViewModel(@NonNull Application application) {
        super(application);
    }
    private MutableLiveData<Boolean> isNetworkConnected = new MutableLiveData<>();

    public LiveData<Boolean> getNetworkStatus() {
        return isNetworkConnected;
    }

    public void setNetworkStatus(boolean isConnected) {
        isNetworkConnected.setValue(isConnected);
    }
}
