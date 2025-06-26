package com.compdfkit.tools.signature.pdfproperties.pdfsign;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.compdfkit.core.document.CPDFSdk;
import com.compdfkit.core.signature.CPDFDigitalSigConfig;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.utils.image.CImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CDigitalSignStylePreviewView extends FrameLayout {

    private AppCompatImageView ivSignImage;

    private AppCompatTextView tvSignInfo;

    private AppCompatImageView ivLogo;

    private ConstraintLayout clPreview;

    private String location = "";

    private String reason;

    private String commonName;

    private String distinguishableName;

    private String date;

    private CPDFDigitalSigConfig digitalSigConfig = new CPDFDigitalSigConfig();

    private ConstraintSet constraintSet = new ConstraintSet();

    private String signImagePath;

    private boolean showName = false;

    private boolean showDate = false;

    private boolean showLogo = false;

    private boolean showDistinguishableName;

    private boolean showSdkVersion;

    private boolean showTab;

    private boolean showLocation;

    private boolean showReason;

    private boolean contentAlignLeft = false;

    public CDigitalSignStylePreviewView(@NonNull Context context) {
        this(context, null);
    }

    public CDigitalSignStylePreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CDigitalSignStylePreviewView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.tools_sign_style_preview_view, this);
        ivSignImage = findViewById(R.id.iv_sign);
        tvSignInfo = findViewById(R.id.tv_sign_info);
        ivLogo = findViewById(R.id.iv_logo);
        clPreview = findViewById(R.id.cl_preview);
        ConstraintLayout clRoot = findViewById(R.id.cl_root);
        constraintSet.clone(clPreview);
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
        update();
    }

    public void setDistinguishableName(String distinguishableName) {
        this.distinguishableName = distinguishableName;
        update();
    }

    public void setSignImage(String signImagePath) {
        this.signImagePath = signImagePath;
        update();
    }

    public void setShowName(boolean show) {
        this.showName = show;
        update();
    }

    public void setShowTab(boolean show) {
        this.showTab = show;
        update();
    }

    public void setShowSdkVersion(boolean show) {
        this.showSdkVersion = show;
        update();
    }

    public void setShowDate(boolean show) {
        this.date = show ? CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT_1) : "";
        this.showDate = show;
        update();
    }

    public void setShowLogo(boolean show) {
        this.showLogo = show;
        update();
    }

    public void setShowDistinguishableName(boolean show) {
        this.showDistinguishableName = show;
        update();
    }

    public void setShowLocation(boolean show) {
        this.showLocation = show;
        update();
    }

    public void setShowReason(boolean show) {
        this.showReason = show;
        update();
    }

    public void setLocation(String location) {
        this.location = location;
        update();
    }

    public void setReason(String reason) {
        this.reason = reason;
        update();
    }

    public CPDFDigitalSigConfig getConfig() {
        digitalSigConfig.setDrawLogo(showLogo);
        digitalSigConfig.setContentColor(0xFF000000);
        digitalSigConfig.setTextColor(0xFF000000);
        digitalSigConfig.setContentAlginLeft(contentAlignLeft);
        digitalSigConfig.setContent(getContent());
        digitalSigConfig.setDrawOnlyContent(TextUtils.isEmpty(signImagePath));
        if (!TextUtils.isEmpty(signImagePath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(signImagePath);
            digitalSigConfig.setImage(bitmap);
        }
        if (showLogo) {
            CFileUtils.copyFileFromAssets(getContext(), "tools_logo.png", getContext().getCacheDir().getAbsolutePath(), "tools_logo.png", true);
            File logoFile = new File(getContext().getCacheDir(), "tools_logo.png");
            if (logoFile.exists()) {
                digitalSigConfig.setLogo(BitmapFactory.decodeFile(logoFile.getAbsolutePath()));
            }
        }
        return digitalSigConfig;
    }

    public String getLocation() {
        return location;
    }

    public String getReason() {
        return reason;
    }


    public void setContentAlignLeft(boolean contentAlignLeft) {
        this.contentAlignLeft = contentAlignLeft;
        update();
        if (!contentAlignLeft) {
            constraintSet.connect(R.id.iv_sign, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(R.id.iv_sign, ConstraintSet.END, R.id.tv_sign_info, ConstraintSet.START);
            constraintSet.connect(R.id.tv_sign_info, ConstraintSet.START, R.id.iv_sign, ConstraintSet.END);
            constraintSet.connect(R.id.tv_sign_info, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            constraintSet.connect(R.id.iv_sign, ConstraintSet.START, R.id.tv_sign_info, ConstraintSet.END);
            constraintSet.connect(R.id.iv_sign, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.connect(R.id.tv_sign_info, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(R.id.tv_sign_info, ConstraintSet.END, R.id.iv_sign, ConstraintSet.START);
        }
        TransitionManager.beginDelayedTransition(clPreview);
        constraintSet.applyTo(clPreview);
    }

    private void update() {
        if (!TextUtils.isEmpty(signImagePath)) {
            Glide.with(getContext())
                    .load(signImagePath)
                    .into(ivSignImage);
        }
        ivSignImage.setVisibility(TextUtils.isEmpty(signImagePath) ? GONE : VISIBLE);
        ivLogo.setVisibility(showLogo ? VISIBLE : GONE);

        tvSignInfo.setText(getContent());
        if (!contentAlignLeft) {
            tvSignInfo.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        } else {
            tvSignInfo.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        }
    }

    private String getContent() {
        List<String> list = new ArrayList<>();
        if (showName) {
            appendData(list, R.string.tools_field_name, commonName);
        }
        if (showDate) {
            appendData(list, R.string.tools_date, date);
        }
        if (showReason) {
            appendData(list, R.string.tools_reason, reason);
        }
        if (showDistinguishableName) {
            appendData(list, R.string.tools_dn, distinguishableName);
        }
        if (showSdkVersion) {
            appendData(list, R.string.tools_compdfkit_versions, CPDFSdk.getSDKVersion());
        }
        if (showLocation) {
            appendData(list, R.string.tools_location, location);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i));
            if (i != list.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private void appendData(List<String> list, @StringRes int tabStringResId, String value) {
        if (showTab) {
            list.add(getString(tabStringResId) + ": " + value);
        } else {
            list.add(value);
        }
    }

    private String getString(@StringRes int stringResId) {
        return getContext().getString(stringResId);
    }

    public Bitmap getBitmap() {
        return CImageUtil.getViewBitmap(this);
    }

    public enum Alignment {
        /**
         * content align left
         */
        left,
        /**
         * content align right
         */
        right
    }

}
