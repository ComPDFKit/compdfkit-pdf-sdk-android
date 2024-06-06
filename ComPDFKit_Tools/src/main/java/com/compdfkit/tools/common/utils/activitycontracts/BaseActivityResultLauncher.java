package com.compdfkit.tools.common.utils.activitycontracts;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BaseActivityResultLauncher<I, O> {

    private final ActivityResultLauncher<I> launcher;
    private ActivityResultCallback<O> callback;

    public BaseActivityResultLauncher(@NonNull ActivityResultCaller caller,
                                      @NonNull ActivityResultContract<I, O> contract) {
        launcher = caller.registerForActivityResult(contract, result -> {
            if (callback != null) {
                callback.onActivityResult(result);
                callback = null;
            }
        });
    }

    public void launch(@Nullable I input, @NonNull ActivityResultCallback<O> callback) {
        this.callback = callback;
        launcher.launch(input);
    }

    public void launch(@Nullable I input) {
        launcher.launch(input);
    }
}
