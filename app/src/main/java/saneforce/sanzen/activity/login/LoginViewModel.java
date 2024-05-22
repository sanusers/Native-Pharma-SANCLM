package saneforce.sanzen.activity.login;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonElement;

public class LoginViewModel extends ViewModel {

    private final LoginRepo loginRepo;
    private MutableLiveData<JsonElement> mutableLiveData=new MutableLiveData<>();

    public LoginViewModel(){
        loginRepo = new LoginRepo();
    }

    public LiveData<JsonElement> loginProcess(Context context, String url, String object) {
        mutableLiveData = loginRepo.requestLogin(context,url,object);
        return mutableLiveData;
    }
}
