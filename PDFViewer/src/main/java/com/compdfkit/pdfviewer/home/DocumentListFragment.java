/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFInfo;
import com.compdfkit.pdfviewer.R;
import com.compdfkit.pdfviewer.databinding.FragmentDocumentListBinding;
import com.compdfkit.pdfviewer.home.datas.FunDatas;
import com.compdfkit.pdfviewer.home.datas.SettingDatas;
import com.compdfkit.pdfviewer.home.samples.SamplesFactory;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.activitycontracts.CSelectPDFDocumentResultContract;
import com.compdfkit.tools.docseditor.pdfpageeditinsert.CInsertBlankPageDialogFragment;
import com.compdfkit.tools.docseditor.pdfpageeditinsert.CSelectInsertPageTypeDialogFragment;

import java.io.File;


public class DocumentListFragment extends Fragment {

    private FragmentDocumentListBinding binding;

    private CHomeFunListAdapter funListAdapter;

    private HomeFunBean.FunType funType;

    private ActivityResultLauncher<Void> selectDocumentLauncher = registerForActivityResult(new CSelectPDFDocumentResultContract(), uri -> {
        if (uri != null) {
            enterFunctions(null, uri);
        }
    });

    public static DocumentListFragment newInstance(HomeFunBean.FunType funType) {
        Bundle args = new Bundle();
        DocumentListFragment fragment = new DocumentListFragment();
        fragment.setFunType(funType);
        fragment.setArguments(args);
        return fragment;
    }

    public void setFunType(HomeFunBean.FunType funType) {
        this.funType = funType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDocumentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (funType == null){
            if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString("funType"))) {
                funType = HomeFunBean.FunType.valueOf(savedInstanceState.getString("funType"));
            }
        }
        initDocumentList();
        binding.fabAdd.setOnClickListener(v -> {
            // A pop-up window will pop up, giving you the option to
            // create a blank page document or select a pdf file from the file manager.
            Bundle args = new Bundle();
            args.putString(CSelectInsertPageTypeDialogFragment.EXTRA_CREATE_BLANK_PAGE_ITEM_TITLE, getString(R.string.tools_create_a_new_file));
            args.putString(CSelectInsertPageTypeDialogFragment.EXTRA_INSERT_PDF_PAGE_ITEM_TITLE, getString(com.compdfkit.tools.R.string.tools_open_document));
            CSelectInsertPageTypeDialogFragment insertDialogFragment = CSelectInsertPageTypeDialogFragment.newInstance(args);
            insertDialogFragment.setInsertBlankPageClickListener(v1 -> {
                // show create a blank page document dialog
                CPDFDocument document = CPDFDocument.createDocument(getContext());
                CInsertBlankPageDialogFragment blankPageDialogFragment = CInsertBlankPageDialogFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putBoolean(CInsertBlankPageDialogFragment.EXTRA_SHOW_INSERT_LOCATION, false);
                bundle.putBoolean(CInsertBlankPageDialogFragment.EXTRA_DEFAULT_VISIBLE_PAGE_SIZE, true);
                bundle.putString(CInsertBlankPageDialogFragment.EXTRA_TITLE, getString(R.string.tools_create_a_new_file));
                bundle.putString(CInsertBlankPageDialogFragment.EXTRA_SAVE_BUTTON_TEXT, getString(com.compdfkit.tools.R.string.tools_okay));
                blankPageDialogFragment.setArguments(bundle);
                blankPageDialogFragment.setDocument(document);
                blankPageDialogFragment.setOnEditDoneCallback(() -> {
                    try {
                        // create document
                        if (document.getInfo() != null) {
                            CPDFInfo info = document.getInfo();
                            info.setAuthor(SettingDatas.getDocumentAuthor(getContext()));
                            document.setInfo(info);
                        }
                        String fileName = "SampleBlankPDF.pdf";
                        File pdfFile = new File(getContext().getCacheDir(), CFileUtils.CACHE_FOLDER + File.separator + fileName);
                        pdfFile = CFileUtils.renameNameSuffix(pdfFile);
                        pdfFile.getParentFile().mkdirs();
                        // save file
                        boolean result = document.saveAs(pdfFile.getAbsolutePath(), false);
                        if (result) {
                            // Display corresponding functions according to the selected function type
                            enterFunctions(pdfFile.getAbsolutePath(), null);
                        }
                    } catch (CPDFDocumentException e) {

                    }
                });
                blankPageDialogFragment.show(getChildFragmentManager(), "blank page");
                insertDialogFragment.dismiss();
            });
            insertDialogFragment.setInsertPdfPageClickListener(v1 -> {
                // Enter the file manager and select a pdf file
                selectDocumentLauncher.launch(null);
                insertDialogFragment.dismiss();
            });
            insertDialogFragment.show(getChildFragmentManager(), "selectInsertTypeDialog");
        });
    }

    /**
     * Initialize the sample document list
     */
    private void initDocumentList() {
        if (funType == null) {
            return;
        }
        funListAdapter = new CHomeFunListAdapter();
        funListAdapter.setList(FunDatas.getDocumentListByFunType(getContext(), funType));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(funListAdapter);
        funListAdapter.setOnItemClickListener((adapter, view, position) -> {
            HomeFunBean bean = adapter.list.get(position);
            if (bean.getType() == HomeFunBean.FunType.SamplePDF) {
                // Display corresponding functions according to the selected function type
                enterFunctions(bean.getFilePath(), null);
            }
        });
    }

    private void enterFunctions(String filePath, Uri uri) {
        SamplesFactory factory = new SamplesFactory(this, filePath, uri);
        factory.getImpl(funType).run();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("funType", funType.name());
        super.onSaveInstanceState(outState);
    }
}
