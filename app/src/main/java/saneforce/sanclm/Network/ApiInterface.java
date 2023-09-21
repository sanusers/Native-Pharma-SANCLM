package saneforce.sanclm.Network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
    Call<JsonArray> configuration (@Url String url);

    @FormUrlEncoded
    @POST("?axn=action/login")
    Call<JsonObject> login (@Field("data") String userData);

    @FormUrlEncoded
    @POST("?axn=table/dcrmasterdata")
    Call<JsonArray> getDrMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/subordinates")
    Call<JsonArray> getSubordinateMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/products")
    Call<JsonArray> getProductMaster(@Field("data") String postObj);
    @FormUrlEncoded
    @POST("?axn=get/leave")
    Call<JsonArray> getLeaveMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/slides")
    Call<JsonArray> getSlideMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/setups")
    Call<JsonArray> getSetupMaster(@Field("data") String postObj);


    @FormUrlEncoded
    @POST("?axn=table/additionaldcrmasterdata")
    Call<JsonArray> getAdditionalMaster(@Field("data") String postObj);

}
