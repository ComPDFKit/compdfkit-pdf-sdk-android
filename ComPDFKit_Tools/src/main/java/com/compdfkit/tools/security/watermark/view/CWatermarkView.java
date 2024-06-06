/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.watermark.view;


import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;

import java.util.Arrays;
import java.util.List;


public class CWatermarkView extends View {

    /**
     * Control zoom and rotate the position of the four points of the icon
     */
    public static final int LEFT_TOP = 0;
    public static final int RIGHT_TOP = 1;
    public static final int RIGHT_BOTTOM = 2;
    public static final int LEFT_BOTTOM = 3;

    /**
     * Default border and text padding values
     */
    public static final int DEFAULT_FRAME_PADDING = 8;

    /**
     * Default border width
     */
    public static final int DEFAULT_FRAME_WIDTH = 2;

    /**
     * default border color value
     */
    public static final int DEFAULT_FRAME_COLOR = Color.WHITE;

    /**
     * Default watermark scaling value
     */
    public static final float DEFAULT_SCALE = 1.0F;

    /**
     * Default watermark rotation angle
     */
    public static final float DEFAULT_DEGREE = 0F;

    /**
     * The display position of the icon used for dragging, rotating, and scaling the watermark.
     * The default is the lower right corner.
     */
    public static final int DEFAULT_CONTROL_LOCATION = RIGHT_BOTTOM;

    /**
     * Edit mode default value, edit mode is enabled by default
     * true: editable mode
     * false : preview mode
     */
    public static final boolean DEFAULT_EDITABLE = true;

    public static final int DEFAULT_OTHER_DRAWABLE_WIDTH = 50;
    public static final int DEFAULT_OTHER_DRAWABLE_HEIGHT = 50;

    /**
     * Click time less than 300ms is regarded as a click
     **/
    public static final int CLICK_TIME = 100;

    /**
     * init status
     */
    public static final int STATUS_INIT = 0;

    /**
     * drag status
     */
    public static final int STATUS_DRAG = 1;

    /**
     * Rotate or zoom in state
     */
    public static final int STATUS_ROTATE_ZOOM = 2;

    /**
     * Text watermark default text content
     */
    public static final String DEFAULT_STR = "";

    public enum EditType {
        TXT, Image
    }

    /**
     * Set the currently edited watermark type
     */
    private EditType editType = EditType.TXT;

    /**
     * image watermark bitmap
     */
    private Bitmap mBitmap = null;

    /**
     * The center point coordinates of Single Touch View, relative to its parent layout
     */
    private PointF mCenterPoint = new PointF();

    /**
     * The width and height of the View change with the rotation of the image
     * (excluding controlling rotation and scaling the width and height of the image)
     */
    private int mViewWidth = 0;

    private int mViewHeight = 0;

    /**
     * Image rotation angle
     */
    private float mDegree = DEFAULT_DEGREE;

    /**
     * Image scaling
     */
    private float mScale = DEFAULT_SCALE;

    /**
     * Matrices for scaling, rotation, translation
     */
    private Matrix mMatrix = new Matrix();

    /**
     * Left spacing of Single Touch View from parent layout
     */
    private int mViewPaddingLeft = 0;

    /**
     * Single Touch View's top spacing from parent layout
     */
    private int mViewPaddingTop = 0;

    /**
     * Coordinates of four points in the picture
     */
    private Point mLTPoint = new Point();
    private Point mRTPoint = new Point();
    private Point mRBPoint = new Point();
    private Point mLBPoint = new Point();

    /**
     * Coordinates of control points used for scaling, rotation
     */
    private Point mControlPoint = new Point();

    /**
     * 用于缩放，Icons for zoom, rotate
     */
    private Drawable controlDrawable = null;

    /**
     * Control scaling, rotating icon width and height
     */
    private int mControlDrawableWidth = 0;
    private int mControlDrawableHeight = 0;

    /**
     * The distance from the initial image boundary point to the image center
     **/
    private float drawToCenterDistance = 0F;

    /**
     * Path to draw the outer frame
     */
    private Path mPath = new Path();

    /**
     * Brush for drawing outer borders
     */
    private Paint mPaint = new Paint();

    /**
     * Brush for painting pictures
     **/
    private Paint mDrawPaint = new Paint();

    /**
     * text brush
     **/
    private TextPaint txtPaint = new TextPaint();

    /**
     * Current status
     */
    private int mStatus = STATUS_INIT;

    /**
     * The distance between the outer border and the image, the unit is dip
     */
    private int framePadding = DEFAULT_FRAME_PADDING;

    /**
     * Outer border color
     */
    private int frameColor = DEFAULT_FRAME_COLOR;

    /**
     * Outer border line thickness, unit is dip
     */
    private int frameWidth = DEFAULT_FRAME_WIDTH;

    /**
     * Whether it is in a state where it can be zoomed, translated, and rotated
     */
    private boolean isEditable = DEFAULT_EDITABLE;
    private DisplayMetrics metrics = null;
    private PointF mPreMovePointF = new PointF();
    private PointF mCurMovePointF = new PointF();

    /**
     * The offset in the x direction when the image is rotated
     */
    private int offsetX = 0;

    /**
     * The offset in the y direction of the image when rotating
     */
    private int offsetY = 0;

    /**
     * Control the location of the icon (such as upper left, upper right, lower left, lower right)
     */
    private int controlLocation = DEFAULT_CONTROL_LOCATION;

    /**
     * txt Watermark Text
     **/
    private String txtContent = DEFAULT_STR;

    /**
     * Record click time and coordinates
     **/
    private long downClickTime = 0L;
    private PointF downClickPoint = new PointF();

    /**
     * Draw the baseline of txt
     **/
    private float baseLineY = 0F;

    /**
     * Draw the original width and height of txt
     **/
    private float textWidth = 0F;

    private float textHeight = 0F;

    /**
     * Draw area m Path area, determine click and drag
     **/
    private Region dragArea = new Region();

    private ClickDrawAreaListener clickDrawAreaListener;

    private ClickEventActionListener clickEventActionListener;

    private Typeface typeface = Typeface.DEFAULT;


    private String fontPsName = CPDFTextAttribute.FontNameHelper.FontType.Helvetica.name();

    private int textColor = Color.BLACK;

    private int watermarkAlpha = 255;

    private boolean enableDrag = true;

    public CWatermarkView(Context context) {
        this(context, null);
    }

    public CWatermarkView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CWatermarkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        metrics = getResources().getDisplayMetrics();

        framePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_PADDING, metrics);
        frameWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_WIDTH, metrics);

        mControlDrawableWidth = CDimensUtils.dp2px(getContext(), 25);
        mControlDrawableHeight = CDimensUtils.dp2px(getContext(), 25);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.WatermarkView);
        Drawable srcDrawable = mTypedArray.getDrawable(R.styleable.WatermarkView_src);
        mBitmap = drawable2Bitmap(srcDrawable);
        framePadding = mTypedArray.getDimensionPixelSize(R.styleable.WatermarkView_framePadding, framePadding);
        frameWidth = mTypedArray.getDimensionPixelSize(R.styleable.WatermarkView_frameWidth, frameWidth);
        frameColor = mTypedArray.getColor(R.styleable.WatermarkView_frameColor, DEFAULT_FRAME_COLOR);
        mScale = mTypedArray.getFloat(R.styleable.WatermarkView_scale, DEFAULT_SCALE);
        mDegree = mTypedArray.getFloat(R.styleable.WatermarkView_degree, DEFAULT_DEGREE);
        controlLocation = mTypedArray.getInt(R.styleable.WatermarkView_controlLocation, DEFAULT_CONTROL_LOCATION);
        isEditable = mTypedArray.getBoolean(R.styleable.WatermarkView_editable, DEFAULT_EDITABLE);
        mControlDrawableWidth = (int) mTypedArray.getDimension(R.styleable.WatermarkView_controlDrawableWidth, (float) mControlDrawableWidth);
        mControlDrawableHeight = (int) mTypedArray.getDimension(R.styleable.WatermarkView_controlDrawableHeight, (float) mControlDrawableHeight);
        mTypedArray.recycle();

        controlDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tools_ic_watermark_control);

        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(CDimensUtils.dp2px(getContext(), 30));
        txtPaint.setColor(textColor);
        txtPaint.setAlpha(watermarkAlpha);
        txtPaint.setTypeface(typeface);

        mPaint.setAntiAlias(true);
        mPaint.setColor(frameColor);
        mPaint.setStrokeWidth(frameWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mDrawPaint.setAntiAlias(true);

        fontPsName = CPDFTextAttribute.FontNameHelper.obtainFontName(
                CPDFTextAttribute.FontNameHelper.FontType.Helvetica, false, false
        );

    }

    /**
     * Initialize image watermark
     *
     * @param bitmap Picture as watermark
     */
    public void setImageBitmap(Bitmap bitmap) {
        editType = EditType.Image;
        mBitmap = bitmap;
        framePadding = 0;
        calculateDrawToCenterDistance();
        transformDraw();
        invalidate();
    }

    /**
     * image watermark preview mode
     *
     * @param bitmap watermark image
     * @param alpha  image opacity
     * @param degree image rotation angle
     * @param scale  scale
     * @param center center point
     **/
    public void initPreviewImageBitmap(Bitmap bitmap, int alpha, float degree, float scale, PointF center) {
        editType = EditType.Image;
        isEditable = false;
        framePadding = 0;
        mControlDrawableWidth = 0;
        mControlDrawableHeight = 0;
        mBitmap = bitmap;
        watermarkAlpha = alpha;
        mDrawPaint.setAlpha(alpha);
        mDegree = degree;
        mScale = scale;
        mCenterPoint = center;
        calculateDrawToCenterDistance();
        transformDraw();
    }

    /**
     * txt watermark preview mode
     *
     * @param text   text content
     * @param color  text color
     * @param size   font size
     * @param alpha  font opacity
     * @param degree rotation angle
     * @param scale  scale
     * @param center center point
     **/
    public void initPreviewTextWaterMark(String text, @ColorInt int color, float size, int alpha, float degree, float scale, PointF center) {
        editType = EditType.TXT;
        watermarkAlpha = alpha;
        isEditable = false;
        framePadding = 0;
        mControlDrawableWidth = 0;
        mControlDrawableHeight = 0;
        txtPaint.setColor(color);
        txtPaint.setTextSize(size);
        txtPaint.setAlpha(alpha);
        txtPaint.setTypeface(typeface);
        txtContent = TextUtils.isEmpty(text) ? DEFAULT_STR : text;
        mDegree = degree;
        mScale = scale;
        mCenterPoint = center;
        calculateBaseLine();
        calculateDrawToCenterDistance();
        transformDraw();
    }

    /**
     * init text watermark
     *
     * @param text  text content
     * @param color font color
     * @param size  font size
     * @param alpha font color opacity
     **/
    public void initTextWaterMark(String text, @ColorInt int color, float size, int alpha) {
        editType = EditType.TXT;
        txtPaint.setColor(color);
        txtPaint.setTextSize(size);
        txtPaint.setAlpha(alpha);
        watermarkAlpha = alpha;
        txtContent = TextUtils.isEmpty(text) ? DEFAULT_STR : text;
        calculateBaseLine();
        calculateDrawToCenterDistance();
        transformDraw();
    }

    public void resetLocation(PointF center) {
        offsetX = 0;
        offsetY = 0;
        if (editType == EditType.Image) {
            if (mBitmap != null) {
                setCenter(center);
                calculateDrawToCenterDistance();
                transformDraw();
            }
        } else {
            setCenter(center);
            calculateBaseLine();
            calculateDrawToCenterDistance();
            transformDraw();
        }

    }

    public void setTextSize(float textSize) {
        txtPaint.setTextSize(textSize);
        calculateBaseLine();
        calculateDrawToCenterDistance();
        transformDraw();
    }

    public int getTextSize() {
        return (int) txtPaint.getTextSize();
    }

    /**
     * set text or image watermark opacity
     *
     * @param alpha opacity
     */
    public void setWatermarkAlpha(@IntRange(from = 0, to = 255) int alpha) {
        watermarkAlpha = alpha;
        txtPaint.setAlpha(alpha);
        mDrawPaint.setAlpha(alpha);
        invalidate();
    }

    public int getWatermarkAlpha() {
        return watermarkAlpha;
    }

    public void setTypeface(String fontPsName) {
        Typeface typeface = CPDFTextAttribute.FontNameHelper.getTypeface(getContext(), fontPsName);
        this.typeface = typeface;
        this.fontPsName = fontPsName;
        txtPaint.setTypeface(this.typeface);
        calculateBaseLine();
        calculateDrawToCenterDistance();
        transformDraw();
    }

    public String getFontPsName() {
        return fontPsName;
    }

    /**
     * 设置旋转图
     *
     * @param drawable
     */
    public void setImageDrawable(Drawable drawable) {
        mBitmap = drawable2Bitmap(drawable);
        transformDraw();
    }

    public Bitmap getImageWatermarkBitmap() {
        return mBitmap;
    }

    public float getDegree() {
        return mDegree;
    }

    /**
     * Set watermark rotation angle
     *
     * @param degree
     */
    public void setDegree(float degree) {
        if (mDegree != degree) {
            mDegree = degree;
            transformDraw();
        }
    }

    public void setScale(float scale) {
        if (mScale != scale) {
            mScale = scale;
            transformDraw();
        }
    }

    public float getScale() {
        return mScale;
    }

    public PointF centerPoint() {
        return mCenterPoint;
    }

    public void setImageResource(@DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
        setImageDrawable(drawable);
    }

    public void setFramePadding(int framePadding) {
        if (this.framePadding == framePadding) {
            return;
        }
        this.framePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) framePadding, metrics);
        transformDraw();
    }

    /**
     * Peripheral border color
     *
     * @param frameColor
     */
    public void setFrameColor(@ColorInt int frameColor) {
        if (this.frameColor == frameColor) {
            return;
        }
        this.frameColor = frameColor;
        mPaint.setColor(frameColor);
        invalidate();
    }

    public void setFrameWidth(int frameWidth) {
        if (this.frameWidth == frameWidth) {
            return;
        }
        this.frameWidth = frameWidth;
        mPaint.setStrokeWidth(frameWidth);
        invalidate();
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    /**
     * Set the position of the control icon. The set values can only be
     * LEFT_TOP ，RIGHT_TOP， RIGHT_BOTTOM，LEFT_BOTTOM
     *
     * @param location controlLocation
     */
    public void setControlLocation(@IntRange(from = 0, to = 3) int location) {
        if (controlLocation == location) {
            return;
        }
        controlLocation = location;
        transformDraw();
    }

    /**
     * Set the center point position of the image, relative to the parent layout
     *
     * @param center
     */
    public void setCenter(PointF center) {
        this.mCenterPoint = center;
        adjustLayout();
    }

    /**
     * Set whether it is zoomable, panning, and rotating
     *
     * @param isEditable
     */
    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        invalidate();
    }

    public boolean isEditable() {
        return isEditable;
    }

    public Bitmap getBitmap() {
        boolean isEdit = isEditable;
        setEditable(false);
        buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache());
        setEditable(isEdit);
        return bitmap;
    }

    public void setTextColor(@ColorInt int color) {
        txtPaint.setColor(color);
        txtPaint.setAlpha(watermarkAlpha);
        invalidate();
    }

    public int getTextColor() {
        return txtPaint.getColor();
    }

    public void setText(String text) {
        txtContent = text;
        calculateBaseLine();
        calculateDrawToCenterDistance();
        transformDraw();
    }

    public String getText() {
        return txtContent;
    }

    public EditType getWatermarkType() {
        return editType;
    }

    public void setWatermarkType(EditType editType) {
        this.editType = editType;
    }

    public float getRadian() {
        return (float) (mDegree * Math.PI / 180);
    }

    public void setEnableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    public int getControlDrawableWidth() {
        return mControlDrawableWidth;
    }

    public int getFramePadding() {
        return framePadding;
    }

    public int getWatermarkPadding() {
        return (int) ((mControlDrawableWidth / 2F) + framePadding + (frameWidth / 2));
    }

    /**
     * Adjust the size and position of View
     */
    private void adjustLayout() {
        int actualWidth = mViewWidth + mControlDrawableWidth;
        int actualHeight = mViewHeight + mControlDrawableHeight;
        int newPaddingLeft = (int) (mCenterPoint.x - actualWidth / 2);
        int newPaddingTop = (int) (mCenterPoint.y - actualHeight / 2);
        if (mViewPaddingLeft != newPaddingLeft || mViewPaddingTop != newPaddingTop) {
            mViewPaddingLeft = newPaddingLeft;
            mViewPaddingTop = newPaddingTop;
        }
        layout(newPaddingLeft, newPaddingTop, newPaddingLeft + actualWidth, newPaddingTop + actualHeight);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForceDarkAllowed(false);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Adjust the position and size of the View before each draw
        super.onDraw(canvas);
        switch (editType) {
            case Image:
                if (mBitmap == null) {
                    return;
                }
                canvas.save();
                canvas.drawBitmap(mBitmap, mMatrix, mDrawPaint);
                canvas.restore();
                break;
            default:
                canvas.save();
                if (!TextUtils.isEmpty(txtContent)) {
                    float drawWidth = textWidth * mScale;
                    float drawHeight = textHeight * mScale;

                    //1、Set the starting point for drawing text (the offset of the actual position)
                    canvas.translate((offsetX + mControlDrawableWidth / 2F), (offsetY + mControlDrawableHeight / 2F));
                    //2、Rotate around the center of the text
                    canvas.rotate(mDegree % 360, drawWidth / 2, drawHeight / 2);
                    //3、Set zoom ratio
                    canvas.scale(mScale, mScale);
                    canvas.drawText(txtContent, 0f, baseLineY, txtPaint);
                    canvas.restore();
                }
                break;
        }

        canvas.save();
        //Draw borders and control icons only when in editable state

        if (isEditable) {
            canvas.drawPath(mPath, mPaint);
            //draw rotate zoom icon
            controlDrawable.setBounds(
                    mControlPoint.x - mControlDrawableWidth / 2,
                    mControlPoint.y - mControlDrawableHeight / 2, mControlPoint.x + mControlDrawableWidth
                            / 2, mControlPoint.y + mControlDrawableHeight / 2
            );
            controlDrawable.draw(canvas);
        }
        canvas.restore();
        adjustLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEditable) {
            return super.onTouchEvent(event);
        }
        float px = event.getX();
        float py = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreMovePointF.set(px + mViewPaddingLeft, py + mViewPaddingTop);
                mStatus = judgeStatus(px, py);
                downClickTime = System.currentTimeMillis();
                downClickPoint.set(px, py);
                break;
            case MotionEvent.ACTION_UP:
                mStatus = STATUS_INIT;
                // Determine whether it is a click event: the click time must be less than CLICK_TIME,
                // and the coordinate point is the ACTION_DOWN coordinate point
                if ((System.currentTimeMillis() - downClickTime) <= CLICK_TIME && abs(px - downClickPoint.x) < 1 && abs(py - downClickPoint.y) < 1) {
                    if (judgeStatus(px, py) == STATUS_DRAG) {
                        if (clickDrawAreaListener != null) {
                            clickDrawAreaListener.callback();
                        }
                    }
                }
                // Callback after dragging
                if (clickEventActionListener != null) {
                    clickEventActionListener.callback(event, mScale, mCenterPoint);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // If the coordinate point is an ACTION DOWN coordinate point, no processing will be performed.
                if (px == downClickPoint.x && py == downClickPoint.y) {
                    return true;
                }


                mCurMovePointF.set(px + mViewPaddingLeft, py + mViewPaddingTop);
                if (mStatus == STATUS_ROTATE_ZOOM) {
                    // The distance between the moved point and the center of the picture
                    float moveToCenterDistance = distance4PointF(mCenterPoint, mCurMovePointF);
                    // The distance from the last point to the center of the picture
                    float preToCenterDistance = distance4PointF(mCenterPoint, mPreMovePointF);

                    // Whether it is a zoom gesture drag
                    boolean isReduce = preToCenterDistance > moveToCenterDistance;
                    // Calculate scaling
                    float scale = moveToCenterDistance / drawToCenterDistance;
                    // If it is a zoom out gesture, but the calculated scale is larger than the last time,
                    // no scaling will be performed, and vice versa.
                    if (isReduce && scale < mScale) {
                        mScale = max(0.3F, scale);
                    } else if (!isReduce && scale > mScale) {
                        mScale = scale;
                    }


                    // angle
                    double a = distance4PointF(mCenterPoint, mPreMovePointF);
                    double b = distance4PointF(mPreMovePointF, mCurMovePointF);
                    double c = distance4PointF(mCenterPoint, mCurMovePointF);
                    double cosb = (a * a + c * c - b * b) / (2 * a * c);
                    if (cosb >= 1) {
                        cosb = 1.0;
                    }
                    double radian = acos(cosb);
                    double newDegree = radianToDegree(radian);

                    //center -> vector of proMove, we use PointF to implement
                    PointF centerToProMove = new PointF(mPreMovePointF.x - mCenterPoint.x, mPreMovePointF.y - mCenterPoint.y);

                    //center -> vector of curMove
                    PointF centerToCurMove = new PointF(mCurMovePointF.x - mCenterPoint.x, mCurMovePointF.y - mCenterPoint.y);

                    // Vector cross product result. If the result is a negative number, it means counterclockwise.
                    // If the result is a positive number, it means clockwise.
                    double result = centerToProMove.x * centerToCurMove.y - centerToProMove.y * centerToCurMove.x;
                    if (result < 0) {
                        newDegree = -newDegree;
                    }
                    mDegree += newDegree;
                    transformDraw();
                } else if (mStatus == STATUS_DRAG) {
                    // Modify center point
                    if (enableDrag) {
                        mCenterPoint.x += mCurMovePointF.x - mPreMovePointF.x;
                        mCenterPoint.y += mCurMovePointF.y - mPreMovePointF.y;
                        adjustLayout();
                    }
                }
                mPreMovePointF.set(mCurMovePointF);
                if (clickEventActionListener != null) {
                    clickEventActionListener.callback(event, mScale, mCenterPoint);
                }
                break;
        }
        return true;
    }

    /**
     * Set Matrix, force refresh
     */
    private void transformDraw() {
        float drawWidth = 0;
        float drawHeight = 0;
        switch (editType) {
            case Image:
                if (mBitmap == null) {
                    return;
                }
                drawWidth = (mBitmap.getWidth() * mScale);
                drawHeight = (mBitmap.getHeight() * mScale);
                break;
            default:
                drawWidth = (textWidth * mScale);
                drawHeight = (textHeight * mScale);
                break;
        }
        if (drawWidth == 0 || drawHeight == 0) {
            return;
        }

        computeRect(-framePadding, -framePadding, (int) (drawWidth + framePadding), (int) (drawHeight + framePadding), (int) mDegree);

        //Set zoom ratio
        mMatrix.setScale(mScale, mScale);
        //Rotate around the center of the image
        mMatrix.postRotate(mDegree % 360, (drawWidth / 2F), (drawHeight / 2F));
        //Set the starting point for drawing the picture
        mMatrix.postTranslate((offsetX + mControlDrawableWidth / 2F), (offsetY + mControlDrawableHeight / 2F));

        adjustLayout();
    }

    /**
     * Get four points and the size of the View
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param degree
     */
    private void computeRect(int left, int top, int right, int bottom, int degree) {
        Point lt = new Point(left, top);
        Point rt = new Point(right, top);
        Point rb = new Point(right, bottom);
        Point lb = new Point(left, bottom);
        Point cp = new Point((left + right) / 2, (top + bottom) / 2);
        mLTPoint = obtainRotationPoint(cp, lt, degree);
        mRTPoint = obtainRotationPoint(cp, rt, degree);
        mRBPoint = obtainRotationPoint(cp, rb, degree);
        mLBPoint = obtainRotationPoint(cp, lb, degree);

        //Calculate the maximum value and minimum value of the X coordinate
        int maxCoordinateX = maxOrNull(Arrays.asList(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x), 0);

        int minCoordinateX = minOrNull(Arrays.asList(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x), 0);
        mViewWidth = maxCoordinateX - minCoordinateX;

        //Calculate the largest and smallest value of the Y coordinate
        int maxCoordinateY = maxOrNull(Arrays.asList(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y), 0);
        int minCoordinateY = minOrNull(Arrays.asList(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y), 0);
        mViewHeight = maxCoordinateY - minCoordinateY;

        //The coordinates of the center point of the View
        Point viewCenterPoint = new Point((maxCoordinateX + minCoordinateX) / 2, (maxCoordinateY + minCoordinateY) / 2);
        offsetX = mViewWidth / 2 - viewCenterPoint.x;
        offsetY = mViewHeight / 2 - viewCenterPoint.y;
        int halfControlDrawableWidth = mControlDrawableWidth / 2;
        int halfControlDrawableHeight = mControlDrawableHeight / 2;

        //Move the X coordinates of the four points of the Bitmap by offsetX + halfDrawableWidth
        mLTPoint.x += offsetX + halfControlDrawableWidth;
        mRTPoint.x += offsetX + halfControlDrawableWidth;
        mRBPoint.x += offsetX + halfControlDrawableWidth;
        mLBPoint.x += offsetX + halfControlDrawableWidth;

        //Move the Y coordinates of the four points of the Bitmap by offsetY + halfDrawableHeight
        mLTPoint.y += offsetY + halfControlDrawableHeight;
        mRTPoint.y += offsetY + halfControlDrawableHeight;
        mRBPoint.y += offsetY + halfControlDrawableHeight;
        mLBPoint.y += offsetY + halfControlDrawableHeight;
        mControlPoint = locationToPoint(controlLocation);

        //Calculate drawing area
        RectF rect = new RectF();

        mPath.reset();
        mPath.moveTo(mLTPoint.x, mLTPoint.y);
        mPath.lineTo(mRTPoint.x, mRTPoint.y);
        mPath.lineTo(mRBPoint.x, mRBPoint.y);
        mPath.lineTo(mLBPoint.x, mLBPoint.y);
        mPath.lineTo(mLTPoint.x, mLTPoint.y);
        mPath.computeBounds(rect, true);

        dragArea.setPath(mPath, new Region((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom));
    }

    /**
     * Get the point after rotating by a certain angle
     *
     * @param center viewCenter
     * @param source
     * @param degree
     * @return
     */
    private Point obtainRotationPoint(Point center, Point source, int degree) {
        //distance between the two
        Point disPoint = new Point();
        disPoint.x = source.x - center.x;
        disPoint.y = source.y - center.y;

        //The arc before rotation
        double originRadian = 0.0;

        //Angle before rotation
        double originDegree = 0.0;

        //Angle after rotation
        double resultDegree = 0.0;

        //arc after rotation
        double resultRadian = 0.0;

        //The coordinates of the point after rotation
        Point resultPoint = new Point();
        double distance = sqrt((disPoint.x * disPoint.x + disPoint.y * disPoint.y));
        if (disPoint.x == 0 && disPoint.y == 0) {
            return center;
            // Quadrant 1
        } else if (disPoint.x >= 0 && disPoint.y >= 0) {
            // Calculate the angle with the positive x direction
            originRadian = asin(disPoint.y / distance);

            // Quadrant 2
        } else if (disPoint.x < 0 && disPoint.y >= 0) {
            // Calculate the angle with the positive x direction
            originRadian = asin(abs(disPoint.x) / distance);
            originRadian += Math.PI / 2;

            // Quadrant 3
        } else if (disPoint.x < 0 && disPoint.y < 0) {
            // Calculate the angle with the positive x direction
            originRadian = asin(abs(disPoint.y) / distance);
            originRadian += Math.PI;
        } else if (disPoint.x >= 0 && disPoint.y < 0) {
            // Calculate the angle with the positive x direction
            originRadian = asin(disPoint.x / distance);
            originRadian += Math.PI * 3 / 2;
        }

        // Convert radians to degrees
        originDegree = radianToDegree(originRadian);
        resultDegree = originDegree + degree;

        // angle to radian
        resultRadian = degreeToRadian(resultDegree);
        resultPoint.x = (int) Math.round(distance * cos(resultRadian));
        resultPoint.y = (int) Math.round(distance * sin(resultRadian));
        resultPoint.x += center.x;
        resultPoint.y += center.y;
        return resultPoint;
    }

    /**
     * Determine which point the control icon is based on its position
     *
     * @return
     */
    private Point locationToPoint(int location) {
        switch (location) {
            case LEFT_TOP:
                return mLTPoint;
            case RIGHT_TOP:
                return mRTPoint;
            case RIGHT_BOTTOM:
                return mRBPoint;
            case LEFT_BOTTOM:
                return mLBPoint;
        }
        return mLTPoint;
    }

    /**
     * Convert angles to radians
     *
     * @param degree
     * @return
     */
    private double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    /**
     * Based on the clicked position, determine whether the click is in the control of rotation,
     * zooming of the picture, and rough calculation.
     *
     * @param x
     * @param y
     * @return
     */
    private int judgeStatus(float x, float y) {
        PointF touchPoint = new PointF(x, y);
        PointF controlPointF = new PointF(mControlPoint);

        //The distance from the clicked point to the point that controls rotation and scaling
        float distanceToControl = distance4PointF(touchPoint, controlPointF);

        // If the distance between the two is less than the minimum value of the width and height of the control icon,
        // it is considered that the control icon has been clicked.
        if (distanceToControl < coerceAtMost((mControlDrawableWidth / 2), mControlDrawableHeight / 2)) {
            return STATUS_ROTATE_ZOOM;
        } else if (dragArea.contains((int) x, (int) y)) {
            return STATUS_DRAG;
        } else {
            return STATUS_INIT;
        }
    }

    /**
     * @methodName： created by liujiyuan on 2021/3/29 下午6:13.
     * @description：Calculate the distance from the initial watermark image boundary point to the image center
     */
    private void calculateDrawToCenterDistance() {
        float halfDrawWidth = 0;
        float halfDrawHeight = 0;
        switch (editType) {
            case Image:
                if (mBitmap == null) {
                    return;
                }
                halfDrawWidth = mBitmap.getWidth() / 2F;
                halfDrawHeight = mBitmap.getHeight() / 2F;
                break;
            default:
                halfDrawWidth = (txtPaint.measureText(txtContent) / 2);
                Paint.FontMetrics font = txtPaint.getFontMetrics();
                halfDrawHeight = ((font.descent - font.ascent) / 2);
                break;
        }
        drawToCenterDistance = (float) sqrt((halfDrawWidth * halfDrawWidth + halfDrawHeight * halfDrawHeight));
    }

    /**
     * Calculate baseline
     **/
    private void calculateBaseLine() {
        Paint.FontMetrics fontMetrics = txtPaint.getFontMetrics();
        textHeight = fontMetrics.descent - fontMetrics.ascent;
        // Calculate the difference between the midline and the baseline
        float dy = textHeight / 2 - fontMetrics.bottom;
        // calculate baseLineY
        int drawHeight = (int) textHeight;
        baseLineY = drawHeight / 2 + dy;
        textWidth = txtPaint.measureText(txtContent);
    }

    /**
     * distance between two points
     *
     * @param pf1 (x1,y1)
     * @param pf2 (x2,y2)
     */
    private float distance4PointF(PointF pf1, PointF pf2) {
        float disX = pf2.x - pf1.x;
        float disY = pf2.y - pf1.y;
        return (float) sqrt((disX * disX + disY * disY));
    }

    /**
     * Get Bitmap object from Drawable
     *
     * @param drawable
     * @return
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        try {
            if (drawable == null) {
                return null;
            }
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int bitmapWidth = intrinsicWidth <= 0 ? DEFAULT_OTHER_DRAWABLE_WIDTH : intrinsicWidth;
            int bitmapHeight = intrinsicHeight <= 0 ? DEFAULT_OTHER_DRAWABLE_HEIGHT
                    : intrinsicHeight;
            Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public void refresh() {
        invalidate();
    }

    private double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }

    private <T extends Comparable<T>> T maxOrNull(List<T> list, T defaultValue) {
        if (list.isEmpty()) {
            return defaultValue;
        }
        T max = list.get(0);
        for (int i = 0; i < list.size(); i++) {
            T e = list.get(i);
            if (max.compareTo(e) < 0) {
                max = e;
            }
        }
        return max;
    }

    private <T extends Comparable<T>> T minOrNull(List<T> list, T defaultValue) {
        if (list.isEmpty()) {
            return defaultValue;
        }
        T min = list.get(0);
        for (int i = 0; i < list.size(); i++) {
            T e = list.get(i);
            if (min.compareTo(e) > 0) {
                min = e;
            }
        }
        return min;
    }


    private int coerceAtMost(int value, int maximumValue) {
        return (value > maximumValue) ? maximumValue : value;
    }

    public void setClickDrawAreaListener(ClickDrawAreaListener clickDrawAreaListener) {
        this.clickDrawAreaListener = clickDrawAreaListener;
    }

    public void setClickEventActionListener(ClickEventActionListener clickEventActionListener) {
        this.clickEventActionListener = clickEventActionListener;
    }

    public interface ClickDrawAreaListener {
        void callback();
    }

    public interface ClickEventActionListener {
        void callback(MotionEvent event, float a, PointF pointF);
    }


}
