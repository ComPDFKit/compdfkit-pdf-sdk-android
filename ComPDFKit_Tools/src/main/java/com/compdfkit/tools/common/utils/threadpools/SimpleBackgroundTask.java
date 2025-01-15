/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.threadpools;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

import java.lang.ref.WeakReference;


public abstract class SimpleBackgroundTask<T> extends AsyncTask<Void, Void, T> {
    WeakReference<Context> weakActivity;

    public SimpleBackgroundTask(Context context) {
        weakActivity = new WeakReference<>(context);
    }

    @Override
    protected final T doInBackground(Void... voids) {
        return onRun();
    }

    @Override
    protected void onPostExecute(T t) {
        Activity activity = null;
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(weakActivity.get());
        if ( weakActivity.get() instanceof Activity){
            activity = (Activity) weakActivity.get();
        } else if (fragmentActivity != null) {
            activity = CViewUtils.getFragmentActivity(weakActivity.get());
        }
        if ((null == activity) || activity.isFinishing() || activity.isDestroyed()) {
            cancel(true);
            return;
        } else {
            onSuccess(t);
            cancel(true);
        }
    }

    public SimpleBackgroundTask<T> execute() {
        executeOnExecutor(CThreadPoolUtils.getInstance().poolExecutor);
        return this;
    }

    abstract protected T onRun();

    abstract protected void onSuccess(T result);
}
