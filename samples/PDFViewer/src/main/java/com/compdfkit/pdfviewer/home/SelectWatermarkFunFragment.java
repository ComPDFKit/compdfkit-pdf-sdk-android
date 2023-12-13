package com.compdfkit.pdfviewer.home;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.pdfviewer.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class SelectWatermarkFunFragment extends CBasicBottomSheetDialogFragment {

    private View.OnClickListener addWatermarkClickListener;

    private View.OnClickListener removeWatermarkClickListener;

    public static SelectWatermarkFunFragment newInstance() {
        return new SelectWatermarkFunFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_watermark_select_fun;
    }

    @Override
    public void onCreateView(View rootView) {
        ConstraintLayout clAddWatermark = rootView.findViewById(R.id.cl_add_watermark);
        ConstraintLayout clRemoveWatermark = rootView.findViewById(R.id.cl_remove_watermark);
        clAddWatermark.setOnClickListener(addWatermarkClickListener);
        clRemoveWatermark.setOnClickListener(removeWatermarkClickListener);
    }

    @Override
    public void onViewCreate() {

    }

    public void setAddWatermarkClickListener(View.OnClickListener addWatermarkClickListener) {
        this.addWatermarkClickListener = addWatermarkClickListener;
    }

    public void setRemoveWatermarkClickListener(View.OnClickListener removeWatermarkClickListener) {
        this.removeWatermarkClickListener = removeWatermarkClickListener;
    }
}
