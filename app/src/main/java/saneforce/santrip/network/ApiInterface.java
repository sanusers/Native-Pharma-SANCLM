package saneforce.santrip.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
    Call<JsonArray> configuration(@Url String url);
    @FormUrlEncoded
    @POST()
    Call<JsonElement> getJSONElement(@Url String url, @QueryMap Map<String, String> qry, @Field("data") String postObj);

    @FormUrlEncoded
    @POST()
    Call<ResponseBody> getResponseBody(@Url String url, @QueryMap Map<String, String> qry, @Field("data") String postObj);

    @Multipart
    @POST("?axn=save/image")
    Call<JsonObject> SaveImg(@PartMap() HashMap<String, RequestBody> values, @Part MultipartBody.Part file);

 /*   @FormUrlEncoded
    @POST("?axn=action/login")
    Call<JsonElement> login(@Field("data") String userData);

    @FormUrlEncoded
    @POST("?axn=table/dcrmasterdata")
    Call<JsonElement> getDrMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/subordinates")
    Call<JsonElement> getSubordinateMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/products")
    Call<JsonElement> getProductMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=get/leave")
    Call<JsonElement> getLeaveMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=home")
    Call<JsonElement> getDCRMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=get/misseddcr")
    Call<JsonElement> getMissedDCRMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/slides")
    Call<JsonElement> getSlideMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/setups")
    Call<JsonElement> getSetupMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/additionaldcrmasterdata")
    Call<JsonElement> getAdditionalMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=geodetails")
    Call<JsonObject> saveMapGeoTag(@Field("data") String saveGeo);

    @FormUrlEncoded
    @POST("?axn=get/approvals")
    Call<JsonElement> getApprovalJsonArrayList(@Field("data") String GetTpList);

    @FormUrlEncoded
    @POST("?axn=save/approvals")
    Call<JsonObject> saveApprovalList(@Field("data") String saveTpDeviation);

    @FormUrlEncoded
    @POST("?axn=edit/dcr")
    Call<JsonObject> getEditCallDetails(@Field("data") String GetEditCallDetails);

    @FormUrlEncoded
    @POST("?axn=delete/dcr")
    Call<ResponseBody> DeleteCall(@Field("data") String DeleteCall);

    @FormUrlEncoded
    @POST("?axn=save/activity")
    Call<JsonArray> saveCheckInOut(@Field("data") String CheckInOut);

    @FormUrlEncoded
    @POST("?axn=save/masterdata")
    Call<JsonObject> ChangePwd(@Field("data") String ChangePwd);

    @FormUrlEncoded
    @POST("?axn=save/leavemodule")
    Call<JsonArray> getLeaveSave(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=get/tp")
    Call<JsonElement> getTP(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=savenew/tp")
    Call<JsonObject> saveTP(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=save/tp")
    Call<JsonObject> saveTPStatus(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=save/dcr")
    Call<JsonObject> saveDcr(@Field("data") String SaveDcr);

    @FormUrlEncoded
    @POST("?axn=edetsave/dayplan")
    Call<JsonObject> saveMyDayPlan(@Field("data") String SaveDcr);

    @FormUrlEncoded
    @POST("?axn=get/reports")
    Call<JsonElement> getReports(@Field("data") String postObj);*/
}
