package saneforce.santrip.activity.login;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SharedPref;


public class LoginRepo {

    public MutableLiveData<JsonElement> requestLogin(Context context, String baseUrl, String object){
        final MutableLiveData<JsonElement> mutableLiveData = new MutableLiveData<>();

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "action/login");
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, baseUrl);
        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context),mapString,object);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse (@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null){
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure (@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                Log.e("test","login failed : " + t);
            }
        });

        return mutableLiveData;
    }
}
