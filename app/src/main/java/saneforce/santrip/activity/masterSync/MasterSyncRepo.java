package saneforce.santrip.activity.masterSync;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.network.ApiInterface;


public class MasterSyncRepo {

    public MutableLiveData<JsonArray> masterSyncRequest(Context context,String baseUrl,String object,String masterFor,String remoteTable) {
        MutableLiveData<JsonArray> mutableLiveData = new MutableLiveData<>();

//        try {
//            ApiInterface apiInterface = saneforce.santrip.network.RetrofitClient.getRetrofit(context, baseUrl);
//            Call<JsonArray> call = null;
//            JSONObject jsonObject = new JSONObject(object);
//            if (masterFor.equalsIgnoreCase("Doctor")){
//                call = apiInterface.getDrMaster(jsonObject.toString());
//            } else if (masterFor.equalsIgnoreCase("Subordinate")) {
//                call = apiInterface.getSubordinateMaster(jsonObject.toString());
//            }else if (masterFor.equalsIgnoreCase("Product")) {
//                call = apiInterface.getProductMaster(jsonObject.toString());
//            }else if (masterFor.equalsIgnoreCase("Leave")) {
//                call = apiInterface.getLeaveMaster(jsonObject.toString());
//            }else if (masterFor.equalsIgnoreCase("Slide")) {
//                call = apiInterface.getSlideMaster(jsonObject.toString());
//            }else if (masterFor.equalsIgnoreCase("Setup")) {
//                call = apiInterface.getSetupMaster(jsonObject.toString());
//            }else if (masterFor.equalsIgnoreCase("TP")) {
//                call = apiInterface.getAdditionalMaster(jsonObject.toString());
//            }
//
//            if (call != null){
//                call.enqueue(new Callback<JsonArray>() {
//                    @Override
//                    public void onResponse (@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
//                        if (response.isSuccessful() && response.body() != null){
//                            mutableLiveData.setValue(response.body());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure (@NonNull Call<JsonArray> call, @NonNull Throwable t) {
//                        Log.e("test","master sync " + remoteTable + " failed : " + t.toString() );
//                    }
//                });
//            }
//        } catch (Exception exception){
//            exception.printStackTrace();
//            Log.e("test","master sync exception " + remoteTable + " : " + exception);
//
//        }

        return mutableLiveData;
    }
}
