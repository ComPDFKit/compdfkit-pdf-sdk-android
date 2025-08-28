package com.compdfkit.tools.common.pdf.config;

import java.io.Serializable;


public class GlobalConfig  implements Serializable {

    public enum CThemeMode{
        Light,

        Dark,

        System;


        public static CThemeMode fromString(String str) {
            try {
                String firstLetter = str.substring(0, 1).toUpperCase();
                String result = firstLetter + str.substring(1);
                return CThemeMode.valueOf(result);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public enum CSignatureType{
        Manual,

        Digital,

        Electronic;

        public static CSignatureType fromString(String str) {
            try {
                String firstLetter = str.substring(0, 1).toUpperCase();
                String result = firstLetter + str.substring(1);
                return CSignatureType.valueOf(result);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public CThemeMode themeMode  = CThemeMode.Light;

    /**
     * Whether to save the used font set to the PDF file when saving the file.
     * Saving to the file will increase the file size.
     * Enabled by default
     */
    public boolean fileSaveExtraFontSubset = true;

    public CPDFWatermarkConfig watermark = new CPDFWatermarkConfig();

    public CSignatureType signatureType = CSignatureType.Manual;

    public boolean enableExitSaveTips = true;

    public CPDFThumbnailConfig thumbnail = new CPDFThumbnailConfig();

    public boolean enableErrorTips = true;

}
