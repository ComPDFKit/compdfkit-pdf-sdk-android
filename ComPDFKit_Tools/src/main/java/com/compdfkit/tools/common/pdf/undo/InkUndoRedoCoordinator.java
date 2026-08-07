/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.undo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.common.pdf.config.AnnotationsConfig;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Coordinates Ink and document histories so Redo follows the recorded Undo order.
 */
public final class InkUndoRedoCoordinator {

    public interface OnStateChangedListener {
        void onStateChanged();
    }

    // TInkDrawHelper accepts one callback, so controls attached to one reader share a coordinator.
    private static final Map<CPDFReaderView, InkUndoRedoCoordinator> COORDINATORS =
            new WeakHashMap<>();

    private final WeakReference<CPDFReaderView> readerViewRef;
    private final RedoHistoryOrder redoOrder = new RedoHistoryOrder();
    private final List<WeakReference<OnStateChangedListener>> stateListeners = new ArrayList<>();
    private AnnotationsConfig.InkUndoRedoMode activeMode;
    // The document history listener can arrive after undo()/redo() returns.
    @Nullable
    private InkUndoRedoResolver.HistoryTarget pendingHistorySource;

    private InkUndoRedoCoordinator(CPDFReaderView readerView) {
        readerViewRef = new WeakReference<>(readerView);
        readerView.getUndoManager().enable(true);
        readerView.getUndoManager().addOnUndoHistoryChangeListener(
                (undoManager, operation, type) -> onHistoryChanged(
                        InkUndoRedoResolver.HistoryTarget.DOCUMENT));
        readerView.getInkDrawHelper().setInkUndoRedoCallback((undo, redo) -> onHistoryChanged(
                InkUndoRedoResolver.HistoryTarget.INK));
    }

    @NonNull
    public static synchronized InkUndoRedoCoordinator get(@NonNull CPDFReaderView readerView) {
        InkUndoRedoCoordinator coordinator = COORDINATORS.get(readerView);
        if (coordinator == null) {
            coordinator = new InkUndoRedoCoordinator(readerView);
            COORDINATORS.put(readerView, coordinator);
        }
        return coordinator;
    }

    public void addOnStateChangedListener(@NonNull OnStateChangedListener listener) {
        removeOnStateChangedListener(listener);
        stateListeners.add(new WeakReference<>(listener));
    }

    public void removeOnStateChangedListener(@NonNull OnStateChangedListener listener) {
        Iterator<WeakReference<OnStateChangedListener>> iterator = stateListeners.iterator();
        while (iterator.hasNext()) {
            OnStateChangedListener item = iterator.next().get();
            if (item == null || item == listener) {
                iterator.remove();
            }
        }
    }

    public boolean canUndo(@NonNull AnnotationsConfig.InkUndoRedoMode mode, boolean isInkEditing) {
        return resolveUndoTarget(mode, isInkEditing) != InkUndoRedoResolver.HistoryTarget.NONE;
    }

    public boolean canRedo(@NonNull AnnotationsConfig.InkUndoRedoMode mode, boolean isInkEditing) {
        return resolveRedoTarget(mode, isInkEditing) != InkUndoRedoResolver.HistoryTarget.NONE;
    }

    @NonNull
    public InkUndoRedoResolver.HistoryTarget undo(
            @NonNull AnnotationsConfig.InkUndoRedoMode mode, boolean isInkEditing) {
        InkUndoRedoResolver.HistoryTarget target = resolveUndoTarget(mode, isInkEditing);
        if (target == InkUndoRedoResolver.HistoryTarget.NONE) {
            return target;
        }
        if (perform(target, InkUndoRedoResolver.Action.UNDO)) {
            redoOrder.recordUndo(target);
        }
        notifyStateChanged();
        return target;
    }

    @NonNull
    public InkUndoRedoResolver.HistoryTarget redo(
            @NonNull AnnotationsConfig.InkUndoRedoMode mode, boolean isInkEditing) {
        InkUndoRedoResolver.HistoryTarget target = resolveRedoTarget(mode, isInkEditing);
        if (target == InkUndoRedoResolver.HistoryTarget.NONE) {
            return target;
        }
        boolean isRecordedUndo = !redoOrder.isEmpty();
        if (perform(target, InkUndoRedoResolver.Action.REDO) && isRecordedUndo) {
            redoOrder.completeRedo();
        }
        notifyStateChanged();
        return target;
    }

    public void invalidateRedoHistory() {
        pendingHistorySource = null;
        if (!redoOrder.isEmpty()) {
            redoOrder.clear();
            notifyStateChanged();
        }
    }

    private InkUndoRedoResolver.HistoryTarget resolveUndoTarget(
            AnnotationsConfig.InkUndoRedoMode mode, boolean isInkEditing) {
        ensureMode(mode);
        CPDFReaderView readerView = readerViewRef.get();
        if (readerView == null) {
            return InkUndoRedoResolver.HistoryTarget.NONE;
        }
        return InkUndoRedoResolver.resolve(mode, InkUndoRedoResolver.Action.UNDO, isInkEditing,
                readerView.getInkDrawHelper().canUndo(), readerView.getUndoManager().canUndo());
    }

    private InkUndoRedoResolver.HistoryTarget resolveRedoTarget(
            AnnotationsConfig.InkUndoRedoMode mode, boolean isInkEditing) {
        ensureMode(mode);
        CPDFReaderView readerView = readerViewRef.get();
        if (readerView == null) {
            return InkUndoRedoResolver.HistoryTarget.NONE;
        }
        if (!redoOrder.isEmpty()) {
            InkUndoRedoResolver.HistoryTarget target = redoOrder.peekRedo();
            if (!isTargetAllowed(target, mode, isInkEditing) || !isAvailable(target,
                    InkUndoRedoResolver.Action.REDO, readerView)) {
                redoOrder.clear();
                return InkUndoRedoResolver.HistoryTarget.NONE;
            }
            return target;
        }

        if (mode == AnnotationsConfig.InkUndoRedoMode.HYBRID && isInkEditing) {
            boolean inkCanRedo = readerView.getInkDrawHelper().canRedo();
            boolean documentCanRedo = readerView.getUndoManager().canRedo();
            if (inkCanRedo == documentCanRedo) {
                return InkUndoRedoResolver.HistoryTarget.NONE;
            }
            return inkCanRedo ? InkUndoRedoResolver.HistoryTarget.INK
                    : InkUndoRedoResolver.HistoryTarget.DOCUMENT;
        }
        return InkUndoRedoResolver.resolve(mode, InkUndoRedoResolver.Action.REDO, isInkEditing,
                readerView.getInkDrawHelper().canRedo(), readerView.getUndoManager().canRedo());
    }

    private boolean perform(InkUndoRedoResolver.HistoryTarget target,
                            InkUndoRedoResolver.Action action) {
        CPDFReaderView readerView = readerViewRef.get();
        if (readerView == null || !isAvailable(target, action, readerView)) {
            return false;
        }
        pendingHistorySource = target;
        try {
            if (target == InkUndoRedoResolver.HistoryTarget.INK) {
                if (action == InkUndoRedoResolver.Action.UNDO) {
                    readerView.getInkDrawHelper().onUndo();
                } else {
                    readerView.getInkDrawHelper().onRedo();
                }
            } else {
                if (action == InkUndoRedoResolver.Action.UNDO) {
                    readerView.getUndoManager().undo();
                } else {
                    readerView.getUndoManager().redo();
                }
            }
            return true;
        } catch (Exception e) {
            pendingHistorySource = null;
            return false;
        }
    }

    private boolean isTargetAllowed(InkUndoRedoResolver.HistoryTarget target,
                                    AnnotationsConfig.InkUndoRedoMode mode,
                                    boolean isInkEditing) {
        if (!isInkEditing) {
            return target == InkUndoRedoResolver.HistoryTarget.DOCUMENT;
        }
        if (mode == AnnotationsConfig.InkUndoRedoMode.INK_ONLY) {
            return target == InkUndoRedoResolver.HistoryTarget.INK;
        }
        if (mode == AnnotationsConfig.InkUndoRedoMode.DOCUMENT_ONLY) {
            return target == InkUndoRedoResolver.HistoryTarget.DOCUMENT;
        }
        return true;
    }

    private boolean isAvailable(InkUndoRedoResolver.HistoryTarget target,
                                InkUndoRedoResolver.Action action, CPDFReaderView readerView) {
        if (target == InkUndoRedoResolver.HistoryTarget.INK) {
            return action == InkUndoRedoResolver.Action.UNDO
                    ? readerView.getInkDrawHelper().canUndo() : readerView.getInkDrawHelper().canRedo();
        }
        if (target == InkUndoRedoResolver.HistoryTarget.DOCUMENT) {
            return action == InkUndoRedoResolver.Action.UNDO
                    ? readerView.getUndoManager().canUndo() : readerView.getUndoManager().canRedo();
        }
        return false;
    }

    private void ensureMode(AnnotationsConfig.InkUndoRedoMode mode) {
        if (activeMode != mode) {
            activeMode = mode;
            redoOrder.clear();
        }
    }

    private void onHistoryChanged(InkUndoRedoResolver.HistoryTarget source) {
        if (isPendingOperation(source)) {
            pendingHistorySource = null;
        } else {
            redoOrder.clear();
        }
        notifyStateChanged();
    }

    private boolean isPendingOperation(InkUndoRedoResolver.HistoryTarget source) {
        return pendingHistorySource == source;
    }

    private void notifyStateChanged() {
        Iterator<WeakReference<OnStateChangedListener>> iterator = stateListeners.iterator();
        while (iterator.hasNext()) {
            OnStateChangedListener listener = iterator.next().get();
            if (listener == null) {
                iterator.remove();
            } else {
                listener.onStateChanged();
            }
        }
    }
}
