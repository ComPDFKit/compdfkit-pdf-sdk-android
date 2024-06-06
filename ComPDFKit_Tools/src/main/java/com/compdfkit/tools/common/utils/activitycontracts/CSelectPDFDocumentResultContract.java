package com.compdfkit.tools.common.utils.activitycontracts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.common.utils.CFileUtils;


public class CSelectPDFDocumentResultContract extends ActivityResultContract<Void, Uri> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void unused) {
        return CFileUtils.getContentIntent();
    }

    @Override
    public Uri parseResult(int i, @Nullable Intent intent) {
        return intent != null ? intent.getData() : null;
    }
}
