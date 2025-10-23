/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

class SampleListAdapter : RecyclerView.Adapter<SampleListAdapter.SampleListViewHolder>() {

    private val list: MutableList<PDFSamples> = ArrayList()

    var onItemClickListener : ((position : Int) -> Unit)? = null

    init {
        list.addAll(SampleApplication.instance.samplesList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleListViewHolder {
        return SampleListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_sample_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: SampleListViewHolder, position: Int) {
        holder.tvSampleTitle.text = list[holder.adapterPosition].title
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SampleListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSampleTitle: AppCompatTextView

        init {
            tvSampleTitle = itemView.findViewById(R.id.tv_sample_title)
        }
    }

}