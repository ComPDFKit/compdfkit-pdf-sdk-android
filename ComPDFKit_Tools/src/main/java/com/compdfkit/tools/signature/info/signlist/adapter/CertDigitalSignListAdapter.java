package com.compdfkit.tools.signature.info.signlist.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.signature.CPDFCertInfo;
import com.compdfkit.core.signature.CPDFSigner;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickAdapter;
import com.compdfkit.tools.common.utils.adapter.CBaseQuickViewHolder;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.signature.bean.CPDFSignatureStatusInfo;


public class CertDigitalSignListAdapter extends CBaseQuickAdapter<CPDFSignatureStatusInfo, CBaseQuickViewHolder> {

    private CPDFDocument document;

    public CertDigitalSignListAdapter(CPDFDocument document) {
        this.document = document;
    }

    @Override
    protected CBaseQuickViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType) {
        return new CBaseQuickViewHolder(R.layout.tools_sign_cert_digital_sign_list_item, parent);
    }

    @Override
    protected void onBindViewHolder(CBaseQuickViewHolder holder, int position, CPDFSignatureStatusInfo item) {
        CPDFSigner[] signers = item.getSignature().getSignerArr();
        if (signers != null && signers.length > 0){
            CPDFCertInfo certInfos = signers[0].getCert().getCertInfo();
            holder.setText(R.id.tv_sign_common_name, certInfos.getSubject().getCommonName());
        }else {
            holder.setText(R.id.tv_sign_common_name, "");
        }
        if (TextUtils.isEmpty(item.getSignature().getDate())) {
            holder.setText(R.id.tv_sign_date, "");
        } else {
            String date1 = CDateUtil.transformPDFDate(item.getSignature().getDate());
            holder.setText(R.id.tv_sign_date, CDateUtil.formatDate(date1, holder.itemView.getContext().getString(R.string.tools_signature_date_pattern)));
        }
        switch (item.getStatus()) {
            case SUCCESS:
                holder.setImageResource(R.id.iv_icon, R.drawable.tools_ic_digital_sign_is_valid);
                break;
            case FAILURE:
                holder.setImageResource(R.id.iv_icon, R.drawable.tools_ic_digital_sign_is_failures);
                break;
            default:
                holder.setImageResource(R.id.iv_icon, R.drawable.tools_ic_digital_sign_is_wrong);
                break;
        }
        holder.setText(R.id.tv_sign_status, getCertAuthorityStatementsStr(item));
    }

    public String getCertAuthorityStatementsStr(CPDFSignatureStatusInfo statusInfo){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < statusInfo.getCertAuthorityStatements().length; i++) {
            String item = statusInfo.getCertAuthorityStatements()[i];
            builder.append(item);
        }
        return builder.toString();
    }
}
