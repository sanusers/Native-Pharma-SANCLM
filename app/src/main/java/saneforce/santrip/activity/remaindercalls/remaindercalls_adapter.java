package saneforce.santrip.activity.remaindercalls;

import static saneforce.santrip.activity.call.DCRCallActivity.CallActivityCustDetails;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.DCRCallActivity;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.storage.SQLite;


public class remaindercalls_adapter extends RecyclerView.Adapter<remaindercalls_adapter.ViewHolder> {
    Context context;
    ArrayList<remainder_modelclass> listeduser;
    private AlertDialog alertDialog;
    SQLite sqLite;
    private boolean isFirstClick = true;
    private static final long LOCK_DURATION = 5000; // Lock clicks for 2 seconds
    private Handler handler = new Handler();


    public remaindercalls_adapter(Context context, ArrayList<remainder_modelclass> listeduser) {
        this.context = context;
        this.listeduser = listeduser;
        sqLite = new SQLite(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.remaindercall_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        remainder_modelclass app_adapt = listeduser.get(position);
        holder.doc_name.setText(app_adapt.getDoc_name());
        holder.doc_cat.setText(app_adapt.getDoc_cat());
        holder.doc_spec.setText(app_adapt.getDoc_spec());
        holder.town_name.setText(" " + app_adapt.getDoc_town() + " ");




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstClick) {
                    // Perform action for the first click
                    isFirstClick = false; // Disable further clicks

                    // Set a timer to enable clicks after LOCK_DURATION milliseconds

                    // Your action code for the first click goes here
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isFirstClick = true; // Enable clicks after LOCK_DURATION milliseconds
                        }
                    }, LOCK_DURATION);

                        Intent intent12 = new Intent(context, DCRCallActivity.class);
                        CallActivityCustDetails = new ArrayList<>();
                        CallActivityCustDetails.add(0, new CustList(listeduser.get(position).getDoc_name(),
                                listeduser.get(position).getDoc_code(), listeduser.get(position).getDoc_sftype(),
                                listeduser.get(position).getDoc_cat(), listeduser.get(position).getDoc_catcode(),
                                listeduser.get(position).getDoc_spec(), listeduser.get(position).getDoc_speccode(), listeduser.get(position).getDoc_town(), listeduser.get(position).getDoc_towncode(), "", "", "", "",
                                "", "", "", "", "", "", "", "", "", "", ""));

                        sqLite.saveOfflineCallIN(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"),  CommonUtilsMethods.getCurrentInstance("hh:mm aa"), listeduser.get(0).getDoc_code(), listeduser.get(0).getDoc_name(), "1");

                        intent12.putExtra("isDetailingRequired", "false");
                        intent12.putExtra("dcr_from_activity", "");
                        intent12.putExtra("remainder_save", "1");
                        intent12.putExtra("hq_code", RemaindercallsActivity.REm_hq_code );
                        Log.d("REm_hq_code", RemaindercallsActivity.REm_hq_code);
                        intent12.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        sqLite.saveOfflineCallIN(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType());
                        v.getContext().startActivity(intent12);



                }
            }
        });

/*
        holder.layout_dcr.setOnClickListener(new OnMultiClickListener() {

            @Override
            public void onMultiClick(View v) {
                if (isFirstClick) {
                    Intent intent12 = new Intent(context, DCRCallActivity.class);
                    DCRCallActivity.CallActivityCustDetails = new ArrayList<>();
                    DCRCallActivity.CallActivityCustDetails.add(0, new CustList(listeduser.get(position).getDoc_name(),
                            listeduser.get(position).getDoc_code(), listeduser.get(position).getDoc_sftype(),
                            listeduser.get(position).getDoc_cat(), listeduser.get(position).getDoc_catcode(),
                            listeduser.get(position).getDoc_spec(), listeduser.get(position).getDoc_speccode(), listeduser.get(position).getDoc_town(), listeduser.get(position).getDoc_towncode(), "", "", "", "",
                            "", "", "", "", "", "", "", "", "", "", ""));

                    sqLite.saveOfflineCallIN(CommonUtilsMethods.getCurrentDate(), CommonUtilsMethods.getCurrentTimeAMPM(), listeduser.get(0).getDoc_code(), listeduser.get(0).getDoc_name(), "1");

                    intent12.putExtra("isDetailedRequired", "false");
                    intent12.putExtra("from_activity", "");
                    intent12.putExtra("remainder_save", "1");
                    intent12.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    sqLite.saveOfflineCallIN(CommonUtilsMethods.getCurrentDate(), CommonUtilsMethods.getCurrentTimeAMPM(), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType());
                    v.getContext().startActivity(intent12);

                    isFirstClick=true;
                }else{

                }
            }
        });
*/


//        holder.layout_dcr.setOnClickListener(new View.OnClickListener() {
//            private boolean clicked = false;
//
//            @Override
//            public void onClick(View v) {
//                if (!clicked) {
//
//
//                        Remaindercalls_activity.vals_rm = "val";
//                        Log.d("datacheck_1234", Remaindercalls_activity.vals_rm);
//                        Intent intent12 = new Intent(context, DCRCallActivity.class);
//                        DCRCallActivity.CallActivityCustDetails = new ArrayList<>();
//                        DCRCallActivity.CallActivityCustDetails.add(0, new CustList(listeduser.get(position).getDoc_name(),
//                                listeduser.get(position).getDoc_code(), listeduser.get(position).getDoc_sftype(),
//                                listeduser.get(position).getDoc_cat(), listeduser.get(position).getDoc_catcode(),
//                                listeduser.get(position).getDoc_spec(), listeduser.get(position).getDoc_speccode(), listeduser.get(position).getDoc_town(), listeduser.get(position).getDoc_towncode(), "", "", "", "",
//                                "", "", "", "", "", "", "", "", "", "", ""));
//
//                        sqLite.saveOfflineCallIN(CommonUtilsMethods.getCurrentDate(), CommonUtilsMethods.getCurrentTimeAMPM(), listeduser.get(0).getDoc_code(), listeduser.get(0).getDoc_name(), "1");
//
//                        intent12.putExtra("isDetailedRequired", "false");
//                        intent12.putExtra("from_activity", "");
//                        intent12.putExtra("remainder_save", "1");
//                        intent12.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        sqLite.saveOfflineCallIN(CommonUtilsMethods.getCurrentDate(), CommonUtilsMethods.getCurrentTimeAMPM(), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType());
//                        v.getContext().startActivity(intent12);
//                    clicked = true;
//
//
//                }
//            }
//        });



    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<remainder_modelclass> filterdNames) {
        this.listeduser = filterdNames;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return listeduser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView doc_name, town_name, doc_spec, doc_cat;
        LinearLayout layout_dcr;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_dcr = itemView.findViewById(R.id.layout_dcr);
            doc_name = itemView.findViewById(R.id.doc_name);
            town_name = itemView.findViewById(R.id.town_name);
            doc_spec = itemView.findViewById(R.id.doc_spec);
            doc_cat = itemView.findViewById(R.id.doc_cat);
        }
    }
    // In your activity or fragment
//    public void showPopupDialog() {
//        // Create the dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        View dialogView = getLayoutInflater().inflate(R.layout.forms_adapt, null);
//        builder.setView(dialogView);
//
//        // Get views from the layout
//        TextView textViewTitle = dialogView.findViewById(R.id.textViewTitle);
//        Button buttonOk = dialogView.findViewById(R.id.buttonOk);
//
//        // Customize the dialog content
//        textViewTitle.setText("Your Custom Title");
//
//        // Set up button click listener
//        buttonOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Dismiss the dialog when OK button is clicked
//                alertDialog.dismiss();
//            }
//        });
//
//        // Create and show the dialog
//        alertDialog = builder.create();
//        alertDialog.show();
//    }


}
