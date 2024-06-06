/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

public class CGotoPageDialog extends CCommonInputDialog {
    private COnSetPDFDisplayPageIndexListener pdfDisplayPageIndexListener;

    private int pageCount = 1;

    public CGotoPageDialog(Context context) {
        super(context, R.style.tools_dialog_theme);
        initDialogView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initDialogView() {
//        setTitle(R.string.tools_goto_page_dialog_title);
        setMessage(getContext().getResources().getText(R.string.tools_enter_a_page_number).toString());
        setInputType(InputType.TYPE_CLASS_NUMBER);
        CViewUtils.showKeyboard(getEtMessage());
        setConfirmCallback(getContext().getResources().getText(R.string.tools_okay).toString(), text -> {
            if (null != pdfDisplayPageIndexListener && (!TextUtils.isEmpty(text))) {
                int page;
                try {
                    page = Integer.parseInt(text);
                    if (page >= 0) {
                        pdfDisplayPageIndexListener.displayPage(page);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            dismiss();
        });
        setCancelCallback(getContext().getResources().getText(R.string.tools_cancel).toString(), this::dismiss);

        getEtMessage().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    if (!TextUtils.isEmpty(s)) {
                        int page = Integer.parseInt(s.toString());
                        getBtnConfirm().setEnabled(page > 0 && page <= pageCount);
                    } else {
                        getBtnConfirm().setEnabled(false);
                    }
                }catch (Exception e){
                    getBtnConfirm().setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void dismiss() {
        CViewUtils.hideKeyboard(getEtMessage());
        super.dismiss();
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setOnPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener displayPageIndexListener) {
        this.pdfDisplayPageIndexListener = displayPageIndexListener;
    }
}
