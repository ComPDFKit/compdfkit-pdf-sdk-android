package com.compdfkit.tools.common.views.pdfproperties.writing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.compdfkit.ui.utils.CPDFBitmapUtils;
import com.compdfkit.ui.utils.CPDFScreenUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class CPDFSignatureEditView extends View {

    private final int padding = 50;

    /****** 涂鸦的路径 ******/
    private ArrayList<PathPoints> drawPathPoints = null;
    /****** 需要在该控件上绘制的bitmap ******/
    private Bitmap pictureBitmap;

    /****** 上一次点击的坐标，防止记录重复的绘制点 ******/
    private float lastTouchX = 0;
    private float lastTouchY = 0;

    /****** 记录绘制区域的大小 ******/
    private float left = 0;
    private float right = 0;
    private float top = 0;
    private float bottom = 0;
    private PointF firstTouchPoint;
    private Paint markerPenPaint_signEdit;

    /****** 画笔属性值 ******/
    private float lineWidth;
    private int lineColor = Color.parseColor("#DD2C00");
    private int lineAlpha = 255;

    public CPDFSignatureEditView(Context context) {
        super(context);
        initView(context);
    }

    public CPDFSignatureEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CPDFSignatureEditView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context){
        lineWidth = CPDFScreenUtils.dp2px(context, 3.3f);
    }

    public void setPictureBitmap(Bitmap bitmap) {
        if (pictureBitmap != null && !pictureBitmap.isRecycled()) {
            pictureBitmap.recycle();
        }
        this.pictureBitmap = Bitmap.createBitmap(bitmap);
    }

    public void setLineWidth(int lineWidth){
        this.lineWidth = lineWidth;
        markerPenPaint_signEdit.setStrokeWidth(lineWidth);
        if (drawPathPoints != null && drawPathPoints.size() > 0) {
            for (int i = 0; i < drawPathPoints.size(); i++) {
                PathPoints points = drawPathPoints.get(i);
                points.pointSize = lineWidth;
            }
        }
        invalidate();
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        markerPenPaint_signEdit.setColor(lineColor);
        if (drawPathPoints != null && drawPathPoints.size() > 0) {
            for (int i = 0; i < drawPathPoints.size(); i++) {
                PathPoints points = drawPathPoints.get(i);
                points.pointColor = lineColor;
            }
        }
        invalidate();
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setAttribute(float lineWidth, int lineColor, int lineAlpha) {
        if (markerPenPaint_signEdit == null) {
            markerPenPaint_signEdit = new Paint();
        }

        this.lineWidth = lineWidth;
        this.lineColor = lineColor;
        this.lineAlpha = lineAlpha;

        markerPenPaint_signEdit.setStrokeWidth(lineWidth);
        markerPenPaint_signEdit.setColor(lineColor);
        markerPenPaint_signEdit.setAlpha(lineAlpha);
    }

    public Rect getSignViewRect() {
        float l = left > 0 ? left : 0;
        float t = top > 0 ? top : 0;
        float r = right < getWidth() ? right : getWidth();
        float b = bottom < getHeight() ? bottom : getHeight();
        float offset = lineWidth / 2.0f;
        l -= offset;
        t -= offset;
        r += offset;
        b += offset;
        //添加一些padding
        return new Rect((int) l-padding, (int) t-padding, (int) r+padding, (int) b+padding);
    }

    public ArrayList<PathPoints> getDrawPathPoints() {
        return drawPathPoints;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawBitmap(canvas);
        drawPaintPath(canvas);
    }

    private void drawBitmap(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int pw = 0;
        int ph = 0;
        if (pictureBitmap != null && !pictureBitmap.isRecycled()) {
            pw = pictureBitmap.getWidth();
            ph = pictureBitmap.getHeight();
            canvas.drawBitmap(pictureBitmap, (width - pw) / 2, (height - ph) / 2, null);
        }
        if ((width - pw) / 2 < left) {
            left = (width - pw) / 2;
        }
        if ((height - ph) / 2 < top) {
            top = (height - ph) / 2;
        }
        if (((width - pw) / 2 + pw) > right) {
            right = (width - pw) / 2 + pw;
        }
        if (((height - ph) / 2 + ph) > bottom) {
            bottom = (height - ph) / 2 + ph;
        }
    }

    private void drawPaintPath(Canvas canvas) {
        if (drawPathPoints != null) {
            PointF p;
            /****** 保存绘制的每条路径 ******/
            ArrayList<PathItem> pathList = new ArrayList<>();

            Iterator<PathPoints> it = drawPathPoints.iterator();
            while (it.hasNext()) {
                PathPoints pps = it.next();
                ArrayList<PointF> arc = pps.locations;
                /****** 获取每条线的颜色值 ******/
                int lineColor = pps.pointColor;
                float lineSize = pps.pointSize;
                int lineAlpha = pps.pointAlpha;
                if (arc.size() >= 2) {
                    Path drawingPath = new Path();
                    Iterator<PointF> iit = arc.iterator();
                    p = iit.next();
                    float mX = p.x;
                    float mY = p.y;
                    drawingPath.moveTo(mX, mY);
                    while (iit.hasNext()) {
                        p = iit.next();
                        float x = p.x;
                        float y = p.y;
                        drawingPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                        mX = x;
                        mY = y;
                    }
                    drawingPath.lineTo(mX, mY);
                    /****** 每条绘制的线和颜色值保存下来 ******/
                    pathList.add(new PathItem(drawingPath, lineColor, lineSize, lineAlpha));
                } else {
                    p = arc.get(0);
                    markerPenPaint_signEdit.setStyle(Paint.Style.FILL);
                    markerPenPaint_signEdit.setColor(lineColor);
                    markerPenPaint_signEdit.setAlpha(lineAlpha);
                    canvas.drawCircle(p.x, p.y, lineSize / 2, markerPenPaint_signEdit);
                }
            }

            Iterator<PathItem> ipath = pathList.iterator();
            /****** 绘制每一条保存的路径 ******/
            while (ipath.hasNext()) {
                PathItem iipath = ipath.next();
                markerPenPaint_signEdit.setStyle(Paint.Style.STROKE);
                markerPenPaint_signEdit.setColor(iipath.pathColor);
                markerPenPaint_signEdit.setAlpha(iipath.pathAlpha);
                markerPenPaint_signEdit.setStrokeWidth(iipath.pathSize);
                canvas.drawPath(iipath.path, markerPenPaint_signEdit);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (firstTouchPoint == null) {
            firstTouchPoint = new PointF(x, y);
        }
        findPaintingRect(x, y);
        if (x != lastTouchX || y != lastTouchY) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startDraw(x, y);
                    lastTouchX = x;
                    lastTouchY = y;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    continueDraw(x, y);
                    lastTouchX = x;
                    lastTouchY = y;
                    return true;
                case MotionEvent.ACTION_UP:
                    return true;
                default:
                    return super.onTouchEvent(event);
            }
        }

        return super.onTouchEvent(event);
    }

    private void findPaintingRect(float x, float y) {
        if (firstTouchPoint.x == x && firstTouchPoint.y == y) {
            left = x;
            right = x;
            top = y;
            bottom = y;
        } else {
            if (x < left) {
                left = x;
            }
            if (x > right) {
                right = x;
            }
            if (y < top) {
                top = y;
            }
            if (y > bottom) {
                bottom = y;
            }
        }
    }

    private void startDraw(float x, float y) {
        if (drawPathPoints == null) {
            drawPathPoints = new ArrayList<>();
        }

        ArrayList<PointF> arc = new ArrayList<>();
        arc.add(new PointF(x, y));
        PathPoints pps = new PathPoints(arc, lineColor,
                lineWidth,
                lineAlpha);
        drawPathPoints.add(pps);
        invalidate();
    }

    private void continueDraw(float x, float y) {
        if (drawPathPoints != null && drawPathPoints.size() > 0) {
            PathPoints pps = drawPathPoints.get(drawPathPoints.size() - 1);
            ArrayList<PointF> arc = pps.locations;
            arc.add(new PointF(x, y));
            invalidate();
        }
    }

    public void cancelDraw() {
        drawPathPoints = null;
        firstTouchPoint = null;
        if (pictureBitmap != null) {
            pictureBitmap.recycle();
            pictureBitmap = null;
        }
    }

    public Bitmap getBitmap(){
        if (getDrawPathPoints() == null || getDrawPathPoints().size()<=0){
            return null;
        }
        Bitmap signEditPic = CPDFBitmapUtils.loadBitmapFromView(this);
        signEditPic = CPDFBitmapUtils.cropBitmap(signEditPic, getSignViewRect());
        return signEditPic;
    }

    class PathPoints {
        ArrayList<PointF> locations;
        int pointColor;
        float pointSize;
        int pointAlpha;

        public PathPoints(ArrayList<PointF> locations, int pointColor, float pointSize, int pointAlpha) {
            this.locations = locations;
            this.pointColor = pointColor;
            this.pointSize = pointSize;
            this.pointAlpha = pointAlpha;
        }
    }

    class PathItem {
        Path path;
        int pathColor;
        float pathSize;
        int pathAlpha;

        public PathItem(Path path, int pathColor, float pathSize, int pathAlpha) {
            this.path = path;
            this.pathColor = pathColor;
            this.pathSize = pathSize;
            this.pathAlpha = pathAlpha;
        }
    }
}



