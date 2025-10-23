/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.info;


import android.content.Context;

import com.compdfkit.core.signature.CPDFAuinfoac;
import com.compdfkit.core.signature.CPDFCertInfo;
import com.compdfkit.core.signature.CPDFExtInfo;
import com.compdfkit.core.signature.CPDFX509;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.bean.CPDFCertAttrDataItem;

import java.util.ArrayList;
import java.util.List;

public class CertAttrDatas {

    public static List<CPDFCertAttrDataItem> getAbstractInfo(Context context, CPDFX509 cpdfx509){
        List<CPDFCertAttrDataItem> list = new ArrayList<>();
        CPDFCertInfo certInfo = cpdfx509.getCertInfo();
        CPDFExtInfo extInfo = certInfo.getExtInfo();
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_issued_to), CertificateDigitalDatas.getOwnerContent(certInfo.getSubject(),"\n")));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_issuer), CertificateDigitalDatas.getOwnerContent(certInfo.getIssuer(), "\n")));
        String validForm = CDateUtil.formatPDFUTCDate(certInfo.getValidDateBegin());
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_valid_from),
                CDateUtil.formatDate(validForm, context.getString(R.string.tools_signature_date_pattern))));
        String validTo =  CDateUtil.formatPDFUTCDate(certInfo.getValidDateEnd());
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_valid_to), CDateUtil.formatDate(validTo,
                context.getString(R.string.tools_signature_date_pattern))));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_intended_usage), extInfo.getConvertKeyUsage()));
        return list;
    }


    public static List<CPDFCertAttrDataItem> getCertAttrDetailInfoList(Context context, CPDFX509 cpdfx509){
        List<CPDFCertAttrDataItem> list = new ArrayList<>();
        CPDFCertInfo certInfo = cpdfx509.getCertInfo();
        CPDFExtInfo extInfo = certInfo.getExtInfo();
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_version), certInfo.getVersion()));
        CPDFCertInfo.CPDFAlgorithmType algorithmType = certInfo.getAlgorithmType(certInfo.getSignatureAlgOid());
        String signatureAlgorithm = algorithmType.name().replace("PDFSignatureAlgorithmType","");
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_algorithm), signatureAlgorithm + "(" +  certInfo.getSignatureAlgOid() +")"));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_subject), CertificateDigitalDatas.getOwnerContent(certInfo.getSubject(), "\n")));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_issuer), CertificateDigitalDatas.getOwnerContent(certInfo.getIssuer(), "\n")));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_serial_number), certInfo.getSeriaNumber()));
        String validForm = CDateUtil.formatPDFUTCDate(certInfo.getValidDateBegin());
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_valid_from), CDateUtil.formatDate(validForm, context.getString(R.string.tools_signature_date_pattern))));
        String validTo =  CDateUtil.formatPDFUTCDate(certInfo.getValidDateEnd());
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_valid_to), CDateUtil.formatDate(validTo, context.getString(R.string.tools_signature_date_pattern))));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_certificate_policy), getCertStringArrayInfo(extInfo.getCertificatePolicies())));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_crl_distribution_point), getCertStringArrayInfo(extInfo.getCrlDistribute())));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_issuer_info_access), getAuthInfoAccessContent(extInfo.getAuthorizedInforAccess())));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_issuing_auth_key_identifier), extInfo.getAuthKeyIdentifier()));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_subject_key_identifier), extInfo.getSubjectKeyIdentifier()));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_basic_constraints), cpdfx509.getBasicConstraints()));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_key_usage), extInfo.getConvertKeyUsage()));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_public_key), certInfo.getPublicKey()));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_x_509_data), certInfo.getX509Data()));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_sha1_digest), certInfo.getSha1Digest()));
        list.add(new CPDFCertAttrDataItem(context.getString(R.string.tools_md5_digest), certInfo.getMd5Digest()));
        return list;
    }

    private static String getAuthInfoAccessContent(CPDFAuinfoac[] cpdfAuinfoacs){
        if (cpdfAuinfoacs != null && cpdfAuinfoacs.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < cpdfAuinfoacs.length; i++) {
                CPDFAuinfoac item = cpdfAuinfoacs[i];
                if (item != null){
                    builder.append("CAIssuers = ").append(item.getMethod_type()).append("\n");
                    builder.append("URL = ").append(item.getUrl());
                }
                if (i != cpdfAuinfoacs.length -1){
                    builder.append("\n\n");
                }
            }
            return builder.toString();
        }
        return "";
    }

    private static String getCertStringArrayInfo(String[] array){
        if (array != null && array.length > 0){
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                String policy = array[i];
                builder.append(policy);
                if (i != array.length -1){
                    builder.append("\n");
                }
            }
            return builder.toString();
        }
        return "";
    }

}
