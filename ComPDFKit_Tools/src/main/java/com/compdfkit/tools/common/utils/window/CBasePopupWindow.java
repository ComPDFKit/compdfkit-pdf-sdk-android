package com.compdfkit.tools.common.utils.window;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

public abstract class CBasePopupWindow extends PopupWindow implements View.OnClickListener {
    private final static String TAG = "BasePopupWindow";
    private InputMethodManager imm;
    protected View mContentView;
    private LayoutInflater mInflater;
    protected Context mContext;

    @SuppressLint("WrongConstant")
    public CBasePopupWindow(Context context) {
        super(context);

        this.mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = setLayout(mInflater);
        if (mContentView == null) {
            return;
        }
        setContentView(mContentView);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setFocusable(true);
//        setOutsideTouchable(true);
//        setTouchInterceptor((v, event) -> {
//            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                dismiss();
//                return false;
//            }
//            return false;
//        });

        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        initView();

        initResource();

        initListener();
    }

    protected abstract View setLayout(LayoutInflater inflater);

    protected abstract void initResource();

    protected abstract void initListener();

    protected abstract void initView();

    protected abstract void onClickListener(View view);

    @Override
    public void onClick(View view) {
        onClickListener(view);
    }

    @Override
    public void dismiss() {
        changeWindowAlpha((Activity) mContext, 1.0f);
        super.dismiss();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    public void changeWindowAlpha(Activity activity, float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = alpha;
        if (lp.alpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }
}
