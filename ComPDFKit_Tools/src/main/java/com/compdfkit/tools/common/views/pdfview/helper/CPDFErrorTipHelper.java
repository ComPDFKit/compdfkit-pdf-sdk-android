/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.views.pdfview.helper;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.ui.reader.CPDFReaderView;

/**
 * Handles PDF error tip callbacks (toast / dialog) for the reader view.
 *
 * <p>Extracted from {@code CPDFViewCtrl.setErrorCallback()}.
 *
 * <p>Bug fix: the original code would set the error callback when
 * {@code cpdfConfiguration} was null (defaulting to "on"). Now the callback
 * is only set when {@code enableErrorTips} is explicitly true, defaulting
 * to safe (off) when configuration is absent.
 */
public final class CPDFErrorTipHelper {

    private CPDFErrorTipHelper() {
    }

    /**
     * Apply the error message callback to the reader view if
     * {@code configuration.globalConfig.enableErrorTips} is true.
     * Does nothing when configuration is null or the flag is false.
     */
    public static void applyErrorCallback(CPDFReaderView readerView,
                                          CPDFConfiguration configuration,
                                          Context context) {
        if (readerView == null || context == null) {
            return;
        }
        if (configuration == null || configuration.globalConfig == null) {
            return;
        }
        if (!configuration.globalConfig.enableErrorTips) {
            return;
        }
        readerView.setPdfErrorMessageCallback(errorId -> {
            switch (errorId) {
                case NO_TEXT_ON_PAGE:
                    CAlertDialog alertDialog = CAlertDialog.newInstance(
                            context.getString(R.string.tools_warning),
                            context.getString(R.string.tools_scan_pdf_annot_warning)
                    );
                    alertDialog.setConfirmClickListener(v -> alertDialog.dismiss());
                    FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(context);
                    if (fragmentActivity != null) {
                        alertDialog.show(fragmentActivity.getSupportFragmentManager(), "alertDialog");
                    }
                    break;
                case CANNOT_EDIT:
                    CToastUtil.showToast(context, R.string.tools_can_not_edit);
                    break;
                case NO_EMAIL_APP:
                    CToastUtil.showToast(context, R.string.tools_reader_view_error_no_email);
                    break;
                case NO_BROWSE_APP:
                    CToastUtil.showToast(context, R.string.tools_reader_view_error_no_browser);
                    break;
                case INVALID_LINK:
                    CToastUtil.showToast(context, R.string.tools_reader_view_error_invalid_link);
                    break;
                default:
                    break;
            }
        });
    }
}
