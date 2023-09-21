package saneforce.sanclm.Activities.masterSync;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;

public class MasterSyncViewModel extends ViewModel {

    MasterSyncRepo masterSyncRepo;
    MutableLiveData<JsonArray> mutableLiveData;

    public MutableLiveData<JsonArray> masterSync(Context context,String baseUrl,String object,String masterFor,String remoteTable){
        if (mutableLiveData == null){
            try {
                mutableLiveData = masterSyncRepo.masterSyncRequest(context,baseUrl,object,masterFor,remoteTable);
            }catch (Exception exception){
                exception.printStackTrace();
                Log.e("test","master sync exception : " + exception);
            }
        }

        return mutableLiveData;
    }
}
