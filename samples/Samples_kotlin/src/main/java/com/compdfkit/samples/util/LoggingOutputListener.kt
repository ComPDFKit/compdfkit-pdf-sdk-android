/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.util

import android.view.View
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView

class LoggingOutputListener(private val logTextview: AppCompatTextView,
                            private val scrollView: ScrollView) : OutputListener {

    override fun print(output: String?) {
        scrollView.post {
            logTextview.append(output)
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun print() {
        logTextview.append("")
    }

    override fun println(output: String?) {
        scrollView.post {
            logTextview.append("$output \n")
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun println() {
        logTextview.post { logTextview.append("\n") }
    }

    override fun printError(errorMessage: String?) {
        println(errorMessage)
    }
}