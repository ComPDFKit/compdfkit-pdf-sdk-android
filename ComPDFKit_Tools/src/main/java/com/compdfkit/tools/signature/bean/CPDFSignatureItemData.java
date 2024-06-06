package com.compdfkit.tools.signature.bean;


import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.tools.signature.SignatureStatus;

public class CPDFSignatureItemData {

    public CPDFSignatureItemData(){}

    public CPDFSignatureItemData(CPDFSignature signature, SignatureStatus signatureStatus){
        this.signature = signature;
        this.signatureStatus = signatureStatus;
    }

    private CPDFSignature signature;

    private SignatureStatus signatureStatus;

    public CPDFSignature getSignature() {
        return signature;
    }

    public void setSignature(CPDFSignature signature) {
        this.signature = signature;
    }

    public SignatureStatus getSignatureStatus() {
        return signatureStatus;
    }

    public void setSignatureStatus(SignatureStatus signatureStatus) {
        this.signatureStatus = signatureStatus;
    }
}
