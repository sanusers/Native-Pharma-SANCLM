package saneforce.sanclm.Activities.MasterSync;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.Network.ApiInterface;

public class MasterSyncAdapter extends RecyclerView.Adapter<MasterSyncAdapter.MyViewHolder> {

    Context context;
    ArrayList<MasterSyncItemModel> masterSyncItemModels;
    MasterSyncItemClick masterSyncItemClick;
    ApiInterface apiInterface;

    public MasterSyncAdapter () {
    }

    public MasterSyncAdapter (ArrayList<MasterSyncItemModel> masterSyncItemModels, Context context, MasterSyncItemClick masterSyncItemClick) {
        this.context = context;
        this.masterSyncItemModels = masterSyncItemModels;
        this.masterSyncItemClick = masterSyncItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.master_sync_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
        MasterSyncItemModel data = masterSyncItemModels.get(position);
        String name = data.getName();
        String count = String.valueOf(data.getCount());

        holder.name.setText(name);
        holder.count.setText(count);

        if (data.isPB_visibility()){
            holder.progressBar.setVisibility(View.VISIBLE);
        }else {
            holder.progressBar.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Log.e("test","item click : " + name);
                holder.progressBar.setVisibility(View.VISIBLE);
                masterSyncItemClick.itemClick(data,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount () {
        return masterSyncItemModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,count;
        ProgressBar progressBar;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }

//    public void sync(String masterFor,ProgressBar progressBar,TextView count)  {
//
//        try {
//            progressBar.setVisibility(View.VISIBLE);
//            String baseUrl = SharedPref.getBaseUrl(context);
//            String pathUrl = SharedPref.getPhpPathUrl(context);
//            String replacedUrl = pathUrl.replace("?","/");
//            Log.e("test","master url : "  + baseUrl + replacedUrl);
//            Log.e("test","base url from sharePref  : " + SharedPref.getBaseUrl(context));
//            apiInterface = RetrofitClient.getRetrofit(context, "http://crm.saneforce.in/iOSServer/db_api.php/");
//
//            Log.e("test","master sync obj : " + postObject);
//            Call<JsonArray> call = null;
//            if (masterFor.equalsIgnoreCase("Doctor")){
//                call = apiInterface.getDrMaster(postObject.toString());
//            } else if (masterFor.equalsIgnoreCase("Subordinate")) {
//                call = apiInterface.getSubordinateMaster(postObject.toString());
//            }
//            call.enqueue(new Callback<JsonArray>() {
//                @Override
//                public void onResponse (@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
//                    progressBar.setVisibility(View.GONE);
//
//                    if (response.isSuccessful()) {
//                        Log.e("test","res body : " + response.body().toString());
//                        try {
//                            JSONArray jsonArray = new JSONArray(response.body().toString());
//                            count.setText(String.valueOf(jsonArray.length()));
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onFailure (@NonNull Call<JsonArray> call, @NonNull Throwable t) {
//                    Log.e("test","failed : " + t);
//                    progressBar.setVisibility(View.GONE);
//                }
//            });
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
}
