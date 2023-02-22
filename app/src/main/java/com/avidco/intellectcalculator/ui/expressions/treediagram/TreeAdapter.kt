package com.avidco.intellectcalculator.ui.expressions.treediagram

import com.avidco.intellectcalculator.ui.expressions.treediagram.graphview.AbstractGraphAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.avidco.intellectcalculator.R
import android.widget.TextView

class TreeAdapter : AbstractGraphAdapter<RecyclerView.ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return if (getNodeOperand(position)!!.isNotEmpty()) NODE_VIEW else PARENT_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return if (viewType == NODE_VIEW) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.node, parent, false)
            ViewHolderNode(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.parent_node, parent, false)
            ViewHolderParentNode(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == NODE_VIEW) {
            holder as ViewHolderNode

            holder.charTextView.text = getNodeOperand(position).toString()

        } else {
            holder as ViewHolderParentNode

            holder.prefixTextView.text = getNodePrefix(position)
            holder.postfixTextView.text = getNodePostfix(position)
        }
    }

    //**************** NODE VIEW HOLDER 1 ******************//
    inner class ViewHolderNode internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var charTextView: TextView

        init {
            charTextView = itemView.findViewById(R.id.character)
        }
    }
    //**************** PARENT NODE VIEW HOLDER ******************//
    inner class ViewHolderParentNode(itemView: View) : RecyclerView.ViewHolder(itemView){
        var prefixTextView: TextView
        var postfixTextView: TextView

        init {
            prefixTextView = itemView.findViewById(R.id.prefix)
            postfixTextView = itemView.findViewById(R.id.postfix)
        }
    }

    companion object {
        private const val NODE_VIEW = 0
        private const val PARENT_VIEW = 1
    }
}