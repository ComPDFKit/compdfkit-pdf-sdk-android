/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfbota.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.compdfkit.tools.annotation.pdfannotationlist.CPDFAnnotationListFragment;
import com.compdfkit.tools.common.interfaces.COnSetPDFDisplayPageIndexListener;
import com.compdfkit.tools.common.views.pdfbota.CPDFBOTA;
import com.compdfkit.tools.common.views.pdfbota.CPDFBotaEmptyFragment;
import com.compdfkit.tools.common.views.pdfbota.CPDFBotaFragmentTabs;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.viewer.pdfbookmark.CPDFBookmarkFragment;
import com.compdfkit.tools.viewer.pdfoutline.CPDFOutlineFragment;
import com.compdfkit.tools.viewer.pdfthumbnail.CPDFThumbnailFragment;

import java.util.ArrayList;


/**
 * The adapter contains two fragments, namely the thumbnail list fragment and the PDF outline list fragment.
 * @see CPDFThumbnailFragment
 * @see CPDFOutlineFragment
 */
public class CBotaViewPagerAdapter extends FragmentStateAdapter {

    /**
     * The PDF reader view object
     */
    private CPDFViewCtrl pdfView;

    private ArrayList<CPDFBotaFragmentTabs> tabs;

    /**
     * Listener for setting the display page index of the PDF
     */
    private COnSetPDFDisplayPageIndexListener pdfDisplayPageIndexListener;

    public CBotaViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, CPDFViewCtrl pdfView, ArrayList<CPDFBotaFragmentTabs> tabs) {
        super(fragmentManager, lifecycle);
        this.pdfView = pdfView;
        this.tabs = tabs;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (tabs.get(position).getBotaType()) {
            case CPDFBOTA.OUTLINE:
                return outlineFragment();
            case CPDFBOTA.THUMBNAIL:
                return thumbnailFragment();
            case CPDFBOTA.BOOKMARKS:
                return bookmarkFragment();
            case CPDFBOTA.ANNOTATION:
                return annotationListFragment();
            default:
                return new CPDFBotaEmptyFragment();
        }
    }

    @Override
    public int getItemCount() {
        return tabs.size();
    }

    /**
     * Method that creates and returns a new outline fragment
     * @return CPDFOutlineFragment
     */
    private CPDFOutlineFragment outlineFragment() {
        CPDFOutlineFragment fragment = CPDFOutlineFragment.newInstance();
        fragment.setOutlineClickListener(pdfDisplayPageIndexListener);
        fragment.initWithPDFView(pdfView);
        return fragment;
    }

    /**
     * Method that creates and returns a new thumbnail fragment
     * @return CPDFThumbnailFragment
     */
    private CPDFThumbnailFragment thumbnailFragment() {
        CPDFThumbnailFragment fragment = CPDFThumbnailFragment.newInstance();
        fragment.setPDFDisplayPageIndexListener(pdfDisplayPageIndexListener);
        fragment.initWithPDFView(pdfView);
        return fragment;
    }

    private CPDFBookmarkFragment bookmarkFragment() {
        CPDFBookmarkFragment bookmarkFragment = CPDFBookmarkFragment.newInstance();
        bookmarkFragment.initWithPDFView(pdfView);
        bookmarkFragment.setPDFDisplayPageIndexListener(pdfDisplayPageIndexListener);
        return bookmarkFragment;
    }

    private CPDFAnnotationListFragment annotationListFragment(){
        CPDFAnnotationListFragment listFragment = CPDFAnnotationListFragment.newInstance();
        listFragment.initWithPDFView(pdfView);
        listFragment.setPDFDisplayPageIndexListener(pdfDisplayPageIndexListener);
        return listFragment;
    }

    /**
     * Method that sets the PDF display page index listener
     * @param displayPageIndexListener
     */
    public void setPDFDisplayPageIndexListener(COnSetPDFDisplayPageIndexListener displayPageIndexListener) {
        this.pdfDisplayPageIndexListener = displayPageIndexListener;
    }
}
