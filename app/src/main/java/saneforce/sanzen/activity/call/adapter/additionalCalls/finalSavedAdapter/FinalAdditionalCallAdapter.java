package saneforce.sanzen.activity.call.adapter.additionalCalls.finalSavedAdapter;

import static saneforce.sanzen.activity.call.DCRCallActivity.StockInput;
import static saneforce.sanzen.activity.call.DCRCallActivity.StockSample;
import static saneforce.sanzen.activity.call.DCRCallActivity.clickedLocalDate;
import static saneforce.sanzen.activity.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanzen.activity.call.DCRCallActivity.isFromActivity;
import static saneforce.sanzen.activity.call.adapter.product.CheckProductListAdapter.saveCallProductListArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.adapter.input.CheckInputListAdapter;
import saneforce.sanzen.activity.call.adapter.input.FinalInputCallAdapter;
import saneforce.sanzen.activity.call.fragments.additionalCall.AdditionalCallDetailedSide;
import saneforce.sanzen.activity.call.fragments.additionalCall.AdditionalCallFragment;
import saneforce.sanzen.activity.call.fragments.input.InputFragment;
import saneforce.sanzen.activity.call.fragments.product.ProductFragment;
import saneforce.sanzen.activity.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanzen.activity.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanzen.activity.call.pojo.additionalCalls.SaveAdditionalCall;
import saneforce.sanzen.activity.call.pojo.product.SaveCallProductList;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.additionalCalls.AdditionalCusListAdapter;
import saneforce.sanzen.activity.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall;
import saneforce.sanzen.activity.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall;
import saneforce.sanzen.activity.call.adapter.product.FinalProductCallAdapter;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.activity.call.pojo.input.SaveCallInputList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;


public class FinalAdditionalCallAdapter extends RecyclerView.Adapter<FinalAdditionalCallAdapter.ViewHolder> {
    public static RecyclerView rv_nested_calls_sample_data, rv_nested_calls_input_data;
    public static ArrayList<AddInputAdditionalCall> nestedInput;
    public static ArrayList<AddSampleAdditionalCall> nestedProduct;
    @SuppressLint("StaticFieldLeak")
    public static AdapterNestedInput adapterNestedInput;
    @SuppressLint("StaticFieldLeak")
    public static AdapterNestedProduct adapterNestedProduct;
    public static ArrayList<SaveAdditionalCall> saveAdditionalCalls;
    public static ArrayList<CallCommonCheckedList> checked_arrayList = new ArrayList<>();
    public static ArrayList<AddInputAdditionalCall> dummyNestedInput = new ArrayList<>();
    public static ArrayList<AddSampleAdditionalCall> dummyNestedSample = new ArrayList<>();
    public static String Selected_name, Selected_code;
    public static String New_Edit;
    Context context;
    Activity activity;
    AdditionalCusListAdapter additionalCusListAdapter;
    CommonUtilsMethods commonUtilsMethods;
    ArrayList<String> dummyNames = new ArrayList<>();
    FinalInputCallAdapter finalInputCallAdapter;
    FinalProductCallAdapter finalProductCallAdapter;

    RoomDB roomDB;

    MasterDataDao masterDataDao;

    public FinalAdditionalCallAdapter(Activity activity, Context context, ArrayList<SaveAdditionalCall> saveAdditionalCalls, ArrayList<CallCommonCheckedList> CheckedCusListArrayList) {
        this.activity = activity;
        this.context = context;
        FinalAdditionalCallAdapter.saveAdditionalCalls = saveAdditionalCalls;
        checked_arrayList = CheckedCusListArrayList;

        roomDB=RoomDB.getDatabase(context);
        masterDataDao=roomDB.masterDataDao();
    }

    public FinalAdditionalCallAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> cusListArrayList, ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList, ArrayList<AddInputAdditionalCall> nestedInput, ArrayList<AddSampleAdditionalCall> nestedProduct, ArrayList<AddInputAdditionalCall> dummyNestedInput, ArrayList<AddSampleAdditionalCall> dummyNestedSample) {
        this.activity = activity;
        this.context = context;
        FinalAdditionalCallAdapter.checked_arrayList = cusListArrayList;
        FinalAdditionalCallAdapter.saveAdditionalCalls = saveAdditionalCallArrayList;
        FinalAdditionalCallAdapter.nestedInput = nestedInput;
        FinalAdditionalCallAdapter.nestedProduct = nestedProduct;
        FinalAdditionalCallAdapter.dummyNestedInput = dummyNestedInput;
        FinalAdditionalCallAdapter.dummyNestedSample = dummyNestedSample;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_calls, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(saveAdditionalCalls.get(position).getName());

        rv_nested_calls_sample_data = holder.rv_nested_calls_sample_data;
        rv_nested_calls_input_data = holder.rv_nested_calls_input_data;

        dummyNames.clear();
        if (nestedInput.size() > 0 || nestedProduct.size() > 0) {
            for (int i = 0; i < nestedInput.size(); i++) {
                if (nestedInput.get(i).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(position).getCode())) {
                    dummyNames.add(saveAdditionalCalls.get(position).getName());
                    break;
                }
            }

            for (int i = 0; i < nestedProduct.size(); i++) {
                if (nestedProduct.get(i).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(position).getCode())) {
                    dummyNames.add(saveAdditionalCalls.get(position).getName());
                    break;
                }
            }
        }

        if (dummyNames.size() > 0) {
            holder.rv_nested_calls_sample_data.setVisibility(View.VISIBLE);
            holder.rv_nested_calls_input_data.setVisibility(View.VISIBLE);
            holder.tv_edit.setVisibility(View.VISIBLE);
            holder.tag_add_details.setVisibility(View.GONE);
            holder.img_view_rv.setVisibility(View.VISIBLE);
        } else {
            holder.rv_nested_calls_sample_data.setVisibility(View.GONE);
            holder.rv_nested_calls_input_data.setVisibility(View.GONE);
            holder.tv_edit.setVisibility(View.GONE);
            holder.tag_add_details.setVisibility(View.VISIBLE);
            holder.img_view_rv.setVisibility(View.GONE);
        }

        if (saveAdditionalCalls.get(position).isSamInpView()) {
            AssignRVInputSampleFull(holder.rv_nested_calls_input_data, holder.rv_nested_calls_sample_data, holder.getBindingAdapterPosition());
            holder.img_view_rv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.arrow_rv_down));
        } else {
            AssignRVInputSampleSingle(holder.rv_nested_calls_input_data, holder.rv_nested_calls_sample_data, holder.getBindingAdapterPosition());
            holder.img_view_rv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.arrow_right_rv));
        }

        holder.img_view_rv.setOnClickListener(view -> {
            if (Objects.equals(holder.img_view_rv.getDrawable().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.arrow_right_rv)).getConstantState())) {
                holder.img_view_rv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.arrow_rv_down));
                AssignRVInputSampleFull(holder.rv_nested_calls_input_data, holder.rv_nested_calls_sample_data, holder.getBindingAdapterPosition());
                saveAdditionalCalls.set(position, new SaveAdditionalCall(saveAdditionalCalls.get(position).getName(), saveAdditionalCalls.get(position).getCode(), saveAdditionalCalls.get(position).getTown_name(), saveAdditionalCalls.get(position).getTown_code(), true));
            } else {
                holder.img_view_rv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.arrow_right_rv));
                AssignRVInputSampleSingle(holder.rv_nested_calls_input_data, holder.rv_nested_calls_sample_data, holder.getBindingAdapterPosition());
                saveAdditionalCalls.set(position, new SaveAdditionalCall(saveAdditionalCalls.get(position).getName(), saveAdditionalCalls.get(position).getCode(), saveAdditionalCalls.get(position).getTown_name(), saveAdditionalCalls.get(position).getTown_code(), false));
            }
        });

        if (AdditionalCusListAdapter.isCheckedAddCall && !AdditionalCusListAdapter.UnSelectedDrCode.isEmpty()) {
            for (int i = 0; i < saveAdditionalCalls.size(); i++) {
                if (AdditionalCusListAdapter.UnSelectedDrCode.equalsIgnoreCase(saveAdditionalCalls.get(position).getCode())) {
                    new CountDownTimer(80, 80) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            try {
                                updateInputStock(holder.getBindingAdapterPosition());
                                updateProductStock(holder.getBindingAdapterPosition());
                                removeAt(position);
                                AdditionalCusListAdapter.isCheckedAddCall = false;
                            } catch (Exception ignored) {

                            }
                        }
                    }.start();
                    break;
                }
            }
        }


        holder.tv_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, saveAdditionalCalls.get(position).getName()));


        holder.img_del_add_call.setOnClickListener(view -> {
            for (int j = 0; j < checked_arrayList.size(); j++) {
                if (checked_arrayList.get(j).getCode().equalsIgnoreCase(saveAdditionalCalls.get(position).getCode())) {
                    checked_arrayList.set(j, new CallCommonCheckedList(saveAdditionalCalls.get(position).getName(), saveAdditionalCalls.get(position).getCode(), false));
                }
            }
            updateInputStock(holder.getBindingAdapterPosition());
            updateProductStock(holder.getBindingAdapterPosition());

            try {
                if (isFromActivity.equalsIgnoreCase("edit_local")) {

                    JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(clickedLocalDate) && jsonObject.getString("CustCode").equalsIgnoreCase(saveAdditionalCalls.get(position).getCode())) {
                            jsonArray.remove(i);
                            break;
                        }
                    }

                    MasterDataTable inputdata =new MasterDataTable();
                    inputdata.setMasterKey(Constants.CALL_SYNC);
                    inputdata.setMasterValues(jsonArray.toString());
                    inputdata.setSyncStatus(0);
                    MasterDataTable nChecked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
                    if(nChecked !=null){
                        masterDataDao.updateData(Constants.CALL_SYNC, jsonArray.toString());
                    }else {
                        masterDataDao.insert(inputdata);
                    }

                }
            } catch (Exception ignored) {
            }

            additionalCusListAdapter = new AdditionalCusListAdapter(activity, context, checked_arrayList, saveAdditionalCalls);
            commonUtilsMethods.recycleTestWithDivider(AdditionalCallFragment.additionalCallBinding.rvCheckDataList);
            AdditionalCallFragment.additionalCallBinding.rvCheckDataList.setAdapter(additionalCusListAdapter);
            removeAt(position);
        });

        holder.tag_add_details.setOnClickListener(view -> {
            New_Edit = "New";
            AdditionalCallDetailedSide.addInputAdditionalCallArrayList.clear();
            AdditionalCallDetailedSide.addProductAdditionalCallArrayList.clear();
            Selected_name = saveAdditionalCalls.get(position).getName();
            Selected_code = saveAdditionalCalls.get(position).getCode();

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            AdditionalCallDetailedSide.callDetailsSideBinding.rvAddInputsAdditional.setLayoutManager(mLayoutManager);
            commonUtilsMethods.recycleTestWithoutDivider(AdditionalCallDetailedSide.callDetailsSideBinding.rvAddInputsAdditional);
            AdditionalCallDetailedSide.callDetailsSideBinding.rvAddInputsAdditional.setAdapter(AdditionalCallDetailedSide.adapterInputAdditionalCall);
            AdditionalCallDetailedSide.adapterInputAdditionalCall.notifyDataSetChanged();

            RecyclerView.LayoutManager mLayoutManagerPrd = new LinearLayoutManager(context);
            AdditionalCallDetailedSide.callDetailsSideBinding.rvAddSampleAdditional.setLayoutManager(mLayoutManagerPrd);
            commonUtilsMethods.recycleTestWithoutDivider(AdditionalCallDetailedSide.callDetailsSideBinding.rvAddSampleAdditional);
            AdditionalCallDetailedSide.callDetailsSideBinding.rvAddSampleAdditional.setAdapter(AdditionalCallDetailedSide.adapterSampleAdditionalCall);
            AdditionalCallDetailedSide.adapterSampleAdditionalCall.notifyDataSetChanged();

            dcrCallBinding.fragmentAddCallDetailsSide.setVisibility(View.VISIBLE);
        });

        holder.tv_edit.setOnClickListener(view -> {
            New_Edit = "Edit";
            AdditionalCallDetailedSide.addInputAdditionalCallArrayList.clear();
            AdditionalCallDetailedSide.editedInpList.clear();
            AdditionalCallDetailedSide.addProductAdditionalCallArrayList.clear();
            AdditionalCallDetailedSide.editedPrdList.clear();
            Selected_name = saveAdditionalCalls.get(position).getName();
            Selected_code = saveAdditionalCalls.get(position).getCode();

            for (int i = 0; i < nestedInput.size(); i++) {
                if (nestedInput.get(i).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(holder.getBindingAdapterPosition()).getCode())) {
                    for (int j = 0; j < StockInput.size(); j++) {
                        int final_value;
                        if (StockInput.get(j).getStockCode().equalsIgnoreCase(nestedInput.get(i).getInput_code())) {
                            if (nestedInput.get(i).getInp_qty().equalsIgnoreCase("0") || nestedInput.get(i).getInp_qty().isEmpty()) {
                                final_value = Integer.parseInt(StockInput.get(j).getCurrentStock());
                            } else {
                                final_value = Integer.parseInt(StockInput.get(j).getCurrentStock()) + Integer.parseInt(nestedInput.get(i).getInp_qty());
                            }
                            AdditionalCallDetailedSide.addInputAdditionalCallArrayList.add(new AddInputAdditionalCall(nestedInput.get(i).getCust_name(), nestedInput.get(i).getCust_code(), nestedInput.get(i).getInput_name(), nestedInput.get(i).getInput_code(), StockInput.get(j).getCurrentStock(), String.valueOf(final_value), nestedInput.get(i).getInp_qty()));
                            AdditionalCallDetailedSide.editedInpList.add(new AddInputAdditionalCall(nestedInput.get(i).getCust_name(), nestedInput.get(i).getCust_code(), nestedInput.get(i).getInput_name(), nestedInput.get(i).getInput_code(), StockInput.get(j).getCurrentStock(), String.valueOf(final_value), nestedInput.get(i).getInp_qty()));
                        }
                    }
                }
            }

            AdditionalCallDetailedSide.adapterInputAdditionalCall = new AdapterInputAdditionalCall(context, AdditionalCallDetailedSide.addInputAdditionalCallArrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
            AdditionalCallDetailedSide.callDetailsSideBinding.rvAddInputsAdditional.setLayoutManager(mLayoutManager);
            AdditionalCallDetailedSide.callDetailsSideBinding.rvAddInputsAdditional.setAdapter(AdditionalCallDetailedSide.adapterInputAdditionalCall);
            AdditionalCallDetailedSide.adapterInputAdditionalCall.notifyDataSetChanged();


            for (int i = 0; i < nestedProduct.size(); i++) {
                if (nestedProduct.get(i).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(position).getCode())) {
                    for (int j = 0; j < StockSample.size(); j++) {
                        int final_value;
                        if (StockSample.get(j).getStockCode().equalsIgnoreCase(nestedProduct.get(i).getPrd_code())) {
                            if (nestedProduct.get(i).getSample_qty().equalsIgnoreCase("0") || nestedProduct.get(i).getSample_qty().isEmpty()) {
                                final_value = Integer.parseInt(StockSample.get(j).getCurrentStock());
                            } else {
                                final_value = Integer.parseInt(StockSample.get(j).getCurrentStock()) + Integer.parseInt(nestedProduct.get(i).getSample_qty());
                            }
                            AdditionalCallDetailedSide.addProductAdditionalCallArrayList.add(new AddSampleAdditionalCall(nestedProduct.get(i).getCust_name(), nestedProduct.get(i).getCust_code(), nestedProduct.get(i).getPrd_name(), nestedProduct.get(i).getPrd_code(), StockSample.get(j).getCurrentStock(), String.valueOf(final_value), nestedProduct.get(i).getSample_qty(), nestedProduct.get(i).getCategory()));
                            AdditionalCallDetailedSide.editedPrdList.add(new AddSampleAdditionalCall(nestedProduct.get(i).getCust_name(), nestedProduct.get(i).getCust_code(), nestedProduct.get(i).getPrd_name(), nestedProduct.get(i).getPrd_code(), StockSample.get(j).getCurrentStock(), String.valueOf(final_value), nestedProduct.get(i).getSample_qty(), nestedProduct.get(i).getCategory()));
                        }
                    }
                }
            }

            AdditionalCallDetailedSide.adapterSampleAdditionalCall = new AdapterSampleAdditionalCall(context, AdditionalCallDetailedSide.addProductAdditionalCallArrayList);
            RecyclerView.LayoutManager mLayoutManagerPrd = new LinearLayoutManager(activity);
            AdditionalCallDetailedSide.callDetailsSideBinding.rvAddSampleAdditional.setLayoutManager(mLayoutManagerPrd);
            AdditionalCallDetailedSide.callDetailsSideBinding.rvAddSampleAdditional.setAdapter(AdditionalCallDetailedSide.adapterSampleAdditionalCall);
            AdditionalCallDetailedSide.adapterSampleAdditionalCall.notifyDataSetChanged();

            dcrCallBinding.fragmentAddCallDetailsSide.setVisibility(View.VISIBLE);
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void updateProductStock(int adapterPosition) {
        for (int j = 0; j < nestedProduct.size(); j++) {
            if (nestedProduct.get(j).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getCode())) {
                if (DCRCallActivity.SampleValidation.equalsIgnoreCase("1")) {
                    for (int i = 0; i < StockSample.size(); i++) {
                        int currentBalance;
                        if (StockSample.get(i).getStockCode().equalsIgnoreCase(nestedProduct.get(j).getPrd_code())) {
                            if (nestedProduct.get(j).getSample_qty().equalsIgnoreCase("0") || nestedProduct.get(j).getSample_qty().isEmpty()) {
                                currentBalance = Integer.parseInt(StockSample.get(i).getCurrentStock());
                            } else {
                                currentBalance = Integer.parseInt(StockSample.get(i).getCurrentStock()) + Integer.parseInt(nestedProduct.get(j).getSample_qty());
                            }
                            StockSample.set(i, new CallCommonCheckedList(StockSample.get(i).getStockCode(), StockSample.get(i).getActualStock(), String.valueOf(currentBalance)));

                            if (saveCallProductListArrayList.size() > 0) {
                                for (int k = 0; k < saveCallProductListArrayList.size(); k++) {
                                    if (saveCallProductListArrayList.get(k).getCode().equalsIgnoreCase(nestedProduct.get(j).getPrd_code())) {
                                        int finalBalance;
                                        if (saveCallProductListArrayList.get(k).getSample_qty().equalsIgnoreCase("0") || saveCallProductListArrayList.get(k).getSample_qty().isEmpty()) {
                                            finalBalance = Integer.parseInt(StockSample.get(i).getCurrentStock());
                                        } else {
                                            finalBalance = Integer.parseInt(StockSample.get(i).getCurrentStock()) + Integer.parseInt(saveCallProductListArrayList.get(k).getSample_qty());
                                        }
                                        saveCallProductListArrayList.set(k, new SaveCallProductList(saveCallProductListArrayList.get(k).getName(), saveCallProductListArrayList.get(k).getCode(), saveCallProductListArrayList.get(k).getCategory(), StockSample.get(i).getCurrentStock(), String.valueOf(finalBalance), saveCallProductListArrayList.get(k).getSample_qty(), saveCallProductListArrayList.get(k).getRx_qty(), saveCallProductListArrayList.get(k).getRcpa_qty(), saveCallProductListArrayList.get(k).getPromoted(), saveCallProductListArrayList.get(k).isClicked()));
                                    }
                                }
                                finalProductCallAdapter = new FinalProductCallAdapter(activity, context, saveCallProductListArrayList, ProductFragment.checkedPrdList);
                                ProductFragment.productsBinding.rvListPrd.setAdapter(finalProductCallAdapter);
                                finalProductCallAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                nestedProduct.remove(j);
                j--;
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateInputStock(int adapterPosition) {
        for (int j = 0; j < nestedInput.size(); j++) {
            if (nestedInput.get(j).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getCode())) {
                if (DCRCallActivity.InputValidation.equalsIgnoreCase("1")) {
                    for (int i = 0; i < StockInput.size(); i++) {
                        int currentBalance;
                        if (StockInput.get(i).getStockCode().equalsIgnoreCase(nestedInput.get(j).getInput_code())) {
                            if (nestedInput.get(j).getInp_qty().equalsIgnoreCase("0") || nestedInput.get(j).getInp_qty().isEmpty()) {
                                currentBalance = Integer.parseInt(StockInput.get(i).getCurrentStock());
                            } else {
                                currentBalance = Integer.parseInt(StockInput.get(i).getCurrentStock()) + Integer.parseInt(nestedInput.get(j).getInp_qty());
                            }
                            StockInput.set(i, new CallCommonCheckedList(StockInput.get(i).getStockCode(), StockInput.get(i).getActualStock(), String.valueOf(currentBalance)));

                            if (CheckInputListAdapter.saveCallInputListArrayList.size() > 0) {
                                for (int k = 0; k < CheckInputListAdapter.saveCallInputListArrayList.size(); k++) {
                                    if (CheckInputListAdapter.saveCallInputListArrayList.get(k).getInp_code().equalsIgnoreCase(nestedInput.get(j).getInput_code())) {
                                        int finalBalance;
                                        if (CheckInputListAdapter.saveCallInputListArrayList.get(k).getInp_qty().equalsIgnoreCase("0") || CheckInputListAdapter.saveCallInputListArrayList.get(k).getInp_qty().isEmpty()) {
                                            finalBalance = Integer.parseInt(StockInput.get(i).getCurrentStock());
                                        } else {
                                            finalBalance = Integer.parseInt(StockInput.get(i).getCurrentStock()) + Integer.parseInt(CheckInputListAdapter.saveCallInputListArrayList.get(k).getInp_qty());
                                        }
                                        CheckInputListAdapter.saveCallInputListArrayList.set(k, new SaveCallInputList(CheckInputListAdapter.saveCallInputListArrayList.get(k).getInput_name(), CheckInputListAdapter.saveCallInputListArrayList.get(k).getInp_code(), CheckInputListAdapter.saveCallInputListArrayList.get(k).getInp_qty(), StockInput.get(i).getCurrentStock(), String.valueOf(finalBalance)));
                                    }
                                }
                                finalInputCallAdapter = new FinalInputCallAdapter(activity, context, CheckInputListAdapter.saveCallInputListArrayList, InputFragment.checkedInputList);
                                InputFragment.fragmentInputBinding.rvListInput.setAdapter(finalInputCallAdapter);
                                finalInputCallAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                nestedInput.remove(j);
                j--;
            }
        }
    }

    private void AssignRVInputSampleFull(RecyclerView rv_nested_calls_input_data, RecyclerView rv_nested_calls_sample_data, int adapterPosition) {
        dummyNestedInput = new ArrayList<>();
        dummyNestedSample = new ArrayList<>();
        for (int i = 0; i < nestedInput.size(); i++) {
            if (nestedInput.get(i).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getCode())) {
                if (!nestedInput.get(i).getInput_name().equalsIgnoreCase("Select") && !nestedInput.get(i).getInput_name().isEmpty()) {
                    dummyNestedInput.add(new AddInputAdditionalCall(nestedInput.get(i).getCust_name(), nestedInput.get(i).getCust_code(), nestedInput.get(i).getInput_name(), nestedInput.get(i).getInp_qty()));
                }
            }
        }

        adapterNestedInput = new AdapterNestedInput(activity, context, dummyNestedInput);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rv_nested_calls_input_data.setLayoutManager(mLayoutManager);
        rv_nested_calls_input_data.setAdapter(adapterNestedInput);

        for (int i = 0; i < nestedProduct.size(); i++) {
            if (nestedProduct.get(i).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getCode())) {
                if (!nestedProduct.get(i).getPrd_name().equalsIgnoreCase("Select") && !nestedProduct.get(i).getPrd_name().isEmpty()) {
                    dummyNestedSample.add(new AddSampleAdditionalCall(nestedProduct.get(i).getCust_name(), nestedProduct.get(i).getCust_code(), nestedProduct.get(i).getPrd_name(), nestedProduct.get(i).getSample_qty()));
                }
            }
        }

        adapterNestedProduct = new AdapterNestedProduct(activity, context, dummyNestedSample);
        RecyclerView.LayoutManager mLayoutManagerSam = new LinearLayoutManager(context);
        rv_nested_calls_sample_data.setLayoutManager(mLayoutManagerSam);
        rv_nested_calls_sample_data.setAdapter(adapterNestedProduct);
    }

    private void AssignRVInputSampleSingle(RecyclerView rv_nested_calls_input_data, RecyclerView rv_nested_calls_sample_data, int adapterPosition) {
        dummyNestedInput = new ArrayList<>();
        dummyNestedSample = new ArrayList<>();
        if (nestedInput.size() > 0) {
            for (int i = 0; i < nestedInput.size(); i++) {
                if (nestedInput.get(i).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getCode())) {
                    if (!nestedInput.get(i).getInput_name().equalsIgnoreCase("Select") && !nestedInput.get(i).getInput_name().isEmpty()) {
                        dummyNestedInput.add(new AddInputAdditionalCall(nestedInput.get(i).getCust_name(), nestedInput.get(i).getCust_code(), nestedInput.get(i).getInput_name(), nestedInput.get(i).getInp_qty()));
                        break;
                    }
                }
            }
        }

        adapterNestedInput = new AdapterNestedInput(activity, context, dummyNestedInput);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rv_nested_calls_input_data.setLayoutManager(mLayoutManager);
        rv_nested_calls_input_data.setAdapter(adapterNestedInput);

        for (int i = 0; i < nestedProduct.size(); i++) {
            if (nestedProduct.get(i).getCust_code().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getCode())) {
                if (!nestedProduct.get(i).getPrd_name().equalsIgnoreCase("Select") && !nestedProduct.get(i).getPrd_name().isEmpty()) {
                    dummyNestedSample.add(new AddSampleAdditionalCall(nestedProduct.get(i).getCust_name(), nestedProduct.get(i).getCust_code(), nestedProduct.get(i).getPrd_name(), nestedProduct.get(i).getSample_qty()));
                    break;
                }
            }
        }

        adapterNestedProduct = new AdapterNestedProduct(activity, context, dummyNestedSample);
        RecyclerView.LayoutManager mLayoutManagerSam = new LinearLayoutManager(context);
        rv_nested_calls_sample_data.setLayoutManager(mLayoutManagerSam);
        rv_nested_calls_sample_data.setAdapter(adapterNestedProduct);
    }


    @Override
    public int getItemCount() {
        return saveAdditionalCalls.size();
    }

    public void removeAt(int position) {
        saveAdditionalCalls.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, saveAdditionalCalls.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tag_add_details, tv_edit;
        RecyclerView rv_nested_calls_sample_data, rv_nested_calls_input_data;
        ImageView img_del_add_call, img_view_rv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_cust_name);
            rv_nested_calls_sample_data = itemView.findViewById(R.id.rv_additional_calls_sample_data);
            rv_nested_calls_input_data = itemView.findViewById(R.id.rv_additional_calls_input_data);
            tag_add_details = itemView.findViewById(R.id.tag_add_data);
            img_del_add_call = itemView.findViewById(R.id.img_del_additional_call);
            img_view_rv = itemView.findViewById(R.id.iv_img_view);
            tv_edit = itemView.findViewById(R.id.tv_edit_calls);
        }
    }
}