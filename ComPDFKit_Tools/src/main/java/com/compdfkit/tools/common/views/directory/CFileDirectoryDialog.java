package com.compdfkit.tools.common.views.directory;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.ComponentDialog;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CPermissionUtil;
import com.compdfkit.tools.common.utils.activitycontracts.CMultiplePermissionResultLauncher;
import com.compdfkit.tools.common.utils.storage.CPDFStorageManager;
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

  private static final String TAG = "CPDFStorage";

  public static final String EXTRA_ROOT_DIR = "extra_root_dir";

  public static final String EXTRA_TITLE = "extra_title";

  public static final String EXTRA_CONFIRM_BUTTON_TITLE = "extra_confirm_button_title";

  private CToolBar toolBar;

  private RecyclerView rvFolderTitle;

  private RecyclerView rvFolderList;

  private AppCompatButton btnConfirm;

  private LinearLayout permissionDeniedLayout;

  private AppCompatButton btnOpenSettings;

  private CFileDirectoryTitleAdapter titleAdapter;

  private CFileDirectoryAdapter directoryAdapter;

  private COnSelectFolderListener selectFolderListener;

  private OnBackPressedCallback callback;

  private boolean waitingForLegacyPermissionFromSettings = false;

  private final CMultiplePermissionResultLauncher multiplePermissionResultLauncher =
      new CMultiplePermissionResultLauncher(this);

  public static CFileDirectoryDialog newInstance(String rootDir, String title,
      String confirmBtnTitle) {
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
          back();
        }
      };
      componentDialog.getOnBackPressedDispatcher().addCallback(callback);
    }
    return dialog;
  }

  private void back() {
    if (permissionDeniedLayout != null && permissionDeniedLayout.getVisibility() == View.VISIBLE) {
      dismiss();
      return;
    }
    if (titleAdapter == null || titleAdapter.list == null || titleAdapter.list.isEmpty()) {
      dismiss();
      return;
    }
    String dir = titleAdapter.getLastFolder();
    if (!TextUtils.isEmpty(dir) && titleAdapter.list.size() != 1) {
      titleAdapter.toupperLevel();
      String upperLevelDir = titleAdapter.getLastFolder();
      refreshDirectories(upperLevelDir);
    } else {
      dismiss();
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    if (getDialog() != null) {
      getDialog().setCancelable(false);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!waitingForLegacyPermissionFromSettings) {
      return;
    }
    waitingForLegacyPermissionFromSettings = false;
    if (!CPDFStorageManager.shouldRequestLegacyWritePermission()
        || CPermissionUtil.hasStoragePermissions(requireContext())) {
      initFolderTitleList();
      initDirectoriesList();
    } else {
      showPermissionDeniedView();
    }
  }

  @Override
  protected void onCreateView(View rootView) {
    toolBar = rootView.findViewById(R.id.tool_bar);
    rvFolderTitle = rootView.findViewById(R.id.rv_folder_title);
    rvFolderList = rootView.findViewById(R.id.recycler_view);
    btnConfirm = rootView.findViewById(R.id.btn_ok);
    permissionDeniedLayout = rootView.findViewById(R.id.layout_permission_denied);
    btnOpenSettings = rootView.findViewById(R.id.btn_open_settings);
    btnConfirm.setEnabled(false);
    btnOpenSettings.setOnClickListener(v -> {
      waitingForLegacyPermissionFromSettings = true;
      CPermissionUtil.toSelfSetting(requireContext());
    });
    btnConfirm.setOnClickListener(v -> {
      if (permissionDeniedLayout != null && permissionDeniedLayout.getVisibility() == View.VISIBLE) {
        return;
      }
      if (titleAdapter == null || titleAdapter.list == null || titleAdapter.list.isEmpty()) {
        return;
      }
      String dir = titleAdapter.getLastFolder();
      if (selectFolderListener != null) {
        if (!TextUtils.isEmpty(dir)) {
          selectFolderListener.folder(dir);
        } else {
          selectFolderListener.folder(getNormalFolder());
        }
      }
      dismiss();
    });
  }

  @Override
  protected void onViewCreate() {
    toolBar.setBackBtnClickListener(v -> back());
    if (getArguments() != null) {
      String title = getArguments().getString(EXTRA_TITLE);
      if (!TextUtils.isEmpty(title)) {
        toolBar.setTitle(title);
      }
      String confirmButtonTitle = getArguments().getString(EXTRA_CONFIRM_BUTTON_TITLE);
      if (!TextUtils.isEmpty(confirmButtonTitle)) {
        btnConfirm.setText(confirmButtonTitle);
      }
    }
    prepareDirectoryAccess();
  }

  private void prepareDirectoryAccess() {
    if (!CPDFStorageManager.shouldRequestLegacyWritePermission()) {
      initFolderTitleList();
      initDirectoriesList();
      return;
    }
    if (CPermissionUtil.hasStoragePermissions(requireContext())) {
      initFolderTitleList();
      initDirectoriesList();
      return;
    }
    CLog.d(TAG, "CFileDirectoryDialog request legacy storage permission");
    multiplePermissionResultLauncher.launch(CPermissionUtil.STORAGE_PERMISSIONS, result -> {
      if (CPermissionUtil.hasStoragePermissions(requireContext())) {
        initFolderTitleList();
        initDirectoriesList();
      } else {
        CLog.d(TAG, "CFileDirectoryDialog legacy storage permission denied");
        showPermissionDeniedView();
      }
    });
  }

  private void initFolderTitleList() {
    showDirectoryListView();
    titleAdapter = new CFileDirectoryTitleAdapter();
    rvFolderTitle.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
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
    String rootDir = titleAdapter.getLastFolder();
    refreshDirectories(rootDir);
  }

  private List<CFileDirectoryTitleBean> createTitleList() {
    String rootDir = CPDFStorageManager.getDefaultDirectoryDialogPath();
    if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString(EXTRA_ROOT_DIR))) {
      rootDir = getArguments().getString(EXTRA_ROOT_DIR);
    }
    if (Environment.getExternalStorageDirectory().getAbsolutePath().equals(rootDir)) {
      rootDir = CPDFStorageManager.getDefaultDirectoryDialogPath();
    }
    CLog.d(TAG, "CFileDirectoryDialog createTitleList rootDir=" + rootDir);
    return createPublicDirectoryBreadcrumb(rootDir, true);
  }

  private List<CFileDirectoryTitleBean> createPublicDirectoryBreadcrumb(String targetDir, boolean includeExternalRoot) {
    List<CFileDirectoryTitleBean> list = new ArrayList<>();
    File targetFile = new File(targetDir);
    File externalRoot = Environment.getExternalStorageDirectory();
    String normalizedTarget = targetFile.getAbsolutePath().replace('\\', '/');
    String normalizedRoot = externalRoot.getAbsolutePath().replace('\\', '/');
    if (!normalizedTarget.startsWith(normalizedRoot + "/")) {
      list.add(new CFileDirectoryTitleBean(targetFile));
      return list;
    }
    if (includeExternalRoot) {
      list.add(new CFileDirectoryTitleBean(externalRoot));
    }
    String relativePath = normalizedTarget.substring(normalizedRoot.length() + 1);
    if (TextUtils.isEmpty(relativePath)) {
      if (list.isEmpty()) {
        list.add(new CFileDirectoryTitleBean(targetFile));
      }
      return list;
    }
    String[] parts = relativePath.split("/");
    File current = externalRoot;
    for (String part : parts) {
      if (TextUtils.isEmpty(part)) {
        continue;
      }
      current = new File(current, part);
      if (!list.isEmpty()) {
        list.add(CFileDirectoryTitleBean.separator());
      }
      list.add(new CFileDirectoryTitleBean(current));
    }
    if (list.isEmpty()) {
      list.add(new CFileDirectoryTitleBean(targetFile));
    }
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


  private void refreshDirectories(String rootDir) {
    refreshConfirmBtn();
    new SimpleBackgroundTask<List<File>>(getContext()) {

      @Override
      protected List<File> onRun() {
        return CFileDirectoryDatas.getDirectories(getContext(), rootDir);
      }

      @Override
      protected void onSuccess(List<File> result) {
        directoryAdapter.setList(result);
      }
    }.execute();
  }

  private String getNormalFolder() {
    return CPDFStorageManager.getDefaultDirectoryDialogPath();
  }

  private void showPermissionDeniedView() {
    rvFolderTitle.setVisibility(View.GONE);
    rvFolderList.setVisibility(View.GONE);
    permissionDeniedLayout.setVisibility(View.VISIBLE);
    btnConfirm.setVisibility(View.GONE);
    btnConfirm.setEnabled(false);
  }

  private void showDirectoryListView() {
    rvFolderTitle.setVisibility(View.VISIBLE);
    rvFolderList.setVisibility(View.VISIBLE);
    permissionDeniedLayout.setVisibility(View.GONE);
    btnConfirm.setVisibility(View.VISIBLE);
    refreshConfirmBtn();
  }

  private void refreshConfirmBtn() {
    if (btnConfirm == null) {
      return;
    }
    if (titleAdapter == null || titleAdapter.list == null || titleAdapter.list.isEmpty()) {
      btnConfirm.setEnabled(false);
      return;
    }
    String dir = titleAdapter.getLastFolder();
    if (!TextUtils.isEmpty(dir) && !Environment.getExternalStorageDirectory().getAbsolutePath().equals(dir)) {
      btnConfirm.setEnabled(true);
    } else {
      btnConfirm.setEnabled(false);
    }
    Log.i("ComPDFKit",
        "refreshConfirmBtn: " + dir + ", size: " + titleAdapter.list.size() + ", enabled: "
            + btnConfirm.isEnabled() + " , visable:" + (btnConfirm.getVisibility() == View.VISIBLE));
  }

  public void setSelectFolderListener(COnSelectFolderListener selectFolderListener) {
    this.selectFolderListener = selectFolderListener;
  }

  public interface COnSelectFolderListener {

    void folder(String dir);
  }


}
