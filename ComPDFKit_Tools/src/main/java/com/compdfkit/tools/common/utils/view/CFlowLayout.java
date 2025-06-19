package com.compdfkit.tools.common.utils.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author RAW
 */
public class CFlowLayout extends ViewGroup {

    private int line_height;

    private int maxWidth = Integer.MAX_VALUE;

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public final int horizontal_spacing;
        public final int vertical_spacing;

        /**
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing Pixels between items, vertically
         */
        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
            super(0, 0);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;
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

        int widthLimit = (parentWidthMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : parentWidth)
                - getPaddingLeft() - getPaddingRight();

        widthLimit = Math.min(widthLimit, maxWidth - getPaddingLeft() - getPaddingRight());

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        int lineHeight = 0;
        int maxLineWidth = 0;

        int childHeightMeasureSpec = (parentHeightMode == MeasureSpec.AT_MOST)
                ? MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.AT_MOST)
                : MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.measure(MeasureSpec.makeMeasureSpec(widthLimit, MeasureSpec.AT_MOST), childHeightMeasureSpec);
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                lineHeight = Math.max(lineHeight, childHeight + lp.vertical_spacing);

                if (xpos + childWidth > widthLimit + getPaddingLeft()) {
                    // New line
                    maxLineWidth = Math.max(maxLineWidth, xpos - lp.horizontal_spacing);
                    xpos = getPaddingLeft();
                    ypos += lineHeight;
                    lineHeight = childHeight + lp.vertical_spacing;
                }

                xpos += childWidth + lp.horizontal_spacing;
            }
        }

        maxLineWidth = Math.max(maxLineWidth, xpos);

        int measuredWidth = (parentWidthMode == MeasureSpec.EXACTLY)
                ? parentWidth
                : maxLineWidth + getPaddingRight();

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
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(
            android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(1,1);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + lp.horizontal_spacing;
            }
        }
    }
}