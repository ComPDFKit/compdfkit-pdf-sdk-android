/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.docseditor.drag;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CDefaultItemTouchHelpCallback extends ItemTouchHelper.Callback {

    /**
     * Item操作的回调
     */
    private OnItemTouchCallbackListener onItemTouchCallbackListener;

    /**
     * 是否可以交换
     **/
    private boolean isCanSwap = false;
    /**
     * 是否可以拖拽
     */
    private boolean isCanDrag = false;
    /**
     * 是否可以被滑动
     */
    private boolean isCanSwipe = false;

    /**
     * 是否正在拖拽中：目前应用在Swap中
     **/
    private boolean isSwaping = false;

    /**
     * 是否正在拖拽中：isDraging
     **/
    private boolean isDraging = false;

    /**
     * 记录每次拖拽过程中触发的指定目标对象：目前应用在Swap中
     **/
    private RecyclerView.ViewHolder lastViewHolder;

    /**
     * 记录开始拖拽时触发的执行的当前item
     **/
    private int sourcePosition;

    public CDefaultItemTouchHelpCallback(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置Item操作的回调，去更新UI和数据源
     *
     * @param onItemTouchCallbackListener
     */
    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置是否可以被拖拽
     *
     * @param canDrag 是true，否false
     */
    public void setDragEnable(boolean canDrag) {
        isCanDrag = canDrag;
    }

    /**
     * @param ：[canSwap]
     * @return : void
     * @description ：设置是否可以被位置交换
     */
    public void setSwapEnable(boolean canSwap) {
        isCanSwap = canSwap;
    }

    /**
     * 设置是否可以被滑动
     *
     * @param canSwipe 是true，否false
     */
    public void setSwipeEnable(boolean canSwipe) {
        isCanSwipe = canSwipe;
    }

    /**
     * 当Item被长按的时候是否可以被拖拽
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }

    /**
     * Item是否可以被滑动(H：左右滑动，V：上下滑动)
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return isCanSwipe;
    }

    /**
     * 当用户拖拽或者滑动Item的时候需要我们告诉系统滑动或者拖拽的方向
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // flag如果值是0，相当于这个功能被关闭
            int dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlag = 0;
            // create make
            return makeMovementFlags(dragFlag, swipeFlag);
        } else if (layoutManager instanceof LinearLayoutManager) {
            /****** linearLayoutManager ******/
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();

            int dragFlag = 0;
            int swipeFlag = 0;

            // 为了方便理解，相当于分为横着的ListView和竖着的ListView
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                /****** 如果是横向的布局 ******/
                swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (orientation == LinearLayoutManager.VERTICAL) {
                /****** 如果是竖向的布局，相当于ListView ******/
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFlag, swipeFlag);
        }
        return 0;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && null != viewHolder) {
            //当滑动或者拖拽view的时候通过接口返回该ViewHolder
            //当拖拽选中时放大选中的view
            viewHolder.itemView.setScaleX(0.7f);
            viewHolder.itemView.setScaleY(0.7f);

            /****** 记录拖拽时，执行对象的item ******/
            if (isCanDrag && !isDraging) {
                sourcePosition = viewHolder.getAdapterPosition();
                isDraging = true;
            }

            /****** 是否可以交换 ******/
            if (isCanSwap) {
                isSwaping = true;
                if (null != lastViewHolder) {
                    lastViewHolder.itemView.setAlpha(1.0f);
                    lastViewHolder = null;
                }
            }
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (!recyclerView.isComputingLayout()) {
            //当需要清除之前在onSelectedChanged或者onChildDraw,onChildDrawOver设置的状态或者动画时通过接口返回该ViewHolder
            //拖拽结束后恢复view的状态
            viewHolder.itemView.setScaleX(1.0f);
            viewHolder.itemView.setScaleY(1.0f);

            if (isCanSwap) {
                /****** 拖拽实现item的互相交换 ******/
                if (null != onItemTouchCallbackListener && null != lastViewHolder && isSwaping) {
                    onItemTouchCallbackListener.onSwaped(viewHolder, lastViewHolder);
                    lastViewHolder.itemView.setAlpha(1.0f);
                    isSwaping = false;
                }
            } else {
                /****** 拖拽排序中 ******/
                if ((null != onItemTouchCallbackListener) && isDraging && (sourcePosition != viewHolder.getAdapterPosition())) {
                    isDraging = false;
                    onItemTouchCallbackListener.onMoved(viewHolder, sourcePosition, viewHolder.getAdapterPosition());
                }
            }
        }
    }

    /**
     * 当Item被拖拽的时候被回调
     *
     * @param recyclerView recyclerView
     * @param source       拖拽的ViewHolder
     * @param target       目的地的viewHolder
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (isCanSwap) {
            /****** 如果已开启Swap功能，实现位置的交换 ******/
            if (onItemTouchCallbackListener != null && source.getAdapterPosition() != target.getAdapterPosition()) {
                if (lastViewHolder != null) {
                    lastViewHolder.itemView.setAlpha(1.0f);
                }
                lastViewHolder = target;
                lastViewHolder.itemView.setAlpha(0.4f);
                return false;
            }
        } else {//否则保持正常的排序
            if (onItemTouchCallbackListener != null && source.getAdapterPosition() != target.getAdapterPosition()) {
                /****** 通过接口传递拖拽交换数据的起始位置和目标位置的ViewHolder ******/
                return onItemTouchCallbackListener.onDragging(source, target);
            }
        }
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (onItemTouchCallbackListener != null) {
            onItemTouchCallbackListener.onSwiped(viewHolder.getAdapterPosition());
        }
    }

    public interface OnItemTouchCallbackListener {

        /**
         * 当某个Item被滑动删除的时候
         *
         * @param adapterPosition item的position
         */
        void onSwiped(int adapterPosition);

        /**
         * 当两个Item位置互换的时候被回调
         *
         * @param sourceViewHolder 拖拽的item的position
         * @param targetViewHolder 目的地的Item的position
         * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        boolean onDragging(RecyclerView.ViewHolder sourceViewHolder, RecyclerView.ViewHolder targetViewHolder);


        /**
         * 拖拽排序
         *
         * @param sourcePosition：sourcePosition
         * @param targetPosition：targetPosition
         * @param sourceViewHolder：sourceViewHolder
         */
        void onMoved(RecyclerView.ViewHolder sourceViewHolder, int sourcePosition, int targetPosition);

        /**
         * @description：实现两个item的位置交换
         */
        void onSwaped(RecyclerView.ViewHolder sourceViewHolder, RecyclerView.ViewHolder targetViewHolder);
    }
}