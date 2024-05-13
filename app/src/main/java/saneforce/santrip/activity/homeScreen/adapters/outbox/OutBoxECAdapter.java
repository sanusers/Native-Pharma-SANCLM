package saneforce.santrip.activity.homeScreen.adapters.outbox;

import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.santrip.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.santrip.roomdatabase.RoomDB;


public class OutBoxECAdapter extends RecyclerView.Adapter<OutBoxECAdapter.ViewHolder> {
    Context context;
    ArrayList<EcModelClass> ecModelClasses;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
    private RoomDB roomDB;
    private CallOfflineECDataDao callOfflineECDataDao;
    private CallOfflineDataDao callOfflineDataDao;


    public OutBoxECAdapter(Activity activity, Context context, ArrayList<EcModelClass> ecModelClasses) {
        this.activity = activity;
        this.context = context;
        this.ecModelClasses = ecModelClasses;
        commonUtilsMethods = new CommonUtilsMethods(context);
        roomDB = RoomDB.getDatabase(context);
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        callOfflineDataDao = roomDB.callOfflineDataDao();
    }

    @NonNull
    @Override
    public OutBoxECAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_ec_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxECAdapter.ViewHolder holder, int position) {
        holder.tvImageName.setText(ecModelClasses.get(position).getImg_name());
        holder.tvStatus.setText(ecModelClasses.get(position).getSync_status());

        File imgFile = new File(ecModelClasses.get(position).getFilePath());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imgView.setImageBitmap(myBitmap);
        }

        holder.imgView.setOnClickListener(v -> {
            File imgFile1 = new File(ecModelClasses.get(position).getFilePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
            showImage(myBitmap);
        });

        holder.tvMenu.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
            popup.inflate(R.menu.ec_call_menu);
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menuSync) {
                    CallImageApi();
                } else if (menuItem.getItemId() == R.id.menuDelete) {
                    try {
                        JSONObject jsonObject;
                        jsonObject = new JSONObject(callOfflineDataDao.getJsonCallList(ecModelClasses.get(position).getDates(), ecModelClasses.get(position).getCusCode()));
                        JSONArray jsonArray = jsonObject.getJSONArray("EventCapture");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObjectEC = jsonArray.getJSONObject(i);
                            if (jsonObjectEC.getString("EventImageName").equalsIgnoreCase(ecModelClasses.get(position).getImg_name())) {
                                jsonArray.remove(i);
                                break;
                            }
                        }

                        for (int i = 0; i < listDates.size(); i++) {
                            if (listDates.get(i).getGroupName().equalsIgnoreCase(ecModelClasses.get(position).getDates())) {
                                for (int j = 0; j < listDates.get(i).getChildItems().get(2).getOutBoxCallLists().size(); j++) {
                                    OutBoxCallList outBoxCallList = listDates.get(i).getChildItems().get(2).getOutBoxCallLists().get(j);
                                    if (outBoxCallList.getCusCode().equalsIgnoreCase(ecModelClasses.get(position).getImg_name())) {
                                        jsonObject = new JSONObject(outBoxCallList.getJsonData());
                                        for (int m = 0; m < jsonArray.length(); m++) {
                                            JSONObject jsonObjectEC = jsonArray.getJSONObject(i);
                                            if (jsonObjectEC.getString("EventImageName").equalsIgnoreCase(ecModelClasses.get(position).getImg_name())) {
                                                jsonArray.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        callOfflineDataDao.saveOfflineUpdateJson(ecModelClasses.get(position).getDates(), ecModelClasses.get(position).getCusCode(), jsonObject.toString());
                        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
                        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
                        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
                        outBoxHeaderAdapter.notifyDataSetChanged();
                    } catch (Exception ignored) {
                    }
                    File fileDelete = new File(ecModelClasses.get(position).getFilePath());
                    if (fileDelete.exists()) {
                        if (fileDelete.delete()) {
                            System.out.println("file Deleted :" + ecModelClasses.get(position).getFilePath());
                        } else {
                            System.out.println("file not Deleted :" + ecModelClasses.get(position).getFilePath());
                        }
                    }
                    callOfflineECDataDao.deleteOfflineEC(String.valueOf(ecModelClasses.get(position).getId()));
                    removeAt(position);
                }
                return true;
            });
            popup.show();
        });
    }

    private void CallImageApi() {
    }

    @Override
    public int getItemCount() {
        return ecModelClasses.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        ecModelClasses.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ecModelClasses.size());
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    public void showImage(Bitmap img_view) {
        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(true);
        Objects.requireNonNull(builder.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(img_view);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams((int) context.getResources().getDimension(R.dimen._300sdp), (int) context.getResources().getDimension(R.dimen._300sdp)));
        builder.show();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvImageName, tvStatus, tvMenu;
        ImageView imgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvImageName = itemView.findViewById(R.id.tvImageName);
            imgView = itemView.findViewById(R.id.img_view);
            tvMenu = itemView.findViewById(R.id.optionView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
