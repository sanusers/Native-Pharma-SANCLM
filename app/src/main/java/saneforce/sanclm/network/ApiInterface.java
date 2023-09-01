package saneforce.sanclm.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import saneforce.sanclm.response.LoginResponse;

public interface ApiInterface {

    @GET
    Call<JsonArray> Configuration(@Url String url);

    @FormUrlEncoded
    @POST("?axn=action/login")
    Call<JsonObject> Login(@Field("data") String userData);


}
