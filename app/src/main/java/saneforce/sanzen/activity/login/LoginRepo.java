package saneforce.sanzen.activity.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;


public class LoginRepo {

    public MutableLiveData<JsonElement> requestLogin(Context context, String baseUrl, String object){
        final MutableLiveData<JsonElement> mutableLiveData = new MutableLiveData<>();
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "action/login_edet");
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, baseUrl);
        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context),mapString,object);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse (@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                Log.e("Response",""+response.body().toString());
                if (response.isSuccessful() && response.body() != null){
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure (@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                Log.e("test","login failed : " + t);
                if(!SharedPref.getLoginId(context).equalsIgnoreCase("")){
                    LoginActivity.binding.progressBar.setVisibility(View.GONE);
                    LoginActivity.binding.loginBtn.setEnabled(true);
                    Toast.makeText(context.getApplicationContext(), R.string.please_try_again,Toast.LENGTH_SHORT).show();
                   context.startActivity(new Intent(context, HomeDashBoard.class));

                }else {
                    LoginActivity.binding.progressBar.setVisibility(View.GONE);
                    LoginActivity.binding.loginBtn.setEnabled(true);
                    Toast.makeText(context.getApplicationContext(), R.string.please_try_again,Toast.LENGTH_SHORT).show();
                }


            }
        });

        return mutableLiveData;
    }
}