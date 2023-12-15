package saneforce.sanclm.activity.approvals;

import saneforce.sanclm.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanclm.activity.approvals.tp.TpModelList;

public interface OnItemClickListenerApproval {
    void onClick(DCRApprovalList dcrApprovalList, int pos);

    void onClickDcrDetail(DcrDetailModelList dcrDetailModelList);

    void onItemClick(TpModelList tpModelLists);
}
