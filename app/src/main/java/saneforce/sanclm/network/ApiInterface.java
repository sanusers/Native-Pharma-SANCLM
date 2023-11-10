package saneforce.sanclm.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

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
import saneforce.sanclm.activity.profile.DCRLastVisitDetails;

public interface ApiInterface {

    @GET
    Call<JsonArray> configuration (@Url String url);

    @FormUrlEncoded
    @POST("?axn=action/login")
    Call<JsonObject> login (@Field("data") String userData);

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
    @POST("?axn=table/slides")
    Call<JsonElement> getSlideMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/setups")
    Call<JsonElement> getSetupMaster(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/additionaldcrmasterdata")
    Call<JsonArray> getAdditionalMaster(@Field("data") String postObj);

    @Multipart
    @POST("?axn=save/image")
    Call<JsonObject> imgUploadMap(@PartMap() HashMap<String, RequestBody> values, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("?axn=geodetails")
    Call<JsonObject> saveMapGeoTag(@Field("data") String saveGeo);

    @FormUrlEncoded
    @POST("?axn=get/approvals")
    Call<JsonArray> getTpApprovalList(@Field("data") String GetTpList);

    @FormUrlEncoded
    @POST("?axn=get/approvals")
    Call<JsonArray> getLeaveApprovalList(@Field("data") String GetLeaveList);

    @FormUrlEncoded
    @POST("?axn=home")
    Call<JsonArray> getcalldetails(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=get/leave")
    Call<JsonArray> getLeavesetdata(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=save/leavemodule")
    Call<JsonArray> getleavesave(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=get/tp")
    Call<JsonElement> getTP(@Field("data") String postObj);


    @FormUrlEncoded
    @POST("?axn=table/additionaldcrmasterdata")
    Call<List<DCRLastVisitDetails>> LastVisitDetails(@Field("data") String GetLastVisit);

    @FormUrlEncoded
    @POST("?axn=save/dcr")
    Call<JsonObject> saveDcr(@Field("data") String SaveDcr);

    @Multipart
    @POST("?axn=save/image")
    Call<JsonObject> saveImgDcr(@PartMap() HashMap<String, RequestBody> values, @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("?axn=save/dayplan")
    Call<JsonObject> saveMydayPlan(@Field("data") String SaveDcr);
}
