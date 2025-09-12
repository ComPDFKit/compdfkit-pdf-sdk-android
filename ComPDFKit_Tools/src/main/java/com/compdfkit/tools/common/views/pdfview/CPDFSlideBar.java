package com.compdfkit.tools.common.views.pdfview;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.compdfkit.ui.utils.CPDFBitmapUtils;
import com.compdfkit.ui.utils.CPDFScreenUtils;
import com.compdfkit.ui.utils.CPDFTextUtils;


public class CPDFSlideBar extends View {

    private final static String TAG = "PDFSlideBar";

    public enum SlideBarPosition {
        LEFT, TOP, RIGHT, BOTTOM
    }

    public interface OnScrollToPageListener {
        void onScrollBegin(int pageNum);

        void onScroll(int pageNum);

        void onScrollEnd(int pageNum);

        void onScrollToPage(int pageNum);
    }

    private Bitmap slideBarBitmap;
    private Bitmap thumbnailBitmap;
    private int slideBarWidth;
    private int slideBarHeight;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private int thumbnailBitmapWidth;
    private int thumbnailBitmapHeight;
    private int parentWidth;
    private int parentHeight;
    /****** Total number of pages in the document ******/
    protected int pageCount;
    /****** Current page number ******/
    protected int pageIndex;
    private float perPageDistance;

    private Paint bitmapPaint;
    private Paint linePaint;
    private Paint whitePaint;
    private Paint textBgPaint;
    private TextPaint textPaint;
    private int lineWidth;
    private int lineColor = Color.parseColor("#B4B4B4");
    private int textSize;
    private int textColor = Color.parseColor("#ffffffff");

    /****** position of the slider ******/
    private RectF currentSlideBarPosition = new RectF();
    /****** position of the thumbnail ******/
    private RectF thumbnailPosition = new RectF();
    private boolean isTouchSlideBar = false;
    private int gapBetweenThumbnailAndSlideBar;

    private boolean isInit = false;

    private float lastX, lastY;

    /****** The position of the slider to the side ******/
    private SlideBarPosition slideBarPosition = SlideBarPosition.RIGHT;
    private OnScrollToPageListener onScrollToPageListener;

    private ValueAnimator mScaleAnimator;
    private String animationTranX = "tranX";
    private String animationTranY = "tranY";

    private boolean showThumbnail = true;

    private boolean onlyShowPageIndex = false;

    public CPDFSlideBar(Context context) {
        this(context, null);
    }

    public CPDFSlideBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CPDFSlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBitmap();
        initPaint();
        initAnimation();
    }

    private void initAnimation() {
        mScaleAnimator = new ValueAnimator();
        mScaleAnimator.setInterpolator(new LinearInterpolator());
        mScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //update tranX & tranY
                // currentSlideBarPosition.top = (float) animation.getAnimatedValue(animationTranY);
                // currentSlideBarPosition.bottom = currentSlideBarPosition.top + slideBarHeight;

                switch (slideBarPosition) {
                    case LEFT:
                    case RIGHT:
                        currentSlideBarPosition.top = (float) animation.getAnimatedValue(animationTranY);
                        currentSlideBarPosition.bottom = currentSlideBarPosition.top + slideBarHeight;
                        break;
                    case TOP:
                    case BOTTOM:
                        currentSlideBarPosition.left = (float) animation.getAnimatedValue(animationTranX);
                        currentSlideBarPosition.right = currentSlideBarPosition.left + slideBarWidth;
                        break;
                    default:
                        break;
                }
                changeThumbnailPosition();
                invalidate();
            }
        });
    }

    private void initPaint() {
        bitmapPaint = new Paint();

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        lineWidth = (int) CPDFScreenUtils.dp2px(getContext(), 1);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(lineColor);

        whitePaint = new Paint();
        whitePaint.setAntiAlias(true);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setColor(Color.parseColor("#ffffffff"));

        textBgPaint = new Paint();
        textBgPaint.setAntiAlias(true);
        textBgPaint.setStyle(Paint.Style.FILL);
        textBgPaint.setColor(Color.parseColor("#60000000"));

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textSize = (int) CPDFScreenUtils.sp2px(getContext(), 14);
        textPaint.setTextSize(textSize);

        gapBetweenThumbnailAndSlideBar = (int) CPDFScreenUtils.dp2px(getContext(), 14);
    }

    public void setDefaultThumbnailSize(int thumbnailWidth, int thumbnailHeight) {
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }

    public void setSlideBarBitmap(Bitmap slideBarBitmap) {
        this.slideBarBitmap = slideBarBitmap;
        initBitmap();
    }

    public void setSlideBarBitmap(int resId) {
        this.slideBarBitmap = CPDFBitmapUtils.getBitmapFromDrawable(getContext(), resId);
        initBitmap();
    }

    private void initBitmap() {
        if (slideBarBitmap == null || slideBarBitmap.isRecycled()) {
            return;
        }
        /****** 如果是横向滑动，则旋转滑块图片的角度90度 ******/
        if (SlideBarPosition.TOP == slideBarPosition ||
                SlideBarPosition.BOTTOM == slideBarPosition) {
            // 定义矩阵对象
            Matrix matrix = new Matrix();
            // 向左旋转45度，参数为正则向右旋转
            matrix.postRotate(-90);
            slideBarBitmap = Bitmap.createBitmap(slideBarBitmap, 0, 0, slideBarBitmap.getWidth(), slideBarBitmap.getHeight(),
                    matrix, true);
        }

        initPosition();
    }

    private void initPosition(){
        slideBarWidth = slideBarBitmap.getWidth();
        slideBarHeight = slideBarBitmap.getHeight();

        /****** 根据滑块停靠的位置，计算出滑块的初始位置 ******/
        switch (slideBarPosition) {
            case LEFT:
            case TOP:
                currentSlideBarPosition.left = 0;
                currentSlideBarPosition.top = 0;
                currentSlideBarPosition.right = slideBarWidth;
                currentSlideBarPosition.bottom = slideBarHeight;
                break;
            case RIGHT:
                currentSlideBarPosition.left = thumbnailWidth + gapBetweenThumbnailAndSlideBar;
                currentSlideBarPosition.top = 0;
                currentSlideBarPosition.right = currentSlideBarPosition.left + slideBarWidth;
                currentSlideBarPosition.bottom = slideBarHeight;
                break;
            case BOTTOM:
                currentSlideBarPosition.left = 0;
                currentSlideBarPosition.top = thumbnailHeight + gapBetweenThumbnailAndSlideBar;
                currentSlideBarPosition.right = slideBarWidth;
                currentSlideBarPosition.bottom = currentSlideBarPosition.top + slideBarHeight;
                break;
            default:break;
        }
        postInvalidate();
    }

    /**
     * Sets which side the SlideBar stays on
     *
     * @param slideBarPosition Four types, up, down, left and right
     */
    public void setSlideBarPosition(SlideBarPosition slideBarPosition) {
        this.slideBarPosition = slideBarPosition;
        initBitmap();
    }

    /**
     * Sets the total number of pages
     *
     * @param pageCount he total number of pages
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Sets the initial position of SlideBar according to the initial page number
     *
     * @param pageIndex initial page number
     */
    public void setPageIndex(int pageIndex) {
        if (!isTouchSlideBar) {
            this.pageIndex = pageIndex;
            switch (slideBarPosition) {
                case LEFT:
                case RIGHT:
                    currentSlideBarPosition.top = pageIndex * perPageDistance;
                    currentSlideBarPosition.bottom = currentSlideBarPosition.top + slideBarHeight;
                    break;
                case TOP:
                case BOTTOM:
                    currentSlideBarPosition.left = pageIndex * perPageDistance;
                    currentSlideBarPosition.right = currentSlideBarPosition.left + slideBarWidth;
                    break;
                default:break;
            }
            changeThumbnailPosition();
        }
        invalidate();
    }

    /**
     * Sets the initial position of the SlideBar according to the initial page number (the movement process is an animation effect)
     *
     * @param pageIndex initial page number
     * @param duration  animation duration
     */
    public void setPageIndexByAnimation(int pageIndex, int duration) {
        if (mScaleAnimator == null) {
            initAnimation();
        }
        if (mScaleAnimator.isRunning()) {
            mScaleAnimator.cancel();
        }
        if (!isTouchSlideBar) {
            this.pageIndex = pageIndex;
            PropertyValuesHolder tranXHolder = PropertyValuesHolder.ofFloat(animationTranX, currentSlideBarPosition.left, pageIndex * perPageDistance);
            PropertyValuesHolder tranYHolder = PropertyValuesHolder.ofFloat(animationTranY, currentSlideBarPosition.top, pageIndex * perPageDistance);
            mScaleAnimator.setValues(tranXHolder, tranYHolder);
            mScaleAnimator.setDuration(duration);
            mScaleAnimator.start();
        }
    }

    /**
     * According to the position of the slider, change the position of the thumbnail, the symmetrical center line of the thumbnail is consistent with the symmetrical center line of the slider
     */
    private void changeThumbnailPosition() {
        switch (slideBarPosition) {
            case LEFT:
                thumbnailPosition.left = slideBarWidth + gapBetweenThumbnailAndSlideBar;
                thumbnailPosition.right = thumbnailPosition.left + thumbnailWidth;
                thumbnailPosition.top = (currentSlideBarPosition.top + currentSlideBarPosition.bottom) / 2 - thumbnailHeight / 2;
                thumbnailPosition.bottom = (currentSlideBarPosition.top + currentSlideBarPosition.bottom) / 2 + thumbnailHeight / 2;
                if (thumbnailPosition.top < 0) {
                    thumbnailPosition.top = 0;
                    thumbnailPosition.bottom = thumbnailHeight;
                }
                if (thumbnailPosition.bottom > parentHeight) {
                    thumbnailPosition.top = parentHeight - thumbnailHeight;
                    thumbnailPosition.bottom = parentHeight;
                }
                break;
            case TOP:
                thumbnailPosition.top = slideBarHeight + gapBetweenThumbnailAndSlideBar;
                thumbnailPosition.bottom = thumbnailPosition.top + thumbnailHeight;
                thumbnailPosition.left = (currentSlideBarPosition.left + currentSlideBarPosition.right) / 2 - thumbnailWidth / 2;
                thumbnailPosition.right = (currentSlideBarPosition.left + currentSlideBarPosition.right) / 2 + thumbnailWidth / 2;
                if (thumbnailPosition.left < 0) {
                    thumbnailPosition.left = 0;
                    thumbnailPosition.right = thumbnailWidth;
                }
                if (thumbnailPosition.right > parentWidth) {
                    thumbnailPosition.left = parentWidth - thumbnailWidth;
                    thumbnailPosition.right = parentWidth;
                }
                break;
            case RIGHT:
                thumbnailPosition.left = 0;
                thumbnailPosition.right = thumbnailWidth;
                thumbnailPosition.top = (currentSlideBarPosition.top + currentSlideBarPosition.bottom) / 2 - thumbnailHeight / 2;
                thumbnailPosition.bottom = (currentSlideBarPosition.top + currentSlideBarPosition.bottom) / 2 + thumbnailHeight / 2;
                if (thumbnailPosition.top < 0) {
                    thumbnailPosition.top = 0;
                    thumbnailPosition.bottom = thumbnailHeight;
                }
                if (thumbnailPosition.bottom > parentHeight) {
                    thumbnailPosition.top = parentHeight - thumbnailHeight;
                    thumbnailPosition.bottom = parentHeight;
                }
                break;
            case BOTTOM:
                thumbnailPosition.top = 0;
                thumbnailPosition.bottom = thumbnailHeight;
                thumbnailPosition.left = (currentSlideBarPosition.left + currentSlideBarPosition.right) / 2 - thumbnailWidth / 2;
                thumbnailPosition.right = (currentSlideBarPosition.left + currentSlideBarPosition.right) / 2 + thumbnailWidth / 2;
                if (thumbnailPosition.left < 0) {
                    thumbnailPosition.left = 0;
                    thumbnailPosition.right = thumbnailWidth;
                }
                if (thumbnailPosition.right > parentWidth) {
                    thumbnailPosition.left = parentWidth - thumbnailWidth;
                    thumbnailPosition.right = parentWidth;
                }
                break;
            default:break;
        }
    }

    /**
     * Sets thumbnail
     *
     * @param thumbnailBitmap Thumbnail
     */
    public void setThumbnailBitmap(Bitmap thumbnailBitmap) {
        this.thumbnailBitmap = thumbnailBitmap;
        if (thumbnailBitmap != null && !thumbnailBitmap.isRecycled()) {
            thumbnailBitmapWidth = thumbnailBitmap.getWidth();
            thumbnailBitmapHeight = thumbnailBitmap.getHeight();
        }

        switch (slideBarPosition) {
            case LEFT:
                currentSlideBarPosition.left = 0;
                currentSlideBarPosition.right = slideBarWidth;
                break;
            case TOP:
                currentSlideBarPosition.top = 0;
                currentSlideBarPosition.bottom = slideBarHeight;
                break;
            case RIGHT:
                currentSlideBarPosition.left = thumbnailWidth + gapBetweenThumbnailAndSlideBar;
                currentSlideBarPosition.right = thumbnailWidth + slideBarWidth + gapBetweenThumbnailAndSlideBar;
                break;
            case BOTTOM:
                currentSlideBarPosition.top = thumbnailHeight + gapBetweenThumbnailAndSlideBar;
                currentSlideBarPosition.bottom = thumbnailHeight + slideBarHeight + gapBetweenThumbnailAndSlideBar;
                break;
            default:break;
        }
    }

    public void showThumbnail(boolean show) {
        this.showThumbnail = show;
        invalidate();
    }

    public void onlyShowPageIndex(boolean only, int textRectWidth) {
        this.onlyShowPageIndex = only;
        thumbnailWidth = textRectWidth;
        thumbnailHeight = textSize * 3 / 2;
        invalidate();
    }

    public void setTextBackgroundColor(@ColorInt int color) {
        textBgPaint.setColor(color);
        invalidate();
    }

    /**
     * Sets the listener for sliding SlideBar
     *
     * @param onScrollToPageListener Listener for sliding SlideBar
     */
    public void setOnScrollToPageListener(OnScrollToPageListener onScrollToPageListener) {
        this.onScrollToPageListener = onScrollToPageListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchSlideBar = judgeTouchSlideBar(event.getX(), event.getY());
                if (isTouchSlideBar) {
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    if (onScrollToPageListener != null) {
                        onScrollToPageListener.onScrollBegin(pageIndex);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchSlideBar) {
                    float distanceX = event.getRawX() - lastX;
                    float distanceY = event.getRawY() - lastY;
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    changeSlideBarPosition(distanceX, distanceY);
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isTouchSlideBar) {
                    if (onScrollToPageListener != null) {
                        onScrollToPageListener.onScrollEnd(pageIndex);
                    }
                    isTouchSlideBar = false;
                    invalidate();
                }
                lastX = 0;
                lastY = 0;
                break;
            default:break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * ACTION_MOVE事件改变滑块的位置，同时改变缩略图的位置
     *
     * @param distanceX：ACTION_MOVE事件的x轴方向的移动距离
     * @param distanceY：ACTION_MOVE事件的y轴方向的移动距离
     */
    private void changeSlideBarPosition(float distanceX, float distanceY) {
        if (SlideBarPosition.TOP == slideBarPosition ||
                SlideBarPosition.BOTTOM == slideBarPosition) {
            if (currentSlideBarPosition.left + distanceX >= 0 && currentSlideBarPosition.right + distanceX <= parentWidth) {
                currentSlideBarPosition.left += distanceX;
                currentSlideBarPosition.right += distanceX;

                if (onScrollToPageListener != null) {
                    int page = Math.round((currentSlideBarPosition.left / perPageDistance));
                    if (pageIndex != page) {
                        pageIndex = page;
                        onScrollToPageListener.onScrollToPage(pageIndex);
                    }
                    onScrollToPageListener.onScroll(page);
                }
            }
        } else {
            if (currentSlideBarPosition.top + distanceY >= 0 && currentSlideBarPosition.bottom + distanceY <= parentHeight) {
                currentSlideBarPosition.top += distanceY;
                currentSlideBarPosition.bottom += distanceY;

                if (onScrollToPageListener != null) {
                    int page = Math.round((currentSlideBarPosition.top / perPageDistance));
                    if (pageIndex != page) {
                        pageIndex = page;
                        onScrollToPageListener.onScrollToPage(pageIndex);
                    }
                    onScrollToPageListener.onScroll(page);
                }
            }
        }
        changeThumbnailPosition();
    }

    /**
     * 判断ACTION_DOWN事件的触控点是否落在滑块上面
     *
     * @param x：ACTION_DOWN事件的触控点的x坐标
     * @param y：ACTION_DOWN事件的触控点的y坐标
     */
    private boolean judgeTouchSlideBar(float x, float y) {
        return currentSlideBarPosition.contains(x, y);
    }

    /**
     * 计算父容器的高宽（注意：添加该view到父view中，高宽必须使用MATCH_PARENT）
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (pageCount > 1) {
            if (SlideBarPosition.LEFT == slideBarPosition ||
                    SlideBarPosition.RIGHT == slideBarPosition) {
                perPageDistance = (float) (parentHeight - slideBarHeight) / (pageCount - 1);
            } else {
                perPageDistance = (float) (parentWidth - slideBarWidth) / (pageCount - 1);
            }
        } else {
            perPageDistance = Integer.MAX_VALUE;
        }

        if (perPageDistance > 0) {
            setPageIndex(pageIndex);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        switch (slideBarPosition) {
            case LEFT:
                super.layout(0, 0, slideBarWidth + thumbnailWidth + gapBetweenThumbnailAndSlideBar, parentHeight);
                break;
            case TOP:
                super.layout(0, 0, parentWidth, slideBarHeight + thumbnailHeight + gapBetweenThumbnailAndSlideBar);
                break;
            case RIGHT:
                super.layout(parentWidth - slideBarWidth - thumbnailWidth - gapBetweenThumbnailAndSlideBar, 0, parentWidth, parentHeight);
                break;
            case BOTTOM:
                super.layout(0, parentHeight - slideBarHeight - thumbnailHeight - gapBetweenThumbnailAndSlideBar, parentWidth, parentHeight);
                break;
            default:break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (slideBarBitmap != null) {
            canvas.drawBitmap(slideBarBitmap, new Rect(0, 0, slideBarWidth, slideBarHeight),
                    currentSlideBarPosition, bitmapPaint);
        }

        if (isTouchSlideBar) {
            if (showThumbnail) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(thumbnailPosition,
                            CPDFScreenUtils.dp2px(getContext(), 3),
                            CPDFScreenUtils.dp2px(getContext(), 3), whitePaint);
                } else {
                    canvas.drawRect(thumbnailPosition, whitePaint);
                }

                if (thumbnailBitmap != null && !thumbnailBitmap.isRecycled()) {
                    int offsetX = (int) ((thumbnailPosition.width() - thumbnailBitmapWidth) / 2);
                    int offsetY = (int) ((thumbnailPosition.height() - thumbnailBitmapHeight) / 2);
                    canvas.drawBitmap(thumbnailBitmap,
                            new Rect(0, 0, thumbnailBitmapWidth, thumbnailBitmapHeight),
                            new RectF(thumbnailPosition.left + offsetX, thumbnailPosition.top + offsetY, thumbnailPosition.right - offsetX, thumbnailPosition.bottom - offsetY),
                            bitmapPaint);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(thumbnailPosition.left + lineWidth / 2,
                            thumbnailPosition.top + lineWidth / 2,
                            thumbnailPosition.right - lineWidth / 2,
                            thumbnailPosition.bottom - lineWidth / 2,
                            CPDFScreenUtils.dp2px(getContext(), 3),
                            CPDFScreenUtils.dp2px(getContext(), 3),
                            linePaint);
                } else {
                    canvas.drawRect(thumbnailPosition.left + lineWidth / 2,
                            thumbnailPosition.top + lineWidth / 2,
                            thumbnailPosition.right - lineWidth / 2,
                            thumbnailPosition.bottom - lineWidth / 2,
                            linePaint);
                }
            }
            /****** 在缩略图下方绘制文字 ******/
            String content;
            if (onlyShowPageIndex) {
                content = String.valueOf((pageIndex + 1));
            } else {
                content = String.format("%d / %d", pageIndex + 1, pageCount);
            }
            StaticLayout staticLayout = CPDFTextUtils.getStaticLayout(content, textPaint, thumbnailWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
            if (staticLayout == null) {
                return;
            }
            float textWidth = staticLayout.getLineWidth(0);
            if (showThumbnail) {
                canvas.translate(0, thumbnailPosition.bottom - textSize * 3 / 2);
            } else {
                if (slideBarPosition == SlideBarPosition.LEFT || slideBarPosition == SlideBarPosition.RIGHT) {
                    canvas.translate(0, currentSlideBarPosition.centerY() - (textSize * 3 / 2 - lineWidth / 2) / 2);
                } else {
                    canvas.translate(0, thumbnailPosition.bottom - textSize * 3 / 2);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (slideBarPosition == SlideBarPosition.LEFT) {
                    canvas.drawRoundRect(thumbnailPosition.left + lineWidth / 2, lineWidth / 2, thumbnailPosition.right - lineWidth / 2, textSize * 3 / 2 - lineWidth / 2, CPDFScreenUtils.dp2px(getContext(), 3), CPDFScreenUtils.dp2px(getContext(), 3), textBgPaint);
                } else if (slideBarPosition == SlideBarPosition.RIGHT) {
                    canvas.drawRoundRect(lineWidth / 2, lineWidth / 2, thumbnailPosition.width() - lineWidth / 2, textSize * 3 / 2 - lineWidth / 2, CPDFScreenUtils.dp2px(getContext(), 3), CPDFScreenUtils.dp2px(getContext(), 3), textBgPaint);
                } else if (slideBarPosition == SlideBarPosition.TOP) {
                    canvas.drawRoundRect(thumbnailPosition.left + lineWidth / 2, lineWidth / 2, thumbnailPosition.right - lineWidth / 2, textSize * 3 / 2 - lineWidth / 2, CPDFScreenUtils.dp2px(getContext(), 3), CPDFScreenUtils.dp2px(getContext(), 3), textBgPaint);
                } else if (slideBarPosition == SlideBarPosition.BOTTOM) {
                    canvas.drawRoundRect(thumbnailPosition.left + lineWidth / 2, lineWidth / 2, thumbnailPosition.right - lineWidth / 2, textSize * 3 / 2 - lineWidth / 2, CPDFScreenUtils.dp2px(getContext(), 3), CPDFScreenUtils.dp2px(getContext(), 3), textBgPaint);
                }
            } else {
                if (slideBarPosition == SlideBarPosition.LEFT) {
                    canvas.drawRect(thumbnailPosition.left + lineWidth / 2, lineWidth / 2, thumbnailPosition.right - lineWidth / 2, textSize * 3 / 2 - lineWidth / 2, textBgPaint);
                } else if (slideBarPosition == SlideBarPosition.RIGHT) {
                    canvas.drawRect(lineWidth / 2, lineWidth / 2, thumbnailPosition.width() - lineWidth / 2, textSize * 3 / 2 - lineWidth / 2, textBgPaint);
                } else if (slideBarPosition == SlideBarPosition.TOP) {
                    canvas.drawRect(thumbnailPosition.left + lineWidth / 2, lineWidth / 2, thumbnailPosition.right - lineWidth / 2, textSize * 3 / 2 - lineWidth / 2, textBgPaint);
                } else if (slideBarPosition == SlideBarPosition.BOTTOM) {
                    canvas.drawRect(thumbnailPosition.left + lineWidth / 2, lineWidth / 2, thumbnailPosition.right - lineWidth / 2, textSize * 3 / 2 - lineWidth / 2, textBgPaint);
                }
            }
            canvas.translate(thumbnailPosition.left + thumbnailWidth / 2 - textWidth / 2, 0);
            float staticLayoutHeight = CPDFTextUtils.getTextSmallestBounds(textPaint, content).height();
            canvas.drawText(content, 0, (textSize * 3 / 2 + staticLayoutHeight) / 2, textPaint);
        }
    }

    public void refreshLayoutPosition(){
        try {
            requestLayout();
            postInvalidate();
            initPosition();
            setPageIndex(pageIndex);
        }catch (Exception e){

        }
    }
}
