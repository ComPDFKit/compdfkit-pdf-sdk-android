
package com.compdfkit.tools.common.utils.task;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;

import java.io.File;


public class CExtractAssetFileTask {

    /**
     * Extracts the file at {@code assetPath} from the app's assets into the private app directory.
     *
     * @param assetPath Path pointing to a file inside the app's assets.
     * @param context   Context used to retrieve the referenced file from the app's assets.
     * @param listener  A listener notified of extraction completion.
     */
    public static void extract(
            @NonNull final Context context,
            @NonNull final String assetPath,
            @NonNull final String fileName,
            @Nullable final OnDocumentExtractedListener listener) {
        extract(context, assetPath, fileName, true, listener);
    }

    /**
     * Extracts the file at {@code assetPath} from the app's assets into the private app directory.
     *
     * @param assetPath         Path pointing to a file inside the app's assets.
     * @param context           Context used to retrieve the referenced file from the app's assets.
     * @param overwriteExisting Whether an existing file in the private app directory should be
     *                          overwritten.
     * @param listener          A listener notified of extraction completion.
     */
    public static void extract(
            @NonNull Context context,
            @NonNull String assetPath,
            @NonNull String fileName,
            boolean overwriteExisting,
            @Nullable OnDocumentExtractedListener listener) {
        CThreadPoolUtils.getInstance().executeIO(() -> {
            File pdfFile;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                 pdfFile = new File(Environment.getExternalStorageDirectory(),CFileUtils.CACHE_FOLDER + File.separator+ fileName);
            }else {
                 pdfFile = new File(context.getFilesDir().getAbsoluteFile(), fileName);
            }
            if (!pdfFile.exists()) {
                CFileUtils.copyFileFromAssets(context, assetPath, pdfFile.getParent(), fileName, overwriteExisting);
            }
            File finalPdfFile = pdfFile;
            CThreadPoolUtils.getInstance().executeMain(()->{
                if (listener != null) {
                    listener.onDocumentExtracted(finalPdfFile.getAbsolutePath());
                }
            });
        });
    }

    /**
     * Listens for document extraction events.
     */
    public interface OnDocumentExtractedListener {
        /**
         * Called when there is a document extraction events.
         *
         * @param documentFilePath extracted assets folder pdf file
         */
        void onDocumentExtracted(@NonNull String documentFilePath);
    }
}
