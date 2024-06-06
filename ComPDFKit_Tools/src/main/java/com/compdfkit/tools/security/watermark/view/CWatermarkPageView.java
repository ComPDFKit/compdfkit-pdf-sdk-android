/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.watermark.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.watermark.CPDFWatermark;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.dialog.CEditDialog;
import com.compdfkit.tools.common.utils.glide.CPDFWrapper;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.security.watermark.pdfproperties.CPageRange;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Watermark editing display page,
 * including display document preview and text watermarks and image watermarks that need to be edited <br></p>
 * <blockquote><pre>
 * CWatermarkPageView.java
 * -----> CWatermarkView.java
 * </pre></blockquote>
 * <blockquote><pre>
 * com.compdfkit.tools.security.watermark.view.CWatermarkPageView
 * android:id="@+id/watermark_page_view"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent" />
 * </pre></blockquote>
 * <blockquote><pre>
 * // init
 * WatermarkPageView watermarkPageView = findViewById(R.id.watermark_page_view);
 * watermarkPageView.setDocument(cpdfDocument, pageIndex);
 * watermarkPageView.afterMeasured(() -> {
 *      if (editType == CWatermarkView.EditType.TXT) {
 *      // If it is a text watermark type, a watermark named 'Watermark' will be created by default.
 *      watermarkPageView.createTextWatermark("Watermark");
 *      } else {
 *
 *      }
 * });
 * // create text watermark
 * watermarkPageView.createTextWatermark("ComPDFKit");
 * // create image watermark
 * watermarkPageView.createImageWatermark(bitmap);
 * // edit current exists watermark
 * watermarkPageView.editWatermark(watermark);
 * // apply watermark
 * watermarkPageView.applyWatermark();
 * </pre></blockquote>
 */
public class CWatermarkPageView extends FrameLayout {

    private AppCompatImageView ivPageView;

    public CWatermarkView watermarkView;

    private FrameLayout watermarkViewContent;

    private CWatermarkTileView tileView;

    private CPDFDocument document;

    private int pageIndex = 0;

    private float currentPageWidth = 0;

    private float currentPageHeight = 0;

    private float whScale = 1F;

    private CPageRange pageRange = CPageRange.AllPages;

    private boolean isTile = false;

    private boolean front = true;

    private int watermarkSpacing = 30;

    public CWatermarkPageView(@NonNull Context context) {
        this(context, null);
    }

    public CWatermarkPageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CWatermarkPageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        // Create a ImageView that displays document pages
        ivPageView = new AppCompatImageView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;

        watermarkViewContent = new FrameLayout(getContext());
        LayoutParams layoutParams1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams1.gravity = Gravity.CENTER;
        watermarkViewContent.setLayoutParams(layoutParams1);
        addView(watermarkViewContent);

        //create watermark view
        watermarkView = new CWatermarkView(getContext());
        LayoutParams watermarkLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        watermarkLayoutParams.gravity = Gravity.CENTER;

        tileView = new CWatermarkTileView(getContext());
        LayoutParams tileLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        watermarkViewContent.addView(ivPageView, layoutParams);
        watermarkViewContent.addView(tileView, tileLp);
        watermarkViewContent.addView(watermarkView, watermarkLayoutParams);

        watermarkView.setEditable(true);
        watermarkView.setFrameColor(ContextCompat.getColor(getContext(), R.color.tools_color_accent));
        watermarkView.setFramePadding(10);
        watermarkView.setFrameWidth(CDimensUtils.dp2px(getContext(), 2));
        watermarkView.setControlLocation(CWatermarkView.LEFT_BOTTOM);
        watermarkView.setClickEventActionListener((event, scale, pointF) -> {
            updateTileWatermark();
        });
        watermarkView.setClickDrawAreaListener(() -> {
            if (watermarkView.getWatermarkType() != CWatermarkView.EditType.TXT) {
                return;
            }
            CEditDialog editDialog = CEditDialog.newInstance(getContext().getString(R.string.tools_text_watermark), watermarkView.getText());
            editDialog.setHint(getContext().getString(R.string.tools_type_your_watermark_text_here));
            editDialog.setEditListener(text -> {
                watermarkView.setText(text);
                editDialog.dismiss();
            });
            if (getContext() instanceof FragmentActivity) {
                editDialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "editDialog");
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (document != null) {
            initPageSize(w, h);
        }
    }

    /**
     * Set the PDF document to be edited and specify the page number to be previewed
     *
     * @param document  pdf document
     * @param pageIndex
     * @see CPDFDocument
     */
    public void setDocument(CPDFDocument document, int pageIndex) {
        this.document = document;
        this.pageIndex = pageIndex;
    }

    private void initDocumentThumbnail() {
        if (document != null && currentPageWidth != 0 && currentPageHeight != 0) {
            Glide.with(getContext())
                    .load(CPDFWrapper.fromDocument(document, pageIndex))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override((int) currentPageWidth, (int) currentPageHeight)
                    .into(ivPageView);
        }
    }

    private void initPageSize(int itemWidth, int itemHeight) {
        RectF rectF = document.pageAtIndex(pageIndex).getSize();
        float imageWidth = itemWidth;
        float imageHeight = (imageWidth / rectF.width()) * rectF.height();
        if (imageHeight > itemHeight) {
            imageHeight = itemHeight;
            imageWidth = (imageHeight / rectF.height()) * rectF.width();
        }

        currentPageWidth = imageWidth;
        currentPageHeight = imageHeight;
        whScale = rectF.width() / currentPageWidth;

        ViewGroup.LayoutParams layoutParams = watermarkViewContent.getLayoutParams();
        layoutParams.width = (int) currentPageWidth;
        layoutParams.height = (int) currentPageHeight;
        watermarkViewContent.setLayoutParams(layoutParams);

        watermarkView.setScale(whScale * 1.5F);
        initDocumentThumbnail();
    }

    public void setPageRange(CPageRange pageRange) {
        this.pageRange = pageRange;
    }

    public CPageRange getPageRange() {
        return pageRange;
    }

    public void setTile(boolean tile) {
        isTile = tile;
        watermarkView.setEnableDrag(!tile);
        updateTileWatermark();
    }

    public boolean isTile() {
        return isTile;
    }

    public void updateTileWatermark() {
        if (isTile) {
            tileView.setSpacing(getSpacing());
            tileView.setTileView(watermarkView);
        } else {
            tileView.clear();
        }
    }

    public void setFront(boolean front) {
        this.front = front;
    }

    public boolean isFront() {
        return front;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = getSize(widthMeasureSpec);
        int viewHeight = getSize(heightMeasureSpec);
        if (document != null) {
            RectF rectF = document.pageAtIndex(pageIndex).getSize();

            float imageWidth = viewWidth;
            float imageHeight = (imageWidth / rectF.width()) * rectF.height();
            if (imageHeight > viewHeight) {
                imageHeight = viewHeight;
                imageWidth = (imageHeight / rectF.height()) * rectF.width();
            }
            setMeasuredDimension((int) imageWidth, (int) imageHeight);
        } else {
            setMeasuredDimension(viewWidth, viewHeight);
        }
    }

    private int getSize(int measureSpec) {
        int customSize = CDimensUtils.getScreenWidth(getContext());

        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);

        switch (mode) {
            case MeasureSpec.AT_MOST:
                customSize = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                customSize = CDimensUtils.getScreenWidth(getContext());
                break;
            case MeasureSpec.EXACTLY:
                customSize = size;
                break;
            default:
                break;
        }
        return customSize;
    }

    /**
     * create a default text watermark
     *
     * @param watermarkText
     */
    public void createTextWatermark(String watermarkText) {
        PointF centerPointF = new PointF(currentPageWidth / 2, currentPageHeight / 2);
        int fontSize = CDimensUtils.spToPx(30, getContext());
        watermarkView.setCenter(centerPointF);
        watermarkView.initTextWaterMark(watermarkText, Color.BLACK, fontSize, 255);
        watermarkView.setDegree(0F);
    }

    public void updateCenterPoint() {
        PointF centerPointF = new PointF(currentPageWidth / 2, currentPageHeight / 2);
        watermarkView.resetLocation(centerPointF);
        watermarkView.postDelayed(() -> {
            updateTileWatermark();
            invalidate();
        }, 100);
    }

    public void createImageWatermark(Bitmap bitmap) {
        PointF centerPointF = new PointF(currentPageWidth / 2, currentPageHeight / 2);
        watermarkView.setCenter(centerPointF);
        watermarkView.setImageBitmap(bitmap);
    }

    public void editWatermark(CPDFWatermark watermark) {

        RectF pageSize = document.pageAtIndex(pageIndex).getSize();
        float pw = pageSize.width();
        float ph = pageSize.height();
        float mScale;
        if (pw <= currentPageWidth && ph >= currentPageHeight) {
            mScale = currentPageHeight / ph;
        } else {
            mScale = currentPageWidth / pw;
        }
        if (watermark.getType() == CPDFWatermark.Type.WATERMARK_TYPE_TEXT) {
            watermarkView.initTextWaterMark(watermark.getText(), watermark.getTextRGBColor(), watermark.getFontSize(), (int) (watermark.getOpacity() * 255F));
            watermarkView.setTypeface(watermark.getFontName());
        } else {
            watermarkView.setImageBitmap(watermark.getImage());
            watermarkView.setWatermarkAlpha((int) (watermark.getOpacity() * 255F));
        }
        watermarkView.setScale(watermark.getScale() * mScale);

        PointF centerPointF = new PointF((watermark.getHorizOffset() + pw / 2) * mScale,
                (ph / 2 - watermark.getVertOffset()) * mScale);
        watermarkView.setCenter(centerPointF);
        watermarkView.setDegree((float) -(watermark.getRotation() * 180 / Math.PI));
        isTile = watermark.isFullScreen();
        front = watermark.isFront();
        String pages = watermark.getPages();
        List<Integer> pageList = new ArrayList<>();
        getPageListForStr(pages, pageList, document.getPageCount(), true);
        if (pageList.size() == 1) {
            if (pageList.get(0) == pageIndex) {
                pageRange = CPageRange.CurrentPage;
            }
        }
    }

    public boolean createWatermark() {
        CPDFWatermark watermark = null;
        if (watermarkView.getWatermarkType() == CWatermarkView.EditType.TXT) {
            watermark = document.createWatermark(CPDFWatermark.Type.WATERMARK_TYPE_TEXT);
        } else {
            watermark = document.createWatermark(CPDFWatermark.Type.WATERMARK_TYPE_IMG);
        }
        if (watermark == null) {
            return false;
        }
        return modifyWatermark(watermark);
    }

    public boolean modifyWatermark(CPDFWatermark watermark) {
        RectF pageSize = document.getPageSize(pageIndex);
        if (watermarkView.getWatermarkType() == CWatermarkView.EditType.TXT) {
            watermark.setScale(watermarkView.getScale());
        } else {
            Bitmap bitmap = watermarkView.getImageWatermarkBitmap();
            if (bitmap != null) {
                watermark.setImage(bitmap, bitmap.getWidth(), bitmap.getHeight());
            }
            watermark.setScale(watermarkView.getScale() * whScale);
        }
        watermark.setFontName(watermarkView.getFontPsName());

        watermark.setText(watermarkView.getText());
        watermark.setTextRGBColor(watermarkView.getTextColor());
        watermark.setFontSize(watermarkView.getTextSize() * whScale);
        watermark.setOpacity(watermarkView.getWatermarkAlpha() / 255F);
        watermark.setPages(getPageRangeStr());
        watermark.setFront(front);
        watermark.setRotation(-watermarkView.getRadian());
        watermark.setHorizalign(CPDFWatermark.Horizalign.WATERMARK_HORIZALIGN_CENTER);
        watermark.setVertalign(CPDFWatermark.Vertalign.WATERMARK_VERTALIGN_CENTER);
        float offsetX = whScale * watermarkView.centerPoint().x - pageSize.width() / 2;
        float offsetY = pageSize.height() / 2 - whScale * watermarkView.centerPoint().y;
        watermark.setHorizOffset(offsetX);
        watermark.setVertOffset(offsetY);
        watermark.setFullScreen(isTile);
        watermark.setHorizontalSpacing(getSpacing());
        watermark.setVerticalSpacing(getSpacing());
        watermark.update();
        return watermark.release();
    }

    private float getSpacing() {
        return watermarkSpacing;
    }

    private String getPageRangeStr() {
        if (pageRange == CPageRange.CurrentPage) {
            return pageIndex + "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < document.getPageCount(); i++) {
            stringBuilder.append(i);
            if (i < document.getPageCount()) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }


    public boolean getPageListForStr(String pageRange, List<Integer> pageNumberList, int pageCount, boolean isPageNumber) {
        List<String> pagesNumber = new ArrayList<>();

        if (TextUtils.isEmpty(pageRange)) {
            return false;
        }

        char[] pageChars = pageRange.toCharArray();

        for (char page : pageChars) {
            if (page != '0' && page != '1' && page != '2' && page != '3' && page != '4'
                    && page != '5' && page != '6' && page != '7' && page != '8' && page != '9'
                    && page != '-' && page != ',') {
                return false;
            }
        }

        if (pageRange.contains("-")) {
            String[] pagesSplit = pageRange.split("-");

            for (int index = 0; index < pagesSplit.length; index++) {
                String pageSplit = pagesSplit[index];

                if (TextUtils.isEmpty(pageSplit)) {
                    return false;
                }

                if (onGetPages(pageSplit, pagesNumber) && index < (pagesSplit.length - 1)) {
                    pagesNumber.add("-");
                }
            }
        } else {
            if (!onGetPages(pageRange, pagesNumber)) {
                pageNumberList.clear();
                return false;
            }
        }

        int lengths = String.valueOf(pageCount).length();
        int dValue = isPageNumber ? 0 : 1;

        for (int index = 0; index < pagesNumber.size(); index++) {
            String page = pagesNumber.get(index);

            if (page.length() > lengths) {
                pageNumberList.clear();
                return false;
            }

            if (isNumeric(page)) {
                pageNumberList.add(Integer.parseInt(page) - dValue);
            } else {
                if (index - 1 >= 0 && index + 1 < pagesNumber.size()) {
                    int first = Integer.parseInt(pagesNumber.get(index - 1));
                    int last = Integer.parseInt(pagesNumber.get(index + 1));

                    if (first >= last) {
                        return true;
                    } else if ((last - first) > 1) {
                        int startPage = first + 1;
                        int endPage = last - 1;

                        if (startPage > endPage) {
                            pageNumberList.clear();
                            return false;
                        }

                        while (startPage <= endPage) {
                            pageNumberList.add(startPage - dValue);
                            startPage++;
                        }
                    }
                }
            }
        }

        if (pageNumberList.isEmpty() || pageNumberList.get(pageNumberList.size() - 1) >= pageCount || pageNumberList.get(0) < 0) {
            pageNumberList.clear();
            return false;
        }

        return true;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean onGetPages(String page, List<String> pagesNumber) {
        if (isNumeric(page)) {
            pagesNumber.add(page);
            return true;
        }
        return false;
    }

    public String changePageStr(String pageRange, boolean isAdd) {
        if (TextUtils.isEmpty(pageRange)) {
            return pageRange;
        }

        StringBuilder buffer = new StringBuilder();
        List<String> strList = new ArrayList<>();

        if (pageRange.contains("-")) {
            String[] list = pageRange.split("-");

            for (int index = 0; index < list.length; index++) {
                String s = list[index];

                if (!TextUtils.isEmpty(s)) {
                    strList.addAll(changePageCo(s, isAdd));

                    if (index != list.length - 1) {
                        strList.add("-");
                    }
                }
            }
        } else {
            strList.addAll(changePageCo(pageRange, isAdd));
        }

        for (String item : strList) {
            buffer.append(item);
        }

        return buffer.toString();
    }

    private List<String> changePageCo(String pageRange, boolean isAdd) {
        List<String> result = new ArrayList<>();
        // Implement your changePageCo logic here for Java
        // You can use a loop or other Java constructs for the same logic as in the Kotlin function.
        // Example: if (isAdd) { ... } else { ... }
        return result;
    }


    public void afterMeasured(CAfterMeasuredCallback callback) {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (callback != null) {
                        callback.callback();
                    }
                }
            }
        });
    }

    public interface CAfterMeasuredCallback {
        void callback();
    }

}
