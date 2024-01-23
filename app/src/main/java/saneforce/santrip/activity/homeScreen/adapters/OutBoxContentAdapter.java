package saneforce.santrip.activity.homeScreen.adapters;

import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;

import saneforce.santrip.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class OutBoxContentAdapter extends RecyclerView.Adapter<OutBoxContentAdapter.listDataViewholider> {
    Context context;
    ArrayList<ChildListModelClass> childListModelClasses;
    OutBoxCallAdapter outBoxCallAdapter;
    SQLite sqLite;
    ApiInterface apiInterface;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    OutBoxECAdapter outBoxECAdapter;

    public OutBoxContentAdapter(Context context, ArrayList<ChildListModelClass> groupModelClasses) {
        this.context = context;
        this.childListModelClasses = groupModelClasses;
        sqLite = new SQLite(context);
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
    }

    @NonNull
    @Override
    public OutBoxContentAdapter.listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outbox_content_view, parent, false);
        return new OutBoxContentAdapter.listDataViewholider(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxContentAdapter.listDataViewholider holder, int position) {
        ChildListModelClass contentList = childListModelClasses.get(position);
        Log.v("outBox", "---" + contentList.getChildName() + "--count--" + childListModelClasses.size() + "----" + contentList.isAvailableList() + "---" + contentList.getCounts());
        holder.tvContentList.setText(contentList.getChildName());
        if (contentList.isAvailableList()) {
            holder.expandContentView.setEnabled(true);
            holder.img_expand_child.setVisibility(View.VISIBLE);
            holder.tvCount.setVisibility(View.VISIBLE);
        } else {
            holder.expandContentView.setEnabled(false);
            holder.constraintRv.setVisibility(View.GONE);
            holder.img_expand_child.setVisibility(View.GONE);
            holder.tvCount.setVisibility(View.GONE);
        }


        if (contentList.getChildId() == 2) {
            holder.tvCount.setText(String.valueOf(contentList.getOutBoxCallLists().size()));
            if (contentList.isExpanded() && contentList.getOutBoxCallLists().size() > 0) {
                holder.constraintRv.setVisibility(View.VISIBLE);
                outBoxCallAdapter = new OutBoxCallAdapter(context, contentList.getOutBoxCallLists());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                holder.rv_outbox_list.setLayoutManager(mLayoutManager);
                holder.rv_outbox_list.setAdapter(outBoxCallAdapter);
                holder.tvContentList.setText(contentList.getChildName());
                holder.img_expand_child.setImageResource(R.drawable.top_vector);
            } else {
                holder.constraintRv.setVisibility(View.GONE);
                holder.img_expand_child.setImageResource(R.drawable.down_arrow);
            }
        }

        if (contentList.getChildId() == 3) {
            holder.tvCount.setText(String.valueOf(contentList.getEcModelClasses().size()));
            if (contentList.isExpanded() && contentList.getEcModelClasses().size() > 0) {
                holder.constraintRv.setVisibility(View.VISIBLE);
                outBoxECAdapter = new OutBoxECAdapter(context, contentList.getEcModelClasses());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                holder.rv_outbox_list.setLayoutManager(mLayoutManager);
                holder.rv_outbox_list.setAdapter(outBoxECAdapter);
                holder.tvContentList.setText(contentList.getChildName());
                holder.img_expand_child.setImageResource(R.drawable.top_vector);
            } else {
                holder.constraintRv.setVisibility(View.GONE);
                holder.img_expand_child.setImageResource(R.drawable.down_arrow);
            }
        }

        /*if (contentList.isAvailableList() && contentList.getChildId() == 2) {
            holder.expandContentView.setEnabled(true);
            holder.img_expand_child.setVisibility(View.VISIBLE);
            holder.tvCount.setVisibility(View.VISIBLE);
            holder.tvCount.setText(String.valueOf(contentList.getOutBoxCallLists().size()));

            if (contentList.isExpanded() && contentList.getOutBoxCallLists().size() > 0) {
                holder.constraintRv.setVisibility(View.VISIBLE);
                outBoxCallAdapter = new OutBoxCallAdapter(context, contentList.getOutBoxCallLists());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                holder.rv_outbox_list.setLayoutManager(mLayoutManager);
                holder.rv_outbox_list.setAdapter(outBoxCallAdapter);
                holder.tvContentList.setText(contentList.getChildName());
                holder.img_expand_child.setImageResource(R.drawable.top_vector);
            } else {
                holder.constraintRv.setVisibility(View.GONE);
                holder.img_expand_child.setImageResource(R.drawable.down_arrow);
            }
        } else {
            holder.expandContentView.setEnabled(false);
            holder.constraintRv.setVisibility(View.GONE);
            holder.img_expand_child.setVisibility(View.GONE);
            holder.tvCount.setVisibility(View.GONE);
        }*/

        /*if (contentList.isAvailableList() && contentList.getChildId() == 3) {
            holder.expandContentView.setEnabled(true);
            holder.img_expand_child.setVisibility(View.VISIBLE);
            holder.tvCount.setVisibility(View.VISIBLE);
             holder.tvCount.setText(String.valueOf(contentList.getEcModelClasses().size()));

            if (contentList.isExpanded() && contentList.getEcModelClasses().size() > 0) {

                holder.constraintRv.setVisibility(View.VISIBLE);
                outBoxECAdapter = new OutBoxECAdapter(context, contentList.getEcModelClasses());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                holder.rv_outbox_list.setLayoutManager(mLayoutManager);
                holder.rv_outbox_list.setAdapter(outBoxECAdapter);
                holder.tvContentList.setText(contentList.getChildName());
                holder.img_expand_child.setImageResource(R.drawable.top_vector);
            } else {
                holder.constraintRv.setVisibility(View.GONE);
                holder.img_expand_child.setImageResource(R.drawable.down_arrow);
            }

        } else {
            holder.expandContentView.setEnabled(false);
            holder.constraintRv.setVisibility(View.GONE);
            holder.img_expand_child.setVisibility(View.GONE);
            holder.tvCount.setVisibility(View.GONE);
        }*/


        holder.sync.setOnClickListener(v -> {
            if (contentList.getChildId() == 2) {
                if (UtilityClass.isNetworkAvailable(context)) {
                    CallAPIList(position, holder.rv_outbox_list);
                } else {
                    Toast.makeText(context, "No Network Available", Toast.LENGTH_SHORT).show();
                }
            }

        });

        holder.expandContentView.setOnClickListener(v -> {
            contentList.setExpanded(Objects.equals(holder.img_expand_child.getDrawable().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.down_arrow)).getConstantState()));
            notifyDataSetChanged();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIList(int position, RecyclerView rv_outbox_list) {
        Log.v("SendOutboxCall", "----" + childListModelClasses.get(position).getOutBoxCallLists().size());
        if (childListModelClasses.get(position).getOutBoxCallLists().size() > 0) {
            for (int i = 0; i < childListModelClasses.get(position).getOutBoxCallLists().size(); i++) {
                OutBoxCallList outBoxCallList = childListModelClasses.get(position).getOutBoxCallLists().get(i);
                Log.v("SendOutboxCall", "----" + outBoxCallList.getCusName() + "---" + outBoxCallList.getSyncCount());
                if (outBoxCallList.getSyncCount() <= 4) {
                    CallSendAPI(rv_outbox_list, position, i, outBoxCallList.getDates(), outBoxCallList.getCusName(), outBoxCallList.getCusCode(), outBoxCallList.getJsonData(), outBoxCallList.getSyncCount());
                    break;
                }
            }
        } else {
            outBoxHeaderAdapter = new OutBoxHeaderAdapter(context, listDates);
            CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
            commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
            outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
            outBoxHeaderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return childListModelClasses.size();
    }

    private void CallSendAPI(RecyclerView rvOutBoxList, int position, int outBoxList, String date, String cusName, String cusCode, String jsonData, int SyncCount) {
        JSONObject jsonSaveDcr;
        try {
            jsonSaveDcr = new JSONObject(jsonData);
            //  Log.v("SendOutboxCall", "----" + jsonSaveDcr);
            Call<JsonObject> callSaveDcr;
            callSaveDcr = apiInterface.saveDcr(jsonSaveDcr.toString());

            callSaveDcr.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));

                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                                childListModelClasses.get(position).getOutBoxCallLists().remove(outBoxList);
                                CallAPIList(position, rvOutBoxList);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(5), "Duplicate Call");
                                CallAPIList(position, rvOutBoxList);
                            }

                        } catch (Exception e) {
                            Log.v("SendOutboxCall", "---" + e);
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(SyncCount + 1), "Call Failed");
                    outBoxHeaderAdapter = new OutBoxHeaderAdapter(context, listDates);
                    CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
                    commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
                    outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
                    outBoxHeaderAdapter.notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static class listDataViewholider extends RecyclerView.ViewHolder {
        TextView tvContentList, tvCount;
        ImageView sync, img_expand_child;
        ConstraintLayout constraintRv;
        CardView expandContentView;
        RecyclerView rv_outbox_list;

        public listDataViewholider(@NonNull View view) {
            super(view);
            tvContentList = view.findViewById(R.id.textViewLabel1);
            tvCount = view.findViewById(R.id.textViewcount);
            sync = view.findViewById(R.id.img_sync);
            img_expand_child = view.findViewById(R.id.img_expand);
            expandContentView = view.findViewById(R.id.card_content_view);
            rv_outbox_list = view.findViewById(R.id.rv_outbox_list);
            constraintRv = view.findViewById(R.id.constraint_rv);
        }
    }
}

