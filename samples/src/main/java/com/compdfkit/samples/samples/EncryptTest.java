/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFDocumentPermissionInfo;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;


public class EncryptTest extends PDFSamples {

    public EncryptTest() {
        setTitle(R.string.encrypt_test_title);
        setDescription(R.string.encrypt_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        printDividingLine();
        outputListener.println("Samples 1 : RC4 encrypt");
        encryptDocument(CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentRC4, "EncryptUseRC4Test.pdf");

        outputListener.println("Samples 2 : AES128 encrypt");
        encryptDocument(CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentAES128, "EncryptUseAES128Test.pdf");

        outputListener.println("Samples 3 : AES236 encrypt");
        encryptDocument(CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentAES128, "EncryptUseAES256Test.pdf");

        outputListener.println("Samples 4 : NoEncryptAlgo encrypt");
        encryptDocument(CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentAES128, "EncryptUseNoEncryptAlgoTest.pdf");

        outputListener.println("Encrypt by user password done.");
        encryptOwnerPassword();
        encryptAllPassword();
        decryptDocument();
        printFooter();
    }

    /**
     * Encrypt document
     */
    private void encryptDocument(CPDFDocument.PDFDocumentEncryptAlgo documentEncryptAlgo, String fileName) {
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        document.setUserPassword("User");
        document.setEncryptAlgorithm(documentEncryptAlgo);

        File resultsFile = new File(outputDir(), "EncryptTest/" + fileName);
        saveSamplePDF(document, resultsFile, true);
        outputListener.println("User password is : User");
        outputListener.println("Done. Results saved in " + fileName);
        outputListener.println();
    }

    private void encryptOwnerPassword() {
        printDividingLine();
        outputListener.println("Samples 5 : Encrypt by owner passwords");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        document.setEncryptAlgorithm(CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentAES256);
        document.setOwnerPassword("owner");

        File resultsFile = new File(outputDir(), "EncryptTest/EncryptByOwnerPasswordsTest.pdf");
        saveSamplePDF(document, resultsFile, true);
        outputListener.println("Owner password is : owner");
        outputListener.println("Done. Results saved in EncryptByOwnerPasswordsTest.pdf");
        outputListener.println();
    }

    private void encryptAllPassword() {
        printDividingLine();
        outputListener.println("Samples 6 : Encrypt by Both user and owner passwords");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        document.setUserPassword("User");
        document.setEncryptAlgorithm(CPDFDocument.PDFDocumentEncryptAlgo.PDFDocumentAES256);
        document.setOwnerPassword("owner");

        File resultsFile = new File(outputDir(), "EncryptTest/EncryptByAllPasswordsTest.pdf");
        saveSamplePDF(document, resultsFile, true);
        outputListener.println("User password is : User");
        outputListener.println("Owner password is : owner");
        outputListener.println("Done. Results saved in EncryptByAllPasswordsTest.pdf");
        outputListener.println();
    }


    private void decryptDocument() {
        outputListener.println("Samples 7 : encrypt document");

        CPDFDocument document = new CPDFDocument(context);
        File file = new File(outputDir(), "EncryptTest/EncryptByAllPasswordsTest.pdf");
        outputListener.println("Unlock with user password");

        document.open(file.getAbsolutePath(), "User");
        document.isEncrypted();

        outputListener.println("Document is " + (document.isEncrypted() ? "locked" : "unlocked"));

        CPDFDocumentPermissionInfo info = document.getPermissionsInfo();
        outputListener.println("AllowsPrinting : " + info.isAllowsPrinting());
        outputListener.println("AllowsCopy : " + info.isAllowsCopying());
        document.close();

        CPDFDocument newDocument = new CPDFDocument(context);
        outputListener.println("Unlock with owner password");
        newDocument.open(file.getAbsolutePath(), "owner");
        CPDFDocumentPermissionInfo info1 = newDocument.getPermissionsInfo();

        outputListener.println("AllowsPrinting : " + info1.isAllowsPrinting());
        outputListener.println("AllowsCopy : " + info1.isAllowsCopying());
        outputListener.println("Unlock done.");
        printDividingLine();
        try {
            File decryptTestFile = new File(outputDir(), "EncryptTest/DecryptTest.pdf");
            decryptTestFile.getParentFile().mkdirs();
            newDocument.saveAs(decryptTestFile.getAbsolutePath(), true);
            if (decryptTestFile.exists()) {
                getOutputFileList().add(decryptTestFile.getAbsolutePath());
            }
            newDocument.close();
            outputListener.println("Document decrypt done.");
            outputListener.println("Decrypt done.");
        } catch (CPDFDocumentException e) {

        }
    }

    @Override
    protected void saveSamplePDF(CPDFDocument document, File file, boolean close) {
        try {
            file.getParentFile().mkdirs();
            document.saveAs(file.getAbsolutePath(), false);
            if (file.exists()) {
                getOutputFileList().add(file.getAbsolutePath());
            }
            if (close) {
                document.close();
            }
        } catch (CPDFDocumentException e) {
            e.printStackTrace();
        }
    }
}
