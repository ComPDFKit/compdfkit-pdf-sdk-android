// java
package com.compdfkit.tools.common.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CFlowLayout extends ViewGroup {

    private int line_height;
    private int maxWidth = Integer.MAX_VALUE;

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public final int horizontal_spacing;
        public final int vertical_spacing;

        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
            super(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;
        }

        public LayoutParams(ViewGroup.LayoutParams src) {
            super(src);
            this.horizontal_spacing = 1;
            this.vertical_spacing = 1;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            // Default spacing when created from XML without explicit spacing
            this.horizontal_spacing = 1;
            this.vertical_spacing = 1;
        }
    }

    public CFlowLayout(Context context) {
        super(context);
    }

    public CFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.maxWidth});
        maxWidth = a.getDimensionPixelSize(0, Integer.MAX_VALUE);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentWidthMode = MeasureSpec.getMode(widthMeasureSpec);

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        int availableWidth = (parentWidthMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : parentWidth);
        availableWidth = Math.min(availableWidth, maxWidth);
        int widthLimit = availableWidth - getPaddingLeft() - getPaddingRight();

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        int lineHeight = 0;
        int maxLineWidth = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int childWidthSpec = getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(widthLimit, MeasureSpec.AT_MOST),
                    0,
                    lp.width
            );
            int childHeightSpec = getChildMeasureSpec(
                    heightMeasureSpec,
                    0,
                    lp.height
            );
            child.measure(childWidthSpec, childHeightSpec);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // Include margins in width/height used
            int childTotalWidth = childWidth + lp.leftMargin + lp.rightMargin;
            int childTotalHeight = childHeight + lp.topMargin + lp.bottomMargin + lp.vertical_spacing;

            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin + lp.vertical_spacing);

            if ((xpos - getPaddingLeft()) + childTotalWidth > widthLimit) {
                maxLineWidth = Math.max(maxLineWidth, xpos - lp.horizontal_spacing);
                xpos = getPaddingLeft();
                ypos += lineHeight;
                lineHeight = childTotalHeight; // start new line height with this child
            }

            xpos += childTotalWidth + lp.horizontal_spacing;
        }

        maxLineWidth = Math.max(maxLineWidth, xpos);

        int measuredWidth = (parentWidthMode == MeasureSpec.EXACTLY)
                ? parentWidth
                : Math.min(maxLineWidth + getPaddingRight(), availableWidth);

        int measuredHeight;
        if (parentHeightMode == MeasureSpec.EXACTLY) {
            measuredHeight = parentHeight;
        } else {
            measuredHeight = ypos + lineHeight + getPaddingBottom();
            if (parentHeightMode == MeasureSpec.AT_MOST) {
                measuredHeight = Math.min(measuredHeight, parentHeight);
            }
        }

        this.line_height = lineHeight;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    // java
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int widthLimit = width - getPaddingLeft() - getPaddingRight();

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        // Temporarily store the current line's views and their LayoutParams
        java.util.ArrayList<View> lineViews = new java.util.ArrayList<>();
        java.util.ArrayList<LayoutParams> lineLPs = new java.util.ArrayList<>();
        int lineMaxHeight = 0; // Maximum total height of the current line (including top/bottom margin, excluding vertical_spacing)

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int childw = child.getMeasuredWidth();
            final int childh = child.getMeasuredHeight();

            int childTotalWidth = childw + lp.leftMargin + lp.rightMargin;
            int childTotalHeight = childh + lp.topMargin + lp.bottomMargin; // Not including vertical_spacing

            // Check if a new line is needed (based on widthLimit excluding padding)
            if ((xpos - getPaddingLeft()) + childTotalWidth > widthLimit && !lineViews.isEmpty()) {
                // Layout the previous line first: vertically center according to the line's maximum height, and consider margin
                int x = getPaddingLeft();
                for (int j = 0; j < lineViews.size(); j++) {
                    View v = lineViews.get(j);
                    LayoutParams vlp = lineLPs.get(j);
                    int vw = v.getMeasuredWidth();
                    int vh = v.getMeasuredHeight();

                    int itemTotalHeight = vh + vlp.topMargin + vlp.bottomMargin;
                    int vCenterOffset = (lineMaxHeight - itemTotalHeight) / 2;
                    int top = ypos + vCenterOffset + vlp.topMargin;
                    int left = x + vlp.leftMargin;
                    v.layout(left, top, left + vw, top + vh);

                    x += vw + vlp.leftMargin + vlp.rightMargin + vlp.horizontal_spacing;
                }

                // Wrap to new line: advance y, reset line data (add previous line's vertical_spacing)
                ypos += lineMaxHeight + (lineLPs.isEmpty() ? 0 : lineLPs.get(0).vertical_spacing);
                xpos = getPaddingLeft();
                lineViews.clear();
                lineLPs.clear();
                lineMaxHeight = 0;
            }

            // Add to the current line
            lineViews.add(child);
            lineLPs.add(lp);
            lineMaxHeight = Math.max(lineMaxHeight, childTotalHeight);
            xpos += childTotalWidth + lp.horizontal_spacing;
        }

        // Layout the last line (if any)
        if (!lineViews.isEmpty()) {
            int x = getPaddingLeft();
            for (int j = 0; j < lineViews.size(); j++) {
                View v = lineViews.get(j);
                LayoutParams vlp = lineLPs.get(j);
                int vw = v.getMeasuredWidth();
                int vh = v.getMeasuredHeight();

                int itemTotalHeight = vh + vlp.topMargin + vlp.bottomMargin;
                int vCenterOffset = (lineMaxHeight - itemTotalHeight) / 2;
                int top = ypos + vCenterOffset + vlp.topMargin;
                int left = x + vlp.leftMargin;
                v.layout(left, top, left + vw, top + vh);

                x += vw + vlp.leftMargin + vlp.rightMargin + vlp.horizontal_spacing;
            }
        }
    }


}