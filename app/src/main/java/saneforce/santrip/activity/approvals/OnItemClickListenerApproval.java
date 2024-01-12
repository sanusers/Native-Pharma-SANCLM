package saneforce.santrip.activity.approvals;

import saneforce.santrip.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.santrip.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.santrip.activity.approvals.tp.pojo.TpModelList;

public interface OnItemClickListenerApproval {
    void onClick(DCRApprovalList dcrApprovalList, int pos);

    void onClickDcrDetail(DcrDetailModelList dcrDetailModelList);

    void onItemClick(TpModelList tpModelLists,int pos);
}
