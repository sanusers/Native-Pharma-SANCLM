package saneforce.sanzen.activity.BusinessEntry;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.BusinessEntry.ModelClass.AddDoctorEntryProducts;
import saneforce.sanzen.activity.BusinessEntry.ModelClass.ProductListModel;
import saneforce.sanzen.activity.BusinessEntry.adapter.DoctorBusinessProductListAdapter;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityDocbusinessProductListBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DocBusinessProductList  extends AppCompatActivity {
    public static ActivityDocbusinessProductListBinding docbusinessProductListBinding;
    DoctorBusinessProductListAdapter ProductListAdapter;
    public static ArrayList<CallCommonCheckedList> allPrdList;
    List<ProductListModel> selectedProductList = new ArrayList<>();
    RoomDB roomDB;
    JSONArray updatedjsonArrayproducts = new JSONArray();
    ProgressDialog progressDialog;
    CommonUtilsMethods commonUtilsMethods;
    ApiInterface apiInterface;
    double prdtotalvalue=0.0;
    String Doc_name="",Month="",sfcode="",sfname="",Rsf="",selmonth="",selyear="",activeflag="";
    String Doc_code="",detailcode="",selectedhq="",ter_code="",ter_name="",catcode="",catname="",speccode="",specname="",classcode="",classname="",productJson="";
    MasterDataDao masterDataDao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        docbusinessProductListBinding = ActivityDocbusinessProductListBinding.inflate(getLayoutInflater());
        setContentView(docbusinessProductListBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            Doc_name = extra.getString("doc_name");
            Month = extra.getString("Month");
            Doc_code = extra.getString("dr_code");
            catcode = extra.getString("cat_code");
            catname = extra.getString("cat_name");
            ter_code = extra.getString("ter_code");
            ter_name = extra.getString("ter_name");
            speccode = extra.getString("spec_code");
            specname = extra.getString("spec_name");
            classcode = extra.getString("class_code");
            classname = extra.getString("class_name");
            productJson= extra.getString("product_json");
            activeflag= extra.getString("active_flag");
            detailcode= extra.getString("detailcode");
            selectedhq= extra.getString("selectedhq");
        }
        if(activeflag.equalsIgnoreCase("1")){
            docbusinessProductListBinding.btnSave.setVisibility(View.GONE);
            docbusinessProductListBinding.btnCancel.setVisibility(View.GONE);
        }
        else {
            docbusinessProductListBinding.btnSave.setVisibility(View.VISIBLE);
            docbusinessProductListBinding.btnCancel.setVisibility(View.VISIBLE);
        }
            String dateStr = Month;
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
            try {
                Date date = sdf.parse(dateStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
                int year = calendar.get(Calendar.YEAR);
                selmonth=String.valueOf(month);
                selyear=String.valueOf(year);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        docbusinessProductListBinding.drbusiness.setText(Doc_name + " " + "Business Entry" + " (" + Month + ")");
        if (!productJson.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(productJson);
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ProductListModel>>() {
                }.getType();
                selectedProductList = gson.fromJson(jsonArray.toString(), listType);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SetUpAdapter();

        docbusinessProductListBinding.drBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCancel();
            }
        });

        docbusinessProductListBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCancel();
            }
        });

        docbusinessProductListBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docbusinessProductListBinding.btnSave.setEnabled(false);
                JSONObject json = CommonUtilsMethods.CommonObjectParameter(DocBusinessProductList.this);
                try {
                    json.put("tableName", "doc_business_entry");
                    json.put("Trans_Year", selyear);
                    json.put("ListedDrCode", Doc_code);
                    json.put("ListedDr_Name", Doc_name);
                    if (SharedPref.getSfType(DocBusinessProductList.this).equalsIgnoreCase("2")) {
                        sfcode = SharedPref.getSfCode(DocBusinessProductList.this);
                        sfname = SharedPref.getSfName(DocBusinessProductList.this);
                        Rsf=SharedPref.getHqCode(DocBusinessProductList.this);
                    } else {
                        sfcode = SharedPref.getSfCode(DocBusinessProductList.this);
                        sfname = SharedPref.getSfName(DocBusinessProductList.this);
                        Rsf=sfcode;
                    }
                    json.put("Sf_Code", sfcode);
                    json.put("sfname", sfname);
                    json.put("Rsf", selectedhq);
                    json.put("Trans_Month", selmonth);
                    json.put("Detail_No", detailcode);
                    json.put("Division_Code", SharedPref.getDivisionCode(DocBusinessProductList.this));
                    json.put("createdDate", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
                    json.put("updatedDate", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
                    json.put("Category_Code", catcode);
                    json.put("Category_Name", catname);
                    json.put("Territory_Name", ter_name);
                    json.put("Territory_Code", ter_code);
                    json.put("Speciality_Code", speccode);
                    json.put("Speciality_Name", specname);
                    json.put("Class_Code", classcode);
                    json.put("Class_Name", classname);
                    List<AddDoctorEntryProducts> selectedProducts = ProductListAdapter.getSelectedProducts();
                    JSONArray jsonArrayproducts = new JSONArray();

                    for (AddDoctorEntryProducts product : selectedProducts) {
                            JSONObject json_products = new JSONObject();
                                    json_products.put("NSR_Price",product.getNSR_Price());
                                    json_products.put("Sample_Price",product.getSample_Price());
                                    json_products.put("Target_Price", product.getTarget_Price());
                                    json_products.put("Distributor_Price",product.getDistributor_Price());
                                    json_products.put("Product_Detail_Name",product.getProduct_Detail_Name());
                                    json_products.put("value", product.getValue());
                                    prdtotalvalue+=Double.parseDouble(product.getValue());
                                    json_products.put("Retailor_Price",product.getRetailor_Price());
                                    json_products.put("Product_Code",product.getProduct_Code());
                                    json_products.put("Product_Sale_Unit", product.getProduct_Sale_Unit());
                                    json_products.put("MRP_Price",product.getMRP_Price());
                                    json_products.put("Product_Quantity", product.getProduct_Quantity());
                                    jsonArrayproducts.put(json_products);

                        JSONObject updatedproducts = new JSONObject();
                        updatedproducts.put("Detail_No",product.getDetailcode());
                        updatedproducts.put("Division_Code",SharedPref.getDivisionCode(DocBusinessProductList.this));
                        updatedproducts.put("ListedDrCode",Doc_code );
                        json_products.put("Speciality_Code",speccode);
                        updatedproducts.put("Territory_Code",ter_code);
                        updatedproducts.put("Product_Code", product.getProduct_Code());
                        updatedproducts.put("Product_Quantity",product.getProduct_Quantity());
                        updatedproducts.put("MRP_Price",product.getMRP_Price());
                        updatedproducts.put("Retailor_Price", product.getRetailor_Price());
                        updatedproducts.put("Distributor_Price",product.getDistributor_Price());
                        updatedproducts.put("NSR_Price", product.getNSR_Price());
                        updatedproducts.put("Sample_Price",product.getSample_Price());
                        updatedproducts.put("value", product.getValue());
                        updatedproducts.put("Product_Detail_Name", product.getProduct_Detail_Name());
                        updatedproducts.put("Product_Sale_Unit",product.getProduct_Sale_Unit());
                        updatedproducts.put("Territory_Name", ter_name);
                        updatedproducts.put("Target_Price", product.getTarget_Price());
                        updatedjsonArrayproducts.put(updatedproducts);
                    }
                    json.put("Product_data", jsonArrayproducts);
                    Log.v ("products_final",json.toString());
                    finalsubmit(json.toString());
                }
                catch (Exception e){
                }
            }
        });

        docbusinessProductListBinding.searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }
    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filteredNames = new ArrayList<>();
        for (CallCommonCheckedList s : allPrdList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        ProductListAdapter.filterList(filteredNames);
    }

    public void SetUpAdapter() {
        allPrdList = new ArrayList<>();
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.PRODUCT).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (!jsonObject.getString("Code").equalsIgnoreCase("-1")) {
                    String code = jsonObject.getString("Code");
                    String detail_code = jsonObject.getString("Product_Detail_Code");
                    CallCommonCheckedList product = new CallCommonCheckedList(
                            jsonObject.getString("Name"),
                            jsonObject.getString("Pack"),
                            jsonObject.getString("DRate"),
                            code,detail_code, 0,"","","","","","","","");
                    for (ProductListModel selectedProduct : selectedProductList) {
                        if (selectedProduct.Detail_No.equalsIgnoreCase(detail_code)) {
                            try {
                                product.setQty(Integer.parseInt(selectedProduct.Product_Quantity));
                                product.setSelectedMRP(Double.parseDouble(selectedProduct.MRP_Price));
                                int qty = Integer.parseInt(selectedProduct.Product_Quantity);
                                product.setProduct_Sale_Unit(selectedProduct.Product_Sale_Unit);
                                product.setRetailor_Price(selectedProduct.Retailor_Price);
                                product.setDistributor_Price(selectedProduct.Distributor_Price);
                                product.setNSR_Price(selectedProduct.NSR_Price);
                                product.setSample_Price(selectedProduct.Sample_Price);
                                product.setTarget_Price(selectedProduct.Target_Price);
                                double mrp = Double.parseDouble(selectedProduct.MRP_Price);
                                product.setSelectedValue(qty * mrp);
                                product.setSelected(true);
                            }
                            catch (Exception e){
                            }
                            break;
                        }
                    }
                    allPrdList.add(product);
                }
            }
            ProductListAdapter = new DoctorBusinessProductListAdapter(DocBusinessProductList.this, DocBusinessProductList.this, allPrdList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DocBusinessProductList.this);
            docbusinessProductListBinding.rvPrdList.setLayoutManager(mLayoutManager);
            docbusinessProductListBinding.rvPrdList.setItemAnimator(new DefaultItemAnimator());
            docbusinessProductListBinding.rvPrdList.addItemDecoration(new DividerItemDecoration(DocBusinessProductList.this, LinearLayoutManager.VERTICAL));
            docbusinessProductListBinding.rvPrdList.setAdapter(ProductListAdapter);
            ProductListAdapter.countvalues();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCancel() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_no = dialog.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) DocBusinessProductList.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void finalsubmit(String val) {
        try {
            if(progressDialog == null) {
                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(this);
                progressDialog = CommonUtilsMethods.createProgressDialog(this);
                progressDialog.show();
            }
            else {
                progressDialog.show();
            }
            if(isNetworkConnected()) {
                String baseUrl = SharedPref.getBaseWebUrl(this);
                String pathUrl = SharedPref.getPhpPathUrl(this);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                Log.e("test", "login url : " + baseUrl + replacedUrl);
                apiInterface = RetrofitClient.getRetrofit(this, baseUrl + replacedUrl);
                Log.d("save_obj", String.valueOf(val));
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "save/business_product");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(DocBusinessProductList.this), mapString, val);

                if(call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if(response.isSuccessful()) {
                                progressDialog.dismiss();
                                try {
                                    assert response.body() != null;
                                    Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        if(jsonObject.getString("success").equalsIgnoreCase("true")) {
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("doctorCode", Doc_code);
                                            resultIntent.putExtra("doctorName", Doc_name);
                                            resultIntent.putExtra("activeflg", "0");
                                            resultIntent.putExtra("headerno", detailcode);
                                            resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                                            resultIntent.putExtra("updatedValue", String.valueOf(prdtotalvalue));
                                            resultIntent.putExtra("updatedProductJson", updatedjsonArrayproducts.toString());
                                            setResult(RESULT_OK, resultIntent);
                                            finish();
                                        }
                                    }
                                    catch (Exception e) {
                                        Log.v("chkSamStk", "error---" + e);
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                docbusinessProductListBinding.btnSave.setEnabled(true);
                                commonUtilsMethods.showToastMessage(DocBusinessProductList.this, getResources().getString(R.string.something_wrong), true);
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            docbusinessProductListBinding.btnSave.setEnabled(true);
                            commonUtilsMethods.showToastMessage(DocBusinessProductList.this, getResources().getString(R.string.no_network), true);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
            else {
                docbusinessProductListBinding.btnSave.setEnabled(true);
                commonUtilsMethods.showToastMessage(DocBusinessProductList.this, getResources().getString(R.string.no_network), true);
                progressDialog.dismiss();
            }
        }
        catch (Exception e) {
            docbusinessProductListBinding.btnSave.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }
}
