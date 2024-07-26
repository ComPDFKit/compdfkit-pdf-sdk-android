package com.compdfkit.tools.common.utils.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;

import java.util.List;


public class CBaseQuickViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views = new SparseArray<View>();

    public CBaseQuickViewHolder(@LayoutRes int layoutResId, ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false));
    }

    public CBaseQuickViewHolder(View rootView){
        super(rootView);
    }


    public <T extends View> T getView(@IdRes int viewId) {
        if (views.get(viewId) != null) {
            return (T) views.get(viewId);
        } else {
            return itemView.findViewById(viewId);
        }
    }

    public void setText(@IdRes int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
    }

    public void setText(@IdRes int viewId, @StringRes int stringResId) {
        TextView textView = getView(viewId);
        textView.setText(stringResId);
    }

    public void setTextColorRes(@IdRes int viewId, @ColorRes int colorResId) {
        TextView textView = getView(viewId);
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), colorResId));
    }

    public void setTextColor(@IdRes int viewId, @ColorInt int color) {
        TextView textView = getView(viewId);
        textView.setTextColor(color);
    }

    public void setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(imageResId);
    }

    public void setImageDrawable(@IdRes int viewId, Drawable drawable){
        ImageView imageView = getView(viewId);
        imageView.setImageDrawable(drawable);
    }

    public void setImageTintList(@IdRes int viewId, ColorStateList colorStateList){
        ImageView imageView = getView(viewId);
        imageView.setImageTintList(colorStateList);
    }

    public void setBackgroundResource(@IdRes int viewId, @DrawableRes int backgroundRes) {
        getView(viewId).setBackgroundResource(backgroundRes);
    }

    public void setBackgroundColor(@IdRes int viewId, @ColorInt int color){
        getView(viewId).setBackgroundColor(color);
    }

    public void setVisible(@IdRes int viewId, boolean visible){
        getView(viewId).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setEnabled(@IdRes int viewId, boolean enable) {
        getView(viewId).setEnabled(enable);
    }

    public void setChecked(@IdRes int viewId, boolean checked){
        CheckBox checkBox = getView(viewId);
        checkBox.setChecked(checked);
    }

    public void setSelected(@IdRes int viewId, boolean selected) {
        getView(viewId).setSelected(selected);
    }

    public void setOnClickListener(@IdRes int viewId, View.OnClickListener onClickListener) {
        getView(viewId).setOnClickListener(onClickListener);
    }

    public void setOnLongClickListener(@IdRes int viewId, View.OnLongClickListener longClickListener) {
        getView(viewId).setOnLongClickListener(longClickListener);
    }

    public void setItemHorizontalMargin(List list, int startMarginDp, int normalMarginDp, int endMarginDp){
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        int startMarginPx = CDimensUtils.dp2px(itemView.getContext(), startMarginDp);
        int endMarginPx = CDimensUtils.dp2px(itemView.getContext(), endMarginDp);
        int normalMarginPx = CDimensUtils.dp2px(itemView.getContext(), normalMarginDp);

        if (getAdapterPosition() == 0){
            //first item
            layoutParams.setMargins(startMarginPx, layoutParams.topMargin, normalMarginPx, layoutParams.bottomMargin);
        } else if (getAdapterPosition() == list.size() -1) {
            //end item
            layoutParams.setMargins(normalMarginPx, layoutParams.topMargin, endMarginPx, layoutParams.bottomMargin);
        }else {
            layoutParams.setMargins(normalMarginPx, layoutParams.topMargin, normalMarginPx, layoutParams.bottomMargin);
        }
    }


}
