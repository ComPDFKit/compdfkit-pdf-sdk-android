/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.compdfkit.pdfviewer.R;
import com.compdfkit.pdfviewer.databinding.FragmentHomeBinding;
import com.compdfkit.pdfviewer.home.datas.FunDatas;
import com.compdfkit.pdfviewer.home.samples.SamplesFactory;
import com.compdfkit.tools.common.pdf.CPDFDocumentActivity;
import com.compdfkit.tools.common.utils.activitycontracts.CSelectPDFDocumentResultContract;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.ui.utils.CPDFCommomUtils;


public class HomeFunFragment extends Fragment {

    private CHomeFunListAdapter funListAdapter;

    private FragmentHomeBinding binding;

    private ActivityResultLauncher<Void> selectDocumentLauncher = registerForActivityResult(new CSelectPDFDocumentResultContract(), uri -> {
        if (uri != null) {
            SamplesFactory factory = new SamplesFactory(this, "", uri);
            factory.getImpl(HomeFunBean.FunType.Compress).run();
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFunList();
    }

    private void initFunList() {
        funListAdapter = new CHomeFunListAdapter();
        funListAdapter.setList(FunDatas.getFunList(getContext()));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(funListAdapter);
        funListAdapter.setOnItemClickListener((adapter, view, position) -> {
            HomeFunBean homeFunBean = funListAdapter.list.get(position);
            if (homeFunBean.isHead()) {
                return;
            }
            switch (homeFunBean.getType()) {
                case SamplePDF:
                    CPDFDocumentActivity.startActivity(getContext(), homeFunBean.getFilePath(),
                            "", FunDatas.getConfiguration(getContext(), CPreviewMode.Viewer));
                    break;
                case Viewer:
                case Annotations:
                case Forms:
                case Signature:
                case DocumentEditor:
                case ContentEditor:
                case Security:
                case Watermark:
                    showDocumentListFragment(homeFunBean.getType());
                    break;
                case Redaction:
                    CPDFCommomUtils.gotoWebsite(getContext(), getString(R.string.tools_compdf_security_url), null);
                    break;
                case CompareDocuments:
                    CPDFCommomUtils.gotoWebsite(getContext(), getString(R.string.tools_compdf_compare_url), null);
                    break;
                case Conversion:
                    CPDFCommomUtils.gotoWebsite(getContext(), getString(R.string.tools_compdf_conversion_url), null);
                    break;
                case Compress:
                    selectDocumentLauncher.launch(null);
                    break;
                case Measurement:
                    CPDFCommomUtils.gotoWebsite(getContext(), getString(R.string.tools_contact_sales_url), null);
                    break;
                case FunSample:
                    startActivity(new Intent(getContext(), FunSamplesActivity.class));
                    break;
                default:
                    break;
            }
        });

    }

    private void showDocumentListFragment(HomeFunBean.FunType funType) {
        getParentFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.tools_slide_in_right, R.anim.tools_slide_out_left, R.anim.tools_slide_in_left, R.anim.tools_slide_out_right)
                .replace(R.id.fragment_content, DocumentListFragment.newInstance(funType), funType.name() + "_DocumentListFragment")
                .commit();
    }
}
