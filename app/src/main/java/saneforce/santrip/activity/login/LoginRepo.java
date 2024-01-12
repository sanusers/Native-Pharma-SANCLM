package saneforce.santrip.activity.login;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;


public class LoginRepo {

    public MutableLiveData<JsonObject> requestLogin(Context context,String baseUrl,String object){
        final MutableLiveData<JsonObject> mutableLiveData = new MutableLiveData<>();

        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, baseUrl);
        Call<JsonObject> call = apiInterface.login(object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse (@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null){
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure (@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("test","login failed : " + t);
            }
        });

        return mutableLiveData;
    }
}
