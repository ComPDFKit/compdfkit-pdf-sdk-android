package com.compdfkit.tools.common.utils;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class CToastUtil {
    private static Toast mToast;
    private static Handler mHandler;

    private static Handler getMainThreadHandler() {
        if (mHandler == null) {
            synchronized (CToastUtil.class) {
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mHandler;
    }

    public static void showToast(Context context, String text, int duration) {
        if (mToast != null) {
            mToast.cancel();
        }

        if (context != null) {
            final Context appContext = context;
            getMainThreadHandler().post(() -> {
                mToast = Toast.makeText(appContext, text, duration);
                mToast.show();
            });
        }
    }

    public static void showToast(Context mContext, String text) {
        if (mContext != null) {
            showToast(mContext, text, Toast.LENGTH_SHORT);
        }
    }

    public static void showToast(Context mContext, int resourceId) {
        if (mContext != null) {
            showToast(mContext, mContext.getResources().getString(resourceId), Toast.LENGTH_SHORT);
        }
    }

    public static void showLongToast(Context mContext, int resourceId) {
        if (mContext != null) {
            showToast(mContext, mContext.getResources().getString(resourceId), Toast.LENGTH_LONG);
        }
    }

    public static void showLongToast(Context mContext, String msg) {
        if (mContext != null) {
            showToast(mContext, msg, Toast.LENGTH_LONG);
        }
    }

    public static void showToast(Context mContext, int resourceId, int duration) {
        if (mContext != null) {
            showToast(mContext, mContext.getResources().getString(resourceId), duration);
        }
    }
}
