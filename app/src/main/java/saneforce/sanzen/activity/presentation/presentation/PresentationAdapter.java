package saneforce.sanzen.activity.presentation.presentation;

import static saneforce.sanzen.activity.presentation.presentation.PresentationActivity.binding;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.from_where;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.presentation.SupportClass;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.sanzen.activity.presentation.playPreview.PlaySlidePreviewActivity;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;


public class PresentationAdapter extends RecyclerView.Adapter<PresentationAdapter.MyViewHolder> {
    Context context;
    ArrayList<BrandModelClass.Presentation> arrayList = new ArrayList<>();
    PopupWindow mypopupWindow;
    Menu menu;
    String isClickedFrom;
    Intent intent;
    private RoomDB roomDB;
    private PresentationDataDao presentationDataDao;

    public PresentationAdapter(Context context, ArrayList<BrandModelClass.Presentation> arrayList, String isClickedFrom) {
        this.context = context;
        this.arrayList = arrayList;
        roomDB = RoomDB.getDatabase(context);
        presentationDataDao = roomDB.presentationDataDao();
        this.isClickedFrom = isClickedFrom;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.presentation_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (isClickedFrom.equalsIgnoreCase("presentation")) {
            holder.menu.setVisibility(View.VISIBLE);
            holder.playButton.setVisibility(View.GONE);
        } else if (isClickedFrom.equalsIgnoreCase("customized")) {
            holder.menu.setVisibility(View.GONE);
            holder.playButton.setVisibility(View.VISIBLE);
        }

        BrandModelClass.Presentation presentation = arrayList.get(position);
        ArrayList<BrandModelClass.Product> products = presentation.getProducts();

        if (products.size() > 0)
            SupportClass.setThumbnail(context, products.get(0).getSlideName(), holder.imageView);

        holder.name.setText(presentation.getPresentationName());
        if (products.size() > 1)
            holder.count.setText(products.size() + " Asserts");
        else
            holder.count.setText(products.size() + " Assert");


        holder.playButton.setOnClickListener(view -> {
            int SelectedPos = 0;
            int count = products.size();
            if (count > 0) {
                ArrayList<BrandModelClass.Product> productsList = new ArrayList<>();
                if (from_where.equalsIgnoreCase("call")) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        for (int j = 0; j < arrayList.get(i).getProducts().size(); j++) {
                            productsList.add(new BrandModelClass.Product(arrayList.get(i).getPresentationName(), arrayList.get(i).getProducts().get(j).getBrandName(), arrayList.get(i).getProducts().get(j).getBrandCode(), arrayList.get(i).getProducts().get(j).getSlideId()
                                    , arrayList.get(i).getProducts().get(j).getSlideName(), arrayList.get(i).getProducts().get(j).getPriority(), arrayList.get(i).getProducts().get(j).isImageSelected()));
                        }
                    }
                    for (int i = 0; i < productsList.size(); i++) {
                        if (productsList.get(i).getPresentationName().equalsIgnoreCase(arrayList.get(position).getPresentationName())) {
                            SelectedPos = i;
                            break;
                        }
                    }
                    intent = new Intent(context, PlaySlideDetailing.class);
                } else {
                    productsList = arrayList.get(position).getProducts();
                    intent = new Intent(context, PlaySlidePreviewActivity.class);
                }

                String data = new Gson().toJson(productsList);
                Bundle bundle = new Bundle();
                bundle.putString("slideBundle", data);
                bundle.putString("position", String.valueOf(SelectedPos));
                intent.putExtra("bundle", bundle);
                context.startActivity(intent);
            }
        });

        holder.menu.setOnClickListener(view -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, view, Gravity.END);
            popup.inflate(R.menu.presentation_menu);

            popup.setOnMenuItemClickListener(menuItem -> {
                PresentationActivity presentationActivity = (PresentationActivity) context;

                if (menuItem.getItemId() == R.id.menuPlay) {
                    Intent intent = new Intent(presentationActivity, PlaySlidePreviewActivity.class);
                    String data = new Gson().toJson(products);
                    Bundle bundle = new Bundle();
                    bundle.putString("slideBundle", data);
                    bundle.putString("position", String.valueOf(0));
                    intent.putExtra("bundle", bundle);
                    context.startActivity(intent);
                } else if (menuItem.getItemId() == R.id.menuEdit) {
                    Intent intent = new Intent(presentationActivity, CreatePresentationActivity.class);
                    String data = new Gson().toJson(products);
                    Bundle bundle = new Bundle();
                    bundle.putString("slideBundle", data);
                    bundle.putString("position", String.valueOf(0));
                    bundle.putString("presentationName", presentation.getPresentationName());
                    intent.putExtra("bundle", bundle);
                    context.startActivity(intent);
                } else if (menuItem.getItemId() == R.id.menuDelete) {
                    removeAt(position);
                    presentationDataDao.deletePresentation(presentation.getPresentationName());
                    ArrayList<BrandModelClass.Presentation> savedPresentation = new ArrayList<>();
                    savedPresentation = presentationDataDao.getPresentations();
                    if (savedPresentation.size() > 0) {
                        binding.constraintNoData.setVisibility(View.GONE);
                        binding.presentationRecView.setVisibility(View.VISIBLE);
                    } else {
                        binding.constraintNoData.setVisibility(View.VISIBLE);
                        binding.presentationRecView.setVisibility(View.GONE);
                    }
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout menu;
        TextView name, count;
        ImageView imageView, playButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            menu = itemView.findViewById(R.id.verticalDot);
            count = itemView.findViewById(R.id.assertCount);
            name = itemView.findViewById(R.id.presentationName);
            imageView = itemView.findViewById(R.id.imageView);
            playButton = itemView.findViewById(R.id.play_button);
        }
    }

    public void removeAt(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
    }

    private void setPopUpWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.pop_up_presentation, null);
        mypopupWindow = new PopupWindow(view, 200, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
    }

}