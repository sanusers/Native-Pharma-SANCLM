package saneforce.sanzen.activity.approvals;

import saneforce.sanzen.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanzen.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanzen.activity.approvals.tp.pojo.TpModelList;

public interface OnItemClickListenerApproval {
    void onClick(DCRApprovalList dcrApprovalList, int pos);

    void onClickDcrDetail(DcrDetailModelList dcrDetailModelList);

    void onItemClick(TpModelList tpModelLists,int pos);
}
