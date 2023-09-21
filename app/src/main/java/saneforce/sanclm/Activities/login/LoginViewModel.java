package saneforce.sanclm.Activities.login;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

public class LoginViewModel extends ViewModel {

    private final LoginRepo loginRepo;
    private MutableLiveData<JsonObject> mutableLiveData;

    public LoginViewModel(){
        loginRepo = new LoginRepo();
    }

    public LiveData<JsonObject> loginProcess(Context context, String url, String object) {
        if(mutableLiveData==null){
            mutableLiveData = loginRepo.requestLogin(context,url,object);
        }
        return mutableLiveData;
    }
}
