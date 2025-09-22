package saneforce.sanzen.activity.myresource.Categoryview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.storage.SharedPref;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<CategoryModel> listeduser;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> listeduser) {
        this.context = context;
        this.listeduser = listeduser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CategoryModel categoryModel = listeduser.get(position);
        String count = String.valueOf((position + 1));
        holder.tvCount.setText(count + ")");
        holder.tvCategory.setText(categoryModel.getName());
        holder.tvCategoryName.setText(categoryModel.getCategoryName());
        if (categoryModel.getNoOfVisits() != null && !categoryModel.getNoOfVisits().isEmpty() && SharedPref.getVstNd(context).equalsIgnoreCase("0")) {
            holder.tvNoOfVisits.setText(categoryModel.getNoOfVisits() + " visit");
        }
    }

    @Override
    public int getItemCount() {
        return listeduser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCount, tvCategoryName, tvCategory, tvNoOfVisits;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCount = (itemView).findViewById(R.id.count);
            tvCategoryName = (itemView).findViewById(R.id.Doc_name);
            tvCategory = (itemView).findViewById(R.id.Category);
            tvNoOfVisits = (itemView).findViewById(R.id.category_visit);
        }
    }
}
