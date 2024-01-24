package saneforce.santrip.activity.homeScreen.adapters;

import static android.renderscript.Allocation.createFromString;
import static saneforce.santrip.activity.homeScreen.call.fragments.JWOthersFragment.callCaptureImageLists;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;

import saneforce.santrip.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;
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

            if (contentList.getChildId() == 3) {
                if (UtilityClass.isNetworkAvailable(context)) {
                    CallAPIListImage(position, holder.rv_outbox_list);
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
    private void CallAPIListImage(int position, RecyclerView rvOutboxList) {
        Log.v("SendOutboxCall", "----" + childListModelClasses.get(position).getEcModelClasses().size());
        if (childListModelClasses.get(position).getEcModelClasses().size() > 0) {
            for (int i = 0; i < childListModelClasses.get(position).getEcModelClasses().size(); i++) {
                EcModelClass ecModelClass = childListModelClasses.get(position).getEcModelClasses().get(i);
                Log.v("SendOutboxCall", "----" + ecModelClass.getDates() + "---" + ecModelClass.getName());
                CallSendAPIImage(rvOutboxList, position, i, ecModelClass.getJson_values(), ecModelClass.getFilePath());
                break;
            }
        } else {
            RefreshAdapter();

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void RefreshAdapter() {
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(context, listDates);
        CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    private void CallSendAPIImage(RecyclerView rvOutboxList, int position, int i, String jsonValues, String filePath) {
        MultipartBody.Part img = convertImg("EventImg", filePath);
        HashMap<String, RequestBody> values = field(jsonValues.toString());
        Call<JsonObject> saveImgDcr = apiInterface.saveImgDcr(values, img);

        saveImgDcr.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body().toString());
                        Log.v("ImgUpload", json.toString());
                        childListModelClasses.get(position).getEcModelClasses().remove(i);
                        CallAPIListImage(position, rvOutboxList);
                    } catch (Exception ignored) {
                        RefreshAdapter();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                RefreshAdapter();
            }
        });
    }


    public HashMap<String, RequestBody> field(String val) {
        HashMap<String, RequestBody> xx = new HashMap<>();
        xx.put("data", createFromString(val));
        return xx;
    }

    private RequestBody createFromString(String txt) {
        return RequestBody.create(txt, MultipartBody.FORM);
    }


    public MultipartBody.Part convertImg(String tag, String path) {
        Log.d("path", tag + "-" + path);
        MultipartBody.Part yy = null;
        try {
            File file;
            if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                file = new Compressor(context).compressToFile(new File(path));
                Log.d("path", tag + "-" + path);
            } else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);

            Log.d("path", String.valueOf(yy));
        } catch (Exception ignored) {
        }
        return yy;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIList(int position, RecyclerView rv_outbox_list) {
        if (childListModelClasses.get(position).getOutBoxCallLists().size() > 0) {
            for (int i = 0; i < childListModelClasses.get(position).getOutBoxCallLists().size(); i++) {
                OutBoxCallList outBoxCallList = childListModelClasses.get(position).getOutBoxCallLists().get(i);
                if (outBoxCallList.getStatus().equalsIgnoreCase("Waiting for Sync") || outBoxCallList.getStatus().equalsIgnoreCase("Call Failed")) {
                    if (outBoxCallList.getSyncCount() <= 4) {
                        CallSendAPI(rv_outbox_list, position, i, outBoxCallList.getDates(), outBoxCallList.getCusName(), outBoxCallList.getCusCode(), outBoxCallList.getJsonData(), outBoxCallList.getSyncCount());
                        break;
                    } else {
                        CallAPIList(position, rv_outbox_list);
                    }
                }
            }
        } else {
            RefreshAdapter();
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
                                sqLite.deleteOfflineCalls(cusCode, cusName, date);
                                CallAPIList(position, rvOutBoxList);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(5), "Duplicate Call");
                                CallAPIList(position, rvOutBoxList);
                            }
                        } catch (Exception e) {
                            RefreshAdapter();
                            Log.v("SendOutboxCall", "---" + e);
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(SyncCount + 1), "Call Failed");
                    CallAPIList(position, rvOutBoxList);
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

