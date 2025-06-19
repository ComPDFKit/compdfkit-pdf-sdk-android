package com.compdfkit.pdfviewer.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.pdfviewer.R;
import com.compdfkit.pdfviewer.home.datas.FunDatas;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.CPDFConfigurationUtils;
import com.compdfkit.tools.common.pdf.CPDFDocumentActivity;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
//import com.compdfkit.tools.compress.CPDFCompressDialog;
import com.compdfkit.tools.compress.CPDFCompressDialog;
import com.compdfkit.tools.security.encryption.CDocumentEncryptionDialog;
import com.compdfkit.tools.security.encryption.CInputOwnerPwdDialog;
import com.compdfkit.tools.security.watermark.CWatermarkEditDialog;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.io.File;


public class FunSamplesActivity extends AppCompatActivity implements View.OnClickListener {

    private CPDFDocument document;

    private String imagePath;

    private AppCompatButton btnAddWatermark;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fun_samples_activity);
        btnAddWatermark = findViewById(R.id.btn_add_watermark);
        AppCompatButton btnEncryption = findViewById(R.id.btn_encryption);
        AppCompatButton btnCompress = findViewById(R.id.btn_compress);
        btnCompress.setOnClickListener(this);
        btnAddWatermark.setOnClickListener(this);
        btnEncryption.setOnClickListener(this);
        String path = CFileUtils.getAssetsTempFile(this, "ComPDFKit_Annotations_Sample_File.pdf", "ComPDFKit_Annotations_Sample_File.pdf");
        document = new CPDFDocument(this);
        document.open(path);
        CPDFReaderView readerView = findViewById(R.id.readerView);
        readerView.setPDFDocument(document);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_add_watermark){
            showAddWatermarkDialog();
        } else if (view.getId() == R.id.btn_encryption){
            encryption();
        } else if (view.getId() == R.id.btn_compress){
            compressDialog();
        }
    }

    private void showAddWatermarkDialog(){
        // add watermark
        String path = CFileUtils.getAssetsTempFile(this, "ComPDFKit_Annotations_Sample_File.pdf", "ComPDFKit_Annotations_Sample_File.pdf");
        CWatermarkEditDialog editDialog = CWatermarkEditDialog.newInstance();
        // You can pass the file path or URI of the PDF document directly
        editDialog.setDocument(path, null);
        // Or pass an existing CPDFDocument object
//        editDialog.setDocument(document);
        editDialog.setPageIndex(0);
        // Set the saving path of the document with watermark added. If not set, when you click [Save],
        // a file directory selection pop-up window will pop up for saving.
        File saveFile = new File(getFilesDir(), "temp/test.pdf");
        saveFile = CFileUtils.renameNameSuffix(saveFile);
//        editDialog.setSavePath(saveFile.getAbsolutePath());
//        editDialog.setDefaultText("ComPDFKit");
//                editDialog.setDefaultImagePath("xxx.png");
        editDialog.setCompleteListener((saveAsNewFile, pdfFile) -> {
            if (!TextUtils.isEmpty(pdfFile)){
                openPDF(pdfFile);
            }
            editDialog.dismiss();
        });
        editDialog.show(getSupportFragmentManager(), "addWatermarkDialog");
    }

    private void encryption(){
        String path = CFileUtils.getAssetsTempFile(this, "test_SetPassword.pdf", "test_SetPassword.pdf");

        CPDFDocument document = new CPDFDocument(this);
        document.open(path);

        CPDFDocument.PDFDocumentPermissions permission = document.getPermissions();
        //only has user permission
        if (permission == CPDFDocument.PDFDocumentPermissions.PDFDocumentPermissionsUser) {
          // You can enter the owner permissions password and reload the pdf to gain owner permissions
            CInputOwnerPwdDialog inputOwnerPwdDialog = CInputOwnerPwdDialog.newInstance();
            inputOwnerPwdDialog.setDocument(document);
            inputOwnerPwdDialog.setConfirmClickListener(ownerPassword -> {
                inputOwnerPwdDialog.dismiss();
                document.reload(ownerPassword);
                showEncryptionDialog(document);
            });
            inputOwnerPwdDialog.setCancelClickListener(view -> {
                inputOwnerPwdDialog.dismiss();
            });
            inputOwnerPwdDialog.show(getSupportFragmentManager(), "inputOwnerPwdDialog");
          return;
        }
        showEncryptionDialog(document);

    }

    private void showEncryptionDialog(CPDFDocument document){
        CDocumentEncryptionDialog encryptionDialog = CDocumentEncryptionDialog.newInstance();
        encryptionDialog.setDocument(document);
        encryptionDialog.setEncryptionResultListener((isRemoveSecurity, result, filePath, password) -> openPDF(filePath));
        encryptionDialog.show(getSupportFragmentManager(), "showEncryptionDialog");
    }

    private void compressDialog(){
        CPDFCompressDialog compressDialog = new CPDFCompressDialog();
        compressDialog.setDocument(document);
        compressDialog.setCompressDocumentListener((result, path) -> {
            compressDialog.dismiss();
            if (result && !TextUtils.isEmpty(path)){
                openPDF(path);
                CToastUtil.showLongToast(this, R.string.tools_compressed_successfully);
            }
        });
        compressDialog.show(getSupportFragmentManager(), "compressedDialog");
    }

    private void openPDF(String path){
        CPDFDocumentActivity.startActivity(this, path, "",
                CPDFConfigurationUtils.normalConfig(this, "tools_default_configuration.json"));
    }
}
