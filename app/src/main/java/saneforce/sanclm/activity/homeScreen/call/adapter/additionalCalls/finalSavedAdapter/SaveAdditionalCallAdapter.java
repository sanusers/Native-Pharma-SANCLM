package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrcallBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.CallAddCustListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallFragment;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.NestedAddInputCallDetails;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.NestedAddSampleCallDetails;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.SaveAdditionalCall;
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class SaveAdditionalCallAdapter extends RecyclerView.Adapter<SaveAdditionalCallAdapter.ViewHolder> {
    public static int pos;
    public static RecyclerView rv_nested_calls_sample_data, rv_nested_calls_input_data;
    public static ArrayList<NestedAddInputCallDetails> nestedAddInputCallDetails;
    public static ArrayList<NestedAddSampleCallDetails> nestedAddSampleCallDetails;
    @SuppressLint("StaticFieldLeak")
    public static AdapterNestedInput adapterNestedInput;
    public static AdapterNestedSample adapterNestedSample;
    public static ArrayList<SaveAdditionalCall> saveAdditionalCalls;
    public static ArrayList<CallCommonCheckedList> checked_arrayList = new ArrayList<>();
    public static ArrayList<NestedAddInputCallDetails> dummyNestedInput = new ArrayList<>();
    public static ArrayList<NestedAddSampleCallDetails> dummyNestedSample = new ArrayList<>();
    Context context;
    Activity activity;
    CommonSharedPreference mCommonsharedpreference;
    CallAddCustListAdapter callAddCustListAdapter;
    CommonUtilsMethods commonUtilsMethods;
    ArrayList<String> dummyNames = new ArrayList<>();


    public SaveAdditionalCallAdapter(Activity activity, Context context, ArrayList<SaveAdditionalCall> saveAdditionalCalls, ArrayList<CallCommonCheckedList> CheckedcustListArrayList) {
        this.activity = activity;
        this.context = context;
        SaveAdditionalCallAdapter.saveAdditionalCalls = saveAdditionalCalls;
        checked_arrayList = CheckedcustListArrayList;
    }

    public SaveAdditionalCallAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> custListArrayList, ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList, ArrayList<NestedAddInputCallDetails> nestedAddInputCallDetails, ArrayList<NestedAddSampleCallDetails> nestedAddSampleCallDetails, ArrayList<NestedAddInputCallDetails> dummyNestedInput, ArrayList<NestedAddSampleCallDetails> dummyNestedSample) {
        this.activity = activity;
        this.context = context;
        SaveAdditionalCallAdapter.checked_arrayList = custListArrayList;
        SaveAdditionalCallAdapter.saveAdditionalCalls = saveAdditionalCallArrayList;
        SaveAdditionalCallAdapter.nestedAddInputCallDetails = nestedAddInputCallDetails;
        SaveAdditionalCallAdapter.nestedAddSampleCallDetails = nestedAddSampleCallDetails;
        SaveAdditionalCallAdapter.dummyNestedInput = dummyNestedInput;
        SaveAdditionalCallAdapter.dummyNestedSample = dummyNestedSample;
    }

    public SaveAdditionalCallAdapter(Context context) {
        this.context = context;
    }

    public static int getPosition() {
        return pos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_calls, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mCommonsharedpreference = new CommonSharedPreference(context);
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(saveAdditionalCalls.get(position).getName());

        rv_nested_calls_sample_data = holder.rv_nested_calls_sample_data;
        rv_nested_calls_input_data = holder.rv_nested_calls_input_data;

        Log.v("countnested", "-input--" + nestedAddInputCallDetails.size() + "---sam---" + nestedAddSampleCallDetails.size());
        dummyNames.clear();
        if (nestedAddInputCallDetails.size() > 0 || nestedAddSampleCallDetails.size() > 0) {
            for (int i = 0; i < nestedAddInputCallDetails.size(); i++) {
                if (nestedAddInputCallDetails.get(i).getCust_name().equalsIgnoreCase(saveAdditionalCalls.get(position).getName())) {
                    dummyNames.add(saveAdditionalCalls.get(position).getName());
                    break;
                }
            }

            for (int i = 0; i < nestedAddSampleCallDetails.size(); i++) {
                if (nestedAddSampleCallDetails.get(i).getCust_name().equalsIgnoreCase(saveAdditionalCalls.get(position).getName())) {
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


        if (holder.img_view_rv.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.arrow_right_rv).getConstantState())) {
            AssignRVInputSampleSingle(holder.rv_nested_calls_input_data, holder.rv_nested_calls_sample_data, holder.getAdapterPosition());
        }

        holder.img_view_rv.setOnClickListener(view -> {
            if (holder.img_view_rv.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.arrow_right_rv).getConstantState())) {
                holder.img_view_rv.setImageDrawable(context.getResources().getDrawable(R.drawable.arrow_rv_down));
                AssignRVInputSampleFull(holder.rv_nested_calls_input_data, holder.rv_nested_calls_sample_data, holder.getAdapterPosition());
            } else {
                holder.img_view_rv.setImageDrawable(context.getResources().getDrawable(R.drawable.arrow_right_rv));
                AssignRVInputSampleSingle(holder.rv_nested_calls_input_data, holder.rv_nested_calls_sample_data, holder.getAdapterPosition());
            }
        });

        //  if (mCommonsharedpreference.getBooleanValueFromPreference("checked_add_call") && !mCommonsharedpreference.getValueFromPreference("unselect_data_add_call").isEmpty()) {
        if (CallAddCustListAdapter.isCheckedAddCall && !CallAddCustListAdapter.UnSelectedDrCode.isEmpty()) {
            for (int i = 0; i < saveAdditionalCalls.size(); i++) {
                if (CallAddCustListAdapter.UnSelectedDrCode.equalsIgnoreCase(saveAdditionalCalls.get(position).getName())) {
                    new CountDownTimer(200, 200) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            pos = position;
                            removeAt(position);
                            CallAddCustListAdapter.isCheckedAddCall = false;
                            //   mCommonsharedpreference.setValueToPreference("checked_add_call", false);
                        }
                    }.start();
                    break;
                }
            }
        }


        holder.tv_name.setOnClickListener(view -> {
                commonUtilsMethods.displayPopupWindow(activity, context, view, saveAdditionalCalls.get(position).getName());
        });


        holder.img_del_add_call.setOnClickListener(view -> {
            for (int j = 0; j < checked_arrayList.size(); j++) {
                if (checked_arrayList.get(j).getName().equalsIgnoreCase(saveAdditionalCalls.get(position).getName())) {
                    checked_arrayList.set(j, new CallCommonCheckedList(saveAdditionalCalls.get(position).getName(),saveAdditionalCalls.get(position).getCode(), false));
                }
            }

            callAddCustListAdapter = new CallAddCustListAdapter(activity, context, checked_arrayList, saveAdditionalCalls);
            commonUtilsMethods.recycleTestWithDivider(AdditionalCallFragment.rv_list_data);
            AdditionalCallFragment.rv_list_data.setAdapter(callAddCustListAdapter);
            removeAt(position);
        });
        holder.tag_add_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdditionalCallDetailedSide.addInputAdditionalCallArrayList = new ArrayList<>();
                AdditionalCallDetailedSide.addSampleAdditionalCallArrayList = new ArrayList<>();
                mCommonsharedpreference.setValueToPreference("selected_add_call_name", saveAdditionalCalls.get(position).getName());

                AdditionalCallDetailedSide.addInputAdditionalCallArrayList.add(new AddInputAdditionalCall(saveAdditionalCalls.get(position).getName(), "Select", "", ""));
                AdditionalCallDetailedSide.adapterInputAdditionalCall = new AdapterInputAdditionalCall(context, AdditionalCallDetailedSide.addInputAdditionalCallArrayList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
                AdditionalCallDetailedSide.rv_add_input_list.setLayoutManager(mLayoutManager);
                AdditionalCallDetailedSide.rv_add_input_list.setAdapter(AdditionalCallDetailedSide.adapterInputAdditionalCall);

                AdditionalCallDetailedSide.addSampleAdditionalCallArrayList.add(new AddSampleAdditionalCall(saveAdditionalCalls.get(position).getName(), "Select", "", ""));
                AdditionalCallDetailedSide.adapterSampleAdditionalCall = new AdapterSampleAdditionalCall(context, AdditionalCallDetailedSide.addSampleAdditionalCallArrayList);
                RecyclerView.LayoutManager mLayoutManagerprd = new LinearLayoutManager(activity);
                AdditionalCallDetailedSide.rv_add_sample_list.setLayoutManager(mLayoutManagerprd);
                AdditionalCallDetailedSide.rv_add_sample_list.setAdapter(AdditionalCallDetailedSide.adapterSampleAdditionalCall);

                dcrcallBinding.fragmentAddCallDetailsSide.setVisibility(View.VISIBLE);
            }
        });

        holder.tv_edit.setOnClickListener(view -> dcrcallBinding.fragmentAddCallDetailsSide.setVisibility(View.VISIBLE));

    }

    private void AssignRVInputSampleFull(RecyclerView rv_nested_calls_input_data, RecyclerView rv_nested_calls_sample_data, int adapterPosition) {
        dummyNestedInput.clear();
        dummyNestedSample.clear();
        for (int i = 0; i < nestedAddInputCallDetails.size(); i++) {
            if (nestedAddInputCallDetails.get(i).getCust_name().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getName())) {
                dummyNestedInput.add(new NestedAddInputCallDetails(nestedAddInputCallDetails.get(i).getCust_name(), nestedAddInputCallDetails.get(i).getInp_name(), nestedAddInputCallDetails.get(i).getInp_qty()));
            }
        }
        adapterNestedInput = new AdapterNestedInput(context, dummyNestedInput);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rv_nested_calls_input_data.setLayoutManager(mLayoutManager);
        rv_nested_calls_input_data.setItemAnimator(new DefaultItemAnimator());
        rv_nested_calls_input_data.setAdapter(adapterNestedInput);


        for (int i = 0; i < nestedAddSampleCallDetails.size(); i++) {
            if (nestedAddSampleCallDetails.get(i).getCust_name().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getName())) {
                dummyNestedSample.add(new NestedAddSampleCallDetails(nestedAddSampleCallDetails.get(i).getCust_name(), nestedAddSampleCallDetails.get(i).getSam_name(), nestedAddSampleCallDetails.get(i).getSam_qty(), nestedAddSampleCallDetails.get(i).getRx_qty()));
            }
        }
        adapterNestedSample = new AdapterNestedSample(context, dummyNestedSample);
        RecyclerView.LayoutManager mLayoutManagersam = new LinearLayoutManager(context);
        rv_nested_calls_sample_data.setLayoutManager(mLayoutManagersam);
        rv_nested_calls_sample_data.setItemAnimator(new DefaultItemAnimator());
        rv_nested_calls_sample_data.setAdapter(adapterNestedSample);

    }

    private void AssignRVInputSampleSingle(RecyclerView rv_nested_calls_input_data, RecyclerView rv_nested_calls_sample_data, int adapterPosition) {
        Log.v("pos", "---" + saveAdditionalCalls.get(adapterPosition).getName());
        dummyNestedInput.clear();
        dummyNestedSample.clear();
        if (nestedAddInputCallDetails.size() > 0) {
            for (int i = 0; i < nestedAddInputCallDetails.size(); i++) {
                if (nestedAddInputCallDetails.get(i).getCust_name().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getName())) {
                    dummyNestedInput.add(new NestedAddInputCallDetails(nestedAddInputCallDetails.get(0).getCust_name(), nestedAddInputCallDetails.get(0).getInp_name(), nestedAddInputCallDetails.get(0).getInp_qty()));
                    break;
                }
            }
        }

        adapterNestedInput = new AdapterNestedInput(context, dummyNestedInput);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rv_nested_calls_input_data.setLayoutManager(mLayoutManager);
        rv_nested_calls_input_data.setItemAnimator(new DefaultItemAnimator());
        rv_nested_calls_input_data.setAdapter(adapterNestedInput);

        for (int i = 0; i < nestedAddSampleCallDetails.size(); i++) {
            if (nestedAddSampleCallDetails.get(i).getCust_name().equalsIgnoreCase(saveAdditionalCalls.get(adapterPosition).getName())) {
                dummyNestedSample.add(new NestedAddSampleCallDetails(nestedAddSampleCallDetails.get(0).getCust_name(), nestedAddSampleCallDetails.get(0).getSam_name(), nestedAddSampleCallDetails.get(0).getSam_qty(), nestedAddSampleCallDetails.get(0).getRx_qty()));
                break;
            }
        }
        adapterNestedSample = new AdapterNestedSample(context, dummyNestedSample);
        RecyclerView.LayoutManager mLayoutManagersam = new LinearLayoutManager(context);
        rv_nested_calls_sample_data.setLayoutManager(mLayoutManagersam);
        rv_nested_calls_sample_data.setItemAnimator(new DefaultItemAnimator());
        rv_nested_calls_sample_data.setAdapter(adapterNestedSample);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
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
