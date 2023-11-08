package saneforce.sanclm.activity.presentation.createPresentation.selectedSlide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallBack extends ItemTouchHelper.Callback {

    SelectedSlidesAdapter selectedSlidesAdapter;

    public ItemTouchHelperCallBack (SelectedSlidesAdapter selectedSlidesAdapter) {
        this.selectedSlidesAdapter = selectedSlidesAdapter;
    }


    @Override
    public int getMovementFlags (@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags,0);
    }

    @Override
    public boolean onMove (@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
       selectedSlidesAdapter.onItemMove(viewHolder.getAbsoluteAdapterPosition(),target.getAbsoluteAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped (@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

//    @Override
//    public boolean isLongPressDragEnabled () {
//        return super.isLongPressDragEnabled();
//    }
//
//    @Override
//    public boolean isItemViewSwipeEnabled () {
//        return super.isItemViewSwipeEnabled();
//    }
}
