package saneforce.santrip.activity.login;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LoginViewModel extends ViewModel {

    private final LoginRepo loginRepo;
    private MutableLiveData<JsonElement> mutableLiveData;

    public LoginViewModel(){
        loginRepo = new LoginRepo();
    }

    public LiveData<JsonElement> loginProcess(Context context, String url, String object) {
            mutableLiveData = loginRepo.requestLogin(context,url,object);
        return mutableLiveData;
    }
}
