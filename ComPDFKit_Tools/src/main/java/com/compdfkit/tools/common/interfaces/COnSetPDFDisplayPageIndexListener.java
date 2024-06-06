/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.interfaces;


/**
 * Interface for handling the event of setting the display page index of a PDF.
 */
public interface COnSetPDFDisplayPageIndexListener {

    /**
     * Called when the display page index of a PDF is being set.
     * @param pageIndex The page index to be displayed.
     */
    void displayPage(int pageIndex);
}
