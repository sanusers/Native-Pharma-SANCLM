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
        selectedSlidesAdapter.onRowMoved(viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof SelectedSlidesAdapter.MyViewHolder) {
                SelectedSlidesAdapter.MyViewHolder myViewHolder= (SelectedSlidesAdapter.MyViewHolder) viewHolder;
                selectedSlidesAdapter.onRowSelected(myViewHolder);
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof SelectedSlidesAdapter.MyViewHolder) {
            SelectedSlidesAdapter.MyViewHolder myViewHolder= (SelectedSlidesAdapter.MyViewHolder) viewHolder;
            selectedSlidesAdapter.onRowClear(myViewHolder);
        }
    }

    @Override
    public void onSwiped (@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled () {
//        return super.isLongPressDragEnabled();
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled () {
//        return super.isItemViewSwipeEnabled();
        return false;
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(SelectedSlidesAdapter.MyViewHolder myViewHolder);
        void onRowClear(SelectedSlidesAdapter.MyViewHolder myViewHolder);

    }
}
