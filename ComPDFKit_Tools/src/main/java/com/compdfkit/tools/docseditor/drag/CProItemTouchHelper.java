/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.docseditor.drag;

public class CProItemTouchHelper extends CDefaultItemTouchHelper {
    private CDefaultItemTouchHelpCallback itemTouchHelpCallback;

    /**
     * Creates an ProItemTouchHelper that will work with the given Callback.
     * <p>
     * You can attach ProItemTouchHelper to a RecyclerView via
     * . Upon attaching, it will add an item decoration,
     * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
     */
    public CProItemTouchHelper(CDefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener) {
        super(new CDefaultItemTouchHelpCallback(onItemTouchCallbackListener));
        itemTouchHelpCallback = (CDefaultItemTouchHelpCallback) getCallback();
    }

    /**
     * @param ：[canDrag]
     * @return : void
     * @description ：设置是否可以被拖拽
     */
    public void setDragEnable(boolean canDrag) {
        itemTouchHelpCallback.setDragEnable(canDrag);
    }

    /**
     * @param ：[canSwap]
     * @return : void
     * @description ：设置是否可以被位置交换
     */
    public void setSwapEnable(boolean canSwap) {
        itemTouchHelpCallback.setSwapEnable(canSwap);
    }

    /**
     * @param ：[canSwipe]
     * @return : void
     * @description ：设置是否可以被滑动
     */
    public void setSwipeEnable(boolean canSwipe) {
        itemTouchHelpCallback.setSwipeEnable(canSwipe);
    }
}

