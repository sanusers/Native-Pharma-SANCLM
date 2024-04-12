package saneforce.santrip.activity.Quiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import saneforce.santrip.R;

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.Viewholder> {

    private ArrayList<QuizOptionModelClass> mDataset;

    OptionChooseInterface optionChooseInterface;

    public QuizQuestionAdapter(ArrayList<QuizOptionModelClass>  mDataset, OptionChooseInterface optionChooseInterface) {
        this.mDataset = mDataset;
        this.optionChooseInterface = optionChooseInterface;

    }

    @NonNull
    @Override
    public QuizQuestionAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_option_item, parent, false);
        return new QuizQuestionAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizQuestionAdapter.Viewholder holder, int position) {
        holder.setIsRecyclable(false);
        holder.radioButton.setText(mDataset.get(position).getOptionName());
        if(mDataset.get(position).ischecked){
            holder.radioButton.setChecked(true);
            holder.radioButton.setOnCheckedChangeListener(null);
        }else {
            holder.radioButton.setChecked(false);
        }

        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(holder.radioButton.isChecked()){
                    optionChooseInterface.classSelceted(mDataset.get(position));
                }else {
                    optionChooseInterface.classUnselected(mDataset.get(position));


                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    ArrayList<QuizOptionModelClass> getList(){
        return mDataset;
    }
    public class Viewholder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            radioButton=itemView.findViewById(R.id.radio_button1);

        }
    }
}
