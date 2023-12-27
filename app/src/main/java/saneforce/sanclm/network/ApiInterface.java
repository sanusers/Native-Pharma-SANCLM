package saneforce.sanclm.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;
import saneforce.sanclm.activity.profile.DCRLastVisitDetails;

public interface ApiInterface {

    @GET
    Call<JsonArray> configuration(@Url String url);

    @FormUrlEncoded
    @POST("?axn=action/login")
    Call<JsonObject> login(@Field("data") String userData);

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

    @Multipart
    @POST("?axn=save/image")
        // Save GeoImage
    Call<JsonObject> imgUploadMap(@PartMap() HashMap<String, RequestBody> values, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("?axn=geodetails")
        // Save GeoTag
    Call<JsonObject> saveMapGeoTag(@Field("data") String saveGeo);

    @FormUrlEncoded
    @POST("?axn=get/approvals")
        // Get Tp ApprovalList
    Call<JsonArray> getTpApprovalList(@Field("data") String GetTpList);

    @FormUrlEncoded
    @POST("?axn=get/tp")
        // Get Tp DetailedList
    Call<JsonArray> getTpDetailedList(@Field("data") String GetTpDetailedList);

    @FormUrlEncoded
    @POST("?axn=save/tp")
    Call<JsonObject> saveApprovedRejectTp(@Field("data") String saveApprovedRejectTp);

    @FormUrlEncoded
    @POST("?axn=get/approvals")
        // Get Leave ApprovalList
    Call<JsonArray> getLeaveApprovalList(@Field("data") String GetLeaveList);

    @FormUrlEncoded
    @POST("?axn=save/approvals")
        // Get TpDeviation ApprovalList
    Call<JsonObject> saveTpDeviation(@Field("data") String saveTpDeviation);

    @FormUrlEncoded
    @POST("?axn=get/approvals")
        // Get TpDeviation ApprovalList
    Call<JsonArray> getTpDeviationList(@Field("data") String getTpDeviationList);

    @FormUrlEncoded
    @POST("?axn=get/approvals")
        // Get DCR ApprovalList
    Call<JsonArray> getDcrApprovalList(@Field("data") String GetDcrApprovalList);

    @FormUrlEncoded
    @POST("?axn=get/approvals")
        // Get DCRDetailed ApprovalList
    Call<JsonArray> getDcrDetailedList(@Field("data") String GetDcrDetailedList);

    @FormUrlEncoded
    @POST("?axn=get/approvals")
        // Get GEOTAG ApprovalList
    Call<JsonArray> getGeoTagList(@Field("data") String GetGeoTagList);

    @FormUrlEncoded
    @POST("?axn=save/approvals")
        // Send GEOTAG ApprovalReject
    Call<JsonObject> SendGeoTagApprovalReject(@Field("data") String SendGeoTagApprovalReject);


    @FormUrlEncoded
    @POST("?axn=get/approvals")
        //Get ListCount of Approvals
    Call<JsonObject> getListCountApprovals(@Field("data") String GetApprovalListCount);

    @FormUrlEncoded
    @POST("?axn=save/approvals")
        //Send DCR Approvals
    Call<JsonObject> sendDCRApproval(@Field("data") String SendDcrApproval);

    @FormUrlEncoded
    @POST("?axn=save/approvals")
        //Send DCR Reject
    Call<JsonObject> sendDCRReject(@Field("data") String SendDcrReject);

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
    @POST("?axn=savenew/tp")
    Call<JsonObject> saveTP(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=save/tp")
    Call<JsonObject> saveTPStatus(@Field("data") String postObj);

    @FormUrlEncoded
    @POST("?axn=table/additionaldcrmasterdata")
        // Get DCR LastVisit Details
    Call<List<DCRLastVisitDetails>> LastVisitDetails(@Field("data") String GetLastVisit);

    @FormUrlEncoded
    @POST("?axn=save/dcr")
        // Save Dcr
    Call<JsonObject> saveDcr(@Field("data") String SaveDcr);

    @Multipart
    @POST("?axn=save/image")
        // Save Dcr EventCapture
    Call<JsonObject> saveImgDcr(@PartMap() HashMap<String, RequestBody> values, @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("?axn=save/dayplan")
    Call<JsonObject> saveMydayPlan(@Field("data") String SaveDcr);

    @FormUrlEncoded
    @POST("?axn=save/approvals")
        // Approved & Reject Leave
    Call<JsonObject> saveLeaveApproval(@Field("data") String SaveLeaveApproval);

    @FormUrlEncoded
    @POST("?axn=get/reports")
    Call<JsonElement> getReports(@Field("data") String postObj);


}
