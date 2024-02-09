package saneforce.santrip.activity.map.custSelection;

import static saneforce.santrip.activity.map.MapsActivity.GeoTagApprovalNeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.map.MapsActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;

public class CustListAdapter extends RecyclerView.Adapter<CustListAdapter.ViewHolder> {
    public static ArrayList<CustList> getCustListNew = new ArrayList<>();
    Context context;
    Activity activity;
    ArrayList<CustList> custListArrayList;
    ArrayList<CustList> custListArrayListNew;
    CommonUtilsMethods commonUtilsMethods;

    public CustListAdapter(Activity activity, Context context, ArrayList<CustList> custListArrayList, ArrayList<CustList> custListArrayListNew) {
        this.activity = activity;
        this.context = context;
        this.custListArrayList = custListArrayList;
        this.custListArrayListNew = custListArrayListNew;
        commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tag_cust, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(custListArrayList.get(position).getName());
        holder.tv_category.setText(custListArrayList.get(position).getCategory());
        holder.tv_specialist.setText(custListArrayList.get(position).getSpecialist());
        holder.tv_area.setText(custListArrayList.get(position).getTown_name());
        holder.tv_count.setText(String.format("%s/%s", custListArrayList.get(position).getTag(), custListArrayList.get(position).getMaxTag()));

        if (GeoTagApprovalNeed.equalsIgnoreCase("0")) {
            for (int m = 0; m < custListArrayListNew.size(); m++) {
                if (custListArrayListNew.get(m).getCode().equalsIgnoreCase(custListArrayList.get(position).getCode())) {
                    if (custListArrayListNew.get(m).getGeoTagStatus().equalsIgnoreCase("1")) {
                        custListArrayList.set(position, new CustList(custListArrayList.get(position).getName(), custListArrayList.get(position).getCode(), custListArrayList.get(position).getType(), custListArrayList.get(position).getCategory(), custListArrayList.get(position).getSpecialist(), custListArrayList.get(position).getLatitude(), custListArrayList.get(position).getLongitude(), custListArrayList.get(position).getAddress(), custListArrayList.get(position).getTown_name(), custListArrayList.get(position).getTown_code(), custListArrayList.get(position).getTag(), custListArrayList.get(position).getMaxTag(), custListArrayList.get(position).getPosition(), "1"));
                        break;
                    }
                }
            }

            if (custListArrayList.get(position).getGeoTagStatus().equalsIgnoreCase("1")) {
                holder.tv_verified.setVisibility(View.VISIBLE);
                holder.tv_verified.setText(context.getResources().getText(R.string.pending));
            } else {
                holder.tv_verified.setVisibility(View.GONE);
            }
        } else {
            holder.tv_verified.setVisibility(View.GONE);
        }

     /*   if (GeoTagApprovalNeed.equalsIgnoreCase("0") && custListArrayList.get(position).getGeoTagStatus().equalsIgnoreCase("1")) {
            holder.tv_verified.setVisibility(View.VISIBLE);
            holder.tv_verified.setText(context.getResources().getText(R.string.pending));
        } else {
            holder.tv_verified.setVisibility(View.GONE);
        }

        for (int m = 0; m < custListArrayListNew.size(); m++) {
            if (custListArrayListNew.get(m).getCode().equalsIgnoreCase(custListArrayList.get(position).getCode())) {
                if (custListArrayListNew.get(m).getGeoTagStatus().equalsIgnoreCase("1")) {
                    custListArrayList.set(position, new CustList(custListArrayList.get(position).getName(), custListArrayList.get(position).getCode(), custListArrayList.get(position).getType(), custListArrayList.get(position).getCategory(), custListArrayList.get(position).getSpecialist(), custListArrayList.get(position).getLatitude(), custListArrayList.get(position).getLongitude(), custListArrayList.get(position).getAddress(), custListArrayList.get(position).getTown_name(), custListArrayList.get(position).getTown_code(), custListArrayList.get(position).getTag(), custListArrayList.get(position).getMaxTag(), custListArrayList.get(position).getPosition(), "1"));
                    holder.tv_verified.setText(context.getResources().getText(R.string.pending));
                    break;
                }
            }
        }
*/
        if (Integer.parseInt(custListArrayList.get(position).getMaxTag()) > Integer.parseInt(custListArrayList.get(position).getTag())) {
            holder.tv_view.setVisibility(View.GONE);
        } else {
            holder.tv_view.setVisibility(View.VISIBLE);
        }

        holder.tv_name.setOnClickListener(view -> {
            commonUtilsMethods.displayPopupWindow(activity, context, view, custListArrayList.get(position).getName());
        });

        holder.constraint_main.setOnClickListener(view -> {
            if (Integer.parseInt(custListArrayList.get(position).getMaxTag()) > Integer.parseInt(custListArrayList.get(position).getTag())) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("from", "tagging");
                intent.putExtra("cus_name", custListArrayList.get(position).getName());
                intent.putExtra("cus_code", custListArrayList.get(position).getCode());
                intent.putExtra("town_name", custListArrayList.get(position).getTown_name());
                intent.putExtra("town_code", custListArrayList.get(position).getTown_code());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TagCustSelectionList.SelectedCustPos = custListArrayList.get(position).getPosition();
                //  SharedPref.setCustomerPosition(context, custListArrayList.get(position).getPosition());
                context.startActivity(intent);
            } else {
                 commonUtilsMethods.ShowToast(context,context.getString(R.string.exceed_tag_limit),100);
            }
        });

        holder.tv_view.setOnClickListener(view -> {
            if (Integer.parseInt(custListArrayList.get(position).getMaxTag()) > Integer.parseInt(custListArrayList.get(position).getTag())) {
               // Toast.makeText(context, "First Tag & View", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(context, MapsActivity.class);
                getCustListNew.clear();
                for (int m = 0; m < custListArrayListNew.size(); m++) {
                    if (custListArrayListNew.get(m).getCode().equalsIgnoreCase(custListArrayList.get(position).getCode())) {
                        if (!custListArrayListNew.get(m).getLatitude().isEmpty() && !custListArrayListNew.get(m).getLongitude().isEmpty()) {
                            getCustListNew.add(new CustList(custListArrayListNew.get(m).getLatitude(), custListArrayListNew.get(m).getLongitude(), custListArrayListNew.get(m).getAddress()));
                        } else {
                            getCustListNew.add(new CustList(custListArrayList.get(position).getLatitude(), custListArrayList.get(position).getLongitude(), custListArrayList.get(position).getAddress()));
                        }
                    }
                }
                intent.putExtra("from", "view_tagged");
                intent.putExtra("cus_name", custListArrayList.get(position).getName());
                intent.putExtra("cus_add", custListArrayList.get(position).getAddress());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return custListArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CustList> filteredNames) {
        this.custListArrayList = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_category, tv_specialist, tv_area, tv_count, tv_view, tv_verified;
        ConstraintLayout constraint_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.txt_cust_name);
            tv_category = itemView.findViewById(R.id.txt_cat);
            tv_specialist = itemView.findViewById(R.id.txt_specialist);
            tv_area = itemView.findViewById(R.id.txt_address);
            tv_count = itemView.findViewById(R.id.txt_count);
            tv_view = itemView.findViewById(R.id.txt_view);
            tv_verified = itemView.findViewById(R.id.txt_verfied);
            constraint_main = itemView.findViewById(R.id.constraint_main);
        }
    }
}
