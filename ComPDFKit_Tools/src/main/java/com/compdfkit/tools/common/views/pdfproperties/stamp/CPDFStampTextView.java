package com.compdfkit.tools.common.views.pdfproperties.stamp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.ui.utils.CPDFScreenUtils;

import java.util.Calendar;


public class CPDFStampTextView extends View {

    public enum TimeType {
        /**
         * gone date
         */
        NULL(0),

        /**
         * show date， 1970-01-01
         */
        DATE(1),

        /**
         * HH:mm:ss
         * 12:01:00
         */
        TIME(2),

        /**
         *  1970-01-01 HH:mm:ss
         */
        TIME_AND_DATE(3);

        TimeType(int id) {
        }

        public static TimeType valueOf(int index) {
            if (index < 0 || index >= values().length) {
                CLog.e("TimeType", "TimeType: IndexOutOfBoundsException");
                index = 0;
            }
            return values()[index];
        }
    }

    private final String defaultContent = "StampText";

    private float gap;

    /**
     * The ratio of the height of a triangle to the font size.
     */
    private final static float RATE = 0.6f;

    private float lineWidth;

    private int lineColor = Color.parseColor("#567335");

    private Paint linePaint;

    private Paint linePaint2;

    private int bgColor = Color.parseColor("#ffCFE0C7");

    private Paint bgPaint;

    private float defaultTextSize;

    private float textSize = defaultTextSize;

    private int textColor = Color.parseColor("#325413");

    private TextPaint textPaint;

    private TextPaint datePaint;

    private float maxWidth;

    private float currentWidth = 0.0f;

    private float currentHeight = 0.0f;

    private String content = "";

    private CPDFStampAnnotation.TextStampShape shape = CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RECT;

    private CPDFStampAnnotation.TextStampColor textStampColor = CPDFStampAnnotation.TextStampColor.TEXTSTAMP_GREEN;

    private boolean timeSwitch, dateSwitch;

    private final static float SPACING_MULT = 1;

    private final static float SPACING_ADD = 0;

    /**
     * The radius of the circular arc drawn with a 90-degree angle divided by Math.min(length, width).
     */
    private static final float A = 0.2F;

    /**
     * The distance by which the length and width need to be reduced divided by
     * the radius of the circular arc drawn with a 90-degree angle.
     */
    private static final float B = 1F;

    /**
     * The distance between the center of the circle and the intersection
     * point of the length and width divided by the radius of the circular arc drawn with a 90-degree angle.
     */
    private static final float C = 1F;

    private static final float ANGLE = 90;

    private static final float SIN12 = 0.2079F;

    /**
     * style colors
     * 0:backgroundColor
     * 1:text color
     * 2:line color
     */
    private final int[] COLOR_WHITE = new int[]{0xFFE9E9EB, Color.BLACK, Color.BLACK};

    private final int[] COLOR_GREEN = new int[]{0xffCFE0C7, 0xff325413, 0xff567335};

    private final int[] COLOR_RED = new int[]{0xffFEC7C7, 0xff77140b, 0xff8c3022};

    private final int[] COLOR_BLUE = new int[]{0xffCBCFE2, 0xff09144a, 0xff313968};

    public CPDFStampTextView(Context context) {
        this(context, null);
    }

    public CPDFStampTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        getAttrs(context, attrs);
    }

    public CPDFStampTextView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context, attrs);
        initConfig(context);
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TPDFStampTextView);
        maxWidth = ta.getInt(R.styleable.TPDFStampTextView_max_Width, (int) (CPDFScreenUtils.getScreenWidth(context) * 0.5f));
        defaultTextSize = ta.getInt(R.styleable.TPDFStampTextView_max_TextSize, 100);
        maxWidth = CPDFScreenUtils.dp2px(context, maxWidth);
        defaultTextSize = CPDFScreenUtils.dp2px(context, defaultTextSize);
        ta.recycle();
    }

    private void initConfig(Context context) {
        gap = CPDFScreenUtils.dp2px(context, 3.3F);
        lineWidth = CPDFScreenUtils.dp2px(context, 0.6F);
        defaultTextSize = CPDFScreenUtils.dp2px(context, 33.3F);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);

        linePaint2 = new Paint();
        linePaint2.setStyle(Paint.Style.STROKE);
        linePaint2.setAntiAlias(true);
        linePaint2.setColor(lineColor);
        linePaint2.setStrokeWidth(lineWidth * 2);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

        datePaint = new TextPaint();
        datePaint.setAntiAlias(true);
        datePaint.setStyle(Paint.Style.FILL);
        datePaint.setColor(textColor);
        datePaint.setTextSize(textSize / 2);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content != null && content.contains("\n")) {
            content = content.replace("\n", " ");
        }
        this.content = content;
        requestLayout();
    }

    public TimeType getTimeType() {
        if (timeSwitch && dateSwitch) {
            return TimeType.TIME_AND_DATE;
        } else if (!timeSwitch && dateSwitch) {
            return TimeType.DATE;
        } else if (timeSwitch && !dateSwitch) {
            return TimeType.TIME;
        } else {
            return TimeType.NULL;
        }
    }

    public void setTimeType(TimeType timeType) {
        if (timeType == TimeType.TIME_AND_DATE) {
            timeSwitch = true;
            dateSwitch = true;
        } else if (timeType == TimeType.TIME) {
            timeSwitch = true;
            dateSwitch = false;
        } else if (timeType == TimeType.DATE) {
            timeSwitch = false;
            dateSwitch = true;
        } else {
            timeSwitch = false;
            dateSwitch = false;
        }
    }

    public CPDFStampAnnotation.TextStampShape getShape() {
        return shape;
    }

    public void setShape(CPDFStampAnnotation.TextStampShape shape) {
        this.shape = shape;
        requestLayout();
    }

    public void setTimeSwitch(boolean timeSwitch) {
        this.timeSwitch = timeSwitch;
        requestLayout();
    }

    public void setDateSwitch(boolean dateSwitch) {
        this.dateSwitch = dateSwitch;
        requestLayout();
    }

    public boolean getTimeSwitch() {
        return timeSwitch;
    }

    public int getShapeId(){
        return shape.id;
    }

    public int getTextStampColorId(){
        return textStampColor.id;
    }


    public boolean getDateSwitch() {
        return dateSwitch;
    }

    public String getDateStr() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (timeSwitch && !dateSwitch) {
            return numberToString(hour) + ":" + numberToString(minute) + ":" + numberToString(second);
        } else if (!timeSwitch && dateSwitch) {
            return "" + year + "/" + numberToString(month) + "/" + numberToString(date);
        } else if (timeSwitch && dateSwitch) {
            return "" + year + "/" + numberToString(month) + "/" + numberToString(date) + " " + numberToString(hour) + ":" + numberToString(minute) + ":" + numberToString(second);
        } else {
            return "";
        }
    }

    private String numberToString(int num) {
        if (num < 10) {
            return "0" + num;
        }
        return "" + num;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        linePaint.setStrokeWidth(lineWidth);
    }

    public int getLineColor() {
        return lineColor;
    }

    private void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        linePaint.setColor(lineColor);
    }

    public int getBgColor() {
        return bgColor;
    }

    private void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        bgPaint.setColor(bgColor);
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        datePaint.setTextSize(textSize / 2);
    }

    public int getTextColor() {
        return textColor;
    }

    private void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        datePaint.setColor(textColor);
    }

    public void setColor(int color){
        int[] colors = COLOR_WHITE;
        if (color == COLOR_RED[0]) {
            colors = COLOR_RED;
            textStampColor = CPDFStampAnnotation.TextStampColor.TEXTSTAMP_RED;
        } else if (color == COLOR_BLUE[0]){
            colors = COLOR_BLUE;
            textStampColor = CPDFStampAnnotation.TextStampColor.TEXTSTAMP_BLUE;
        } else if (color == COLOR_GREEN[0]){
            colors = COLOR_GREEN;
            textStampColor = CPDFStampAnnotation.TextStampColor.TEXTSTAMP_GREEN;
        }else {
            colors = COLOR_WHITE;
            textStampColor = CPDFStampAnnotation.TextStampColor.TEXTSTAMP_WHITE;
        }
        setBgColor(colors[0]);
        setTextColor(colors[1]);
        setLineColor(colors[2]);
        requestLayout();
    }

    private RectF getTextRectf(String contentStr, TextPaint paint) {
        CPDFStaticLayout staticLayout = new CPDFStaticLayout(
                contentStr,//text content
                paint,
                (int) maxWidth,
                Layout.Alignment.ALIGN_NORMAL,
                SPACING_MULT,
                SPACING_ADD,
                true
        );
        RectF rectF = new RectF(0, 0, 0, 0);
        for (int i = 0; i < staticLayout.getLineCount(); i++) {
            rectF.right += staticLayout.getLineWidth(i);
        }
        rectF.bottom = staticLayout.getHeight();
        return rectF;
    }

    private void calculateWidthAndHeight() {
        setTextSize(defaultTextSize);
        RectF dateRect;
        if ("".equals(content) || content == null) {
            dateRect = getTextRectf(getDateStr(), textPaint);
        } else {
            dateRect = getTextRectf(getDateStr(), datePaint);
        }
        RectF textRect = getTextRectf(content.length() > 0 ? content : defaultContent, textPaint);
        float maxContentLan = maxWidth - 2 * gap;
        if (CPDFStampAnnotation.TextStampShape.TEXTSTAMP_NONE != getShape() && CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RECT != getShape()) {
            maxContentLan = maxWidth - currentHeight * RATE - 2 * gap;
        }
        float width = Math.max(dateRect.right, textRect.right);
        if (width > maxContentLan) {
            currentWidth = maxWidth;
            textSize = textSize * maxContentLan / width;
            setTextSize(textSize);
            RectF dateRectTemp;
            if ("".equals(content) || content == null) {
                dateRectTemp = getTextRectf(getDateStr(), textPaint);
            } else {
                dateRectTemp = getTextRectf(getDateStr(), datePaint);
            }
            RectF textRectTemp = getTextRectf(content.length() > 0 ? content : defaultContent, textPaint);
            currentHeight = textRectTemp.bottom + 2 * gap;
            if (getTimeType() != TimeType.NULL) {
                currentHeight = dateRectTemp.bottom + (content.length() > 0 ? textRectTemp.bottom : 0) + 2 * gap;
            }
        } else {
            currentHeight = textRect.bottom + 2 * gap;
            if (getTimeType() != TimeType.NULL) {
                currentHeight = dateRect.bottom + (content.length() > 0 ? textRect.bottom : 0) + 2 * gap;
            }
            if (getContent().length() > 0 || TimeType.NULL == getTimeType()) {
                currentWidth = width + 2 * gap;
            } else {
                currentWidth = dateRect.right + 2 * gap;
            }
            if (CPDFStampAnnotation.TextStampShape.TEXTSTAMP_NONE != getShape() && CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RECT != getShape()) {
                currentWidth += currentHeight * RATE;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        calculateWidthAndHeight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec((int) currentWidth, widthMode);
        int height = MeasureSpec.makeMeasureSpec((int) currentHeight, heightMode);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLineAndBg(canvas);
        if (getContent().length() > 0 || TimeType.NULL == getTimeType()) {
            drawText(canvas);
        }
        if (TimeType.NULL != getTimeType()) {
            drawDate(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        canvas.save();
        if (CPDFStampAnnotation.TextStampShape.TEXTSTAMP_LEFT_TRIANGLE == shape) {
            canvas.translate(currentHeight * RATE, 0);
        }
        canvas.translate(gap, gap);
        CPDFStaticLayout staticLayout = new CPDFStaticLayout(
                content.length() > 0 ? content : defaultContent,
                textPaint,
                (int) currentWidth,
                Layout.Alignment.ALIGN_NORMAL,
                SPACING_MULT,
                SPACING_ADD,
                true
        );
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private void drawDate(Canvas canvas) {
        canvas.save();

        if (CPDFStampAnnotation.TextStampShape.TEXTSTAMP_LEFT_TRIANGLE == shape) {
            canvas.translate(currentHeight * RATE, 0);
        }
        if (getContent().length() > 0) {
            float translate = currentHeight / 2 + textSize / 4;
            canvas.translate(gap, translate);
        } else {
            canvas.translate(gap, gap);
        }
        TextPaint paint;
        if ("".equals(getContent()) || getContent() == null) {
            paint = textPaint;
        } else {
            paint = datePaint;
        }
        CPDFStaticLayout staticLayout = new CPDFStaticLayout(
                getDateStr(),
                paint,
                (int) currentWidth,
                Layout.Alignment.ALIGN_NORMAL,
                SPACING_MULT,
                SPACING_ADD,
                true
        );
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private int getGradientColorStart(int color) {
        return (color & 0x00ffffff) | 0x55000000;
    }

    private void drawLineAndBg(Canvas canvas) {
        float radio = Math.min(currentWidth, currentHeight) * A;
        float cut = radio * B;
        float oDis = radio * C;
        linePaint2.setColor(linePaint.getColor());
        canvas.save();

        LinearGradient linearGradient = new LinearGradient(0, 0, currentWidth, 0, getGradientColorStart(bgColor), bgColor, Shader.TileMode.REPEAT);
        bgPaint.setShader(linearGradient);

        if (CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RECT == shape) {
            float x0 = 0;
            float y0 = 0;
            float x1 = currentWidth;
            float y1 = 0;
            float x2 = currentWidth;
            float y2 = currentHeight;
            float x3 = 0;
            float y3 = currentHeight;

            //Draw a 60-degree arc within the rectFTo region, without connecting the two endpoints.
            Path path = new Path();
            path.moveTo(x0 + cut, y0);
            path.lineTo(x1 - cut, y1);
            RectF rectF1 = new RectF(x1 - 2 * oDis, y1, x1, y1 + 2 * oDis);
            path.arcTo(rectF1, 270 + 45 - ANGLE / 2, ANGLE, false);
            path.lineTo(x2, y2 - cut);
            RectF rectF2 = new RectF(x2 - 2 * oDis, y2 - 2 * oDis, x2, y2);
            path.arcTo(rectF2, 45 - ANGLE / 2, ANGLE, false);
            path.lineTo(x3 + cut, y3);
            RectF rectF3 = new RectF(x3, y3 - 2 * oDis, x3 + 2 * oDis, y3);
            path.arcTo(rectF3, 90 + 45 - ANGLE / 2, ANGLE, false);
            path.lineTo(x0, y0 + cut);
            RectF rectF0 = new RectF(x0, y0, x0 + 2 * oDis, y0 + 2 * oDis);
            path.arcTo(rectF0, 180 + 45 - ANGLE / 2, ANGLE, false);
            path.close();

            canvas.drawPath(path, bgPaint);
            canvas.drawPath(path, linePaint);

            //Bold horizontal and vertical lines.
            canvas.drawLine(x0 + cut - radio * SIN12, y0, x1 - cut + radio * SIN12, y1, linePaint2);
            canvas.drawLine(x1, y1 + cut - radio * SIN12, x2, y2 - cut + radio * SIN12, linePaint2);
            canvas.drawLine(x2 - cut + radio * SIN12, y2, x3 + cut - radio * SIN12, y3, linePaint2);
            canvas.drawLine(x3, y3 - cut + radio * SIN12, x0, y0 + cut - radio * SIN12, linePaint2);
        } else if (CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RIGHT_TRIANGLE == shape) {
            float x0 = 0;
            float y0 = 0;
            float x1 = currentWidth - currentHeight * RATE;
            float y1 = 0;
            float x2 = currentWidth;
            float y2 = currentHeight / 2;
            float x3 = currentWidth - currentHeight * RATE;
            float y3 = currentHeight;
            float x4 = 0;
            float y4 = currentHeight;
            Path path = new Path();
            path.moveTo(x0 + cut, y0);
            path.lineTo(x1, y1);
            path.lineTo(x2, y2);
            path.lineTo(x3, y3);
            path.lineTo(x4 + cut, y4);
            RectF rectF3 = new RectF(x4, y4 - 2 * oDis, x4 + 2 * oDis, y4);
            path.arcTo(rectF3, 90 + 45 - ANGLE / 2, ANGLE, false);
            path.lineTo(x0, y0 + cut);
            RectF rectF0 = new RectF(x0, y0, x0 + 2 * oDis, y0 + 2 * oDis);
            path.arcTo(rectF0, 180 + 45 - ANGLE / 2, ANGLE, false);
            path.close();
            canvas.drawPath(path, bgPaint);
            canvas.drawPath(path, linePaint);

            canvas.drawLine(x0 + cut - radio * SIN12, y0, x1, y1, linePaint2);
            canvas.drawLine(x3, y3, x4 + cut - radio * SIN12, y4, linePaint2);
            canvas.drawLine(x4, y4 - cut + radio * SIN12, x0, y0 + cut - radio * SIN12, linePaint2);
        } else if (CPDFStampAnnotation.TextStampShape.TEXTSTAMP_LEFT_TRIANGLE == shape) {
            float x0 = 0;
            float y0 = currentHeight / 2;
            float x1 = currentHeight * RATE;
            float y1 = 0;
            float x2 = currentWidth;
            float y2 = 0;
            float x3 = currentWidth;
            float y3 = currentHeight;
            float x4 = currentHeight * RATE;
            float y4 = currentHeight;
            Path path = new Path();
            path.moveTo(x0, y0);
            path.lineTo(x1, y1);
            path.lineTo(x2 - cut, y2);
            RectF rectF1 = new RectF(x2 - 2 * oDis, y2, x2, y2 + 2 * oDis);
            path.arcTo(rectF1, 270 + 45 - ANGLE / 2, ANGLE, false);
            path.lineTo(x3, y3 - cut);
            RectF rectF2 = new RectF(x3 - 2 * oDis, y3 - 2 * oDis, x3, y3);
            path.arcTo(rectF2, 45 - ANGLE / 2, ANGLE, false);
            path.lineTo(x4, y4);
            path.close();
            canvas.drawPath(path, bgPaint);
            canvas.drawPath(path, linePaint);
            canvas.drawLine(x1, y1, x2 - cut + radio * SIN12, y2, linePaint2);
            canvas.drawLine(x2, y2 + cut - radio * SIN12, x3, y3 - cut + radio * SIN12, linePaint2);
            canvas.drawLine(x3 - cut + radio * SIN12, y3, x4, y4, linePaint2);
        }
        canvas.restore();
    }
}
