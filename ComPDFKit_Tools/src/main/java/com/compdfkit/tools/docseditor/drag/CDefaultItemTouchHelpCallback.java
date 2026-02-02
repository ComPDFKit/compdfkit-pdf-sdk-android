/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
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

    private OnItemTouchCallbackListener onItemTouchCallbackListener;

    private boolean isCanSwap = false;

    private boolean isCanDrag = false;

    private boolean isCanSwipe = false;

    private boolean isSwaping = false;

    private boolean isDraging = false;

    private RecyclerView.ViewHolder lastViewHolder;

    private int sourcePosition;

    public CDefaultItemTouchHelpCallback(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    public void setDragEnable(boolean canDrag) {
        isCanDrag = canDrag;
    }


    public void setSwapEnable(boolean canSwap) {
        isCanSwap = canSwap;
    }
    public void setSwipeEnable(boolean canSwipe) {
        isCanSwipe = canSwipe;
    }


    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isCanSwipe;
    }

    /**
     * 
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // flag
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


            if (orientation == LinearLayoutManager.HORIZONTAL) {

                swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (orientation == LinearLayoutManager.VERTICAL) {

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

            viewHolder.itemView.setScaleX(0.7f);
            viewHolder.itemView.setScaleY(0.7f);


            if (isCanDrag && !isDraging) {
                sourcePosition = viewHolder.getAdapterPosition();
                isDraging = true;
            }
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
   
            viewHolder.itemView.setScaleX(1.0f);
            viewHolder.itemView.setScaleY(1.0f);

            if (isCanSwap) {

                if (null != onItemTouchCallbackListener && null != lastViewHolder && isSwaping) {
                    onItemTouchCallbackListener.onSwaped(viewHolder, lastViewHolder);
                    lastViewHolder.itemView.setAlpha(1.0f);
                    isSwaping = false;
                }
            } else {
     
                if ((null != onItemTouchCallbackListener) && isDraging && (sourcePosition != viewHolder.getAdapterPosition())) {
                    isDraging = false;
                    onItemTouchCallbackListener.onMoved(viewHolder, sourcePosition, viewHolder.getAdapterPosition());
                }
            }
        }
    }

    /**
     * 
     *
     * @param recyclerView recyclerView
     * @param source       
     * @param target       
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (isCanSwap) {
            if (onItemTouchCallbackListener != null && source.getAdapterPosition() != target.getAdapterPosition()) {
                if (lastViewHolder != null) {
                    lastViewHolder.itemView.setAlpha(1.0f);
                }
                lastViewHolder = target;
                lastViewHolder.itemView.setAlpha(0.4f);
                return false;
            }
        } else {
            if (onItemTouchCallbackListener != null && source.getAdapterPosition() != target.getAdapterPosition()) {
               
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
         * 
         *
         * @param adapterPosition item position
         */
        void onSwiped(int adapterPosition);

        /**
         * 
         *
         * @param sourceViewHolder 
         * @param targetViewHolder 
         * @return 
         */
        boolean onDragging(RecyclerView.ViewHolder sourceViewHolder, RecyclerView.ViewHolder targetViewHolder);


        /**
         * 
         *
         * @param sourcePosition：sourcePosition
         * @param targetPosition：targetPosition
         * @param sourceViewHolder：sourceViewHolder
         */
        void onMoved(RecyclerView.ViewHolder sourceViewHolder, int sourcePosition, int targetPosition);

        /**
         * @description：
         */
        void onSwaped(RecyclerView.ViewHolder sourceViewHolder, RecyclerView.ViewHolder targetViewHolder);
    }
}