package com.compdfkit.tools.common.views.directory;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.ComponentDialog;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.threadpools.SimpleBackgroundTask;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.directory.adapter.CFileDirectoryAdapter;
import com.compdfkit.tools.common.views.directory.adapter.CFileDirectoryTitleAdapter;
import com.compdfkit.tools.common.views.directory.bean.CFileDirectoryTitleBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CFileDirectoryDialog extends CBasicBottomSheetDialogFragment {

    public static final String EXTRA_ROOT_DIR = "extra_root_dir";

    public static final String EXTRA_TITLE = "extra_title";

    public static final String EXTRA_CONFIRM_BUTTON_TITLE = "extra_confirm_button_title";

    private CToolBar toolBar;

    private RecyclerView rvFolderTitle;

    private RecyclerView rvFolderList;

    private AppCompatButton btnConfirm;

    private CFileDirectoryTitleAdapter titleAdapter;

    private CFileDirectoryAdapter directoryAdapter;

    private COnSelectFolderListener selectFolderListener;

    private OnBackPressedCallback callback;

    public static CFileDirectoryDialog newInstance(String rootDir, String title, String confirmBtnTitle) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ROOT_DIR, rootDir);
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_CONFIRM_BUTTON_TITLE, confirmBtnTitle);
        CFileDirectoryDialog fragment = new CFileDirectoryDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean fullScreen() {
        return true;
    }

    @Override
    protected boolean draggable() {
        return false;
    }

    @Override
    protected float dimAmount() {
        return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_file_directory_dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog instanceof ComponentDialog) {
            ComponentDialog componentDialog = (ComponentDialog) dialog;
            callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    String dir = titleAdapter.getLastFolder();
                    if (!TextUtils.isEmpty(dir) && titleAdapter.list.size() != 1) {
                        titleAdapter.toupperLevel();
                        String upperLevelDir = titleAdapter.getLastFolder();
                        refreshDirectories(upperLevelDir);
                    } else {
                        dismiss();
                    }
                }
            };
            componentDialog.getOnBackPressedDispatcher().addCallback(callback);
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCancelable(false);
        }
    }

    @Override
    protected void onCreateView(View rootView) {
        toolBar = rootView.findViewById(R.id.tool_bar);
        rvFolderTitle = rootView.findViewById(R.id.rv_folder_title);
        rvFolderList = rootView.findViewById(R.id.recycler_view);
        btnConfirm = rootView.findViewById(R.id.btn_ok);
        btnConfirm.setOnClickListener(v -> {
            String dir = titleAdapter.getLastFolder();
            if (selectFolderListener != null) {
                if (!TextUtils.isEmpty(dir)){
                    selectFolderListener.folder(dir);

                }else {
                    selectFolderListener.folder(getNormalFolder());
                }
            }
            dismiss();
        });
    }

    @Override
    protected void onViewCreate() {
        toolBar.setBackBtnClickListener(v -> {
            dismiss();
        });
        if (getArguments() != null) {
            String title = getArguments().getString(EXTRA_TITLE);
            if (!TextUtils.isEmpty(title)){
                toolBar.setTitle(title);
            }
            String confirmButtonTitle = getArguments().getString(EXTRA_CONFIRM_BUTTON_TITLE);
            if (!TextUtils.isEmpty(confirmButtonTitle)){
                btnConfirm.setText(confirmButtonTitle);
            }
        }

        initFolderTitleList();
        initDirectoriesList();
    }

    private void initFolderTitleList() {
        titleAdapter = new CFileDirectoryTitleAdapter();
        rvFolderTitle.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFolderTitle.setAdapter(titleAdapter);
        titleAdapter.setList(createTitleList());
        titleAdapter.setOnItemClickListener((adapter, view, position) -> {
            CFileDirectoryTitleBean item = titleAdapter.list.get(position);
            if (!item.isSeparator()) {
                titleAdapter.removeRange(position);
                refreshDirectories(item.getFile().getAbsolutePath());
            }
        });
    }

    private void initDirectoriesList() {
        directoryAdapter = new CFileDirectoryAdapter();
        rvFolderList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFolderList.setAdapter(directoryAdapter);
        directoryAdapter.setOnItemClickListener((adapter, view, position) -> {
            File childDirectory = directoryAdapter.list.get(position);
            applyTitleList(childDirectory);
        });
        String rootDir = createTitleList().get(0).getFile().getAbsolutePath();
        refreshDirectories(rootDir);
    }

    private List<CFileDirectoryTitleBean> createTitleList() {
        String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString(EXTRA_ROOT_DIR))) {
            rootDir = getArguments().getString(EXTRA_ROOT_DIR);
        }
        List<CFileDirectoryTitleBean> list = new ArrayList<>();
        list.add(new CFileDirectoryTitleBean(new File(rootDir)));
        return list;
    }

    private void applyTitleList(File childFile) {
        List<CFileDirectoryTitleBean> list = new ArrayList<>();
        list.add(CFileDirectoryTitleBean.separator());
        list.add(new CFileDirectoryTitleBean(childFile));
        titleAdapter.addList(list);
        rvFolderTitle.smoothScrollToPosition(titleAdapter.list.size() - 1);
        refreshDirectories(childFile.getAbsolutePath());
    }


    private void refreshDirectories(String rootDir){
        new SimpleBackgroundTask<List<File>>(getContext()){

            @Override
            protected List<File> onRun() {
                return CFileDirectoryDatas.getDirectories(rootDir);
            }

            @Override
            protected void onSuccess(List<File> result) {
                directoryAdapter.setList(result);
            }
        }.execute();
    }

    private String getNormalFolder(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public void setSelectFolderListener(COnSelectFolderListener selectFolderListener) {
        this.selectFolderListener = selectFolderListener;
    }

    public interface COnSelectFolderListener{
        void folder(String dir);
    }


}
