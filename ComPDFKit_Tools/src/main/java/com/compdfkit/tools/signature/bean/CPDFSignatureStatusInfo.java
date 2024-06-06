/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.bean;

import android.content.Context;

import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.tools.R;
import com.compdfkit.tools.signature.SignatureStatus;

import java.util.ArrayList;
import java.util.List;

public class CPDFSignatureStatusInfo {

    public CPDFSignatureStatusInfo(Context context, CPDFSignature signature, SignatureStatus status, boolean isValid, boolean isExpired, boolean notChanged, boolean isCertTrusted){
        this.signature = signature;
        this.isValid = isValid;
        this.isExpired = isExpired;
        this.notChanged = notChanged;
        this.isCertTrusted = isCertTrusted;
        this.status = status;
        List<String> list = new ArrayList<>();
        if (isCertTrusted){
            list.add(context.getString(R.string.tools_signer_identity_is_valid));
        }else {
            list.add(context.getString(R.string.tools_signer_identity_is_invalid));
        }
        if (isValid && isCertTrusted){
            list.add(context.getString(R.string.tools_sign_the_sign_is_valid));
        } else if (isValid && !isCertTrusted){
            list.add(context.getString(R.string.tools_sign_validity_is_unknown_trusted_desc));
        } else if (!isValid && !isCertTrusted){
            list.add(context.getString(R.string.tools_sign_the_sign_is_in_valid));
        } else {
            list.add(context.getString(R.string.tools_sign_the_sign_is_in_valid));
        }
        if (isExpired){
            list.add(context.getString(R.string.tools_signer_cert_has_expired));
        }
        if (notChanged){
            list.add(context.getString(R.string.tools_sign_document_has_not_been_modified));
        }else {
            list.add(context.getString(R.string.tools_sign_document_has_been_modified));
        }
        this.certAuthorityStatements = list.toArray(new String[list.size()]);
    }


    CPDFSignature signature;

    private boolean isValid;

    private boolean isExpired;

    private SignatureStatus status;

    private boolean notChanged;

    private boolean isCertTrusted;


    private String[] certAuthorityStatements;

    public CPDFSignature getSignature() {
        return signature;
    }

    public void setSignature(CPDFSignature signature) {
        this.signature = signature;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public String[] getCertAuthorityStatements() {
        return certAuthorityStatements;
    }

    public void setCertAuthorityStatements(String[] certAuthorityStatements) {
        this.certAuthorityStatements = certAuthorityStatements;
    }

    public boolean isNotChanged() {
        return notChanged;
    }

    public void setNotChanged(boolean notChanged) {
        this.notChanged = notChanged;
    }

    public boolean isCertTrusted() {
        return isCertTrusted;
    }

    public void setCertTrusted(boolean certTrusted) {
        isCertTrusted = certTrusted;
    }

    public SignatureStatus getStatus() {
        return status;
    }

    public void setStatus(SignatureStatus status) {
        this.status = status;
    }
}
