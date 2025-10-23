// Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
//
// THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
// AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
// UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
// This notice may not be removed from this file.


package com.compdfkit.tools.common.pdf.config;

import java.io.Serializable;


public class CPDFSearchConfig implements Serializable {

  public CPDFKeywordConfig normalKeyword = new CPDFKeywordConfig(0x00000000, 0x77FFFF00);
  public CPDFKeywordConfig focusKeyword = new CPDFKeywordConfig(0x00000000, 0x77FF0000);

  public static class CPDFKeywordConfig implements Serializable {

      public int borderColor = 0x77FFFF00;
      public int fillColor = 0x00000000;

      public CPDFKeywordConfig() {
      }

      public CPDFKeywordConfig(int borderColor, int fillColor) {
          this.borderColor = borderColor;
          this.fillColor = fillColor;
      }
  }
}
